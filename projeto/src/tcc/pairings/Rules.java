package tcc.pairings;

import tcc.pairings.graph.Edge;
import tcc.pairings.graph.networks.FlightNetworkEdgeLabel;
import tcc.pairings.graph.networks.FlightNetworkNodeLabel;
import tcc.pairings.graph.networks.FlightNetworkPath;

public class Rules {
	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	public static final int MAX_DUTIES = 4;
	public static final int MIN_SIT_TIME = 30;
	public static final int MAX_SIT_TIME = 120; // 02 horas
	public static final int MIN_REST_TIME = 720; // 12 horas
	public static final int MAX_REST_TIME = 2160; // 36 horas
	public static final int MAX_FLIGHT_TIME = 570; // 9.5 horas
	public static final int MAX_DUTY_TIME = 690; // 11.5 horas
	public static final int MAX_LEGS = 5;
	
	public static boolean isPairingLegal(Pairing pairing, String base) {
		if (pairing != null && pairing.getNumberOfDuties() <= MAX_DUTIES) {
			if (originDestinationCheck(pairing, base))
				return pairingDutiesCheck(pairing);
		}
		return false;
	}

	private static boolean originDestinationCheck(Pairing pairing, String base) {
		String from = pairing.getFirstLeg().getFrom();
		String to = pairing.getLastLeg().getTo();
		return from.contentEquals(base) && to.contentEquals(to);
	}

	private static boolean pairingDutiesCheck(Pairing pairing) {
		Duty previous = null;
		for (Duty duty: pairing.getDuties()) {
			if (!isDutyLegal(duty)) 
				return false;
			if (previous != null) {
				int sit = DateUtil.difference(previous.getLastLeg().getArrival(), duty.getFirstLeg().getDeparture());
				if (!restTimeCheck(sit)) 
					return false;
			}
		}
		return true;
	}

	private static boolean isDutyLegal(Duty duty) {
		if (duty != null && duty.getNumberOfLegs() <= MAX_LEGS) {
			if (duty.getFlightTime() <= MAX_FLIGHT_TIME) 
				if (duty.getDutyTime() <= MAX_DUTY_TIME) 
					return checkDutyConnections(duty);
		}
		return false;
	}

	private static boolean checkDutyConnections(Duty duty) {
		Leg previous = null;
		for (Leg leg: duty.getLegs()) {
			if (previous != null) {
				int sit = DateUtil.difference(previous.getArrival(), leg.getDeparture());
				if (!sitTimeCheck(sit))
					return false;
			}
			previous = leg;
		}
		return true;
	}
	
	public static boolean sitTimeCheck(int sit) {
		return (sit >= Rules.MIN_SIT_TIME && sit <= Rules.MAX_SIT_TIME);
	}
	
	public static boolean restTimeCheck(int sit) {
		return (sit >= Rules.MIN_REST_TIME && sit <= Rules.MAX_REST_TIME);
	}
	
	public static boolean isPossibleToAppendConnection(FlightNetworkPath path, Edge<Leg> edge) {
		if (!path.hasSameLegNumber(edge.getIn().getInfo().getNumber())) {
			int flightTime = ((FlightNetworkNodeLabel) edge.getIn().getLabel()).getFlightTime();
			int sitTime = ((FlightNetworkEdgeLabel) edge.getLabel()).getSitTime();
			return flightTimeCheck(path, flightTime) && dutyTimeCheck(path, flightTime, sitTime) && numberOfLegsCheck(path);
		}
		return false;
	}
	
	private static boolean flightTimeCheck(FlightNetworkPath path, int flightTime) {
		return path.getFlightTime() + flightTime <= Rules.MAX_FLIGHT_TIME;
	}
	
	private static boolean dutyTimeCheck(FlightNetworkPath path, int flightTime, int sitTime) { 
		return path.getDutyTime() + flightTime + sitTime <= Rules.MAX_DUTY_TIME;
	}
	
	private static boolean numberOfLegsCheck(FlightNetworkPath path) {
		return path.getNumberOfLegs() + 1 <= Rules.MAX_LEGS;	
	}
	
	public static boolean isPossibleToAppendOvernight(FlightNetworkPath path, Edge<Leg> edge, String base) {
		if(!path.hasSameLegNumber(edge.getIn().getInfo().getNumber()))
			if (!edge.getOut().getInfo().getTo().contentEquals(base))
				return numberOfDutiesCheck(path);
		return false;
	}

	private static boolean numberOfDutiesCheck(FlightNetworkPath path) {
		return path.getNumberOfDuties() + 1 <= Rules.MAX_DUTIES;
	}
}
