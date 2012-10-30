package tcc.pairings.graph.networks;

import java.util.List;

import tcc.pairings.graph.Label;

public class FlightNetworkNodeLabel extends Label {
	private int flightTime;
	private int day;
	private double dual;
	private List<FlightNetworkPath> pathList;
	private int pathsDone;
	
	public int getFlightTime() {
		return flightTime;
	}
	
	public int getDay() {
		return day;
	}
	
	public double getDual() {
		return dual;
	}
	
	public void setDual(double dual) {
		this.dual = dual;
	}
	
	public List<FlightNetworkPath> getPathList() {
		return pathList;
	}
	
	public void setPathList(List<FlightNetworkPath> pathList) {
		this.pathList = pathList;
	}
	
	public int getPathsDone() {
		return pathsDone;
	}
	
	public void setPathsDone(int pathsDone) {
		this.pathsDone = pathsDone;
	}

	public FlightNetworkNodeLabel(int flightTime, int day) {
		this.flightTime = flightTime;
		this.day  = day;
		dual = 0.0;
		pathList = null;
	}


}
