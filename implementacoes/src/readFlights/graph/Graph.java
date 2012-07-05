package readFlights.graph;

import java.util.List;

public class Graph {
	private List<Node> nodeList;
	private List<List<Integer>> adjMatrix;
	private int numberOfNodes;
	private int numberOfEdges;

	public Graph(Node node) {
		if (node != null) {
			this.numberOfNodes = 1;
			this.nodeList.add(node);
		} else {
			this.numberOfNodes = 0;
			this.nodeList = null;
		}
		this.numberOfEdges = 0;
	}

	public void addNode(Node node) {
		if (node != null) {
			nodeList.add(node.getId(), node);
			numberOfNodes++;
		} else
			System.err.println("Error: Trying to add a null node.");
	}

	public void addEdge(int nodeIdA, int nodeIdB) {
		if (nodeList.get(nodeIdA) != null && nodeList.get(nodeIdB) != null
				&& nodeList.get(nodeIdA) != nodeList.get(nodeIdB)) {
			adjMatrix.get(nodeIdA).set(nodeIdB, 1);
			numberOfEdges++;
		} else
			System.err
					.println("Error: Trying to connect null nodes or same node.");
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

	public boolean isEdge(int nodeIdA, int nodeIdB){
		if(adjMatrix.get(nodeIdA).get(nodeIdB) == 1)
			return true;
		return false;
	}
	
	public boolean existsPath(int nodeIdA, int nodeIdB){
		if(adjMatrix.get(nodeIdA).get(nodeIdB) == 1)
			return true;
		else
			for(int i = 0; i<adjMatrix.get(nodeIdA).size(); i++){
				if(adjMatrix.get(nodeIdA).get(i) == 1)
					return existsPath(adjMatrix.get(nodeIdA).get(i), nodeIdB);
			}
		return false;
	}
}
