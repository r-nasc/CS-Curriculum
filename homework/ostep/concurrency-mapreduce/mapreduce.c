/******************************************************************************

Code for mapreduce, a single-machine multi-threaded implementation of the MapReduce paradigm.

*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include "mapreduce.h"
#include "structs.h"

Partitioner g_partitioner;
Mapper g_mapper;
Reducer g_reducer;

int g_partition_count;
int g_cur_partition;
int g_file_count;
int g_cur_file;

table** partition_array;
node** reduce_node_array;

pthread_mutex_t filelock = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t* partitionlock;

char* get_next(char* key, int partition_num) {
    node* tmp_node = reduce_node_array[partition_num];
    subnode* addr = tmp_node->current;
    if (addr) {
        tmp_node->current = addr->next;
        return addr->val;        
    }
    return NULL;
}

void sort_and_reduce(int i) {
    if (partition_array[i] == NULL)
        return;

    size_t node_count = partition_array[i]->nodesize;
    node* list[node_count];

    table* tempTable = partition_array[i];
    size_t reduce_count = 0;
    for (size_t j = 0; j < TABLE_SIZE; j++) {
        if (tempTable->list[j] == NULL)
            continue;
        
        node* tmp_node = tempTable->list[j];
        while (tmp_node) {
            list[reduce_count++] = tmp_node;
            tmp_node = tmp_node->next;
        }
    }

    qsort(list, node_count, sizeof(node*), node_compare);
    for (size_t k = 0; k < reduce_count; k++) {
        reduce_node_array[i] = list[k];
        g_reducer(list[k]->key, get_next, i);
    }
}

void* iter_reducer() {
    pthread_mutex_lock(&filelock);
    while (g_cur_partition < g_partition_count) {
        int x = g_cur_partition++;
        pthread_mutex_unlock(&filelock);
        sort_and_reduce(x);
        pthread_mutex_lock(&filelock);
    }
    pthread_mutex_unlock(&filelock);
}

void* iter_mapper(void* files) {
    pthread_mutex_lock(&filelock);
    while (g_cur_file <= g_file_count) {
        int x = g_cur_file++;
        pthread_mutex_unlock(&filelock);
        g_mapper(((char**)files)[x]);
        pthread_mutex_lock(&filelock);
    }
    pthread_mutex_unlock(&filelock);
    return NULL;
}

unsigned long MR_DefaultHashPartition(char* key, int num_partitions) {
    unsigned long hash = 5381, c = 0;
    while ((c = *key++) != '\0')
        hash = hash * 33 + c;
    return hash % num_partitions;
}

void MR_Emit(char* key, char* value) {
    long partitionNumber = g_partitioner ? g_partitioner(key, g_partition_count) : MR_DefaultHashPartition(key, g_partition_count);
    table_insert(partition_array[partitionNumber], key, value);
}

void MR_Run(int argc, char* argv[argc], Mapper map, int num_mappers, Reducer reduce, int num_reducers, Partitioner partition) {
    g_cur_partition = 0;
    g_cur_file = 1;
    g_partitioner = partition;
    g_mapper = map;
    g_reducer = reduce;
    g_partition_count = num_reducers; //Number of partitions
    g_file_count = argc - 1;

    /* INIT */
    partition_array = malloc(sizeof(table*) * num_reducers);
    for (int i = 0; i < g_partition_count; i++)
        partition_array[i] = createTable(TABLE_SIZE);    

    /* MAP */
    pthread_t mappers[num_mappers];
    for (int i = 0; i < num_mappers || i == argc - 1; i++)
        pthread_create(&mappers[i], NULL, iter_mapper, (void*)argv);    
    for (int i = 0; i < num_mappers || i == argc - 1; i++)
        pthread_join(mappers[i], NULL);    

    /* REDUCE */
    pthread_t reducer[num_reducers];
    reduce_node_array = malloc(sizeof(node*) * num_reducers);
    for (int i = 0; i < num_reducers; i++)
        pthread_create(&reducer[i], NULL, iter_reducer, NULL); 
    for (int i = 0; i < num_reducers; i++)
        pthread_join(reducer[i], NULL);
    
    /* CLEANUP */
    for (int i = 0; i < g_partition_count; i++)
        table_free(partition_array[i]);
    free(partitionlock);
    free(reduce_node_array);
    free(partition_array);
}
