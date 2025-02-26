#include "sort.h"
#include <string.h>

void merge(void *prov, void *base_first, size_t nitems_first, void *base_second, size_t nitems_second, size_t size, int (*compar)(const void*, const void*));

int partition(void *base, size_t nitems, size_t size, int (*compar)(const void*, const void*));

void swap(void *base, int init, int run, size_t size);

/*
    This method starts the recursive merging process. First of all, it find the middle element and its address.
    Then it call the sort the two part of the array. Allocate memory for the merged result, using the size * the number of items.
    The last step is to merge the two halves and to confirm the changes freeing the memory
*/
void merge_sort(void *base, size_t nitems, size_t size, int (*compar)(const void*, const void*)){
    if(nitems > 1){
        size_t middle_element = nitems/2;
        void *middle_address = base + (middle_element * size);
        merge_sort(base, middle_element, size, compar);
        merge_sort(middle_address, nitems-middle_element, size, compar);
        void *prov = (void *) malloc(size * nitems);
        merge(prov, base, middle_element, middle_address, nitems - middle_element, size, compar);
        memcpy(base, prov, nitems * size);
        free(prov);
    }
}

/*
    This method is used by the merge sort to compact 2 half arrays into one. First of all, 2 elements of distinct halves
    are compared with the compar function, useful to compare 2 generic types. Then, using a temporary array stored into the
    memory, it provide the right order of the array. Then, out of the while cycle, it concats the last element of one of the
    halves
*/

void merge(void *prov, void *base_first, size_t nitems_first, void *base_second, size_t nitems_second, size_t size, int (*compar)(const void*, const void*)){
    while(nitems_first > 0 && nitems_second > 0){
        if (compar(base_first, base_second) > 0) {
            memcpy(prov, base_second, size);
            prov = prov + size;
            base_second = base_second + size;
            nitems_second--;
        } else {
            memcpy(prov, base_first, size);
            prov = prov + size;
            base_first = base_first + size;
            nitems_first--;
        }

    }
    if(nitems_first == 0){
        memcpy(prov, base_second, nitems_second*size);
    }
    else if(nitems_second == 0){
        memcpy(prov, base_first, nitems_first*size);
    }

}


/*
    This method is the quick_sort algorithm. It choose, the pivot element (the last one), calling the partition function
    that puts all the lower elements at the left of the pivot and the higher at its right, then it call itself two times
    to reorder the two halves
*/

void quick_sort(void *base, size_t nitems, size_t size, int (*compar)(const void*, const void*)){
    if(nitems > 0){
        int pivot_div = partition(base, nitems - 1, size, compar);
        quick_sort(base, pivot_div,  size, compar);
        quick_sort(base + ((pivot_div + 1) * size), nitems - (pivot_div + 1) , size, compar);
    }
}

/*
    This method puts all the lower elements of the array at the left of the pivot and the higher at its right.
    It use 2 counter, one for processing all the elements (run), end the other (init) contains the nearest element
    lower then the pivot. Then it use the compar function, which if the element is higher it does nothing. If it's
    lower, it swaps the "run" element with the "init" one. At least, the pivot element is swapped with the init+1,
    that is the nearest element higher then the pivot.
*/

int partition(void *base, size_t nitems, size_t size, int (*compar)(const void*, const void*)){
    void *pivot = (void *) malloc(size);
    memcpy(pivot, base + (nitems * size), size);
    int init = -1;
    int run = 0;
    while(run < nitems){
        if(compar((base + (run * size)), pivot) <= 0){
            init++;
            swap(base, init, run, size);
        }
        run++;
    }
    swap(base, init+1, nitems, size);
    free(pivot);
    return init+1;
}

/*
    The swap method use the physical memory as temporary "variable" to save the run element and the init element
*/

void swap(void *base, int init, int run, size_t size){
    void *temp = (void *) malloc(size);
    memcpy(temp, (base + (init * size)), size);
    memcpy((base + (init * size)), (base + (run * size)), size);
    memcpy((base + (run * size)), temp, size);
    free(temp);
}