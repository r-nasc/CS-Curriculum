/******************************************************************************

Code for wunzip, a copy of the unzip command.

*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>

void unzip_content(FILE* fp) {
    int counter = 0;
    
    while (fread(&counter, sizeof(int), 1, fp)) {
        int c = fgetc(fp);
        for (size_t i = 0; i < counter; i++) {
            putchar(c);
        }
    }
}

void unzip_file(char const* path) {
    FILE* fp = fopen(path, "r");
    if (!fp) {
        printf("wunzip: cannot open file\n");
        exit(1);
    }
    unzip_content(fp);
    fclose(fp);
}

int main(int argc, char* argv[argc]) {
    if (argc <= 1) {
        printf("wunzip: file1 [file2 ...]\n");
        return 1;
    }
    
    for (size_t i = 1; i < argc; i++) {
        unzip_file(argv[i]);
    }
    
    return 0;
}
