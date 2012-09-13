package tcc.pairings.solvers;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.generators.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.optimizers.Optimizer;

public abstract class BasicSolver {
	protected String timeTable;
	protected CostCalculator calculator;
	protected List<Leg> legs;
	protected FlightNetwork net;
	protected MemoryOutputer memory;
	protected Outputer[] outputers;
	protected Optimizer optimizer;
	
	public String getTimeTable() {
		return timeTable;
	}

	public void setTimeTable(String timeTable) {
		this.timeTable = timeTable;
	}
	
	public CostCalculator getCalculator() {
		return calculator;
	}

	public void setCalculator(CostCalculator calculator) {
		this.calculator = calculator;
	}
	
	public BasicSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public BasicSolver(String timeTable, CostCalculator calculator) {
		this.timeTable = timeTable;
		this.calculator = calculator;
	}
	
	public Solution getSolution(Base... bases) {
		try {
			return tryToGetSolution(bases);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Solution tryToGetSolution(Base... bases) {
		setLegs();
		buildFlightNetwork();
		setOutputers();
		generatePairings(bases);
		setOptimizer();
		return getOptimalSolution();
	}

	private void setLegs() {
		TimeTableReader reader = new TimeTableReader(timeTable);
		legs = reader.getLegs();
	}

	private void buildFlightNetwork() {
		net = new FlightNetwork(legs);
		net.build();
	}

	protected abstract void setOutputers();

	private void generatePairings(Base... bases) {
		PairingsGenerator generator = new PairingsGenerator(net, outputers, calculator);
		generator.generate(bases);
	}
	
	protected abstract void setOptimizer();
	
	protected Solution getOptimalSolution() {
		if (optimizer.optimize()) {
			Solution solution = new Solution(getOptimalPairings(memory.getPairings()));
			solution.setCost(optimizer.getObjectiveValue());
			return solution;
		}
		return null;
	}
	
	private List<Pairing> getOptimalPairings(List<Pairing> pairings) {
		List<Pairing> list = new ArrayList<Pairing>();
		List<Integer> vars = optimizer.getOptimalVariables();
		for (int i: vars)
			list.add(pairings.get(i - 1));
		return list;
	}
}
