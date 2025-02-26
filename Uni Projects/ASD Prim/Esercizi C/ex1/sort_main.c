#include "sort.h"
#include <time.h>
#include <string.h>
/.\bin\main_ex1.exe "C:\Users\mauro\CLionProjects\laboratorio-algoritmi-2023-2024\.idea\records\records.csv" "C:\Users\mauro\CLionProjects\laboratorio-algoritmi-2023-2024\.idea\records\records-ordered.csv"
#define LENGTH 20000000

int compar_int(const void *base_first, const void *base_second);

int compar_string(const void *base_first, const void *base_second);

int compar_float(const void *base_first, const void *base_second);


typedef struct {
    int id;
    char string[20];
    int number_i;
    float number_f;
} t_row;

int main(int argc, char *argv []){
    FILE *infile = fopen(argv[1], "r"); //"C:\Users\mauro\CLionProjects\laboratorio-algoritmi-2023-2024\.idea\records\records.csv"
    FILE *outfile = fopen(argv[2], "w"); //"C:\Users\mauro\CLionProjects\laboratorio-algoritmi-2023-2024\.idea\records\records-ordered.csv"

    int field = atoi(argv[3]);
    int algo = atoi(argv[4]);

    if((field == 1 || field == 2 || field == 3) && (algo == 1 || algo == 2)){
        sort_records(infile, outfile, field, algo);
    }
    else{
        perror("Invalid parameters");
    }

    fclose(infile);
    fclose(outfile);
}

void sort_records(FILE *infile, FILE *outfile, size_t field, size_t algo){
    t_row *rows = (t_row*) malloc(LENGTH * sizeof(t_row));

    int n_elem = 0;
    while(fscanf(infile, "%d,%19[^,],%d,%f", &(rows[n_elem].id), rows[n_elem].string, &(rows[n_elem].number_i), &(rows[n_elem].number_f)) != EOF){
        n_elem++;
    }
    printf("Inizio sorting\n");
    if(field == 1){
        if(algo == 1){
            merge_sort(rows, n_elem, sizeof(t_row), compar_string);
        }
        if(algo == 2){
            quick_sort(rows, n_elem, sizeof(t_row), compar_string);
        }
    }
    else if(field == 2){
        if(algo == 1){
            merge_sort(rows, n_elem, sizeof(t_row), compar_int);
        }
        if(algo == 2){
            quick_sort(rows, n_elem, sizeof(t_row), compar_int);
        }
    }
    else if(field == 3){
        if(algo == 1){
            merge_sort(rows, n_elem, sizeof(t_row), compar_float);
        }
        if(algo == 2){
            quick_sort(rows, n_elem, sizeof(t_row), compar_float);
        }
    }
    int i = 0;
    printf("Inizio scrittura");
    while(i < n_elem){
        fprintf(outfile, "%d, %s, %d, %f\n", rows[i].id, rows[i].string, rows[i].number_i, rows[i].number_f);
        i++;
    }
    free(rows);
}

int compar_int(const void *base_first, const void *base_second){
    if(((t_row *)base_first)->number_i > ((t_row *)base_second)->number_i){
        return 1;
    }
    else if(((t_row *)base_first)->number_i < ((t_row *)base_second)->number_i){
        return -1;
    }
    else{
        return 0;
    }
}

int compar_float(const void *base_first, const void *base_second){
    if(((t_row *)base_first)->number_f > ((t_row *)base_second)->number_f){
        return 1;
    }
    else if(((t_row *)base_first)->number_f < ((t_row *)base_second)->number_f){
        return -1;
    }
    else{
        return 0;
    }
}

int compar_string(const void *base_first, const void *base_second){
    return strcmp(((t_row *)base_first)->string, ((t_row *)base_second)->string);
}
