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
		Leg coverLeg = getCoverLeg();
		if (coverLeg != null) {
			addCoverPairing(coverLeg);
			legs.remove(coverLeg);
		}
	}
	
	private Leg getCoverLeg() {
		List<Leg> pathLegs = path.getLegs();
		//for (int i = pathLegs.size() - 1; i >= 0; i--)
		for (int i = 0; i <  pathLegs.size(); i++)
			for (Leg leg: legs)
				if (leg.isDuplicate(pathLegs.get(i)))
					return leg;
		return null;
	}
	
	private void addCoverPairing(Leg coverLeg) {
		Pairing pairing = new Pairing(numberOfPairings, path);
		pairing.coverOne(coverLeg);
		pairings.add(pairing);
	}
}
