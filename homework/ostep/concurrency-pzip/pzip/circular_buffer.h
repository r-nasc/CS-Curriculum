#ifndef CIRCULAR_BUFFER
#define CIRCULAR_BUFFER

#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

typedef struct circular_buffer {
    void* buffer;     // data buffer
    void* buffer_end; // end of data buffer
    size_t capacity;  // maximum number of items in the buffer
    size_t count;     // number of items in the buffer
    size_t sz;        // size of each item in the buffer
    void* head;       // pointer to head
    void* tail;       // pointer to tail
} circular_buffer;

void cb_init(circular_buffer* cb, size_t capacity, size_t sz) {
    cb->buffer = malloc(capacity * sz);
    assert(cb->buffer);

    cb->buffer_end = (char*)cb->buffer + capacity * sz;
    cb->capacity = capacity;
    cb->count = 0;
    cb->sz = sz;
    cb->head = cb->buffer;
    cb->tail = cb->buffer;
}

void cb_free(circular_buffer* cb) {
    free(cb->buffer);
}

void cb_push_back(circular_buffer* cb, const void* item) {
    assert(cb->count != cb->capacity);
    memcpy(cb->head, item, cb->sz);
    cb->head = (char*)cb->head + cb->sz;
    if (cb->head == cb->buffer_end)
        cb->head = cb->buffer;
    cb->count++;
}

void cb_pop_front(circular_buffer* cb, void* item) {
    assert(cb->count != 0);
    memcpy(item, cb->tail, cb->sz);
    cb->tail = (char*)cb->tail + cb->sz;
    if (cb->tail == cb->buffer_end)
        cb->tail = cb->buffer;
    cb->count--;
}

#endif // !CIRCULAR_BUFFER