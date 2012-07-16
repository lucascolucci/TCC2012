package pairings.graph;

import java.util.ArrayList;
import java.util.List;

public class Graph<T> {
	private List<Node<T>> nodes;
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
		addEdge(out, in, null, null);
	}
	
	public void addEdge(Node<T> out, Node<T> in, EdgeType type) {
		addEdge(out, in, type, null);
	}
	
	public void addEdge(Node<T> out, Node<T> in, EdgeType type, Label label) {
		if (nodes.contains(out) && nodes.contains(in) && out != in) {
			out.addNeighbor(in, type, label);
			numberOfEdges++;
		}
	}
	
	public boolean hasEdge(Node<T> out, Node<T> in) {
		return nodes.contains(out) && nodes.contains(in) && out.hasNeighbor(in);
	}
	
	public Edge<T> getEdge(Node<T> out, Node<T> in) {
		if (nodes.contains(out) && nodes.contains(in))
			return out.getEdge(in);
		return null;
	}
	
	public List<Node<T>> getNeighbors(Node<T> node) {
		if (nodes.contains(node)) 
			return node.getNeighbors();
		return null;
	}
	
	public List<Edge<T>> getOutwardEdges(Node<T> node) {
		if (nodes.contains(node))
			return node.getEdges();
		return null;
	}
	
	public int numberOfOutwardEdges(Node<T> node) {
		if (nodes.contains(node))
			return node.numberOfNeighbors();
		return -1;
	}
	
	public List<Edge<T>> getInwardEdges(Node<T> node) {
		if (nodes.contains(node)) {
			List<Edge<T>> edges = new ArrayList<Edge<T>>();
			for (Node<T> out: nodes) 
				if (out.hasNeighbor(node))
					edges.add(out.getEdge(node));
			return edges;
		}
		return null;
	}
	
	public int numberOfInwardEdges(Node<T> node) {
		if (nodes.contains(node)) {
			int count = 0;
			for (Node<T> out: nodes) 
				if (out.hasNeighbor(node))
					count++;
			return count;
		}
		return -1;
	}
	
	public void removeNode(Node<T> node) {
		if (nodes.contains(node)) {
			List<Edge<T>> inwards = getInwardEdges(node);
			for (Edge<T> edge: inwards) {
				edge.getOut().removeEdge(edge);
				numberOfEdges--;
			}
			numberOfEdges -= node.numberOfNeighbors();
			node.removeEdges();
			nodes.remove(node);
			numberOfNodes--;
		}
	}
}
