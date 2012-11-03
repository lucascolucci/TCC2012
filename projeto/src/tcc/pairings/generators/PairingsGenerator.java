package tcc.pairings.generators;

import tcc.pairings.DutyData;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.graph.Edge;
import tcc.pairings.graph.Node;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.graph.networks.FlightNetworkEdgeLabel;
import tcc.pairings.graph.networks.FlightNetworkNodeLabel;
import tcc.pairings.graph.networks.FlightNetworkPath;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.rules.Rules;

public class PairingsGenerator extends BasicGenerator {
	private int maxPairings;
	protected FlightNetworkPath path;
	
	public int getMaxPairings() {
		return maxPairings;
	}

	public void setMaxPairings(int maxPairings) {
		this.maxPairings = maxPairings;
	}
	
	public PairingsGenerator(FlightNetwork net) {
		this(net, null, null);
	}
	
	public PairingsGenerator(FlightNetwork net, CostCalculator calculator) {
		this(net, null, calculator);
	}
	
	public PairingsGenerator(FlightNetwork net, Outputer[] outputers) {
		this(net, outputers, null);
	}
	
	public PairingsGenerator(FlightNetwork net, Outputer[] outputers, CostCalculator calculator) {
		super(net, outputers, calculator);
		maxPairings = 0;
		path = new FlightNetworkPath();
	}
	
	@Override
	protected void findPairings(Node<Leg> node) {
		if (maxPairings > 0 && numberOfPairings >= maxPairings)
			return;
		for (Edge<Leg> edge: node.getEdges())
			exploreTrough(edge);		
	}

	protected void exploreTrough(Edge<Leg> edge) {
		switch (edge.getType()) {
		case FROM_SOURCE:
			exploreTroughSource(edge);
			break;
		case CONNECTION:
			exploreTroughConnection(edge);
			break;
		case OVERNIGHT:
			exploreTroughOvernight(edge);
			break;
		case TO_SINK:
			exploreTroughSink();
			break;
		}
	}
	
	private void exploreTroughSource(Edge<Leg> edge) {
		addNewDutyToPath(path, edge);	
		findPairings(edge.getIn());
		resetPath();
	}
	
	private void resetPath() {
		path.removeEdge();
		path.reset();
	}

	private void exploreTroughConnection(Edge<Leg> edge) {
		if (Rules.isPossibleToAppendConnection(path, edge)) {
			addConnectionToPath(path, edge);
			findPairings(edge.getIn());
			removeConnectionFromPath(edge);
		}
	}
	
	private void removeConnectionFromPath(Edge<Leg> edge) {
		int flightTime = ((FlightNetworkNodeLabel) edge.getIn().getLabel()).getFlightTime();
		int sitTime = ((FlightNetworkEdgeLabel) edge.getLabel()).getSitTime();
		short track = edge.getOut().getInfo().getTrack();
		path.removeConnection(flightTime, sitTime, track);
		path.removeEdge();
	}
	
	private void exploreTroughOvernight(Edge<Leg> edge) {
		if (Rules.isPossibleToAppendOvernight(path, edge, base)) {
			DutyData dutyData = path.getDutyData().clone();
			short track = path.getTrack();
			addNewDutyToPath(path, edge);	
			findPairings(edge.getIn());
			removeOvernightFromPath(dutyData, track);
		}
	}

	private void removeOvernightFromPath(DutyData dutyData, short track) {
		path.removeOvernight(dutyData, track);
		path.removeEdge();
	}
	
	private void exploreTroughSink() {
		output();
	}
	
	@Override
	protected void output() {
		numberOfPairings++;
		if (outputers != null) {
			Pairing pairing = getNewPairing();
			for (Outputer outputer: outputers) 
				outputer.output(pairing);
		}
	}

	private Pairing getNewPairing() {
		Pairing pairing = new Pairing(numberOfPairings, path);
		if (calculator != null)
			calculator.setCost(pairing);
		pairing.setCostWithDeadHeads(pairing.getCost());
		return pairing;
	}
}
