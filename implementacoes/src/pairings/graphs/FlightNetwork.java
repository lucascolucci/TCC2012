package pairings.graphs;

import java.util.Date;
import java.util.List;

import pairings.DateUtil;
import pairings.Leg;
import pairings.Rules;

public class FlightNetwork extends Graph<Leg> {
	private List<Leg> legs;
	private int id;
	
	public FlightNetwork(List<Leg> legs) {
		super();
		this.legs = legs;
		id = 0;
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
			addNode(new Node<Leg>(leg, new Label(id++)));
			leg = getNextDayLeg(leg);
		}
	}
	
	private Leg getNextDayLeg(Leg leg) {
		int number = leg.getNumber();
		String from = leg.getFrom();
		String to = leg.getTo();
		Date departure = DateUtil.addOneDay(leg.getDeparture());
		Date arrival = DateUtil.addOneDay(leg.getArrival());
		String tail = leg.getTail();
		return new Leg(number, from, to, departure, arrival, tail);
	}

	private void addLegsConnections() {
		for (Node<Leg> out: nodes) 
			for (Node<Leg> in: nodes) 
				addConnectionIfSpaceAndTimeCompatible(out, in);
	}
	
	private void addConnectionIfSpaceAndTimeCompatible(Node<Leg> out, Node<Leg> in) {
		String to = out.getContent().getTo();
		String from = in.getContent().getFrom();
		if (to == from) 
			addConnectionIfTimeCompatible(out, in);
	}
	
	private void addConnectionIfTimeCompatible(Node<Leg> out, Node<Leg> in) {
		Date arrival = out.getContent().getArrival();
		Date departure = in.getContent().getDeparture();
		if (arrival.before(departure)) {
			int delta = DateUtil.differenceInMinutes(arrival, departure);
			//TODO mudar os ids
			FlightNetworkEdgeLabel label = new FlightNetworkEdgeLabel(0, delta);
			if (Rules.isLegalSitTime(delta)) 
				addEdge(out, in, EdgeType.CONNECTION, label);
			else if (Rules.isLegalRestTime(delta))
				addEdge(out, in, EdgeType.OVERNIGHT, label);
		}
	}
	
	public void addSource(Node<Leg> source) {
		String base = source.getContent().getFrom();
		for (Node<Leg> node: nodes)
			if (node.getContent().getFrom() == base) 
				source.addNeighbor(node, EdgeType.FROM_SOURCE);
	}
	
	public void addSink(Node<Leg> sink) {
		String base = sink.getContent().getFrom();
		for (Node<Leg> node: nodes)
			if (node.getContent().getTo() == base) 
				node.addNeighbor(sink, EdgeType.TO_SINK);
	}
}