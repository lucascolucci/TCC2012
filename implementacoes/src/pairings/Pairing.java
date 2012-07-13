package pairings;

import java.util.ArrayList;
import java.util.List;

import pairings.graphs.Edge;
import pairings.graphs.EdgeType;

public class Pairing {
	private List<Duty> duties;
	
	public Pairing(FlightNetworkPath path) {
		duties = new ArrayList<Duty>();
		build(path);
	}
	
	public List<Duty> getDuties() {
		return duties;
	}
	
	private void build(FlightNetworkPath path) {
		Duty duty = new Duty();
		for (Edge<Leg> edge: path.getEdges()) {			
			if (edge.getType() == EdgeType.OVERNIGHT) 
				duty = new Duty();	
			duty.addLeg(edge.getIn().getContent());
		}
	}
}
