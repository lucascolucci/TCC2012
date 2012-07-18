package pairings;

import java.util.ArrayList;
import java.util.List;

import pairings.graph.Edge;
import pairings.graph.Node;
import pairings.graph.networks.FlightNetwork;
import pairings.graph.networks.FlightNetworkEdgeLabel;
import pairings.graph.networks.FlightNetworkNodeLabel;
import pairings.graph.networks.FlightNetworkPath;
import pairings.io.PairingsOutputer;

public class PairingsGenerator {
	private FlightNetwork net;
	private List<Pairing> pairings;
	private FlightNetworkPath path;
	private String base;

	public PairingsGenerator(FlightNetwork net) {
		this.net = net;
	}
		
	public List<Pairing> getPairings(String base) {
		this.base = base;
		Leg sourceLeg = new Leg(0, base, base, null, null);
		Node<Leg> source = new Node<Leg>(sourceLeg, null);
		Leg sinkLeg = new Leg(0, base, base, null, null);
		Node<Leg> sink = new Node<Leg>(sinkLeg, null);
		net.addSource(source);
		net.addSink(sink);
		path = new FlightNetworkPath();
		pairings = new ArrayList<Pairing>();
		findPairings(source);
		net.removeNode(sink);
		net.removeNode(source);
		return pairings;
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
			// TODO opcção de saída
			Pairing pairing = new Pairing(path);
			//PairingsOutputer.print(pairing);
			pairings.add(pairing);
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
}
