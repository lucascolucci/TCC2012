package pairings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pairings.graph.Edge;
import pairings.graph.EdgeType;
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
		path = new FlightNetworkPath(source);
		findPairing(source);
		return pairings;
	}

	private void findPairing(Node<Leg> node) {
		for (Edge<Leg> edge: node.getEdges()) {
			FlightNetworkNodeLabel nodeLabel = (FlightNetworkNodeLabel) edge.getIn().getLabel();
			if (edge.getType() == EdgeType.TO_SINK) {
				pairings.add(new Pairing(path));
			}
			else if (edge.getType() == EdgeType.CONNECTION && !path.hasSameLegNumber(edge.getIn().getInfo().getNumber())) {
				if (nodeLabel.getFlightTime() + path.getFlightTime() <= Rules.MAX_FLIGHT_TIME) {
					FlightNetworkEdgeLabel edgeLabel = (FlightNetworkEdgeLabel) edge.getLabel();
					if (edgeLabel.getSitTime() + nodeLabel.getFlightTime() +
							path.getDutyTime() <= Rules.MAX_DUTY_TIME) 
						if (path.getNumberOfLegs() + 1 <= Rules.MAX_LEGS) {
							path.incrementDutyTime(edgeLabel.getSitTime() + nodeLabel.getFlightTime());
							path.incrementFlightTime(nodeLabel.getFlightTime());
							path.incrementNumberOfLegs();
							path.addEdge(edge);
							findPairing(edge.getIn());
							path.removeEdge();
							path.decrementNumberOfLegs();
							path.decrementFlightTime(nodeLabel.getFlightTime());
							path.decrementDutyTime(edgeLabel.getSitTime() + nodeLabel.getFlightTime());
						}
				}
			}
			else if (edge.getType() == EdgeType.OVERNIGHT && !path.hasSameLegNumber(edge.getIn().getInfo().getNumber())) {
				if(edge.getOut().getInfo().getTo() != base){
					if (path.getNumberOfDuties() + 1 <= Rules.MAX_DUTIES) {
						int numberOfLegs = path.getNumberOfLegs();
						int dutyTime = path.getDutyTime();
						int flightTime = path.getFlightTime();
						path.setDutyTime(nodeLabel.getFlightTime());
						path.setFlightTime(nodeLabel.getFlightTime());
						path.setNumberOfLegs(1);
						path.incrementNumberOfDuties();
						path.addEdge(edge);	
						findPairing(edge.getIn());
						path.removeEdge();
						path.decrementNumberOfDuties();
						path.setNumberOfLegs(numberOfLegs);
						path.setDutyTime(dutyTime);
						path.setFlightTime(flightTime);
					}
				}
			}
			else if (edge.getType() == EdgeType.FROM_SOURCE) {
				path.setDutyTime(nodeLabel.getFlightTime());
				path.setFlightTime(nodeLabel.getFlightTime());
				path.setNumberOfLegs(1);
				path.incrementNumberOfDuties();
				path.addEdge(edge);	
				findPairing(edge.getIn());
				path.removeEdge();
				path.decrementNumberOfDuties();
				path.setNumberOfLegs(0);
				path.setDutyTime(0);
				path.setFlightTime(0);		
			}
		}
	}
}
