package tcc.pairings.solvers;

import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.InitialGenerator;
import tcc.pairings.Leg;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.TimeTableReader;

public class InitialSolver implements Solver {
	private String timeTable;
	private List<Leg> legs;
	private FlightNetwork net;

	public List<Leg> getLegs() {
		return legs;
	}

	public InitialSolver(List<Leg> legs) {
		this.legs = legs;
	}
	
	public InitialSolver(String timeTable) {
		this.timeTable = timeTable;
	}
	
	@Override
	public Solution getSolution(Base... bases) {
		try {
			return tryToGetSolution(bases);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	private Solution tryToGetSolution(Base... bases) {
		setLegs();
		buildFlightNetwork();
		return getInitialSolution(bases);
	}

	private void setLegs() {
		TimeTableReader reader = new TimeTableReader(timeTable);
		legs = reader.getLegs();
	}

	private void buildFlightNetwork() {
		net = new FlightNetwork(legs);
		net.build();
	}

	private Solution getInitialSolution(Base... bases) {		
		InitialGenerator generator = new InitialGenerator(net);
		generator.generate(bases);
		return new Solution(generator.getPairings());
	}
}
