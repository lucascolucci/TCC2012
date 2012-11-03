package tcc.pairings.graph.networks;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.graph.Label;

public class FlightNetworkNodeLabel extends Label {
	private int flightTime;
	private int day;
	private double dual;
	private List<FlightNetworkPath> paths;
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
	
	public List<FlightNetworkPath> getPaths() {
		return paths;
	}
	
	public int getPathsDone() {
		return pathsDone;
	}
	
	public void setPathsDone(int pathsDone) {
		this.pathsDone = pathsDone;
	}
	
	public FlightNetworkNodeLabel() {
		paths = new ArrayList<FlightNetworkPath>();
	}

	public FlightNetworkNodeLabel(int flightTime, int day) {
		this.flightTime = flightTime;
		this.day  = day;
		dual = 0.0;
		paths = new ArrayList<FlightNetworkPath>();
	}
	
	public void addPath(FlightNetworkPath path) {
		paths.add(path);
	}
	
	public void removePaths(List<FlightNetworkPath> paths) {
		if (!paths.isEmpty())
			paths.removeAll(paths);
	}

	public void clearPaths() {
		paths.clear();
	}
}
