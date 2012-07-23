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
	
	public int getCost() {
		int cost = 0;
		for (int i = 0; i < legs.size(); i++)
			cost += getConnectionCost(i);
		return cost;
	}
	
	private int getConnectionCost(int index) {
		if (index < legs.size() - 1) {
			Leg previous = legs.get(index);
			Leg next = legs.get(index + 1);
			return DateUtil.difference(previous.getArrival(), next.getDeparture()) - Rules.MIN_SIT_TIME;
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
