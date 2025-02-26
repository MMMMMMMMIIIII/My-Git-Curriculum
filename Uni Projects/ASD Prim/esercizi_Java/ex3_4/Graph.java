package ex3_4;

import java.util.*;

public class Graph<V,L> implements AbstractGraph<V,L>{
    private final boolean directed;
    private final boolean labelled;
    private final Map<V, Map<V, L>> nodesList;
    private int numEdges;

    Graph(boolean directed, boolean labelled){
        this.directed = directed;
        this.labelled = labelled;
        nodesList = new HashMap<>();
        numEdges = 0;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    @Override
    public boolean isLabelled() {
        return labelled;
    }

    @Override
    public boolean addNode(V a) {
        if(nodesList.containsKey(a)) return false; //controllo se è presente
        nodesList.put(a, new HashMap<>()); //se non è presente lo creo
        return true;
    }

    @Override
    public boolean addEdge(V a, V b, L l) {
        if(!nodesList.containsKey(a) || !nodesList.containsKey(b)) return false; //controllo se i nodi sono presenti

        if(!labelled) l =null;
        Map<V, L> edges = nodesList.get(a); //inserisco la mappa dei collegamenti di a in una mappa provvisoria

        if(edges.containsKey(b)) return false; // Arco già presente

        edges.put(b, l); // se non è presente lo inserisco
        if(!directed){
            nodesList.get(b).put(a, l); // se il grafo è non orientato inserisco anche il collegamento inverso da b ad a
        }
        numEdges++; //incremento il numero di archi
        return true;
    }

    @Override
    public boolean containsNode(V a) {
        return nodesList.containsKey(a);
    }

    @Override
    public boolean containsEdge(V a, V b) {
        return nodesList.containsKey(a) && nodesList.get(a).containsKey(b);
    }

    @Override
    public boolean removeNode(V a) {
        if(!nodesList.containsKey(a)) return false; //se non è presente non posso rimuoverlo

        numEdges -= nodesList.get(a).size(); //elimino tutti i collegamenti che aveva il nodo a
        nodesList.remove(a); // rimuovo a

        if(!directed){ // se non diretto
            for(Map<V,L> edges : nodesList.values()){ //prendo ogni valore tra i nodi e lo salvo nella mappa provvisoria
                if(edges.containsKey(a)){
                    edges.remove(a); // se la mappa dei collegamenti del nodo ? contiene a lo rimuove
                    numEdges--; // e poi decrementa il numero di archi
                }
            }
        }
        return true;
    }

    @Override
    public boolean removeEdge(V a, V b) {
        if(!containsEdge(a, b)) return false; //se l'arco non è presente non è rimovibile

        nodesList.get(a).remove(b); // rimuovo l'arco
        if(!directed){
            nodesList.get(b).remove(a); // se non diretto rimuovo anche il corrispettivo arco da b ad a
        }
        numEdges--; // decremento il numero di archi
        return true;
    }

    @Override
    public int numNodes() {
        return nodesList.size(); //grandezza lista per numero di nodi
    }

    @Override
    public int numEdges() {
        return numEdges; // numero di archi
    }

    @Override
    public Collection<V> getNodes() {
        return nodesList.keySet(); // restituisce come lista i value della mappa (i nodi)
    }

    @Override
    public Collection<? extends AbstractEdge<V, L>> getEdges() {
        List<AbstractEdge<V,L>> edges = new ArrayList<>(); //creo una lista per gli archi

        for(V node : nodesList.keySet()){ // itera i nodi
            for(Map.Entry<V,L> entry :nodesList.get(node).entrySet()){ // ottiene la mappa dei vicini e itera sugli archi uscenti
                if(directed || node.hashCode() <= entry.getKey().hashCode()){ // verifica quali vanno aggiunti
                    // (caso 2 aggiunge solo gli archi da un nodo con hashCode più basso a uno con hashCode più alto)

                    edges.add(new Edge<>(node, entry.getKey(), entry.getValue())); //aggiunge gli elementi alla lista di archi
                }
            }
        }

        return edges;
    }

    // se a è presente nella mappa restituisce la mappa dei collegamenti di a, una lista vuota altrimenti
    @Override
    public Collection<V> getNeighbours(V a) {
        return nodesList.containsKey(a) ? nodesList.get(a).keySet() : Collections.emptySet();
    }

    // se a è presente restituisce il label b, null altrimenti
    @Override
    public L getLabel(V a, V b) {
        return nodesList.containsKey(a) ? nodesList.get(a).get(b) : null;
    }
}
