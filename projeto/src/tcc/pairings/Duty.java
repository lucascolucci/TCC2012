package tcc.pairings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tcc.pairings.rules.Rules;
import tcc.util.DateUtil;

public class Duty {
	private List<DutyLeg> legs;
	
	public List<DutyLeg> getLegs() {
		return legs;
	}
	
	public Duty() {
		legs = new ArrayList<DutyLeg>();
	}
	
	public int getFlightTime() {
		int total = 0;
		for (DutyLeg leg: legs)
			if (!leg.isDeadHead())
				total += leg.getFlightTime();
		return total;
	}
	
	public int getDutyTime() {
		Date departure = legs.get(0).getDeparture();
		Date arrival = legs.get(legs.size() - 1).getArrival();
		return DateUtil.difference(departure, arrival);
	}
	
	public int getNumberOfLegs() {
		return legs.size();
	}
	
	public int getNumberOfTracks() {
		int count = 0; short track = -1;
		for (int i = 0; i < legs.size(); i++) 
			if (track != legs.get(i).getTrack()) {
				track = legs.get(i).getTrack();
				count++;
			}
		return count;	
	}
	
	public void addLeg(DutyLeg leg) {
		legs.add(leg);
	}
	
	public DutyLeg getFirstLeg() {
		if (!legs.isEmpty()) 
			return legs.get(0);
		return null;
	}
	
	public DutyLeg getLastLeg() {
		if (!legs.isEmpty()) 
			return legs.get(legs.size() - 1);
		return null;
	}

	public boolean contains(Leg leg) {
		return legs.contains(leg);
	}
	
	public int getExcessTime() {
		int excess = 0;
		for (int i = 0; i < legs.size(); i++)
			excess += getConnectionExcessTime(i);
		return excess;
	}
	
	private int getConnectionExcessTime(int index) {
		if (index < legs.size() - 1) {
			DutyLeg previous = legs.get(index);
			DutyLeg next = legs.get(index + 1);
			int sit = DateUtil.difference(previous.getArrival(), next.getDeparture());
			return (sit - Rules.MIN_SIT_TIME);
		}
		return 0;
	}
	
	public void setAllLegsAsDH() {
		for (DutyLeg leg: legs)
			leg.setDeadHead(true);
	}
	
	public int getNumberOfDHLegs() {
		int total = 0;
		for (DutyLeg leg: legs)
			if (leg.isDeadHead())
				total++;
		return total;
	}
	
	public int getNumberOfNonDHLegs() {
		int total = 0;
		for (DutyLeg leg: legs)
			if (!leg.isDeadHead())
				total++;
		return total;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(DutyLeg leg: legs)
			sb.append("\t\t").append(leg.toString()).append('\n');
		return sb.toString();
	}
}
