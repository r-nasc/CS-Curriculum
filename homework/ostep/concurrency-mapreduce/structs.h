#ifndef __linkedlist_h__
#define __linkedlist_h__

#include <pthread.h>
#include <stddef.h>

const int TABLE_SIZE = 1543;

typedef struct {
    char* key;
    node* next;
    subnode* subnode;
    subnode* current;
    int size;
} node;

typedef struct {
    int size;
    node** list;
    pthread_mutex_t* locks;
    long long nodesize;
    pthread_mutex_t keylock;

} table;

typedef struct {
    char* val;
    subnode* next;
} subnode;

unsigned long hash(char* str);
int node_compare(const void* s1, const void* s2);

void table_free(table* t);
table* table_create(size_t size);
void table_insert(table* t, char* key, char* val);

#endif // __linkedlist_h__
