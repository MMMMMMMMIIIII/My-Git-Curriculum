all: test_ex2 main_ex2

test_ex2: edit_distance.o edit_distance_test.o unity.o
	gcc -o bin/test_ex2 edit_distance.o edit_distance_test.o unity.o

main_ex2: edit_distance.o edit_distance_main.o edit_distance.h
	gcc -o bin/main_ex2 edit_distance.o edit_distance_main.o

edit_distance.o: edit_distance.c edit_distance.h
	gcc -c edit_distance.c

edit_distance_test.o: edit_distance_test.c
	gcc -c edit_distance_test.c

edit_distance_main.o: edit_distance_main.c edit_distance.h
	gcc -c edit_distance_main.c

unity.o: ../unity.h ../unity.c
	gcc -c ../unity.c