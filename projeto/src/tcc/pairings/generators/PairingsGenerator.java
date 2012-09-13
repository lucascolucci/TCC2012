package tcc.pairings.generators;

import tcc.pairings.Base;
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
import tcc.pairings.graph.networks.SpecialNode;
import tcc.pairings.io.Outputer;
import tcc.pairings.rules.Rules;

public class PairingsGenerator {
	private FlightNetwork net;
	private Outputer[] outputers;
	private CostCalculator calculator;
	protected int numberOfPairings;
	private Base base;
	protected FlightNetworkPath path;
	private SpecialNode source;
	private SpecialNode sink;
	
	public int getNumberOfPairings() {
		return numberOfPairings;
	}
	
	public void setNumberOfPairings(int numberOfPairings) {
		this.numberOfPairings = numberOfPairings;
	}
	
	public PairingsGenerator(FlightNetwork net) {
		this(net, null, null);
	}
	
	public PairingsGenerator(FlightNetwork net, Outputer[] outputers) {
		this(net, outputers, null);
	}
	
	public PairingsGenerator(FlightNetwork net, Outputer[] outputers, CostCalculator calculator) {
		this.net = net;
		this.outputers = outputers;
		this.calculator = calculator;
		numberOfPairings = 0;
	}
	
	public void generate(Base... bases) {
		for (Base base: bases)
			generate(base);
	}
		
	public void generate(Base base) {
		initialSetUp(base);
		addSourceAndSink();
		findPairings(source);
		removeSourceAndSink();
	}

	private void initialSetUp(Base base) {
		this.base = base;
		path = new FlightNetworkPath();
		setSourceAndSink();
	}

	private void setSourceAndSink() {
		source = new SpecialNode(base);
		sink = new SpecialNode(base);
	}
	
	private void addSourceAndSink() {
		net.addSource(source);
		net.addSink(sink);
	}
	
	private void removeSourceAndSink() {
		net.removeNode(source);
		net.removeNode(sink);
	}

	protected void findPairings(Node<Leg> node) {
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
			incrementNumberOfPairingsAndOutput();
			break;
		}
	}
	
	private void exploreTroughSource(Edge<Leg> edge) {
		addNewDutyToPath(edge);	
		findPairings(edge.getIn());
		resetPath();
	}
	
	private void addNewDutyToPath(Edge<Leg> edge) {
		int flightTime = ((FlightNetworkNodeLabel) edge.getIn().getLabel()).getFlightTime();
		short track = edge.getIn().getInfo().getTrack();
		path.addNewDuty(flightTime, track);
		path.addEdge(edge);
	}

	private void resetPath() {
		path.removeEdge();
		path.reset();
	}

	private void exploreTroughConnection(Edge<Leg> edge) {
		if (Rules.isPossibleToAppendConnection(path, edge)) {
			addConnectionToPath(edge);
			findPairings(edge.getIn());
			removeConnectionFromPath(edge);
		}
	}
	
	private void addConnectionToPath(Edge<Leg> edge) {
		int flightTime = ((FlightNetworkNodeLabel) edge.getIn().getLabel()).getFlightTime();
		int sitTime = ((FlightNetworkEdgeLabel) edge.getLabel()).getSitTime();
		short track = edge.getIn().getInfo().getTrack();
		path.addConnection(flightTime, sitTime, track);
		path.addEdge(edge);
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
			addNewDutyToPath(edge);	
			findPairings(edge.getIn());
			removeOvernightFromPath(dutyData, track);
		}
	}

	private void removeOvernightFromPath(DutyData dutyData, short track) {
		path.removeOvernight(dutyData, track);
		path.removeEdge();
	}
	
	private void incrementNumberOfPairingsAndOutput() {
		++numberOfPairings;
		output();
	}
	
	protected void output() {
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
		return pairing;
	}
}
