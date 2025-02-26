all: test_ex1 main_ex1

test_ex1: sorter.o sort_test.o unity.o
	gcc -o bin/test_ex1 sorter.o sort_test.o unity.o

main_ex1: sorter.o sort_main.o sort.h
	gcc -o bin/main_ex1 sorter.o sort_main.o

sorter.o: sorter.c sort.h
	gcc -c sorter.c

sort_test.o: sort_test.c
	gcc -c sort_test.c

sort_main.o: sort_main.c sort.h
	gcc -c sort_main.c

unity.o: ../unity.h ../unity.c
	gcc -c ../unity.c

clean:
	rm -f *.o bin/test_ex1 bin/main_ex1