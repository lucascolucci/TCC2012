package pairings.graph.networks;

import pairings.graph.Label;

public class FlightNetworkEdgeLabel extends Label {
	private int sitTime;
	
	public FlightNetworkEdgeLabel(int sitTime) {
		super();
		this.sitTime = sitTime;
	}
	
	public int getSitTime() {
		return sitTime;
	}
}
