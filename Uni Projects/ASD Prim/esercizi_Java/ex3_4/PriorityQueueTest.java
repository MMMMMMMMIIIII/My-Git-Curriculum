package ex3_4;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.Comparator;

//java -cp ".;junit-4.13.2.jar;hamcrest-core-1.3.jar" org.junit.runner.JUnitCore ex3_4.PriorityQueueTest

public class PriorityQueueTest {
    private PriorityQueue<Integer> priorityQueue;

    @Before
    public void setUp() {
        priorityQueue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
    }

    @Test
    public void testEmpty() {
        assertTrue("Empty TEST: PASS", priorityQueue.empty());
    }

    @Test
    public void testPushAndTop() {
        priorityQueue.push(5);
        priorityQueue.push(25);
        priorityQueue.push(35);
        assertEquals(Integer.valueOf(5), priorityQueue.top());
    }

    @Test
    public void testPop() {
        priorityQueue.push(5);
        priorityQueue.push(25);
        priorityQueue.push(15);
        priorityQueue.pop();
        assertEquals(Integer.valueOf(15), priorityQueue.top());
    }

    @Test
    public void testRemove() {
        priorityQueue.push(5);
        priorityQueue.push(25);
        priorityQueue.push(15);
        assertTrue(priorityQueue.remove(25));
        assertEquals(Integer.valueOf(5), priorityQueue.top());
    }

}
