#ifndef VEC_H
#define VEC_H

#include <stddef.h>

typedef struct {
    void** data;
    size_t capacity;
    size_t size;
} vec;

void vec_init(vec*);
void vec_destroy(vec*);
void vec_resize(vec*, size_t);
void vec_push_back(vec*, void*);
void* vec_pop_back(vec*);
void* vec_get(vec*, size_t);

#endif //VEC_H