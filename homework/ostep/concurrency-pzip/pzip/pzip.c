/******************************************************************************

Code for pzip, a parallelized version of the zip command.

*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#include <fcntl.h>
#include <unistd.h>

#include <sys/stat.h> 
#include <sys/mman.h> 
#include <sys/sysinfo.h>

#include "pzip.h"
#include "circular_buffer.h"

size_t g_total_pages = 0;
size_t* g_pages_in_file = NULL;
zipped_piece* g_output = NULL;
circular_buffer g_buffer = {};

int g_finished_producing = 0;
pthread_mutex_t work_lock = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t filelock = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t empty = PTHREAD_COND_INITIALIZER;
pthread_cond_t fill = PTHREAD_COND_INITIALIZER;

const size_t PAGE_SIZE = 4096;
const unsigned MAX_ZIPPED_PIECES = (1 << 20);

size_t calc_out_array_pos(file_page_data* temp) {
    size_t file_offset = 0;
    for (size_t i = 0; i < temp->file_number; i++)
        file_offset += g_pages_in_file[i];
    return file_offset + temp->page_number;
}

zipped_piece run_length_encode(file_page_data* temp) {
    size_t byte_count = temp->last_page_size;
    zipped_piece compressed = {};
    compressed.count = malloc(byte_count * sizeof(int));
    
    size_t idx_seq = 0;
    char* rle_sequence = malloc(byte_count);

    for (size_t idx_org = 0; idx_org < byte_count; idx_org++) {
        /* Add new byte to sequence */
        char org_byte = temp->address[idx_org];
        rle_sequence[idx_seq] = org_byte;
        compressed.count[idx_seq] = 1;

        /* Count how many times that byte repeats */
        while (idx_org + 1 < byte_count && org_byte == temp->address[idx_org + 1]) {
            compressed.count[idx_seq]++;
            idx_org++;
        }
        idx_seq++;
    }

    compressed.size = idx_seq;
    compressed.data = realloc(rle_sequence, idx_seq);
    return compressed;
}

void consumer() {
    do {
        pthread_mutex_lock(&work_lock);
            /* If buffer is empty, wake producer and wait for work */
            while (g_buffer.count == 0 && !g_finished_producing) {
                pthread_cond_signal(&empty);
                pthread_cond_wait(&fill, &work_lock);
            }

            /* If producer is done mapping and there's nothing left in the queue */
            if (g_buffer.count == 0 && g_finished_producing) {
                pthread_mutex_unlock(&work_lock);
                break;
            }

            /* Get piece of work and tell producer to generate more */
            file_page_data temp = {};
            cb_pop_front(&g_buffer, &temp);
            if (!g_finished_producing) {
                pthread_cond_signal(&empty);
            }
        pthread_mutex_unlock(&work_lock);

        size_t position = calc_out_array_pos(&temp);
        g_output[position] = run_length_encode(&temp);

    } while (!g_finished_producing || g_buffer.count != 0);
}

void producer(const pzip_input* input) {
    for (size_t file_num = 0; file_num < input->num_files; file_num++) {
        int file = open(input->paths[file_num], O_RDONLY);
        if (file == -1) {
            printf("Error: Can't open file\n");
            exit(1);
        }

        struct stat sb;
        if (fstat(file, &sb) == -1) {
            printf("Error: Can't retrieve file stats");
            close(file);
            exit(1);
        }
        else if (sb.st_size == 0) {
            continue;
        }

        size_t last_page_size = PAGE_SIZE;
        size_t pages_in_file = sb.st_size / PAGE_SIZE;
        if ((sb.st_size % PAGE_SIZE) != 0) {
            pages_in_file += 1;
            last_page_size = sb.st_size % PAGE_SIZE;
        }

        g_total_pages += pages_in_file;
        g_pages_in_file[file_num] = pages_in_file;

        char* map = mmap(NULL, sb.st_size, PROT_READ, MAP_SHARED, file, 0);
        if (map == MAP_FAILED) {
            printf("Error mmapping the file\n");
            close(file);
            exit(1);
        }

        for (size_t page_num = 0; page_num < pages_in_file; page_num++) {
            /* If buffer is full, wake all consumers and wait for empty buffer */
            pthread_mutex_lock(&work_lock);
            while (g_buffer.count == g_buffer.capacity) {
                pthread_cond_broadcast(&fill);
                pthread_cond_wait(&empty, &work_lock);
            }
            pthread_mutex_unlock(&work_lock);

            /* Create file page data */
            int last_page = page_num == (pages_in_file - 1);
            file_page_data temp = {
                .address = map,
                .page_number = page_num,
                .file_number = file_num,
                .last_page_size = (last_page) ? last_page_size : PAGE_SIZE
            };
            map += PAGE_SIZE;

            /* Push file page data to buffer*/
            pthread_mutex_lock(&work_lock);
            cb_push_back(&g_buffer, &temp);
            pthread_mutex_unlock(&work_lock);
            pthread_cond_signal(&fill);
        }
        close(file);
    }

    /* Wake all consumer threads to ensure they can exit when work is done */
    pthread_mutex_lock(&work_lock);
    g_finished_producing = 1;
    pthread_mutex_unlock(&work_lock);
    pthread_cond_broadcast(&fill);
}

void consolidate_pages(zipped_piece* pieces, char** output) {
    for (size_t i = 0; i < g_total_pages; i++) {
        zipped_piece cur_piece = pieces[i];

        if (i < (g_total_pages - 1)) {
            zipped_piece next_piece = pieces[i + 1];

            /* Compare current piece's last char to next piece's first char
               and merge them if they're the same */
            if (cur_piece.data[cur_piece.size - 1] == next_piece.data[0]) {
                next_piece.count[0] += cur_piece.count[cur_piece.size - 1];
                cur_piece.size--;
            }
        }

        for (size_t j = 0; j < cur_piece.size; j++) {
            *((int*)output) = cur_piece.count[j]; output += sizeof(int);
            *((char*)output) = cur_piece.data[j]; output += sizeof(char);
        }
    }
}

void write_zip(zipped_piece* pieces) {
    char* output = malloc(g_total_pages * PAGE_SIZE * (sizeof(int) + sizeof(char)));
    char* output_it = output;
    consolidate_pages(pieces, &output_it);
    fwrite(output, sizeof(char), (output_it - output) / sizeof(char), stdout);
}

void zip_files(pzip_input* input) {
    cb_init(&g_buffer, 10, sizeof(file_page_data));
    g_pages_in_file = malloc(input->num_files * sizeof(int));
    g_output = malloc(sizeof(zipped_piece) * MAX_ZIPPED_PIECES);

    int consumer_thread_count = get_nprocs() - 1;
    pthread_t producer_tid = 0, consumer_tid[consumer_thread_count];
    
    pthread_create(&producer_tid, NULL, (void*(*)(void*))producer, (void*)input);     
    for (int i = 0; i < consumer_thread_count; i++)
        pthread_create(&consumer_tid[i], NULL, (void* (*)(void*))consumer, NULL);
    
    for (int i = 0; i < consumer_thread_count; i++)
        pthread_join(consumer_tid[i], NULL);
    pthread_join(producer_tid, NULL);

    write_zip(g_output);
}

int main(int argc, char* argv[argc]) {
    if (argc <= 1) {
        printf("pzip: file1 [file2 ...]\n");
        return 1;
    }

    pzip_input input = { .num_files = argc - 1, .paths = &(argv[1]) };
    zip_files(&input);
    exit(0);
}
