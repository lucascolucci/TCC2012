package tcc.pairings.graph.networks;

import tcc.pairings.graph.Label;

public class FlightNetworkNodeLabel extends Label {
	private int flightTime;
	private int day;
	
	public FlightNetworkNodeLabel(int flightTime, int day) {
		this.flightTime = flightTime;
		this.day  = day;
	}
	
	public int getFlightTime() {
		return flightTime;
	}
	
	public int getDay() {
		return day;
	}
}
