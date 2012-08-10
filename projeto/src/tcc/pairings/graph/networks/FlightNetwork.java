package tcc.pairings.graph.networks;

import java.util.Date;
import java.util.List;

import tcc.DateUtil;
import tcc.pairings.Leg;
import tcc.pairings.Rules;
import tcc.pairings.graph.EdgeType;
import tcc.pairings.graph.Graph;
import tcc.pairings.graph.Node;

public class FlightNetwork extends Graph<Leg> {
	private List<Leg> legs;

	public List<Leg> getLegs() {
		return legs;
	}
	
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
		int flightTime = evaluateFlightTime(leg); 
		for (int day = 1; day <= Rules.MAX_DUTIES; day++) {
			addNode(new Node<Leg>(leg, new FlightNetworkNodeLabel(flightTime, day)));
			leg = getNextDayLeg(leg);
		}
	}
	
	private int evaluateFlightTime(Leg leg) {
		Date departure = leg.getDeparture();
		Date arrival = leg.getArrival();
		if (departure != null && arrival != null) 
			return DateUtil.difference(departure, arrival);
		return 0;
	}
	
	private Leg getNextDayLeg(Leg leg) {
		short number = leg.getNumber();
		String from = leg.getFrom();
		String to = leg.getTo();
		Date departure = DateUtil.addOneDay(leg.getDeparture());
		Date arrival = DateUtil.addOneDay(leg.getArrival());
		short track = leg.getTrack(); // Possível falha na repetição da malha. 
		return new Leg(number, from, to, departure, arrival, track);
	}

	private void addLegsConnections() {
		for (Node<Leg> out: nodes) 
			for (Node<Leg> in: nodes) 
				addConnectionIfApplicable(out, in);
	}
	
	private void addConnectionIfApplicable(Node<Leg> out, Node<Leg> in) {
		String to = out.getInfo().getTo();
		String from = in.getInfo().getFrom();
		if (to.contentEquals(from))
			addConnectionIfTimeCompatible(out, in);
	}
	
	private void addConnectionIfTimeCompatible(Node<Leg> out, Node<Leg> in) {
		Date arrival = out.getInfo().getArrival();
		Date departure = in.getInfo().getDeparture();
		if (arrival.before(departure)) 
			addProperEdge(out, in, DateUtil.difference(arrival, departure));
	}

	private void addProperEdge(Node<Leg> out, Node<Leg> in, int sit) {
		if (Rules.sitTimeCheck(sit))
			addEdge(out, in, EdgeType.CONNECTION, new FlightNetworkEdgeLabel(sit));
		else if (Rules.restTimeCheck(sit))
			addEdge(out, in, EdgeType.OVERNIGHT, new FlightNetworkEdgeLabel(sit));
	}
	
	public void addSource(SpecialNode source) {
		for (Node<Leg> node: nodes)
			for (String airport: source.getAirports())
				if (shouldBeConnectedToSource(node, airport)) {
					source.addNeighbor(node, EdgeType.FROM_SOURCE);
					numberOfEdges++;
				}
		addNode(source);
	}

	private boolean shouldBeConnectedToSource(Node<Leg> node, String airport) {
		if (node.getClass() != SpecialNode.class) {
			int day = ((FlightNetworkNodeLabel) node.getLabel()).getDay();
			return (day == 1) && node.getInfo().getFrom().contentEquals(airport);
		}
		return false;
	}
	
	public void addSink(SpecialNode sink) {
		for (Node<Leg> node: nodes)
			for (String airport: sink.getAirports())
				if (shouldBeConnectedToSink(node, airport)) {
					node.addNeighbor(sink, EdgeType.TO_SINK);
					numberOfEdges++;
				}
		addNode(sink);
	}

	private boolean shouldBeConnectedToSink(Node<Leg> node, String airport) {
		if (node.getClass() != SpecialNode.class)
			return node.getInfo().getTo().contentEquals(airport);
		return false;
	}
}
