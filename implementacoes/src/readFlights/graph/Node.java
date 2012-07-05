package readFlights.graph;

import java.util.List;

import readFlights.FlightLeg;

public class Node {
	private int id;
	private FlightLeg flightLeg;

	public Node(FlightLeg flightLeg, int id) {
		this.flightLeg = flightLeg;
		this.id = id;
	}

	public FlightLeg getFlightLeg() {
		return this.flightLeg;
	}
	
	public int getId() {
		return id;
	}
}
