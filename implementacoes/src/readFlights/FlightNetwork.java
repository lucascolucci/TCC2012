package readFlights;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import readFlights.graph.Graph;
import readFlights.graph.Node;

public class FlightNetwork extends Graph {
	private List<FlightLeg> legsList;
	private int id;
	
	public FlightNetwork(List<FlightLeg> legsList) {
		super();
		this.legsList = legsList;
		id = 0;
	}

	public void build() throws Exception {
		addFlightLegs();
		addFlightLegsConnections();
	}

	private void addFlightLegs() throws Exception {
		Iterator<FlightLeg> it = legsList.iterator();
		while (it.hasNext()) 
			addSameFlightLegInSubsequentDays(it.next());
	}

	private void addSameFlightLegInSubsequentDays(FlightLeg leg) throws Exception {
		for (int i = 0; i < Rules.MAX_DAYS_PER_PAIRING; i++) {
			addNode(new Node(leg, id++));
			addOneDay(leg);
		}
	}

	private void addOneDay(FlightLeg leg){
		addOneDayToDeparture(leg);
		addOneDayToArrival(leg);
	}

	private void addOneDayToDeparture(FlightLeg leg) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(leg.getDeparture());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		leg.setDeparture(calendar.getTime());
	}
	
	private void addOneDayToArrival(FlightLeg leg) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(leg.getArrival());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		leg.setArrival(calendar.getTime());
	}
	
	private void addFlightLegsConnections() throws Exception {
		Iterator<Node> itOuter = nodeList.iterator();
		while (itOuter.hasNext()) {
			Node from = itOuter.next();
			Iterator<Node> itInner = nodeList.iterator();
			while (itInner.hasNext()) {
				Node to = itInner.next();
				if (legsCanBeConnected(from.getFlightLeg(), to.getFlightLeg()))
					addEdge(from, to);
			}
		}
	}

	// Esta função precisa ser refeita levando em conta os tempos mínimos
	// e máximo de de conexão.
	private boolean legsCanBeConnected(FlightLeg legFrom, FlightLeg legTo) {
		boolean datesOk = legFrom.getArrival().before(legTo.getDeparture());
		boolean citiesOk = legFrom.getTo().contentEquals(legTo.getFrom());
		return datesOk && citiesOk;
	}
}
