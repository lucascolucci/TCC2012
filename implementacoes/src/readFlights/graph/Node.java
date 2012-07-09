package readFlights.graph;

import java.util.ArrayList;
import java.util.List;
import readFlights.Leg;

public class Node {
	private int id;
	private Leg flightLeg;
	private List<Node> neighbors;
	
	public Node(Leg flightLeg, int id) {
		this.flightLeg = flightLeg;
		this.id = id;
		neighbors = new ArrayList<Node>();
	}
	
	public int getId() {
		return id;
	}
	
	public Leg getFlightLeg() {
		return flightLeg;
	}
	
	public List<Node> getNeighbors() {
		return neighbors;
	}
	
	public void addNeighbor(Node node) {
		neighbors.add(node);
	}
	
	public boolean hasNeighbor(Node node) {
		return neighbors.contains(node);
	}
}
