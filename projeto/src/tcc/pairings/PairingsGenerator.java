package tcc.pairings;

import tcc.pairings.graph.Edge;
import tcc.pairings.graph.Node;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.graph.networks.FlightNetworkEdgeLabel;
import tcc.pairings.graph.networks.FlightNetworkNodeLabel;
import tcc.pairings.graph.networks.FlightNetworkPath;
import tcc.pairings.io.Outputer;

public class PairingsGenerator {
	private FlightNetwork net;
	private Outputer[] outputers;
	private int numberOfPairings;
	private String base;
	private FlightNetworkPath path;
	private Node<Leg> source;
	private Node<Leg> sink;
	
	public int getNumberOfPairings() {
		return numberOfPairings;
	}
	
	public void setNumberOfPairings(int numberOfPairings) {
		this.numberOfPairings = numberOfPairings;
	}
	
	public PairingsGenerator(FlightNetwork net, Outputer[] outputers) {
		this.net = net;
		this.outputers = outputers;
		numberOfPairings = 0;
	}
		
	public void generate(String base) {
		initialSetUp(base);
		addSourceAndSink();
		findPairings(source);
		removeSourceAndSink();
	}

	private void initialSetUp(String base) {
		this.base = base;
		path = new FlightNetworkPath();
		setSourceAndSink(base);
	}

	private void setSourceAndSink(String base) {
		Leg sourceLeg = new Leg((short) 0, base, base, null, null);
		Leg sinkLeg = new Leg((short) 0, base, base, null, null);
		source = new Node<Leg>(sourceLeg, null);
		sink = new Node<Leg>(sinkLeg, null);
	}
	
	private void addSourceAndSink() {
		net.addSource(source);
		net.addSink(sink);
	}
	
	private void removeSourceAndSink() {
		net.removeNode(source);
		net.removeNode(sink);
	}

	private void findPairings(Node<Leg> node) {
		for (Edge<Leg> edge: node.getEdges())
			exploreTroughEdge(edge);		
	}

	private void exploreTroughEdge(Edge<Leg> edge) {
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
		String tail = edge.getIn().getInfo().getTail();
		path.addNewDuty(flightTime, tail);
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
		String tail = edge.getIn().getInfo().getTail();
		path.addConnection(flightTime, sitTime, tail);
		path.addEdge(edge);
	}
	
	private void removeConnectionFromPath(Edge<Leg> edge) {
		String tail = edge.getOut().getInfo().getTail();
		int flightTime = ((FlightNetworkNodeLabel) edge.getIn().getLabel()).getFlightTime();
		int sitTime = ((FlightNetworkEdgeLabel) edge.getLabel()).getSitTime();
		path.removeConnection(flightTime, sitTime, tail);
		path.removeEdge();
	}
	
	private void exploreTroughOvernight(Edge<Leg> edge) {
		if (Rules.isPossibleToAppendOvernight(path, edge, base)) {
			DutyData dutyData = path.getDutyData().clone();
			String tail = path.getTail();
			addNewDutyToPath(edge);	
			findPairings(edge.getIn());
			removeOvernightFromPath(dutyData, tail);
		}
	}

	private void removeOvernightFromPath(DutyData dutyData, String tail) {
		path.removeOvernight(dutyData, tail);
		path.removeEdge();
	}
	
	private void incrementNumberOfPairingsAndOutput() {
		++numberOfPairings;
		if (outputers != null)
			output();
	}
	
	private void output() {
		Pairing pairing = new Pairing(numberOfPairings, path);
		for (Outputer outputer: outputers) 
			outputer.output(pairing);
	}
}
