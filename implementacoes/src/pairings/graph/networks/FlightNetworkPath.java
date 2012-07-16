package pairings.graph.networks;

import pairings.Leg;
import pairings.graph.Edge;
import pairings.graph.Path;

public class FlightNetworkPath extends Path<Leg>{
	private int flightTime;
	private int dutyTime;
	private int numberOfLegs;
	private int numberOfDuties;
	
	public FlightNetworkPath() {
		super();
		flightTime = 0;
		dutyTime = 0;
		numberOfLegs = 0;
		numberOfDuties = 0;
	}

	public int getFlightTime() {
		return flightTime;
	}

	public void setFlightTime(int flightTime) {
		this.flightTime = flightTime;
	}

	public int getDutyTime() {
		return dutyTime;
	}

	public void setDutyTime(int dutyTime) {
		this.dutyTime = dutyTime;
	}

	public int getNumberOfLegs() {
		return numberOfLegs;
	}

	public void setNumberOfLegs(int numberOfLegs) {
		this.numberOfLegs = numberOfLegs;
	}

	public int getNumberOfDuties() {
		return numberOfDuties;
	}

	public void setNumberOfDuties(int numberOfDuties) {
		this.numberOfDuties = numberOfDuties;
	}
	
	public boolean hasSameLegNumber(int number) {
		for (Edge<Leg> edge: getEdges())
			if (edge.getOut().getInfo().getNumber() == number)
				return true;
		return false;
	}

	public void incrementNumberOfDuties() {
		numberOfDuties++;
	}

	public void incrementNumberOfLegs() {
		numberOfLegs++;
	}

	public void incrementFlightTime(int time) {
		flightTime += time;
	}

	public void incrementDutyTime(int time) {
		dutyTime += time;
	}
	
	public void decrementNumberOfDuties() {
		numberOfDuties--;
	}

	public void decrementNumberOfLegs() {
		numberOfLegs--;
	}

	public void decrementFlightTime(int time) {
		flightTime -= time;
	}

	public void decrementDutyTime(int time) {
		dutyTime -= time;
	}
}
