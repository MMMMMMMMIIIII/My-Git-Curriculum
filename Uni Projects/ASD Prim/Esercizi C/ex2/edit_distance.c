#include "edit_distance.h"
#include <string.h>

#define MAX 100000000

int min(int no_op, int canc, int ins);

int min_giusto(int ins, int canc);

int memo(const char *s1, const char *s2, int **array_memo, int len1, int len2);

/*
    The edit_distance function is the main function of this algorithm, it calculate the distance from two strings.
    First of all, manages the base cases, as the empty strings. Then it start and check, char by char, if they're equal
    or not. If they are, then it skips to the next char, otherwise it calls itself to check how much is the distance.
    There are 2 different operations: the delete and the insert. The program choose between them looking how much they cost.
    This is the static version of the algorithm.
*/

int edit_distance(const char *s1, const char* s2){
    //Casi base
    if(*s1 == '\0'){
        return strlen(s2);
    }
    else if(*s2 == '\0'){
        return strlen(s1);
    }
    int no_op = MAX;
    int canc;
    int ins;
    if(s1[0] == s2[0]){
        no_op = edit_distance(s1 + 1, s2 + 1);
    }
    canc = 1 + edit_distance(s1, s2 + 1);
    ins = 1 + edit_distance(s1 + 1, s2);


    return min(no_op, canc, ins);
}

/*
    The edit_distance_dyn function is the dynamic version of the previous one. It use the memoization, helping itself
    with the physical memory. Allocating a matrix, initializes it to only -1 value. Then, passing all these informations
    at the memo function, wait for the return value (that is the effective distance between the two strings), and free
    the physical memory.
*/

int edit_distance_dyn(const char *s1, const char* s2){
    int len_s1 = strlen(s1);
    int len_s2 = strlen(s2);

    int **array_memo = (int **)malloc((len_s1 + 1) * sizeof(int *));

    for (int i = 0; i <= len_s1; i++) {
        array_memo[i] = (int *)malloc((len_s2 + 1) * sizeof(int));
        for (int j = 0; j <= len_s2; j++) {
            array_memo[i][j] = -1;
        }
    }

    int result;
    result = memo(s1, s2, array_memo, len_s1, len_s2);

    for (int i = 0; i <= strlen(s1); i++) {
        free(array_memo[i]);
    }
    free(array_memo);

    return result;
}

/*
    The memo function is used by the edit_distance_dyn to calculate the distance. It works as the algorithm does. Calculating
    the three values of the three operations (canc, ins and sub), it choose the lower of them. Then, insert the value into
    the matrix, and return. It is called until reaches the last-right-bottom value, which is the effective distance
*/

int memo(const char *s1, const char *s2, int **array_memo, int len1, int len2){
    if (len1 == 0) return len2;
    if (len2 == 0) return len1;

    if (array_memo[len1][len2] != -1) {
        return array_memo[len1][len2];
    }

    if (s1[0] == s2[0]) {
        array_memo[len1][len2] = memo(s1 + 1, s2 + 1, array_memo, len1 - 1, len2 - 1);
    } else {
        int ins = 1 + memo(s1, s2 + 1, array_memo, len1, len2 - 1);
        int canc = 1 + memo(s1 + 1, s2, array_memo, len1 - 1, len2);
       // int sub = 1 + memo(s1 + 1, s2 + 1, array_memo, len1 - 1, len2 - 1);
        array_memo[len1][len2] = min_giusto(ins, canc);
    }

    return array_memo[len1][len2];
}

/*
    These are two simple functions that find out the smallest value between the two or three operations
*/

int min_giusto(int ins, int canc){
    if (ins <= canc) return ins;
    else return canc;
}

int min(int no_op, int canc, int ins){
    if(no_op < canc && no_op < ins) return no_op;
    else if(canc < ins) return canc;
    return ins;
}