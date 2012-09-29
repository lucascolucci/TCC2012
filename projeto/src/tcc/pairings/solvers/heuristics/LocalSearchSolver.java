package tcc.pairings.solvers.heuristics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tcc.pairings.Base;
import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.rules.Rules;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.ResultsBuffer;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.Solver;
import tcc.pairings.solvers.exacts.SetCoverSolver;

public class LocalSearchSolver implements Solver {
	private int maxIterations = 100;
	private int sampleSize = 2;
	private int initialMaxDuties = 3;
	private int outputStep = 10;
	private ResultsBuffer buffer;
	private CostCalculator calculator;
	private InitialSolver initialSolver;
	private SetCoverSolver coverSolver;
	private Solution solution;
	private Random random;
	private List<Pairing> oldPairings;
	private List<Pairing> newPairings;
	private int numberOfPairings;
	private int infeasibleCount;
	private double solutionTime;
	
	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}
	
	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
	
	public int getInitialMaxDuties() {
		return initialMaxDuties;
	}

	public void setInitialMaxDuties(int initialMaxDuties) {
		this.initialMaxDuties = initialMaxDuties;
	}
	
	public int getOutputStep() {
		return outputStep;
	}

	public void setOutputStep(int outputStep) {
		this.outputStep = outputStep;
	}
	
	public ResultsBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ResultsBuffer buffer) {
		this.buffer = buffer;
	}
	
	public CostCalculator getCalculator() {
		return calculator;
	}

	public void setCalculator(CostCalculator calculator) {
		this.calculator = calculator;
	}
		
	@Override
	public List<Leg> getLegs() {
		return initialSolver.getLegs();
	}
	
	@Override
	public int getNumberOfPairings() {
		return numberOfPairings;
	}
	
	public LocalSearchSolver(String timeTable) {
		this(timeTable, null, null);
	}
	
	public LocalSearchSolver(String timeTable, CostCalculator calculator) {
		this(timeTable, calculator, null);
	}
	
	public LocalSearchSolver(String timeTable, CostCalculator calculator, ResultsBuffer buffer) {
		initialSolver = new InitialSolver(timeTable, calculator);
		this.calculator = calculator;
		random = new Random(0);
		this.buffer = buffer;
		numberOfPairings = 0;
	}
	
	@Override
	public Solution getSolution(Base... bases) {
		setInitialSolution(bases);
		long start = System.currentTimeMillis();
		if (solution != null)
			improveSolution(bases);
		long finish = System.currentTimeMillis();
		solutionTime = (finish - start) / 1000.0; 
		return solution;
	}

	private void setInitialSolution(Base... bases) {
		int maxDuties = Rules.MAX_DUTIES;
		Rules.MAX_DUTIES = initialMaxDuties;
		solution = initialSolver.getSolution(bases);
		numberOfPairings += initialSolver.getNumberOfPairings();
		Rules.MAX_DUTIES = maxDuties;
	}
	
	private void improveSolution(Base... bases) {
		infeasibleCount = 0;
		int iteration = 0;
		outputToBuffer(iteration);
		while (iteration++ < maxIterations) {
			doIteration(bases);
			outputToBuffer(iteration);
		}
	}
	
	private void outputToBuffer(int iteration) {
		if (buffer != null)
			if (iteration % outputStep == 0)
				buffer.output(iteration + "\t" + solution.getCost());
	}

	private void doIteration(Base... bases) {
		setOldAndNewPairings(bases);
		if (newPairings != null) {
			double oldCost = getCost(oldPairings);
			double newCost = getCost(newPairings);
			if (newCost < oldCost)
				updateSolution(oldCost, newCost);
		}
	}

	private void setOldAndNewPairings(Base... bases) {
		oldPairings = getRandomSample();
		List<Leg> oldLegs = getOldLegsToBeCovered();
		coverSolver = new SetCoverSolver(oldLegs, calculator);
		Solution newSolution = coverSolver.getSolution(bases);
		coverSolver.endOptimizerModel();
		if (newSolution != null) {
			newPairings = newSolution.getPairings();
			numberOfPairings += coverSolver.getNumberOfPairings();
			infeasibleCount++;
		} else
			newPairings = null;
	}

	private List<Pairing> getRandomSample() {
		List<Pairing> list = new ArrayList<Pairing>();
		int[] randomIndexes = getRandomIndexes();
		for (int i: randomIndexes)
			list.add(solution.getPairings().get(i));
		return list;
	}
	
	private int[] getRandomIndexes() {
		List<Integer> allIndexes = new ArrayList<Integer>();
		int solutionSize = solution.getPairings().size();
		int[] randomIndexes = new int[sampleSize];
		for (int i = 0; i < solutionSize; i++)
			allIndexes.add(i);
		for (int i = 0; i < sampleSize; i++) {
			int r = random.nextInt(allIndexes.size());
			randomIndexes[i] = allIndexes.get(r);
			allIndexes.remove(r);
		}
		return randomIndexes;
	}
	
	private List<Leg> getOldLegsToBeCovered() {
		List<DutyLeg> oldLegs = getOldLegs();
		List<Leg> originalLegs = initialSolver.getLegs();
		List<Leg> clonedLegs = new ArrayList<Leg>();
		for (Leg oringinal: originalLegs)
			for (Leg leg: oldLegs)
				if (oringinal.isDuplicate(leg))
					clonedLegs.add(oringinal.clone());
		return clonedLegs;
	}

	private List<DutyLeg> getOldLegs() {
		List<DutyLeg> legs = new ArrayList<DutyLeg>();	
		for (Pairing pairing: oldPairings)
			legs.addAll(pairing.getLegs());
		legs.removeAll(getRemovableLegs(legs));
		return legs;
	}

	private List<DutyLeg> getRemovableLegs(List<DutyLeg> legs) {
		List<DutyLeg> removedLegs = new ArrayList<DutyLeg>(); 
		for (DutyLeg leg: legs)
			if (leg.isDeadHead())
				removedLegs.add(leg);
		return removedLegs;
	}
	
	private double getCost(List<Pairing> pairings) {
		double cost = 0.0;
		for (Pairing pairing: pairings) 
			cost += pairing.getCostWithDeadHeads();
		return cost;
	}
	
	private void updateSolution(double oldCost, double newCost) {
		double cost = solution.getCost() - oldCost + newCost;
		solution.setCost(cost);
		solution.getPairings().removeAll(oldPairings);
		solution.getPairings().addAll(newPairings);
	}

	@Override
	public double getSolutionTime() {
		return solutionTime;
	}
	
	public double getInitialSolutionTime() {
		if (initialSolver != null)
			return initialSolver.getSolutionTime();
		return 0.0;
	}
	
	public double getInfeasiblesRelation() {
		return (double) infeasibleCount / maxIterations;
	}
}
