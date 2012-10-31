package tcc.pairings.generators;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
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
		this(net, null);
	}
	
	public InitialGenerator(FlightNetwork net, CostCalculator calculator) {
		super(net, calculator);
		pairings = new ArrayList<Pairing>();
		this.legs = new ArrayList<Leg>(net.getLegs());
	}
	
	public boolean isAllLegsCovered() {
		return legs.isEmpty();
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
		pairing.setAllDeadHeads(true);
		List<Leg> duplicatedLegs = getDuplicatedLegs(pairing);
		if (!duplicatedLegs.isEmpty()) {
			legs.removeAll(duplicatedLegs);
			setCostAndAddPairing(pairing);
		}
	}
	
	private List<Leg> getDuplicatedLegs(Pairing pairing) {
		List<Leg> duplicatedLegs = new ArrayList<Leg>();
		for (DutyLeg pairingLeg: pairing.getLegs()) 
			for (Leg leg: legs)
				if (leg.equals(pairingLeg)) {
					duplicatedLegs.add(leg);
					pairingLeg.setDeadHead(false);
				}
		return duplicatedLegs;
	}
	
	private void setCostAndAddPairing(Pairing pairing) {
		if (calculator != null)
			calculator.setCost(pairing);
		pairings.add(pairing);
	}
}
