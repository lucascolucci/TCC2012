package pairings.graphs;

import java.util.ArrayList;
import java.util.List;

public class Path<T> {
	private List<Edge<T>> edges;
	private Node<T> start;
	private Node<T> end;
	
	public Path(Node<T> start) {
		edges = new ArrayList<Edge<T>>();
		this.start = start;
		this.end = start;
	}
	
	public Node<T> getStart() {
		return start;
	}

	public void setStart(Node<T> start) {
		this.start = start;
	}

	public Node<T> getEnd() {
		return end;
	}

	public void setEnd(Node<T> end) {
		this.end = end;
	}

	public void setEdges(List<Edge<T>> edges) {
		this.edges = edges;
	}

	public List<Edge<T>> getEdges() {
		return edges;
	}
	
	public void addEdge(Edge<T> edge) {
		edges.add(edge);
		end = edges.get(edges.size() - 1).getIn();
	}
	
	public void removeEdge() {
		edges.remove(edges.size() - 1);
		end = edges.get(edges.size() - 1).getIn();
	}
	
}
