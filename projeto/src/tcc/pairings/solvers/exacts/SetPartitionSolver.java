package tcc.pairings.solvers.exacts;

import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.PairingsGenerator;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.CplexOutputer;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.optimizers.CplexOptimizer;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.Solver;

public class SetPartitionSolver implements Solver {
	private String timeTable;
	private CostCalculator calculator;
	private List<Leg> legs;
	private FlightNetwork net;
	private MemoryOutputer memory;
	private CplexOutputer cplex;
	
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
	
	public SetPartitionSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public SetPartitionSolver(String timeTable, CostCalculator calculator) {
		this.timeTable = timeTable;
		this.calculator = calculator;
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
		setOutputers();
		generatePairings(bases);
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

	private void setOutputers() {
		memory = new MemoryOutputer(); 
		cplex = new CplexOutputer(legs);
		cplex.addRows();
	}

	private void generatePairings(Base... bases) {
		Outputer[] outputers = new Outputer[] { memory, cplex };
		PairingsGenerator generator = new PairingsGenerator(net, outputers, calculator);
		generator.generate(bases);
	}
	
	private Solution getOptimalSolution() {
		CplexOptimizer optimizer = new CplexOptimizer(cplex.getModel());
		if (optimizer.optimize()) {
			List<Pairing> pairings = optimizer.getOptimalPairings(memory.getPairings());
			optimizer.endModel();
			return new Solution(pairings);
		}
		return null;
	}
}