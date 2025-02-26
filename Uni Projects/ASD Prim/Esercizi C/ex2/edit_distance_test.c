#include "../unity.h"
#include "edit_distance.h"

void setUp(void) {}

void tearDown(void) {}

void test_edit_distance_equal(void) {
    TEST_ASSERT_EQUAL_INT(0, edit_distance_dyn("ciao", "ciao"));
}

void test_edit_distance_insert(void) {
    TEST_ASSERT_EQUAL_INT(1, edit_distance_dyn("test", "tests"));
}

void test_edit_distance_delete(void) {
    TEST_ASSERT_EQUAL_INT(1, edit_distance_dyn("tests", "test"));
}

void test_edit_distance_substitute(void) {
    TEST_ASSERT_EQUAL_INT(2, edit_distance_dyn("casa", "cava"));
}

void test_edit_distance_mix(void) {
    TEST_ASSERT_EQUAL_INT(4, edit_distance_dyn("tassa", "passato"));
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_edit_distance_equal);
    RUN_TEST(test_edit_distance_insert);
    RUN_TEST(test_edit_distance_delete);
    RUN_TEST(test_edit_distance_substitute);
    RUN_TEST(test_edit_distance_mix);
    return UNITY_END();
}
