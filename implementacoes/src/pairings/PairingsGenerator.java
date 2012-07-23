package pairings;

import pairings.graph.Edge;
import pairings.graph.Node;
import pairings.graph.networks.FlightNetwork;
import pairings.graph.networks.FlightNetworkEdgeLabel;
import pairings.graph.networks.FlightNetworkNodeLabel;
import pairings.graph.networks.FlightNetworkPath;
import pairings.io.Outputer;

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
		Leg sourceLeg = new Leg(0, base, base, null, null);
		Leg sinkLeg = new Leg(0, base, base, null, null);
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
		path.setFlightTime(flightTime);
		path.setDutyTime(flightTime);
		path.setNumberOfLegs(1);
		path.incrementNumberOfDuties();
		path.addEdge(edge);
	}

	private void resetPath() {
		path.removeEdge();
		path.setNumberOfDuties(0);
		path.setNumberOfLegs(0);
		path.setDutyTime(0);
		path.setFlightTime(0);
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
		path.incrementFlightTime(flightTime);
		path.incrementDutyTime(flightTime + sitTime);
		path.incrementNumberOfLegs();
		path.addEdge(edge);
	}
	
	private void removeConnectionFromPath(Edge<Leg> edge) {
		int flightTime = ((FlightNetworkNodeLabel) edge.getIn().getLabel()).getFlightTime();
		int sitTime = ((FlightNetworkEdgeLabel) edge.getLabel()).getSitTime();
		path.removeEdge();
		path.decrementNumberOfLegs();
		path.decrementDutyTime(flightTime + sitTime);
		path.decrementFlightTime(flightTime);
	}
	
	private void exploreTroughOvernight(Edge<Leg> edge) {
		if(Rules.isPossibleToAppendOvernight(path, edge, base)) {
			int numberOfLegs = path.getNumberOfLegs();
			int dutyTime = path.getDutyTime();
			int flightTime = path.getFlightTime();
			addNewDutyToPath(edge);	
			findPairings(edge.getIn());
			removeOvernightFromPath(numberOfLegs, dutyTime, flightTime);
		}
	}

	private void removeOvernightFromPath(int numberOfLegs, int dutyTime, int flightTime) {
		path.removeEdge();
		path.decrementNumberOfDuties();
		path.setNumberOfLegs(numberOfLegs);
		path.setDutyTime(dutyTime);
		path.setFlightTime(flightTime);
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
