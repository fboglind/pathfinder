// PROG2 VT2023, Inlämmningsuppgift, del 1
// Grupp 028
// Gabriel Bendezu gabe3137
// Fredrik Boglind frbo5627
// Maria Fernanda Esquivel Hidalgo maes3583

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Undirected weighted graph where each edge has a name.
 *
 * @param <T> node type
 */
public interface Graph<T> {

    /**
     * Adds a node if it is not already present.
     *
     * @param node node to add
     */
    void add(T node);

    /**
     * Connects two existing nodes with a named, non-negative weighted edge.
     *
     * @param node1 first node
     * @param node2 second node
     * @param name edge name
     * @param weight edge weight
     */
    void connect(T node1, T node2, String name, int weight);

    /**
     * Updates the weight for an existing connection.
     *
     * @param node1 first node
     * @param node2 second node
     * @param weight new non-negative weight
     */
    void setConnectionWeight(T node1, T node2, int weight);

    /**
     * Returns all nodes in the graph.
     *
     * @return graph nodes
     */
    Set<T> getNodes();

    /**
     * Returns all edges leaving a node.
     *
     * @param node node whose edges should be returned
     * @return outgoing edges
     */
    Collection<Edge<T>> getEdgesFrom(T node);

    /**
     * Returns the direct edge between two nodes, or {@code null} when absent.
     *
     * @param node1 first node
     * @param node2 second node
     * @return direct edge, or {@code null}
     */
    Edge<T> getEdgeBetween(T node1, T node2);

    /**
     * Removes the direct connection between two nodes.
     *
     * @param node1 first node
     * @param node2 second node
     */
    void disconnect(T node1, T node2);

    /**
     * Removes a node and all of its connections.
     *
     * @param node node to remove
     */
    void remove(T node);

    /**
     * Checks whether any path exists between two nodes.
     *
     * @param from start node
     * @param to destination node
     * @return {@code true} if a path exists
     */
    boolean pathExists(T from, T to);

    /**
     * Returns one path between two nodes.
     *
     * @param from start node
     * @param to destination node
     * @return ordered path edges, or {@code null} when no path exists
     */
    List<Edge<T>> getPath(T from, T to);
}
