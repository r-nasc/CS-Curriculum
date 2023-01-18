/******************************************************************************

Code for wzip, a copy of the zip command.

*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>

void write_zip(int count, int ch) {
    fwrite(&count, sizeof(count), 1, stdout);
    fputc(ch, stdout);
}

void zip_files(int argc, char* paths[argc]) {
    int c = 0;
    int last = -1;
    int counter = 0;

    for (size_t i = 1; i < argc; i++) {
        FILE* fp = fopen(paths[i], "r");
        if (!fp) {
            printf("wgrep: cannot open file\n");
            exit(1);
        }

        c = fgetc(fp);
        while (c != EOF) {
            if (c != last && last != -1) {
                write_zip(counter, last);
                counter = 0;
            }
            counter++;
            last = c;
            c = fgetc(fp);
        }

        fclose(fp);
    }
    write_zip(counter, last);
}


int main(int argc, char* argv[argc]) {
    if (argc <= 1) {
        printf("wzip: file1 [file2 ...]\n");
        return 1;
    }
    zip_files(argc, argv);
    exit(0);
}
