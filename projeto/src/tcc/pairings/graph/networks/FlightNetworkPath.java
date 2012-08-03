package tcc.pairings.graph.networks;

import tcc.pairings.DutyData;
import tcc.pairings.Leg;
import tcc.pairings.graph.Edge;
import tcc.pairings.graph.Path;

public class FlightNetworkPath extends Path<Leg>{
	private DutyData dutyData;
	private String tail;
	private int numberOfDuties;
	
	public DutyData getDutyData() {
		return dutyData;
	}

	public void setDutyData(DutyData dutyData) {
		this.dutyData = dutyData;
	}

	public String getTail() {
		return tail;
	}

	public void setTail(String tail) {
		this.tail = tail;
	}

	public int getNumberOfDuties() {
		return numberOfDuties;
	}

	public void setNumberOfDuties(int numberOfDuties) {
		this.numberOfDuties = numberOfDuties;
	}
	
	public FlightNetworkPath() {
		super();
		dutyData = new DutyData();
		tail = null;
		numberOfDuties = 0;
	}
	
	public void addNewDuty(int flightTime, String tail) {
		dutyData.startNew(flightTime);
		this.tail = tail;
		numberOfDuties++;
	}
	
	public void reset() {
		dutyData.reset();
		tail = null;
		numberOfDuties = 0;
	}
	
	public void addConnection(int flightTime, int sitTime, String tail) {
		dutyData.addConnection(flightTime, sitTime);
		incrementTailsIfDifferent(tail);
	}

	private void incrementTailsIfDifferent(String newTail) {
		if (newTail != null && !newTail.contentEquals(tail)) {
			dutyData.incrementNumberOfTails();
			tail = newTail;
		}
	}
	
	public void removeConnection(int flightTime, int sitTime, String tail) {
		dutyData.removeConnection(flightTime, sitTime);
		decrementTailsIfDifferent(tail);
	}

	private void decrementTailsIfDifferent(String oldTail) {
		if (oldTail != null && !oldTail.contentEquals(tail)) {
			dutyData.decrementNumberOfTails();
			tail = oldTail;
		}
	}
	
	public void removeOvernight(DutyData lastDuty, String lastTail) {
		dutyData.resume(lastDuty);
		tail = lastTail;
		numberOfDuties--;
	}
	
	public boolean hasSameLegNumber(int number) {
		for (Edge<Leg> edge: edges)
			if (edge.getOut().getInfo().getNumber() == number)
				return true;
		return false;
	}
}
