package pairings.graphs;

import java.util.ArrayList;
import java.util.List;

public class Graph<T> {
	public List<Node<T>> nodes;
	private int numberOfNodes;
	private int numberOfEdges;

	public Graph() {
		nodes = new ArrayList<Node<T>>();
		numberOfNodes = 0;
		numberOfEdges = 0;
	}
	
	public List<Node<T>> getNodes() {
		return nodes;
	}

	public int getNumberOfEdges() {
		return numberOfEdges;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public void addNode(Node<T> node) {
		if (node != null) {
			nodes.add(node);
			numberOfNodes++;
		}
	}
	
	public void addEdge(Node<T> out, Node<T> in) {
		addEdge(out, in, null);
	}
	
	public void addEdge(Node<T> out, Node<T> in, EdgeType type) {
		if (nodes.contains(out) && nodes.contains(in) && out != in) {
			out.addNeighbor(in, type);
			numberOfEdges++;
		}
	}
	
	public void addEdge(Node<T> out, Node<T> in, EdgeType type, Label label) {
		if (nodes.contains(out) && nodes.contains(in) && out != in) {
			out.addNeighbor(in, type, label);
			numberOfEdges++;
		}
	}

	public boolean hasEdge(Node<T> out, Node<T> in) {
		return out.hasNeighbor(in);
	}
	
	public List<Node<T>> getNeighbors(Node<T> node) {
		if (nodes.contains(node)) 
			return node.getNeighbors();
		return null;
	}
	
	public int numberOfInwardEdges(Node<T> node) {
		int count = 0;
		for (Node<T> n: nodes) 
			if (n.hasNeighbor(node))
				count++;
		return count;
	}
	
	public int numberOfOutwardEdges(Node<T> node) {
		return node.numberOfNeighbors();
	}
}
