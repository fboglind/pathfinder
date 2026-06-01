// PROG2 VT2023, Inlämmningsuppgift, del 2
// Grupp 028
// Gabriel Bendezu gabe3137
// Fredrik Boglind frbo5627
// Maria Fernanda Esquivel Hidalgo maes3583

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * {@link Graph} implementation backed by adjacency sets.
 *
 * @param <T> node type
 */
public class ListGraph<T> implements Graph<T> {

    private final Map<T, Set<Edge<T>>> nodes = new HashMap<>();

    @Override
    public void add(T node) {
        nodes.putIfAbsent(node, new HashSet<>());
    }

    @Override
    public void remove(T node) {

        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException("NoSuchElementException");
        }

        for (T neighbourNode : new HashSet<>(nodes.keySet())) {
            if (getEdgeBetween(node, neighbourNode) != null) {
                disconnect(node, neighbourNode);
            }
        }
        nodes.remove(node);

    }

    @Override
    public void connect(T node1, T node2, String name, int weight) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            throw new NoSuchElementException("NoSuchElementException");
        } else if (weight < 0) {
            throw new IllegalArgumentException("IllegalArgumentException");
        } else if (getEdgeBetween(node1, node2) != null) {
            throw new IllegalStateException("IllegalStateException");
        }

        Set<Edge<T>> node1Edges = nodes.get(node1);
        Set<Edge<T>> node2Edges = nodes.get(node2);

        node1Edges.add(new Edge<T>(name, node2, weight));
        node2Edges.add(new Edge<T>(name, node1, weight));

    }

    @Override
    public void disconnect(T node1, T node2) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            throw new NoSuchElementException("NoSuchElementException");
        } else if (getEdgeBetween(node1, node2) == null) {
            throw new IllegalStateException("IllegalStateException");
        }
        Set<Edge<T>> node1Edges = nodes.get(node1);
        Set<Edge<T>> node2Edges = nodes.get(node2);
        Set<Edge<T>> edgesToRemove = new HashSet<>();

        for (Edge<T> edge : node1Edges) {
            if (edge.getDestination().equals(node2)) {
                edgesToRemove.add(edge);
            }
        }

        for (Edge<T> edge : node2Edges) {
            if (edge.getDestination().equals(node1)) {
                edgesToRemove.add(edge);
            }
        }

        node1Edges.removeAll(edgesToRemove);
        node2Edges.removeAll(edgesToRemove);
    }

    @Override
    public void setConnectionWeight(T node1, T node2, int newWeight) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            throw new NoSuchElementException("NoSuchElementException");
        } else if (newWeight < 0) {
            throw new IllegalArgumentException("IllegalArgumentException");
        } else if (getEdgeBetween(node1, node2) == null) {
            throw new IllegalStateException("IllegalStateException");
        }

        Set<Edge<T>> node1Edges = nodes.get(node1);
        Set<Edge<T>> node2Edges = nodes.get(node2);

        for (Edge<T> edge : node1Edges) {
            if (edge.getDestination().equals(node2)) {
                edge.setWeight(newWeight);
            }
        }

        for (Edge<T> edge : node2Edges) {
            if (edge.getDestination().equals(node1)) {
                edge.setWeight(newWeight);
            }
        }
    }

    @Override
    public Set<T> getNodes() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    @Override
    public Set<Edge<T>> getEdgesFrom(T node) {
        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException("NoSuchElementException");
        }
        return Collections.unmodifiableSet(nodes.get(node));
    }

    @Override
    public Edge<T> getEdgeBetween(T currentNode, T nextNode) {
        if (!nodes.containsKey(currentNode) || !nodes.containsKey(nextNode)) {
            throw new NoSuchElementException("NoSuchElementException");
        }

        for (Edge<T> edge : nodes.get(currentNode)) {
            if (edge.getDestination().equals(nextNode)) {
                return edge;
            }
        }
        return null;
    }

    @Override
    public boolean pathExists(T node1, T node2) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            return false;
        }
        Map<T, T> predecessors = new HashMap<>();
        depthFirstSearch(node1, null, predecessors);
        return predecessors.containsKey(node2);

    }

    @Override
    public List<Edge<T>> getPath(T node1, T node2) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            return null;
        }

        Map<T, T> visited = new HashMap<>();

        depthFirstSearch(node1, null, visited);
        List<Edge<T>> path = new ArrayList<>();
        if (!visited.containsKey(node2)) {
            return null;
        }
        T where = node2;
        while (!where.equals(node1)) {
            T node = visited.get(where);
            Edge<T> edge = getEdgeBetween(node, where);
            path.add(edge);
            where = node;
        }
        Collections.reverse(path);
        return Collections.unmodifiableList(path);
    }

    private void depthFirstSearch(T current, T predecessor, Map<T, T> connection) {
        connection.put(current, predecessor);
        for (Edge<T> edge : nodes.get(current)) {
            if (!connection.containsKey(edge.getDestination())) {
                depthFirstSearch(edge.getDestination(), current, connection);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (T node : nodes.keySet()) {
            sb.append(node).append(":").append(nodes.get(node)).append("\n");
        }
        return sb.toString();
    }

}
