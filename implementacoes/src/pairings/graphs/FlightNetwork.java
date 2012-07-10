package pairings.graphs;

import java.util.Calendar;
import java.util.List;

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
		addFlightLegs();
		addFlightLegsConnections();
	}

	private void addFlightLegs() {
		for(Leg leg: legs) 
			addSameFlightLegInSubsequentDays(leg);
	}

	private void addSameFlightLegInSubsequentDays(Leg leg) {
		for (int i = 0; i < Rules.MAX_DAYS_PER_PAIRING; i++) {
			addNode(new Node<Leg>(leg, new Label(id++)));
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
	
	// Esta função pode ser escrita de forma mais eficiente comparando-se
	// primeiro cidade de origem e destino.
	private void addFlightLegsConnections() {
		for (Node<Leg> out: nodes)
			for (Node<Leg> in: nodes)
				if (legsCanBeConnected(out.getContent(), in.getContent()))
					addEdge(out, in, null);
	}

	// Esta função precisa ser refeita levando em conta os tempos mínimos
	// e máximo de de conexão.
	private boolean legsCanBeConnected(Leg legFrom, Leg legTo) {
		boolean datesOk = legFrom.getArrival().before(legTo.getDeparture());
		boolean citiesOk = legFrom.getTo().contentEquals(legTo.getFrom());
		return datesOk && citiesOk;
	}
}