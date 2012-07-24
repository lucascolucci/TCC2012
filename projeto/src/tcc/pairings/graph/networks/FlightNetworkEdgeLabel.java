package tcc.pairings.graph.networks;

import tcc.pairings.graph.Label;

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
