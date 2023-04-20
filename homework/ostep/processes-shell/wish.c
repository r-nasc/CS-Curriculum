#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include "vec.h"

vec PATH = {};

void show_error();
void process_input(FILE* input);
char* get_input_line(FILE* input);
int interactive_mode();
int batch_mode(const char* path);
vec parse_input(char* line, char* sep, bool* sep_found);
pid_t execute_cmd(char* command, char* redirect_file);


int main(int argc, char* argv[argc]) {
    vec_init(&PATH);
    vec_push_back(&PATH, "/bin");

    if (argc == 2) {
        return batch_mode(argv[1]);
    }
    if (argc == 1) {
        return interactive_mode();
    }
    show_error();
    exit(1);
}

int interactive_mode() {
    process_input(stdin);
    return 0;
}

int batch_mode(const char* path) {
    FILE* fp = fopen(path, "r");
    if (!fp) {
        show_error();
        exit(1);
    }
    process_input(fp);
    return 0;
}

void process_input(FILE* input) {
    while (true) {
        char* line = get_input_line(input);
        if (line == NULL)
            break;

        vec parallel_commands = parse_input(line, "&", NULL);
        if (parallel_commands.size > 0) {
            pid_t wait_pids[parallel_commands.size];
            size_t pid_count = 0;

            for (size_t i = 0; i < parallel_commands.size; ++i) {
                bool redir_found = false;
                char* raw_command = (char*)vec_get(&parallel_commands, i);
                vec redirection = parse_input(raw_command, ">", &redir_found);

                char *command = NULL, *redirect_file = NULL;
                if (redirection.size == 2) {
                    char* redirect_line = (char*)vec_get(&redirection, 1);
                    vec redir_args = parse_input(redirect_line, " \t", NULL);
                    if (redir_args.size == 1) {
                        redirect_file = vec_get(&redir_args, 0);
                    } else {
                        show_error();
                        break;
                    }
                } else if (redir_found || redirection.size != 1) {
                    show_error();
                    break;
                }
                command = (char*)vec_get(&redirection, 0);

                pid_t pid = execute_cmd(command, redirect_file);
                if (pid != -1)
                    wait_pids[pid_count++] = pid;
                vec_destroy(&redirection);
            }

            for (size_t i = 0; i < pid_count; i++) {
                waitpid(wait_pids[i], NULL, 0);
            }
        }

        free(line);
        vec_destroy(&parallel_commands);
    }
}

char* get_input_line(FILE* input) {
    if (input == stdin)
        printf("wish> ");

    char* line = NULL;
    size_t n = 0;
    int len = getline(&line, &n, input);
    if (len == EOF)
        return NULL;

    line[len-1] = '\0'; // Swap \n by \0
    return line;
}

vec parse_input(char* line, char* sep, bool* sep_found) {
    vec out = {};
    vec_init(&out);

    int n = 0;
    char* found;
    char* line_copy = strdup(line);
    while ((found = strsep(&line_copy, sep)) != NULL) {
        if (sep_found && n++ > 0)
            *sep_found = true;
        if (strlen(found) > 0)
            vec_push_back(&out, found);
    }
    free(line_copy);
    return out;
}

pid_t execute_cmd(char* command, char* redirect_file) {
    vec args = parse_input(command, " \t", NULL);
    if (args.size == 0)
        return -1;
    char* arg1 = (char*)vec_get(&args, 0);

    if (strcmp(arg1, "exit") == 0) {
        if (args.size == 1)
            exit(0);
        show_error();
        return -1;
    }

    if (strcmp(arg1, "cd") == 0) {
        if (args.size == 2)
            chdir((char*)vec_get(&args, 1));
        else
            show_error();
        return -1;
    }

    if (strcmp("path", arg1) == 0) {
        vec_destroy(&PATH);
        vec_init(&PATH);
        for (int i = 1; i < args.size; i++)
            vec_push_back(&PATH, (char*)vec_get(&args, i));
        return -1;
    }

    for (int i = 0; i < PATH.size; i++) {
        char* search_path = (char*)vec_get(&PATH, i);
        char exe_path[256];
        snprintf(exe_path, sizeof(char) * 256, "%s/%s", search_path, arg1);
        if (access(exe_path, X_OK) == 0) {
            char* argv[args.size+1];
            for (int i = 0; i < args.size; i++)
                argv[i] = (char*)vec_get(&args, i);
            argv[args.size] = '\0';

            int child_pid = fork();
            if (child_pid == 0) {
                if (redirect_file) {
                    close(STDOUT_FILENO);
                    open(redirect_file, O_CREAT|O_WRONLY|O_TRUNC, S_IRWXU);
                }
                execv(exe_path, argv);
            }
            return child_pid;
        }
    }
    show_error();
    return -1;
}

void show_error() {
    const char* error_message = "An error has occurred\n";
    fprintf(stderr, error_message, strlen(error_message));
}
