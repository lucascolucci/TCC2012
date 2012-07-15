package pairings.graph.networks;

import pairings.graph.Label;

public class FlightNetworkNodeLabel extends Label {
	private int flightTime;
	
	public FlightNetworkNodeLabel(int flightTime) {
		this.flightTime = flightTime;
	}
	
	public int getFlightTime() {
		return flightTime;
	}
}
