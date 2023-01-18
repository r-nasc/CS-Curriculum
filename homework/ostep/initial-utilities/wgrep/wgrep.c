/******************************************************************************

Code for wgrep, a copy of the grep command.

*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void search_content(FILE* fp, char const* search_str) {
    char *line = NULL;
    size_t len = 0;
    
    while (getline(&line, &len, fp) != -1) {
        if (strstr(line, search_str)) {
            printf("%s", line);
        }
    }
    free(line);
}

void search_file(char const* path, char const* search_str) {
    FILE* fp = fopen(path, "r");
    if (!fp) {
        printf("wgrep: cannot open file\n");
        exit(1);
    }
    search_content(fp, search_str);
    fclose(fp);
}

int main(int argc, char* argv[argc]) {
    if (argc <= 1) {
        printf("wgrep: searchterm [file ...]\n");
        return 1;
    }
    
    char* search_str = argv[1];
    if (strlen(search_str) == 0) {
        return 0;
    }
    
    if (argc == 2) {
        search_content(stdin, search_str);
        return 0;
    }
    
    for (size_t i = 2; i < argc; i++) {
        search_file(argv[i], search_str);
    }
    
    return 0;
}
