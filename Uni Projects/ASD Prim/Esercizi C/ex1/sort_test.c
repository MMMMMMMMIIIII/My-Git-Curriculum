#include "../unity.h"
#include "sort.h"

int compar_int(const void *base_first, const void *base_second);

void setUp(void) {

}

void tearDown(void) {

}

void test_merge(void) {
    int arr[] = {10, 11, 13, 3, 6, 1, 8};
    int expected[] = {1, 3, 6, 8,10, 11, 13};

    merge_sort(arr, 7, sizeof(int), compar_int);

    for (int i = 0; i < 7; i++) {
        TEST_ASSERT_EQUAL_INT(expected[i], arr[i]);
    }
}

void test_quick(void) {
    int arr[] = {10, 7, 3, 11, 1, 3, 20};
    int expected[] = {1, 3, 3, 7, 10, 11, 20};

    quick_sort(arr, 7, sizeof(int), compar_int);

    for (int i = 0; i < 7; i++) {
        TEST_ASSERT_EQUAL_INT(expected[i], arr[i]);
    }
}

int main(void) {
    UNITY_BEGIN();

    RUN_TEST(test_merge);
    RUN_TEST(test_quick);

    return UNITY_END();
}

int compar_int(const void *base_first, const void *base_second){
    if(*(int *)base_first > (*(int *)base_second)){
        return 1;
    }
    else if(*(int *)base_first < *(int *)base_second){
        return -1;
    }
    else{
        return 0;
    }
}
