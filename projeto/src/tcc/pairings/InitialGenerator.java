package tcc.pairings;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.graph.Edge;
import tcc.pairings.graph.Node;
import tcc.pairings.graph.networks.FlightNetwork;

public class InitialGenerator extends PairingsGenerator {
	private List<Pairing> pairings;
	private List<Leg> legs;
	
	public List<Pairing> getPairings() {
		return pairings;
	}
	
	public InitialGenerator(FlightNetwork net) {
		super(net);
		pairings = new ArrayList<Pairing>();
		setLegsList(net.getLegs());
	}
	
	private void setLegsList(List<Leg> legs) {
		this.legs = new ArrayList<Leg>();
		for (Leg leg: legs) {
			this.legs.add(leg);
		}
	}

	@Override
	protected void findPairings(Node<Leg> node) {
		if (legs.isEmpty())
			return;
		for (Edge<Leg> edge: node.getEdges())
			exploreTrough(edge);
	}

	@Override
	protected void output() {
		Pairing pairing = new Pairing(numberOfPairings, path);
		pairing.setAllDutiesAsDH();
		List<Leg> duplicatedLegs = getDuplicatedLegs(pairing);
		if (!duplicatedLegs.isEmpty()) {
			legs.removeAll(duplicatedLegs);
			pairings.add(pairing);
		}
	}

	private List<Leg> getDuplicatedLegs(Pairing pairing) {
		List<Leg> duplicatedLegs = new ArrayList<Leg>();
		for (DutyLeg pairingLeg: pairing.getLegs()) 
			for (Leg leg: legs)
				if (leg.isDuplicate(pairingLeg)) {
					duplicatedLegs.add(leg);
					pairingLeg.setDeadHead(false);
				}
		return duplicatedLegs;
	}
}
