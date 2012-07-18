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
	
	public int getNumberOfDuties() {
		return duties.size();
	}
	
	public Leg getFirstLeg() {
		if (!duties.isEmpty() && !duties.get(0).getLegs().isEmpty()) 
			return duties.get(0).getLegs().get(0);
		return null;
	}
	
	public Leg getLastLeg() {
		if (!duties.isEmpty()) {
			Duty lastDuty = duties.get(duties.size() - 1);
			int numberOfLegs = lastDuty.getLegs().size();
			if (numberOfLegs > 0)
				return lastDuty.getLegs().get(numberOfLegs - 1);
		}
		return null;
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
		duties.add(duty);
	}

	public int getCost() {
		return 45;
	}

	public boolean contains(int number) {
		for (Duty duty: duties)
			if(duty.contains(number))
				return true;
		return false;
	}
}
