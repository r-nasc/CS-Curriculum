/* C program for Merge Sort */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Merges two subarrays arr[l..m] and arr[m+1..r]
void merge(int arr[], size_t l, size_t m, size_t r) {
    size_t lh_size = m - l + 1;
    size_t rh_size = r - m;

    // Copy data to temp arrays
    int L[lh_size], R[rh_size];
    for (size_t i = 0; i < lh_size; i++)
        L[i] = arr[l + i];
    for (size_t i = 0; i < rh_size; i++)
        R[i] = arr[m + 1 + i];

    // Merge the temp arrays back into arr[l..r]
    size_t i = 0, j = 0, k = l;
    while (i < lh_size && j < rh_size) {
        if (L[i] <= R[j])
            arr[k++] = L[i++];
        else
            arr[k++] = R[j++];
    }

    // Copy the remaining elements, if any
    while (i < lh_size) {
        arr[k++] = L[i++];
    }
    while (j < rh_size) {
        arr[k++] = R[j++];
    }
}

// Recursively split array in halves until
// atomic value is reached, then in-place 
// merge parts into arr in sorted order
void merge_sort(int arr[], size_t l, size_t r) {
    if (l >= r)
        return;

    size_t m = (l + r) / 2;
    merge_sort(arr, l, m);
    merge_sort(arr, m + 1, r);
    merge(arr, l, m, r);
}

int main() {
    int arr[] = { 17, 11, 13, 5, 6, 7 };
    int arr_sorted[] = { 5, 6, 7, 11, 13, 17 };
    int arr_unsorted[] = { 6, 5, 7, 11, 13, 17 };
    size_t arr_size = sizeof(arr) / sizeof(arr[0]);

    merge_sort(arr, 0, arr_size - 1);

    int test1_result = memcmp(arr, arr_sorted, arr_size) == 0;
    printf("Test1: %s\n", test1_result ? "Success" : "Failure");

    int test2_result = memcmp(arr, arr_unsorted, arr_size) != 0;
    printf("Test2: %s\n", test2_result ? "Success" : "Failure");

    return 0;
}