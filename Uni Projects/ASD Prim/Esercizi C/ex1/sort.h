#ifndef LABORATORIO_ALGORITMI_2023_2024_SORT_H
#define LABORATORIO_ALGORITMI_2023_2024_SORT_H

#include <stdio.h>
#include <stdlib.h>

void merge_sort(void *base, size_t nitems, size_t size, int (*compar)(const void*, const void*));

void quick_sort(void *base, size_t nitems, size_t size, int (*compar)(const void*, const void*));

void sort_records(FILE *infile, FILE *outfile, size_t field, size_t algo);

#endif //LABORATORIO_ALGORITMI_2023_2024_SORT_H
