package pairings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Duty {
	private List<Leg> legs;
	
	public Duty() {
		legs = new ArrayList<Leg>();
	}
	
	public List<Leg> getLegs() {
		return legs;
	}
	
	public int getNumberOfLegs() {
		return legs.size();
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

	public boolean contains(int number) {
		for (Leg leg: legs)
			if (leg.getNumber() == number)
				return true;
		return false;
	}
}
