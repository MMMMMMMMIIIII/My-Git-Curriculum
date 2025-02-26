package ex3_4;

public class Edge<V,L> implements AbstractEdge<V,L>{
    private final V from;
    private final V to;
    private final L label;

    public Edge(V from, V to, L label) {
        this.from = from;
        this.to = to;
        this.label = label;
    }

    @Override
    public V getStart() {
        return from;
    }

    @Override
    public V getEnd() {
        return to;
    }

    @Override
    public L getLabel() {
        return label;
    }

}
