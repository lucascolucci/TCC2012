package pairings;

import pairings.graph.Edge;
import pairings.graph.networks.FlightNetworkEdgeLabel;
import pairings.graph.networks.FlightNetworkNodeLabel;
import pairings.graph.networks.FlightNetworkPath;

public class Rules {
	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	public static final int MAX_DUTIES = 4;
	public static final int MIN_SIT_TIME = 30;
	public static final int MAX_SIT_TIME = 120;  // 02 horas
	public static final int MIN_REST_TIME = 720;  // 12 horas
	public static final int MAX_REST_TIME = 2880; // 48 horas
	public static final int MAX_FLIGHT_TIME = 570;  // 9.5 horas
	public static final int MAX_DUTY_TIME = 690;  // 11.5 horas;
	public static final int MAX_LEGS = 5;
	
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
