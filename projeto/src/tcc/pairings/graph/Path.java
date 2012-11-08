package tcc.pairings.graph;

import java.util.ArrayList;
import java.util.List;

public class Path<T> {
	protected List<Edge<T>> edges;
	
	public List<Edge<T>> getEdges() {
		return edges;
	}
	
	public Path() {
		edges = new ArrayList<Edge<T>>();
	}
	
	public void addEdge(Edge<T> edge) {
		edges.add(edge);
	}
	
	public void removeEdge() {
		edges.remove(edges.size() - 1);
	}
	
	public Node<T> getStart() {
		if (edges.size() > 0)
			return edges.get(0).getOut();
		return null;
	}
	
	public Node<T> getEnd() {
		int size = edges.size();
		if (size > 0)
			return edges.get(size - 1).getIn();
		return null;
	}
	
	public List<Node<T>> getNodes() {
		List<Node<T>> nodes = new ArrayList<Node<T>>();
		for (Edge<T> edge: edges)
			nodes.add(edge.getOut());
		nodes.add(getEnd());
		return nodes;
	}
}
