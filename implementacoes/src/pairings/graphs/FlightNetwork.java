package pairings.graphs;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import pairings.Leg;
import pairings.Rules;


public class FlightNetwork extends Graph {
	private List<Leg> legsList;
	private int id;
	
	public FlightNetwork(List<Leg> legsList) {
		super();
		this.legsList = legsList;
		id = 0;
	}

	public void build() throws Exception {
		addFlightLegs();
		addFlightLegsConnections();
	}

	private void addFlightLegs() throws Exception {
		Iterator<Leg> it = legsList.iterator();
		while (it.hasNext()) 
			addSameFlightLegInSubsequentDays(it.next());
	}

	private void addSameFlightLegInSubsequentDays(Leg leg) throws Exception {
		for (int i = 0; i < Rules.MAX_DAYS_PER_PAIRING; i++) {
			addNode(new Node(leg, id++));
			addOneDay(leg);
		}
	}

	private void addOneDay(Leg leg){
		addOneDayToDeparture(leg);
		addOneDayToArrival(leg);
	}

	private void addOneDayToDeparture(Leg leg) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(leg.getDeparture());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		leg.setDeparture(calendar.getTime());
	}
	
	private void addOneDayToArrival(Leg leg) {
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
	private boolean legsCanBeConnected(Leg legFrom, Leg legTo) {
		boolean datesOk = legFrom.getArrival().before(legTo.getDeparture());
		boolean citiesOk = legFrom.getTo().contentEquals(legTo.getFrom());
		return datesOk && citiesOk;
	}
}
