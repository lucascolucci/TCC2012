package tcc.pairings.solvers;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.generators.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.io.outputers.MemoryOutputer;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.optimizers.CplexOptimizer;

public abstract class BasicSolver implements Solver {
	protected String timeTable;
	protected CostCalculator calculator;
	protected List<Leg> legs;
	protected FlightNetwork net;
	protected MemoryOutputer memory;
	protected Outputer[] outputers;
	protected CplexOptimizer optimizer;
	protected int numberOfPairings;
	protected double solutionTime;
	protected Base[] bases;
	
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
	
	@Override
	public List<Leg> getLegs() {
		return legs;
	}
	
	@Override
	public int getNumberOfPairings() {
		return numberOfPairings;
	}
	
	@Override
	public double getSolutionTime() {
		return solutionTime;
	}
	
	public BasicSolver(List<Leg> legs) {
		this(legs, null);
	}
	
	public BasicSolver(List<Leg> legs, CostCalculator calculator) {
		this.legs = legs;
		this.calculator = calculator;
	}
	
	public BasicSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public BasicSolver(String timeTable, CostCalculator calculator) {
		this.timeTable = timeTable;
		this.calculator = calculator;
		legs = null;
	}
	
	@Override
	public Solution getSolution(Base... bases) {
		this.bases = bases;
		try {
			long start = System.currentTimeMillis();
			Solution solution = tryToGetSolution();
			long finish = System.currentTimeMillis();
			solutionTime = (finish - start) / 1000.0;
			return solution;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Solution tryToGetSolution() {
		setLegs();
		buildFlightNetwork();
		setOutputers();
		generatePairings();
		setOptimizer();
		return getOptimalSolution();
	}

	protected void setLegs() {
		if (legs == null) {
			TimeTableReader reader = new TimeTableReader(timeTable);
			legs = reader.getLegs();
		}
	}

	protected void buildFlightNetwork() {
		net = new FlightNetwork(legs);
		net.build();
	}

	protected abstract void setOutputers();

	protected void generatePairings() {
		PairingsGenerator generator = new PairingsGenerator(net, outputers, calculator);
		generator.generate(bases);
		numberOfPairings = generator.getNumberOfPairings();
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
	
	public void endOptimizerModel() {
		optimizer.endModel();
	}
	
	protected void setCostsWithDeadHeads(List<Pairing> pairings) {
		for (Pairing pairing: pairings) {
			double cost1 = getDeadHeadsCost(pairing.getLegs());
			double cost2 = pairing.getCost();
			pairing.setCostWithDeadHeads(cost1 + cost2);
		}
	}
	
	private double getDeadHeadsCost(List<DutyLeg> legs) {
		double cost = 0.0;
		for (DutyLeg leg: legs)
			if (leg.isDeadHead())
				if (calculator != null)
					cost += calculator.getDeadHeadingCost(leg);
				else
					cost += 1.0;
		return cost;
	}
	
	protected void setSolutionCost(Solution solution) {
		List<Pairing> pairings = solution.getPairings();
		double cost = 0.0;
		for (Pairing pairing: pairings)
			cost += pairing.getCostWithDeadHeads();
		solution.setCost(cost);
	}
}
