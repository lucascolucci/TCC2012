package pairings.graph.networks;

import java.util.Date;
import java.util.List;

import pairings.DateUtil;
import pairings.Leg;
import pairings.Rules;
import pairings.graph.EdgeType;
import pairings.graph.Graph;
import pairings.graph.Node;

public class FlightNetwork extends Graph<Leg> {
	private List<Leg> legs;
	
	public FlightNetwork(List<Leg> legs) {
		super();
		this.legs = legs;
	}

	public void build() {
		addLegs();
		addLegsConnections();
	}

	private void addLegs() {
		for(Leg leg: legs) 
			addSameLegInSubsequentDays(leg);
	}

	private void addSameLegInSubsequentDays(Leg leg) {
		for (int i = 0; i < Rules.MAX_DUTIES; i++) {
			addNode(new Node<Leg>(leg));
			leg = getNextDayLeg(leg);
		}
	}
	
	private Leg getNextDayLeg(Leg leg) {
		int number = leg.getNumber();
		String from = leg.getFrom();
		String to = leg.getTo();
		Date departure = DateUtil.addOneDay(leg.getDeparture());
		Date arrival = DateUtil.addOneDay(leg.getArrival());
		return new Leg(number, from, to, departure, arrival);
	}

	private void addLegsConnections() {
		for (Node<Leg> out: nodes) 
			for (Node<Leg> in: nodes) 
				addConnectionIfApplicable(out, in);
	}
	
	private void addConnectionIfApplicable(Node<Leg> out, Node<Leg> in) {
		String to = out.getInfo().getTo();
		String from = in.getInfo().getFrom();
		if (to == from) 
			addConnectionIfTimeCompatible(out, in);
	}
	
	private void addConnectionIfTimeCompatible(Node<Leg> out, Node<Leg> in) {
		Date arrival = out.getInfo().getArrival();
		Date departure = in.getInfo().getDeparture();
		if (arrival.before(departure))
			addProperEdge(out, in, arrival, departure);
	}

	private void addProperEdge(Node<Leg> out, Node<Leg> in, Date arrival, Date departure) {
		int sit = DateUtil.difference(arrival, departure);
		FlightNetworkEdgeLabel label = new FlightNetworkEdgeLabel(sit);
		if (Rules.isLegalSitTime(sit)) 
			addEdge(out, in, EdgeType.CONNECTION, label);
		else if (Rules.isLegalRestTime(sit))
			addEdge(out, in, EdgeType.OVERNIGHT, label);
	}
	
	public void addSource(Node<Leg> source) {
		String base = source.getInfo().getFrom();
		for (Node<Leg> node: nodes)
			if (node.getInfo().getFrom() == base) 
				source.addNeighbor(node, EdgeType.FROM_SOURCE);
	}
	
	public void addSink(Node<Leg> sink) {
		String base = sink.getInfo().getFrom();
		for (Node<Leg> node: nodes)
			if (node.getInfo().getTo() == base) 
				node.addNeighbor(sink, EdgeType.TO_SINK);
	}

	public void removeSource(Node<Leg> source) {
		// TODO
	}
	
	public void removeSink(Node<Leg> sink) {
		// TODO
	}
}