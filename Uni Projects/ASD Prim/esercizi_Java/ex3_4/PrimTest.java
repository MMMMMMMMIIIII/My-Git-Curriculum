package ex3_4;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Collection;


public class PrimTest {

    private Graph<String, Double> graph;

    @Before
    public void setUp() {
        // Creazione di un grafo per i test
        graph = new Graph<>(true, true);  // Grafo diretto e con etichette

        // Aggiungi nodi
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");

        // Aggiungi archi con pesi
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 2.0);
        graph.addEdge("B", "C", 3.0);
        graph.addEdge("B", "D", 4.0);
        graph.addEdge("C", "D", 5.0);
    }

    @Test
    public void testMinimumSpanningForest() {
        // Calcolo della foresta minima ricoprente
        Collection<? extends AbstractEdge<String, Double>> mstEdges = Prim.minimumSpanningForest(graph);

        // Verifica che il numero di archi nella foresta sia corretto (n-1 per un grafo connesso)
        assertEquals(3, mstEdges.size());

        // Calcola il peso totale della foresta
        double totalWeight = mstEdges.stream().mapToDouble(edge -> edge.getLabel().doubleValue()).sum();

        // Verifica il peso totale della foresta minima (controllo basato sulla configurazione del grafo)
        assertEquals(7.0, totalWeight, 0.001);  // Tolleranza di errore per il double
    }

    @Test
    public void testNumberOfNodes() {
        // Verifica che il numero di nodi nel grafo sia corretto
        assertEquals(4, graph.numNodes());
    }

    @Test
    public void testNumberOfEdges() {
        // Verifica che il numero di archi aggiunti sia corretto
        assertEquals(5, graph.numEdges());
    }
}
