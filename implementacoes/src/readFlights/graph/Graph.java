package readFlights.graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
	private List<Node> nodeList;
	private int numberOfNodes;
	private int numberOfEdges;

	public Graph() {
		nodeList = new ArrayList<Node>();
		numberOfNodes = 0;
		numberOfEdges = 0;
	}
	
	public List<Node> getNodeList() {
		return nodeList;
	}

	public int getNumberOfEdges() {
		return numberOfEdges;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public void addNode(Node node) throws Exception {
		if (node != null) {
			nodeList.add(node);
			numberOfNodes++;
		} else
			throw new Exception("Error: Trying to add a null node.");
	}

	public void addEdge(Node from, Node to) throws Exception {
		if (nodeList.contains(from) && nodeList.contains(to) && from != to) {
			from.addNeighbor(to);
			numberOfEdges++;
		} else
			throw new Exception("Error: Trying to connect null nodes or same node.");
	}

	public boolean hasEdge(Node from, Node to){
		return nodeList.contains(from) && nodeList.contains(to) 
				&& from.hasNeighbor(to);
	}
}
