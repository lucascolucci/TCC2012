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
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.Solver;
import tcc.pairings.solvers.exacts.SetCoverSolver;
import tcc.util.ResultsBuffer;

public class LocalSearchSolver implements Solver {
	private int maxIterations = 1000;
	private int sampleSize = 3;
	private int initialMaxDuties = 4;
	private int outputStep = 100;
	private Base[] bases;
	private ResultsBuffer buffer;
	private CostCalculator calculator;
	private InitialSolver initialSolver;
	private SetCoverSolver coverSolver;
	private Solution solution;
	private List<Pairing> oldPairings;
	private List<Pairing> newPairings;
	private int numberOfPairings;
	private int infeasibleCount;
	private double solutionTime;
	private History history;
	private static Random random = new Random(0);
	
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
		this.buffer = buffer;
		numberOfPairings = 0;
		history = new History();
	}
	
	@Override
	public Solution getSolution(Base... bases) {
		this.bases = bases;
		setInitialSolution();
		long start = System.currentTimeMillis();
		if (solution != null)
			improveSolution();
		long finish = System.currentTimeMillis();
		solutionTime = (finish - start) / 1000.0; 
		return solution;
	}

	private void setInitialSolution() {
		int maxDuties = Rules.MAX_DUTIES;
		Rules.MAX_DUTIES = initialMaxDuties;
		solution = initialSolver.getSolution(bases);
		numberOfPairings += initialSolver.getNumberOfPairings();
		Rules.MAX_DUTIES = maxDuties;
	}
	
	private void improveSolution() {
		infeasibleCount = 0;
		int iteration = 0;
		output(iteration);
		while (iteration++ < maxIterations) {
			doIteration();
			output(iteration);
		}
	}
	
	private void output(int iteration) {
		if (iteration % outputStep == 0) {
			System.out.println(iteration + "\t" + solution.getCost());
			if (buffer != null)
				buffer.output(iteration + "\t" + solution.getCost());
		}
	}

	private void doIteration() {
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
		newPairings = null;
		List<Leg> oldLegs = getOldLegsToBeCovered();
		Subproblem subproblem = new Subproblem(oldLegs);
		if (!history.contains(subproblem)) {
			history.add(subproblem);
			coverSolver = new SetCoverSolver(oldLegs, calculator);
			Solution newSolution = coverSolver.getSolution(bases);
			coverSolver.endOptimizerModel();
			if (newSolution != null) {
				newPairings = newSolution.getPairings();
				numberOfPairings += coverSolver.getNumberOfPairings();
			} else
				infeasibleCount++;
		}
	}
	
	private List<Pairing> getRandomSample() {
		List<Pairing> list = new ArrayList<Pairing>();
		for (int i = 0; i < sampleSize; i++) {
			int randomIndex = random.nextInt(solution.getPairings().size());
			Pairing selected = solution.getPairings().get(randomIndex);
			if (!list.contains(selected))
				list.add(selected);
		}
		return list;
	}
	
	private List<Leg> getOldLegsToBeCovered() {
		List<DutyLeg> oldLegs = getOldLegs();
		List<Leg> originalLegs = initialSolver.getLegs();
		List<Leg> clonedLegs = new ArrayList<Leg>();
		for (Leg oringinal: originalLegs)
			if (oldLegs.contains(oringinal))
				clonedLegs.add(oringinal.clone());
		return clonedLegs;
	}

	private List<DutyLeg> getOldLegs() {
		List<DutyLeg> legs = new ArrayList<DutyLeg>();	
		for (Pairing pairing: oldPairings)
			for (DutyLeg leg: pairing.getLegs())
				if (!leg.isDeadHead())
					legs.add(leg);
		return legs;
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
	
	public double getInfeasibility() {
		if (history.size() > 0)
			return (double) infeasibleCount / history.size();
		return -1.0;
	}
	
	public int getNumberOfSolvedSubproblem() {
		return history.size();
	}
}
