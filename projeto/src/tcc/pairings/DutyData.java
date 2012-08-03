package tcc.pairings;

public class DutyData implements Cloneable {
	private int flightTime;
	private int dutyTime;
	private int numberOfLegs;
	private int numberOfTails;
	
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
	
	public int getNumberOfTails() {
		return numberOfTails;
	}
	
	public void setNumberOfTails(int numberOfTails) {
		this.numberOfTails = numberOfTails;
	}
	
	public DutyData(int flightTime, int dutyTime, int numberOfLegs, int numberOfTails) {
		this.flightTime = flightTime;
		this.dutyTime = dutyTime;
		this.numberOfLegs = numberOfLegs;
		this.numberOfTails = numberOfTails;
	}
	
	public DutyData() {
		reset();
	}

	public void reset() {
		flightTime = 0;
		dutyTime = 0;
		numberOfLegs = 0;
		numberOfTails = 0;
	}
	
	public void startNew(int flightTime) {
		this.flightTime = flightTime;
		this.dutyTime = flightTime;
		numberOfLegs = 1;
		numberOfTails = 1;
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
		numberOfTails = lastDuty.getNumberOfTails();
	}

	public void incrementNumberOfTails() {
		numberOfTails++;
	}
	
	public void decrementNumberOfTails() {
		numberOfTails--;
	}
	
	@Override
	public DutyData clone() {
		return new DutyData(flightTime, dutyTime, numberOfLegs, numberOfTails);
	}
}
