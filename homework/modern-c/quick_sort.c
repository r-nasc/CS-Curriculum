/* C program for Quick Sort */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void swap_element(int arr[], size_t i, size_t j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
}

// Sort arr in-place by picking a pivot
// element and sorting all other elements
// around it
void quick_sort(int arr[], int l, int r) {
    if (l >= r)
        return;

    int pivot = l, i = l, j = r;
    while (i < j) {
        while (arr[i] <= arr[pivot] && i < r)
            i++;
        while (arr[j] > arr[pivot])
            j--;
        if (i < j)
            swap_element(arr, i, j);
    }

    swap_element(arr, pivot, j);
    quick_sort(arr, l, j - 1);
    quick_sort(arr, j + 1, r);
}

int main() {
    int arr[] = { 17.0, 11.0, 13.0, 5.0, 6.0, 7.0 };
    int arr_sorted[] = { 5.0, 6.0, 7.0, 11.0, 13.0, 17.0 };
    int arr_unsorted[] = { 6.0, 5.0, 7.0, 11.0, 13.0, 17.0 };
    size_t arr_size = sizeof(arr) / sizeof(arr[0]);

    quick_sort(arr, 0, arr_size - 1);

    int test1_result = memcmp(arr, arr_sorted, arr_size) == 0;
    printf("Test1: %s\n", test1_result ? "Success" : "Failure");

    int test2_result = memcmp(arr, arr_unsorted, arr_size) != 0;
    printf("Test2: %s\n", test2_result ? "Success" : "Failure");

    return 0;
}