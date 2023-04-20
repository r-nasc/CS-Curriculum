#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include "vec.h"

void vec_init(vec* v) {
    const unsigned INITIAL_CAPACITY = 8;
    v->size = 0;
    v->capacity = INITIAL_CAPACITY;
    v->data = malloc(v->capacity * sizeof(void*));
}

void vec_resize(vec *v, size_t capacity) {
    if (v->capacity != capacity) {
        void **new_data = realloc(v->data, sizeof(void*) * capacity);
        if (new_data) {
            v->data = new_data;
            v->capacity = capacity;
        }
    }    
}

void vec_push_back(vec* v, void* val) {
    if (v->capacity == v->size)
        vec_resize(v, v->capacity*2);
    v->data[v->size++] = val;
}

void* vec_pop_back(vec* v) {
    if (v->size > 0) {
        v->size--;
        return v->data[v->size+1];
    }
    return NULL;        
}

void* vec_get(vec* v, size_t index) {
    if (index < 0 || index >= v->size)
        return NULL;
    return v->data[index];
}

int search_key(vec* v, char* key) {
    int n = v->size;
    for (int i = 0; i < n; i++) {
        if (strcmp(key, v->data[i]) == 0) return i;
    }
    return -1;
}

void vec_destroy(vec* v) {
    free(v->data);
}