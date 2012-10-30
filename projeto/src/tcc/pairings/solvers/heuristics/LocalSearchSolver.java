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
	private ResultsBuffer buffer;
	private boolean useHistory = false;
	private CostCalculator calculator;
	private Base[] bases;
	private InitialSolver initialSolver;
	private SetCoverSolver coverSolver;
	private Solution solution;
	private List<Pairing> oldPairings;
	private List<Pairing> newPairings;
	private int numberOfPairings;
	private int infeasibleCount;
	private double solutionTime;
	private History history;
	private static final Random random = new Random(0);
	
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
	
	public boolean useHistory() {
		return useHistory;
	}

	public void setUseHistory(boolean useHistory) {
		this.useHistory = useHistory;
	}
	
	public CostCalculator getCalculator() {
		return calculator;
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
		this.calculator = calculator;
		this.buffer = buffer;
		initialSolver = new InitialSolver(timeTable, calculator);
		numberOfPairings = 0;
		history = new History();
	}
	
	@Override
	public Solution getSolution(Base... bases) {
		this.bases = bases;
		long start = System.currentTimeMillis();
		setInitialSolution();
		if (solution != null) {
			System.out.println(solution);
			improveSolution();
		}
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
		for (int iteration = 0; iteration < maxIterations; iteration++) {
			output(iteration);
			doIteration();
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
		setOldAndNewPairings();
		if (newPairings != null) {
			double oldCost = getCost(oldPairings);
			double newCost = getCost(newPairings);
			if (newCost < oldCost)
				updateSolution(oldCost, newCost);
		}
	}

	private void setOldAndNewPairings() {
		oldPairings = getRandomSample();
		setNewPairings();
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

	private void setNewPairings() {
		List<Leg> legs = getSampleLegs();
		if (useHistory)
			setNewPairingsUsingHistory(legs);
		else
			setNewPairingsWithoutHistory(legs);
	}
	
	private List<Leg> getSampleLegs() {
		List<DutyLeg> sampleLegs = getSampleNonDHLegs();
		List<Leg> cloned = new ArrayList<Leg>();
		for (Leg leg: initialSolver.getLegs())
			if (sampleLegs.contains(leg))
				cloned.add(leg.clone());
		return cloned;
	}

	private List<DutyLeg> getSampleNonDHLegs() {
		List<DutyLeg> legs = new ArrayList<DutyLeg>();	
		for (Pairing pairing: oldPairings)
			for (DutyLeg leg: pairing.getLegs())
				if (!leg.isDeadHead())
					legs.add(leg);
		return legs;
	}
	
	private void setNewPairingsUsingHistory(List<Leg> legs) {
		Solution newSolution = getNewSolutionUsingHistory(legs);
		setNewPairingsFromSolution(newSolution);
	}

	private void setNewPairingsWithoutHistory(List<Leg> legs) {
		Solution newSolution = getNewSolutionFromCoverSolver(legs);
		setNewPairingsFromSolution(newSolution);
	}
	
	private void setNewPairingsFromSolution(Solution solution) {
		if (solution != null) {
			newPairings = solution.getPairings();
		} else {
			newPairings = null;
			infeasibleCount++;
		}
	}

	private Solution getNewSolutionUsingHistory(List<Leg> legs) {
		Subproblem subproblem = new Subproblem(legs);
		if (!history.contains(subproblem)) {
			Solution newSolution = getNewSolutionFromCoverSolver(legs);
			subproblem.setSolution(newSolution);
			history.add(subproblem);
			return newSolution;
		} else {
			return history.getSubproblem(subproblem).getSolution();
		}
	}
	
	private Solution getNewSolutionFromCoverSolver(List<Leg> legs) {
		coverSolver = new SetCoverSolver(legs, calculator);
		Solution newSolution = coverSolver.getSolution(bases);
		numberOfPairings += coverSolver.getNumberOfPairings();
		coverSolver.endOptimizerModel();
		return newSolution;
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
		return initialSolver.getSolutionTime();
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
