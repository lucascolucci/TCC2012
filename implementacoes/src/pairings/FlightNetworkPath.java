package pairings;

import pairings.graphs.Node;
import pairings.graphs.Path;

public class FlightNetworkPath extends Path<Leg>{
	private int flightTime;
	private int dutyTime;
	private int numberOfLegs;
	private int numberOfDuties;
	
	public FlightNetworkPath(Node<Leg> source) {
		super(source);
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
}
