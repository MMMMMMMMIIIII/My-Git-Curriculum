package ex3_4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Comparator;

public class Prim {
    // Metodo per calcolare la foresta minima ricoprente utilizzando l'algoritmo di Prim
    public static <V, L extends Number> Collection<? extends AbstractEdge<V, L>> minimumSpanningForest(Graph<V, L> graph) {
        Set<V> visited = new HashSet<>();  // Insieme per tenere traccia dei nodi già visitati
        List<AbstractEdge<V, L>> mstEdges = new ArrayList<>();  // Lista per gli archi della foresta minima ricoprente

        EdgeComparator <V, L> edgeComparator = new EdgeComparator<>();
        // Loop per assicurarsi di coprire tutte le componenti connesse del grafo
        for (V startNode : graph.getNodes()) {
            if (visited.contains(startNode)) continue;  // Salta il nodo se già visitato

            // Coda di priorità per selezionare l'arco minimo per ogni componente del grafo
            PriorityQueue<AbstractEdge<V, L>> priorityQueue = new PriorityQueue<>(edgeComparator);

            // Visita il nodo iniziale e aggiungi i suoi archi alla coda
            visitNode(graph, startNode, visited, priorityQueue);

            // Processa tutti gli archi nella coda finché non rimangono nodi da visitare
            while (!priorityQueue.empty()) {
                AbstractEdge<V, L> edge = priorityQueue.top();  // Ottieni l'arco minimo dalla coda
                priorityQueue.pop();  // Rimuovi l'arco minimo dalla coda

                V to = edge.getEnd();
                if (visited.contains(to)) continue;  // Salta se il nodo di destinazione è già visitato

                mstEdges.add(edge);  // Aggiungi l'arco alla foresta minima ricoprente
                visitNode(graph, to, visited, priorityQueue);  // Visita il nodo di destinazione
            }
        }

        return mstEdges;  // Restituisce la collezione degli archi che formano la foresta
    }

    // Metodo di supporto per visitare un nodo e aggiungere alla coda di priorità gli archi che lo collegano ai vicini non visitati
    private static <V, L extends Number> void visitNode(Graph<V, L> graph, V node, Set<V> visited, PriorityQueue<AbstractEdge<V, L>> priorityQueue) {
        visited.add(node);  // Aggiungi il nodo all'insieme dei nodi visitati

        // Per ogni nodo vicino, se non è stato visitato, aggiungi l'arco alla coda
        for (V neighbor : graph.getNeighbours(node)) {
            if (!visited.contains(neighbor)) {
                L weight = graph.getLabel(node, neighbor);  // Ottieni il peso dell'arco
                priorityQueue.push(new Edge<>(node, neighbor, weight));  // Aggiungi l'arco alla coda di priorità
            }
        }
    }

    //java ex3_4/Prim false true C:\Users\mauro\CLionProjects\laboratorio-algoritmi-2023-2024\.idea\italian_dist_graph\italian_dist_graph.csv

    public static void main(String[] args) {
        // Verifica che ci siano argomenti sufficienti
        if (args.length < 3) {
            System.err.println("Usage: java Prim <oriented> <labelled> <csv_file_path>");
            return;
        }

        boolean oriented = Boolean.parseBoolean(args[0]); // false
        boolean labelled = Boolean.parseBoolean(args[1]); // true 
        Graph<String, Double> graph = new Graph<>(oriented, labelled);

        // Leggi i dati dal file CSV
        String csvFilePath = args[2];  // "C:\Users\sgiac\Downloads\italian_dist_graph.csv"
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;  // Ignora le righe non valide
                String from = parts[0].trim();
                String to = parts[1].trim();
                double weight = Double.parseDouble(parts[2].trim());
                graph.addNode(from);
                graph.addNode(to);
                graph.addEdge(from, to, weight);
            }
        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
            return;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing weight: " + e.getMessage());
            return;
        }

        // Calcola la minima foresta ricoprente
        Collection<? extends AbstractEdge<String, Double>> mstEdges = minimumSpanningForest(graph);

        // Scrivi la foresta calcolata su standard output
        /*for (AbstractEdge<String, Double> edge : mstEdges) {
            System.out.println(edge.getStart() + "," + edge.getEnd() + "," + edge.getLabel());
        }*/

        // Informazioni aggiuntive su standard error
        System.out.println("Number of nodes: " + graph.numNodes());
        System.out.println("Number of edges: " + mstEdges.size());
        // Calcola il peso totale della foresta se necessario
        double totalWeight = mstEdges.stream().mapToDouble(edge -> edge.getLabel().doubleValue()).sum();
        //System.out.println("Total weight: " + totalWeight + " km");
        System.out.printf("Total weight: %.3f km%n", totalWeight/1000);

    }

    public static class EdgeComparator<V, L extends Number> implements Comparator<AbstractEdge<V, L>> {

        @Override
        public int compare(AbstractEdge<V, L> edge1, AbstractEdge<V, L> edge2) {
            // Confronta i label degli archi come double
            return Double.compare(edge1.getLabel().doubleValue(), edge2.getLabel().doubleValue());
        }
    }
}
