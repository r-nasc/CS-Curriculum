#ifndef PZIP_H
#define PZIP_H

#include <stddef.h>

typedef struct {
	char** paths;
	int num_files;
} pzip_input;

typedef struct {
	char* data;
	int* count;
	int size;
} zipped_piece;

typedef struct {
	char* address;
	int file_number;   
	int page_number;   
	size_t last_page_size;
} file_page_data;

#endif // !PZIP_H