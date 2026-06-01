// PROG2 VT2023, Inlämmningsuppgift, del 2
// Grupp 028
// Gabriel Bendezu gabe3137
// Fredrik Boglind frbo5627
// Maria Fernanda Esquivel Hidalgo maes3583

/**
 * Named weighted edge that points to a destination node.
 *
 * @param <T> destination node type
 */
public class Edge<T> {

    private final String name;
    private final T destination;
    private int weight;

    /**
     * Creates an edge.
     *
     * @param name edge name
     * @param destination destination node
     * @param weight non-negative weight
     */
    public Edge(String name, T destination, int weight) {
        if (weight < 0)
            throw new IllegalArgumentException("Weight can not be negative value");
        this.name = name;
        this.destination = destination;
        this.weight = weight;
    }

    /**
     * Returns the node this edge leads to.
     *
     * @return destination node
     */
    public T getDestination() {
        return destination;
    }

    /**
     * Returns the edge weight.
     *
     * @return edge weight
     */
    public int getWeight() {
        return weight;

    }

    /**
     * Updates the edge weight.
     *
     * @param weight new non-negative weight
     */
    public void setWeight(int weight) {
        if (weight < 0)
            throw new IllegalArgumentException("Weight can not be negative value");
        this.weight = weight;
    }

    /**
     * Returns the edge name.
     *
     * @return edge name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "to " + getDestination() + " by " + getName() + " takes " + getWeight(); 
    }
}
