#include "edit_distance.h"
#include <string.h>

#define MAX 7

void merge_sort(void *base, size_t nitems, size_t size, int (*compar)(const void*, const void*));

void merge(void *prov, void *base_first, size_t nitems_first, void *base_second, size_t nitems_second, size_t size, int (*compar)(const void*, const void*));

int compar(const void *base_first, const void *base_second);

typedef struct{
    int dimen;
    char **string;
}dim;

dim *dimension(FILE *file);

typedef struct{
   int distance;
   char string[40];
}sort;

/*
    The main simulate a text corrector system. Having a word in input, it returns a word (from a dictionary of words) and
    its distance from the word in input. It print, for all word of the file, from 1 to MAX suggested word, ordered by the
    merge sort function from the nearest to the farest. Then he free all the space.
*/

int main(int argc, char* argv[]){
    FILE *dictionary = fopen(argv[1], "r");  //C:\Users\sgiac\Downloads\edit_distance_test\dictionary.txt
    FILE *correct = fopen(argv[2], "r");     //C:\Users\sgiac\Downloads\edit_distance_test\correctme.txt

    dim *corr = dimension(correct);
    dim *dict = dimension(dictionary);

    printf("Letture completate\n");
    for (int i = 0; i < corr->dimen; i++) {
        sort *sug = (sort *)calloc(dict->dimen, sizeof(sort));
        for (int j = 0; j < dict->dimen; j++) {
            sug[j].distance = edit_distance_dyn(corr->string[i], dict->string[j]);
            strncpy(sug[j].string, dict->string[j], 40);
        }

        merge_sort(sug, dict->dimen, sizeof(sort), compar);

        printf("Parola: %s\n", corr->string[i]);
        printf("Suggerimenti:\n");
        for (int i = 0; i < MAX && i < dict->dimen; i++) {
            printf("- %s (distanza: %d)\n", sug[i].string, sug[i].distance);
        }
        printf("\n");
    }
    printf("Suggerimenti completati\n");
    for (int i = 0; i < dict->dimen; i++) {
        free(dict->string[i]);
    }
    free(dict->string);
    free(dict);

    for (int i = 0; i < corr->dimen; i++) {
        free(corr->string[i]);
    }
    free(corr->string);
    free(corr);

    fclose(dictionary);
    fclose(correct);
    printf("Pulizia completata");
    return 0;
}

/*
    This function is used to count the number of words contained into the file
*/

dim *dimension(FILE *file){
    dim *prov = (dim *) malloc(sizeof(dim));
    char p[20];
    for(prov->dimen = 0; fscanf(file, "%s[^ -_.:;,\n\t]", p) != EOF; prov->dimen++);

    int i;

    prov->string = (char **)calloc(prov->dimen, sizeof(char *));

    fseek(file, 0, SEEK_SET);

    for(i = 0; i < prov->dimen; i++){
        prov->string[i] = (char *)calloc(25, sizeof(char));
        fscanf(file, "%s[^ -_.:;,\n\t]", prov->string[i]);
    }

    return prov;
}

/*
    The next three function are the copy of the already explained merge sort of the exercise 1
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

int compar(const void *base_first, const void *base_second){
    if(((sort *)base_first)->distance > ((sort *)base_second)->distance){
        return 1;
    }
    else if(((sort *)base_first)->distance < ((sort *)base_second)->distance){
        return -1;
    }
    else{
        return 0;
    }
}