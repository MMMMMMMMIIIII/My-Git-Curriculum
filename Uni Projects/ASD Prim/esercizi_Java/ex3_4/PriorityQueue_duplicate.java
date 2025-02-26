package esercizijava.ex3;
import java.util.*;

public class PriorityQueue<E> implements AbstractQueue<E> {
    private List<E> heap;
    private Map<E, Integer> map;
    private Comparator<E> comparator;

    public PriorityQueue(Comparator<E> comparator) {
        this.heap = new ArrayList<>();
        this.map = new HashMap<>();
        this.comparator = comparator;
    }
    @Override
    public boolean empty() {
        return heap.isEmpty();
    }

    @Override
    public boolean push(E e) {
        if(map.containsKey(e)){
            return false;
        }
        heap.add(e);
        map.put(e, heap.size()-1);
        heapifyUp(heap.size()-1);
        return true;
    }

    @Override
    public boolean contains(E e) {
        return map.containsKey(e);
    }

    @Override
    public E top() {
        if (empty()) {
            return null; // o lanciare un'eccezione
        }
        return heap.get(0); // Cambiato da getFirst() a get(0)
    }

    @Override
    public void pop() {
        if (empty()) return;

        E lastElement = heap.remove(heap.size() - 1); // Usa heap.size() - 1 invece di removeLast()
        map.remove(lastElement);

        if (!heap.isEmpty()) {
            heap.set(0, lastElement);
            map.put(lastElement, 0);
            heapifyDown(0);
        }
    }

    @Override
    public boolean remove(E e) {
        Integer index = map.get(e);
        if (index == null) return false;

        E lastElement = heap.remove(heap.size() - 1); // Usa heap.size() - 1 invece di removeLast()
        map.remove(e);

        if (index < heap.size()) {
            heap.set(index, lastElement);
            map.put(lastElement, index);
            heapifyDown(index);
            heapifyUp(index);
        }
        return true;
    }

    public void heapifyUp(int pos) {
        int parent = (pos - 1) / 2;

        if (pos > 0 && comparator.compare(heap.get(pos), heap.get(parent)) < 0) {
            Collections.swap(heap, parent, pos);
            map.put(heap.get(pos), pos);
            map.put(heap.get(parent), parent);
            heapifyUp(parent); // Cambiato da heapifyDown(parent) a heapifyUp(parent)
        }
    }

    public void heapifyDown(int pos){
        int i = pos;
        int leftChild = (2 * i) + 1;
        int rightChild = (2 * i) + 2;

        if(leftChild < heap.size() && comparator.compare(heap.get(leftChild), heap.get(i)) < 0){
            i = leftChild;
        }
        if(rightChild < heap.size() && comparator.compare(heap.get(rightChild), heap.get(i)) < 0){
            i = rightChild;
        }
        if(i != pos){
            Collections.swap(heap, i, pos);
            map.put(heap.get(pos), pos);
            map.put(heap.get(i), i);
            heapifyDown(i);
        }
    }
}
