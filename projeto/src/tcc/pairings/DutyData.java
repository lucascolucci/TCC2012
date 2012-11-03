package tcc.pairings;

public class DutyData implements Cloneable {
	private int flightTime;
	private int dutyTime;
	private int numberOfLegs;
	private int numberOfTracks;
	
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
	
	public int getNumberOfTracks() {
		return numberOfTracks;
	}
	
	public void setNumberOfTails(int numberOfTracks) {
		this.numberOfTracks = numberOfTracks;
	}
	
	public DutyData(int flightTime, int dutyTime, int numberOfLegs, int numberOfTracks) {
		this.flightTime = flightTime;
		this.dutyTime = dutyTime;
		this.numberOfLegs = numberOfLegs;
		this.numberOfTracks = numberOfTracks;
	}
	
	public DutyData() {
		reset();
	}

	public void reset() {
		flightTime = 0;
		dutyTime = 0;
		numberOfLegs = 0;
		numberOfTracks = 0;
	}
	
	public void startNew(int flightTime) {
		this.flightTime = flightTime;
		this.dutyTime = flightTime;
		numberOfLegs = 1;
		numberOfTracks = 1;
	}
	
	public void addConnection(int flightTime, int sitTime) {
		this.flightTime += flightTime;
		dutyTime += (flightTime + sitTime);
		numberOfLegs++;
	}
	
	public void removeConnection(int flightTime, int sitTime) { 
		this.flightTime -= flightTime;
		dutyTime -= (flightTime + sitTime);
		numberOfLegs--;
	}
	
	public void resume(DutyData lastDuty) {
		flightTime = lastDuty.getFlightTime();
		dutyTime = lastDuty.getDutyTime();
		numberOfLegs = lastDuty.getNumberOfLegs();
		numberOfTracks = lastDuty.getNumberOfTracks();
	}

	public void incrementNumberOfTracks() {
		numberOfTracks++;
	}
	
	public void decrementNumberOfTracks() {
		numberOfTracks--;
	}
	
	public boolean dominates(DutyData other) {
		return flightTime <= other.flightTime && dutyTime <= other.dutyTime 
				&& numberOfLegs <= other.numberOfLegs && numberOfTracks <= other.numberOfTracks;
	}
	
	@Override
	public DutyData clone() {
		return new DutyData(flightTime, dutyTime, numberOfLegs, numberOfTracks);
	}
}
