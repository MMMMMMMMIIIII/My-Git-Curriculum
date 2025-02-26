package ex3_4;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Collection;

public class GraphTest {
    private Graph<String, Integer> graph;

    // Metodo eseguito prima di ogni test per inizializzare un grafo
    @Before
    public void setUp() {
        graph = new Graph<>(true, true); // Grafo orientato e etichettato
    }

    @Test
    public void testAddNode() {
        assertTrue(graph.addNode("A")); // Aggiungi nodo A
        assertTrue(graph.addNode("B")); // Aggiungi nodo B
        assertFalse(graph.addNode("A")); // Aggiungere di nuovo A dovrebbe restituire false
        assertEquals(2, graph.numNodes()); // Dovrebbe avere 2 nodi
    }

    @Test
    public void testAddEdge() {
        graph.addNode("A");
        graph.addNode("B");

        assertTrue(graph.addEdge("A", "B", 5)); // Aggiungi arco da A a B con etichetta 5
        assertTrue(graph.containsEdge("A", "B")); // Dovrebbe contenere l'arco A-B
        assertFalse(graph.containsEdge("B", "A")); // Non dovrebbe contenere l'arco B-A (orientato)
        assertEquals(1, graph.numEdges()); // Dovrebbe avere 1 arco
    }

    @Test
    public void testRemoveNode() {
        graph.addNode("A");
        graph.addNode("B");
        graph.addEdge("A", "B", 5);

        assertTrue(graph.removeNode("A")); // Rimuovi nodo A
        assertFalse(graph.containsNode("A")); // Dovrebbe restituire false
        assertFalse(graph.containsEdge("A", "B")); // Dovrebbe restituire false (l'arco A-B non esiste più)
        assertEquals(1, graph.numNodes()); // Dovrebbe avere 1 nodo (B)
        assertEquals(0, graph.numEdges()); // Dovrebbe avere 0 archi
    }

    @Test
    public void testRemoveEdge() {
        graph.addNode("A");
        graph.addNode("B");
        graph.addEdge("A", "B", 5);

        assertTrue(graph.removeEdge("A", "B")); // Rimuovi arco da A a B
        assertFalse(graph.containsEdge("A", "B")); // Dovrebbe restituire false
        assertEquals(0, graph.numEdges()); // Dovrebbe avere 0 archi
    }

    @Test
    public void testGetNeighbours() {
        graph.addNode("A");
        graph.addNode("B");
        graph.addEdge("A", "B", 5);

        Collection<String> neighbours = graph.getNeighbours("A");
        assertTrue(neighbours.contains("B")); // Dovrebbe contenere il vicino B
        assertEquals(1, neighbours.size()); // Dovrebbe avere 1 vicino
    }

    @Test
    public void testGetLabel() {
        graph.addNode("A");
        graph.addNode("B");
        graph.addEdge("A", "B", 5);

        assertEquals(Integer.valueOf(5), graph.getLabel("A", "B")); // Dovrebbe restituire 5 come etichetta
        assertNull(graph.getLabel("B", "A")); // Dovrebbe restituire null (orientato)
    }
}
