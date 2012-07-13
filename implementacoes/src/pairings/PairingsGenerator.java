package pairings;

import java.util.ArrayList;
import java.util.List;

import pairings.graphs.Edge;
import pairings.graphs.EdgeType;
import pairings.graphs.FlightNetwork;
import pairings.graphs.FlightNetworkEdgeLabel;
import pairings.graphs.Label;
import pairings.graphs.Node;

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
		Node<Leg> source = new Node<Leg>(sourceLeg, new Label(-1));
		Leg sinkLeg = new Leg(0, base, base, null, null);
		Node<Leg> sink = new Node<Leg>(sinkLeg, new Label(-2));
		net.addSource(source);
		net.addSink(sink);
		path = new FlightNetworkPath(source);
		
		findPairing(source);
		
		return null;
	}

	private void findPairing(Node<Leg> node) {
		for (Edge<Leg> edge: node.getEdges()) {
			if (edge.getType() == EdgeType.TO_SINK) {
				pairings.add(new Pairing(path));
			}
			else if (edge.getType() == EdgeType.CONNECTION) {
				if (edge.getIn().getContent().getFlightTime() + path.getFlightTime() <= Rules.MAX_FLIGHT_TIME){
					FlightNetworkEdgeLabel label = (FlightNetworkEdgeLabel) edge.getLabel();
					if (label.getSitTime() + edge.getIn().getContent().getFlightTime() +
							path.getDutyTime() <= Rules.MAX_DUTY_TIME) 
						if(path.getNumberOfLegs() + 1 <= Rules.MAX_LEGS){
							path.incrementDutyTime(label.getSitTime() + edge.getIn().getContent().getFlightTime());
							path.incrementFlightTime(edge.getIn().getContent().getFlightTime());
							path.incrementNumberOfLegs();
							path.addEdge(edge);
							findPairing(edge.getIn());
						}
				}
			}
			else if (edge.getType() == EdgeType.OVERNIGHT) {
				if(path.getNumberOfDuties() + 1 <= Rules.MAX_DUTIES){
					path.setDutyTime(edge.getIn().getContent().getFlightTime());
					path.setFlightTime(edge.getIn().getContent().getFlightTime());
					path.setNumberOfLegs(1);
					path.incrementNumberOfDuties();
					path.addEdge(edge);	
					findPairing(edge.getIn());
				}
			}	
		}
	}
}
