package pairings;

import java.util.ArrayList;
import java.util.List;

import pairings.graph.Edge;
import pairings.graph.EdgeType;
import pairings.graph.networks.FlightNetworkPath;

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
			if (edge.getType() == EdgeType.OVERNIGHT) {
				duties.add(duty);
				duty = new Duty();	
			}
			duty.addLeg(edge.getIn().getInfo());
		}
		if (!duties.contains(duty)) 
			duties.add(duty);
	}
}
