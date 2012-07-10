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
		if (nodes.contains(out) && nodes.contains(in) && out != in) {
			out.addNeighbor(in);
			numberOfEdges++;
		}
	}

	public boolean hasEdge(Node<T> out, Node<T> in) {
		return out.hasNeighbor(in);
	}
}
