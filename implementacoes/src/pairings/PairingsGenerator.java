package pairings;

import java.util.ArrayList;
import java.util.List;

import pairings.graph.Edge;
import pairings.graph.EdgeType;
import pairings.graph.Node;
import pairings.graph.networks.FlightNetwork;
import pairings.graph.networks.FlightNetworkEdgeLabel;
import pairings.graph.networks.FlightNetworkPath;

public class PairingsGenerator {
	private FlightNetwork net;
	private List<Pairing> pairings;
	private FlightNetworkPath path;

	public PairingsGenerator(FlightNetwork net) {
		this.net = net;
		pairings = new ArrayList<Pairing>();
	}
		
	public List<Pairing> getPairings(String base) {
		Leg sourceLeg = new Leg(0, base, base, null, null);
		Node<Leg> source = new Node<Leg>(sourceLeg);
		Leg sinkLeg = new Leg(0, base, base, null, null);
		Node<Leg> sink = new Node<Leg>(sinkLeg);
		net.addSource(source);
		net.addSink(sink);
		path = new FlightNetworkPath(source);
		findPairing(source);
		return pairings;
	}

	private void findPairing(Node<Leg> node) {
		for (Edge<Leg> edge: node.getEdges()) {
			if (edge.getType() == EdgeType.TO_SINK) {
				pairings.add(new Pairing(path));
			}
			else if (edge.getType() == EdgeType.CONNECTION) {
				if (edge.getIn().getInfo().getFlightTime() + path.getFlightTime() <= Rules.MAX_FLIGHT_TIME) {
					FlightNetworkEdgeLabel label = (FlightNetworkEdgeLabel) edge.getLabel();
					if (label.getSitTime() + edge.getIn().getInfo().getFlightTime() +
							path.getDutyTime() <= Rules.MAX_DUTY_TIME) 
						if(path.getNumberOfLegs() + 1 <= Rules.MAX_LEGS){
							path.incrementDutyTime(label.getSitTime() + edge.getIn().getInfo().getFlightTime());
							path.incrementFlightTime(edge.getIn().getInfo().getFlightTime());
							path.incrementNumberOfLegs();
							path.addEdge(edge);
							findPairing(edge.getIn());
						}
				}
			}
			else if (edge.getType() == EdgeType.OVERNIGHT) {
				if(path.getNumberOfDuties() + 1 <= Rules.MAX_DUTIES) {
					path.setDutyTime(edge.getIn().getInfo().getFlightTime());
					path.setFlightTime(edge.getIn().getInfo().getFlightTime());
					path.setNumberOfLegs(1);
					path.incrementNumberOfDuties();
					path.addEdge(edge);	
					findPairing(edge.getIn());
				}
			}	
		}
	}
}
