package tcc.pairings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tcc.DateUtil;

public class Duty {
	private List<Leg> legs;
	
	public List<Leg> getLegs() {
		return legs;
	}
	
	public Duty() {
		legs = new ArrayList<Leg>();
	}
	
	public int getFlightTime() {
		int total = 0;
		for (Leg leg: legs)
			total += DateUtil.difference(leg.getDeparture(), leg.getArrival());
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
	
	public void addLeg(Leg leg) {
		legs.add(leg);
	}
	
	public Leg getFirstLeg() {
		if (!legs.isEmpty()) 
			return legs.get(0);
		return null;
	}
	
	public Leg getLastLeg() {
		if (!legs.isEmpty()) 
			return legs.get(legs.size() - 1);
		return null;
	}

	public boolean contains(Leg leg) {
		for (Leg dutyLeg: legs)
			if (dutyLeg.isDuplicate(leg))
				return true;
		return false;
	}
	
	public int getExcessTime() {
		int excess = 0;
		for (int i = 0; i < legs.size(); i++)
			excess += getConnectionExcessTime(i);
		return excess;
	}
	
	private int getConnectionExcessTime(int index) {
		if (index < legs.size() - 1) {
			Leg previous = legs.get(index);
			Leg next = legs.get(index + 1);
			int sit = DateUtil.difference(previous.getArrival(), next.getDeparture());
			return (sit - Rules.MIN_SIT_TIME);
		}
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Leg leg: legs)
			sb.append("\t\t").append(leg.toString()).append('\n');
		return sb.toString();
	}
}
