package readFlights;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import readFlights.graph.Graph;
import readFlights.graph.Node;

public class FlightNetwork extends Graph {
	private List<FlightLeg> legsList;

	public FlightNetwork() {
		super();
		legsList = null;
	}

	public void build(List<FlightLeg> legsList) throws Exception {
		this.legsList = legsList;
		addFlightLegs();
		addFlightLegsConnections();
	}

	private void addFlightLegs() throws Exception {
		Iterator<FlightLeg> it = legsList.iterator();
		int flightLegId = 0;
		
		while (it.hasNext()) {
			FlightLeg leg = it.next();
			addSameFlightLegInSubsequentDays(Rules.MAX_DAYS_PER_PAIRING, leg, flightLegId);
			flightLegId += Rules.MAX_DAYS_PER_PAIRING;
		}
	}

	private void addSameFlightLegInSubsequentDays(int numberOfSubsequentDays, FlightLeg leg, int flightLegId) throws Exception {
		Node node;
		int id = flightLegId;

		for (int i = 0; i < numberOfSubsequentDays; i++) {
			node = new Node(leg, id++);
			addNode(node);
			leg = addOneDay(leg);
			System.out.println(node.getFlightLeg().getArrival().getDay());
		}
	}

	private FlightLeg addOneDay(FlightLeg leg){
		Calendar calendar = Calendar.getInstance();
		Date departure = leg.getDeparture();
		
		calendar.setTime(departure);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		departure = calendar.getTime();
		leg.setDeparture(departure);

		Date arrival = leg.getArrival();
		calendar.setTime(arrival);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		arrival = calendar.getTime();
		leg.setArrival(arrival);
		
		return leg;
	}
	
	private void addFlightLegsConnections() throws Exception {
		Iterator<Node> it_1 = nodeList.iterator();
		Iterator<Node> it_2;
		Node from, to;
		
		while (it_1.hasNext()) {
			from = it_1.next();
			it_2 = nodeList.iterator();
			while (it_2.hasNext()) {
				to = it_2.next();
				if (legsCanBeConnected(from.getFlightLeg(), to.getFlightLeg()))
					addEdge(from, to);
			}
		}
	}

	private boolean legsCanBeConnected(FlightLeg legA, FlightLeg legB) {
		boolean datesOk = legA.getArrival().before(legB.getDeparture());
		boolean citiesOk = legA.getTo().contentEquals(legB.getFrom());

		return datesOk && citiesOk;
	}
}
