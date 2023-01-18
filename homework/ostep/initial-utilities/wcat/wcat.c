/******************************************************************************

Code for wcat, a copy of the cat command.

*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>

void print_content(char const* path) {
    FILE* fp = fopen(path, "r");
    if (!fp) {
        printf("wcat: cannot open file\n");
        exit(1);
    }
    
    char buffer[4096];
    while (fgets(buffer, sizeof(buffer), fp)) {
        printf("%s", buffer);
    }
    
    fclose(fp);
}

int main(int argc, char* argv[argc]) {
    for (size_t i = 1; i < argc; i++) {
        print_content(argv[i]);
    }

    return 0;
}
