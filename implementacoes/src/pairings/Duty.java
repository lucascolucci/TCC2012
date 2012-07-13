package pairings;

import java.util.ArrayList;
import java.util.List;

public class Duty {
	private List<Leg> legs;
	
	public Duty() {
		legs = new ArrayList<Leg>();
	}
	
	public List<Leg> getLegs() {
		return legs;
	}
	
	public void addLeg(Leg leg) {
		legs.add(leg);
	}
}
