package tcc.pairings.graph.networks;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.DutyData;
import tcc.pairings.Leg;
import tcc.pairings.graph.Edge;
import tcc.pairings.graph.Path;

public class FlightNetworkPath extends Path<Leg>{
	private DutyData dutyData;
	private short track;
	private int numberOfDuties;
	private double cost;
	
	public DutyData getDutyData() {
		return dutyData;
	}

	public void setDutyData(DutyData dutyData) {
		this.dutyData = dutyData;
	}

	public short getTrack() {
		return track;
	}

	public void setTrack(short track) {
		this.track = track;
	}

	public int getNumberOfDuties() {
		return numberOfDuties;
	}

	public void setNumberOfDuties(int numberOfDuties) {
		this.numberOfDuties = numberOfDuties;
	}
	
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public FlightNetworkPath() {
		super();
		dutyData = new DutyData();
		track = -1;
		numberOfDuties = 0;
		cost = 0.0;
	}
	
	public void addNewDuty(int flightTime, short track) {
		dutyData.startNew(flightTime);
		this.track = track;
		numberOfDuties++;
	}
	
	public void reset() {
		dutyData.reset();
		track = -1;
		numberOfDuties = 0;
	}
	
	public void addConnection(int flightTime, int sitTime, short track) {
		dutyData.addConnection(flightTime, sitTime);
		incrementTracksIfDifferent(track);
	}

	private void incrementTracksIfDifferent(short newTrack) {
		if (newTrack != track) {
			dutyData.incrementNumberOfTracks();
			track = newTrack;
		}
	}
	
	public void removeConnection(int flightTime, int sitTime, short track) {
		dutyData.removeConnection(flightTime, sitTime);
		decrementTracksIfDifferent(track);
	}

	private void decrementTracksIfDifferent(short oldTrack) {
		if (oldTrack != track) {
			dutyData.decrementNumberOfTracks();
			track = oldTrack;
		}
	}
	
	public void removeOvernight(DutyData lastDuty, short lastTrack) {
		dutyData.resume(lastDuty);
		track = lastTrack;
		numberOfDuties--;
	}
	
	public boolean contains(Leg leg) {
		for (Edge<Leg> edge: edges) 
			if (leg.equals(edge.getIn().getInfo()))
				return true;
		return false;
	}
	
	public List<Leg> getLegs() {
		List<Leg> legs = new ArrayList<Leg>();
		for (Edge<Leg> edge: edges) {
			Leg leg = edge.getIn().getInfo();
			if (leg != null)
				legs.add(leg);
		}
		return legs;
	}
}
