package pairings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pairings.graph.Edge;
import pairings.graph.Node;
import pairings.graph.networks.FlightNetwork;
import pairings.graph.networks.FlightNetworkEdgeLabel;
import pairings.graph.networks.FlightNetworkNodeLabel;
import pairings.graph.networks.FlightNetworkPath;

public class PairingsGenerator {
	private FlightNetwork net;
	private List<Pairing> pairings;
	private FlightNetworkPath path;
	private String base;

	public PairingsGenerator(FlightNetwork net) {
		this.net = net;
		pairings = new ArrayList<Pairing>();
	}
		
	public List<Pairing> getPairings(String base, Date start) {
		this.base = base;
		Leg sourceLeg = new Leg(0, base, base, start, start);
		Node<Leg> source = new Node<Leg>(sourceLeg, null);
		Leg sinkLeg = new Leg(0, base, base, null, null);
		Node<Leg> sink = new Node<Leg>(sinkLeg, null);
		net.addSource(source);
		net.addSink(sink);
		path = new FlightNetworkPath();
		findPairing(source);
		return pairings;
	}

	private void findPairing(Node<Leg> node) {
		for (Edge<Leg> edge: node.getEdges())
			exploreTroughEdge(edge);		
	}

	private void exploreTroughEdge(Edge<Leg> edge) {
		FlightNetworkNodeLabel nodeLabel = (FlightNetworkNodeLabel) edge.getIn().getLabel();
		switch (edge.getType()) {
		case FROM_SOURCE:
			exploreTroughSourceEdges(edge, nodeLabel);
			break;
		case CONNECTION:
			exploreTroughConnection(edge, nodeLabel);
			break;
		case OVERNIGHT:
			exploreTroughOvernight(edge, nodeLabel);
			break;
		case TO_SINK:
			pairings.add(new Pairing(path));
			break;
		}
	}

	private void exploreTroughConnection(Edge<Leg> edge, FlightNetworkNodeLabel nodeLabel) {
		if (!path.hasSameLegNumber(edge.getIn().getInfo().getNumber())){				
			FlightNetworkEdgeLabel edgeLabel = (FlightNetworkEdgeLabel) edge.getLabel();
			if (Rules.isPossibleToAppendConnectionEdge(path, nodeLabel, edgeLabel)) {
				addConnectionEdgeToPath(edge, nodeLabel, edgeLabel);
				findPairing(edge.getIn());
				removeConnectionEdgeFromPath(nodeLabel, edgeLabel);
			}
		}
	}

	private void addConnectionEdgeToPath(Edge<Leg> edge, FlightNetworkNodeLabel nodeLabel, FlightNetworkEdgeLabel edgeLabel) {
		path.incrementDutyTime(edgeLabel.getSitTime() + nodeLabel.getFlightTime());
		path.incrementFlightTime(nodeLabel.getFlightTime());
		path.incrementNumberOfLegs();
		path.addEdge(edge);
	}
	
	private void removeConnectionEdgeFromPath(FlightNetworkNodeLabel nodeLabel, FlightNetworkEdgeLabel edgeLabel) {
		path.removeEdge();
		path.decrementNumberOfLegs();
		path.decrementFlightTime(nodeLabel.getFlightTime());
		path.decrementDutyTime(edgeLabel.getSitTime() + nodeLabel.getFlightTime());
	}
	
	private void exploreTroughOvernight(Edge<Leg> edge, FlightNetworkNodeLabel nodeLabel) {
		if(!path.hasSameLegNumber(edge.getIn().getInfo().getNumber()))
			if(Rules.isPossibleToAppendOvernightEdge(path, edge, base)) {
				int numberOfLegs = path.getNumberOfLegs();
				int dutyTime = path.getDutyTime();
				int flightTime = path.getFlightTime();
				addNewDutyToPath(edge, nodeLabel);	
				findPairing(edge.getIn());
				removeOvernightEdgeFromPath(numberOfLegs, dutyTime, flightTime);
			}
	}

	private void addNewDutyToPath(Edge<Leg> edge, FlightNetworkNodeLabel nodeLabel) {
		path.setDutyTime(nodeLabel.getFlightTime());
		path.setFlightTime(nodeLabel.getFlightTime());
		path.setNumberOfLegs(1);
		path.incrementNumberOfDuties();
		path.addEdge(edge);
	}

	private void removeOvernightEdgeFromPath(int numberOfLegs, int dutyTime, int flightTime) {
		path.removeEdge();
		path.decrementNumberOfDuties();
		path.setNumberOfLegs(numberOfLegs);
		path.setDutyTime(dutyTime);
		path.setFlightTime(flightTime);
	}
	
	private void exploreTroughSourceEdges(Edge<Leg> edge, FlightNetworkNodeLabel nodeLabel) {
		addNewDutyToPath(edge, nodeLabel);	
		findPairing(edge.getIn());
		resetPath();
	}

	private void resetPath() {
		path.removeEdge();
		path.decrementNumberOfDuties();
		path.setNumberOfLegs(0);
		path.setDutyTime(0);
		path.setFlightTime(0);
	}
}
