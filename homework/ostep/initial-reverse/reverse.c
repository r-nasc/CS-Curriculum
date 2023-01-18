#include <stdio.h>    // getline, fileno, fopen, fclose, fprintf
#include <stdlib.h>   // exit, malloc
#include <string.h>   // strdup
#include <sys/stat.h> // fstat
#include <sys/types.h>

typedef struct node {
  char *text;
  struct node *next;
} node;

void get_in_out(int argc, char *argv[argc], FILE** in, FILE** out) {
    *in = stdin;
    *out = stdout;
  
    if (argc > 3) {
        fprintf(stderr, "usage: reverse <input> <output>\n");
        exit(1);
    }

    if (argc > 1) {
        *in = fopen(argv[1], "r");
        if (!*in) {
            fprintf(stderr, "reverse: cannot open file '%s'\n", argv[1]);
            exit(1);
        }
    }
    
    if (argc > 2) {
        *out = fopen(argv[2], "w");
        if (!*out) {
            fprintf(stderr, "reverse: cannot open file '%s'\n", argv[2]);
            exit(1);
        }
        
        struct stat sb1, sb2;
        if (fstat(fileno(*in), &sb1) == -1 || fstat(fileno(*out), &sb2) == -1) {
            fprintf(stderr, "reverse: fstat error\n");
            exit(1);
        }
          
        if (sb1.st_ino == sb2.st_ino) {
            fprintf(stderr, "reverse: input and output file must differ\n");
            exit(1);
        }
    }
}

int main(int argc, char *argv[argc]) {
    FILE *in = 0, *out = 0;
    get_in_out(argc, argv, &in, &out);

    node *head = NULL;
    char *line = NULL;
    size_t len = 0;
    
    // Build a linked list (FILO)
    while (getline(&line, &len, in) != -1) {
        node *n = malloc(sizeof(node));
        if (!n) {
            fprintf(stderr, "reverse: malloc failed\n");
            exit(1);
        }
        
        n->next = head; 
        n->text = strdup(line);
        if (!n->text) {
            fprintf(stderr, "reverse: strdup failed\n");
            exit(1);
        }
        head = n;
    }
    free(line);
    
    // Print the linked list from end to start
    while (head != NULL) {
        fprintf(out, "%s", head->text);
        node *next = head->next;
        free(head->text);
        free(head);
        head = next;
    }
    
    fclose(in);
    fclose(out);
    return 0;
}