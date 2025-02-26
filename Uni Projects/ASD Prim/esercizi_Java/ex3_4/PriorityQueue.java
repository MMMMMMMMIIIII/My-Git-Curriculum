package ex3_4;

import java.util.*;

public class PriorityQueue<E> implements AbstractQueue<E> {
    private List<E> heap;
    private Comparator<E> comparator;

    public PriorityQueue(Comparator<E> comparator){
        this.heap = new ArrayList<>();
        this.comparator = comparator;
    }

    // Check if the heap is empty
    public boolean empty(){
        return heap.isEmpty();
    }

    // Push a new element into the heap and sort the heap
    @Override
    public boolean push(E e) {
        heap.add(e);
        heapifyUp(heap.size() - 1);
        //heapifyDown(0); heapyfyUp è sufficiente
        return true;
    }

    // Check if the heap contain a specify element
    @Override
    public boolean contains(E e) {
        return heap.contains(e);
    }

    // Return the element on the top of the heap
    @Override
    public E top() {
        if(empty()) return null;
        return heap.get(0);
    }

    // Pop out the element on the top and sort the heap
    @Override
    public void pop() {
        if(empty()) return;

        swap(0, heap.size()-1);
        heap.remove(heap.size()-1);
        heapifyDown(0);
    }

    // Remove a specify element from and sort the heap
    @Override
    public boolean remove(E e) {
        if(!contains(e)) return false;

        int i = heap.indexOf(e);
        swap(i, heap.size()-1);

        heap.remove(heap.size()-1);

        if(i < heap.size()){
            heapifyUp(i);
            heapifyDown(i);
        }
        return true;
    }

    // Sort the heap from the leafs
    public void heapifyUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;

            if (comparator.compare(heap.get(i), heap.get(parent)) < 0) {
                swap(i, parent);
                i = parent;
            } else {
                return;
            }
        }
    }

    // Sort the heap from the parent
    public void heapifyDown(int i) {
        int size = heap.size();

        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            if (left < size && comparator.compare(heap.get(left), heap.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < size && comparator.compare(heap.get(right), heap.get(smallest)) < 0) {
                smallest = right;
            }

            if (smallest != i) {
                swap(i, smallest);
                i = smallest;
            } else {
                return;
            }
        }
    }

    // Swap two elements of the heap
    public void swap(int first, int second){
        E temp = heap.get(first);
        heap.set(first, heap.get(second));
        heap.set(second, temp);
    }
}
