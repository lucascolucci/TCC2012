package pairings.graph;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
	private T info;
	private Label label;
	private List<Edge<T>> edges;

	public Node(T info) {
		this.info = info;
		label = null;
		edges = new ArrayList<Edge<T>>();
	}
	
	public Node(T info, Label label) {
		this.info = info;
		this.label = label;
		edges = new ArrayList<Edge<T>>();
	}
	
	public T getInfo() {
		return info;
	}
	
	public Label getLabel() {
		return label;
	}
	
	public List<Edge<T>> getEdges() {
		return edges;
	}
	
	public int numberOfNeighbors() {
		return edges.size();
	}
	
	public List<Node<T>> getNeighbors() {
		List<Node<T>> neighbors = new ArrayList<Node<T>>();
		for (Edge<T> edge: edges)
			neighbors.add(edge.getIn());
		return neighbors;
	}
	
	public void addNeighbor(Node<T> node, EdgeType type) {
		edges.add(new Edge<T>(this, node, type, null));
	}
	
	public void addNeighbor(Node<T> node, EdgeType type, Label label) {
		edges.add(new Edge<T>(this, node, type, label));
	}
	
	public boolean hasNeighbor(Node<T> neighbor) {
		for (Edge<T> edge: edges)
			if (edge.getIn() == neighbor)
				return true;
		return false;
	}
	
	public Edge<T> getEdge(Node<T> neighbor) {
		for (Edge<T> edge: edges)
			if (edge.getIn() == neighbor)
				return edge;
		return null;
	}
	
	public void removeEdges() {
		edges.clear();
	}
	
	public void removeEdge(Edge<T> edge) {
		edges.remove(edge);
	}
}
