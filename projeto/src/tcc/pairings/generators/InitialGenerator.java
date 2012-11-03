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
import tcc.pairings.io.outputers.Outputer;

public class InitialGenerator extends PairingsGenerator {
	private List<Leg> legs;
	
	public InitialGenerator(FlightNetwork net, Outputer[] outputers) {
		this(net, outputers, null);
	}
	
	public InitialGenerator(FlightNetwork net, Outputer[] outputers, CostCalculator calculator) {
		super(net, outputers, calculator);
		legs = new ArrayList<Leg>(net.getLegs());
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
			numberOfPairings++;
			legs.removeAll(duplicatedLegs);
			output(pairing);
		}
	}
	
	private List<Leg> getDuplicatedLegs(Pairing pairing) {
		List<Leg> duplicatedLegs = new ArrayList<Leg>();
		for (DutyLeg dutyLeg: pairing.getLegs()) 
			for (Leg leg: legs)
				if (leg.equals(dutyLeg)) {
					duplicatedLegs.add(leg);
					dutyLeg.setDeadHead(false);
				}
		return duplicatedLegs;
	}

	private void output(Pairing pairing) {
		if (calculator != null)
			calculator.setCost(pairing);
		for (Outputer outputer: outputers)
			outputer.output(pairing);
	}
	
	public boolean isAllLegsCovered() {
		return legs.isEmpty();
	}
}
