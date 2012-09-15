package tcc.pairings.solvers;

import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.generators.InitialGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.TimeTableReader;

public class InitialSolver implements Solver {
	private String timeTable;
	private CostCalculator calculator;
	private List<Leg> legs;
	private FlightNetwork net;
	private int numberOfPairings;

	public CostCalculator getCalculator() {
		return calculator;
	}

	public void setCalculator(CostCalculator calculator) {
		this.calculator = calculator;
	}

	public List<Leg> getLegs() {
		return legs;
	}
	
	@Override
	public int getNumberOfPairings() {
		return numberOfPairings;
	}

	public InitialSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public InitialSolver(String timeTable, CostCalculator calculator) {
		this.timeTable = timeTable;
		this.calculator = calculator;
	}
	
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
		InitialGenerator generator = new InitialGenerator(net, calculator);
		generator.generate(bases);
		if (!generator.isAllLegsCovered())
			return null;
		numberOfPairings = generator.getNumberOfPairings();
		Solution solution = new Solution(generator.getPairings());
		setSolutionCost(solution);
		return solution;
	}

	private void setSolutionCost(Solution solution) {
		List<Pairing> pairings = solution.getPairings();
		double cost = 0.0;
		for (Pairing pairing: pairings)
			cost += pairing.getCostWithDeadHeads();
		solution.setCost(cost);
	}
}
