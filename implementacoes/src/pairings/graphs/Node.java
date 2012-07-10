package pairings.graphs;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
	private int id;
	private T content;
	private List<Edge<T>> edges;
	
	public Node(int id, T content) {
		this.id = id;
		this.content = content;
		edges = new ArrayList<Edge<T>>();
	}
	
	public int getId() {
		return id;
	}
	
	public T getContent() {
		return content;
	}
	
	public List<Edge<T>> getEdges() {
		return edges;
	}
	
	public List<Node<T>> getNeighbors() {
		List<Node<T>> neighbors = new ArrayList<Node<T>>();
		for (Edge<T> edge: edges) {
			neighbors.add(edge.getOut());
		}
		return neighbors;
	}
	
	public void addNeighbor(Node<T> node) {
		edges.add(new Edge<T>(this, node));
	}
	
	public boolean hasNeighbor(Node<T> node) {
		for (Edge<T> edge: edges)
			if (edge.getIn() == node)
				return true;
		return false;
	}
}
