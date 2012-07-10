package pairings.graphs;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
	private T content;
	private Label label;
	private List<Edge<T>> edges;
	
	public Node(T content, Label label) {
		this.content = content;
		this.label = label;
		edges = new ArrayList<Edge<T>>();
	}
	
	public T getContent() {
		return content;
	}
	
	public Label getLabel() {
		return label;
	}
	
	public List<Edge<T>> getEdges() {
		return edges;
	}
	
	public List<Node<T>> getNeighbors() {
		List<Node<T>> neighbors = new ArrayList<Node<T>>();
		for (Edge<T> edge: edges) {
			neighbors.add(edge.getIn());
		}
		return neighbors;
	}
	
	public void addNeighbor(Node<T> node, EdgeType type) {
		edges.add(new Edge<T>(this, node, type));
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
}
