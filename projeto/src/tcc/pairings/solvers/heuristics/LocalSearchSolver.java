package tcc.pairings.solvers.heuristics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.Solver;
import tcc.pairings.solvers.exacts.SetCoverSolver;

public class LocalSearchSolver implements Solver {
	private static final int MAX_ITERATIONS = 100;
	private static final int SAMPLE_SIZE = 3;
	
	private CostCalculator calculator;
	private int maxIterations = MAX_ITERATIONS;
	private int sampleSize = SAMPLE_SIZE;
	private InitialSolver initialSolver;
	private SetCoverSolver coverSover;
	private Solution currentSolution;
	private Random random;
	private List<Pairing> oldPairings;
	private List<Pairing> newPairings;
	
	public CostCalculator getCalculator() {
		return calculator;
	}

	public void setCalculator(CostCalculator calculator) {
		this.calculator = calculator;
	}
	
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

	@Override
	public List<Leg> getLegs() {
		return initialSolver.getLegs();
	}
	
	@Override
	public int getNumberOfPairings() {
		return initialSolver.getNumberOfPairings();
	}
	
	public LocalSearchSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public LocalSearchSolver(String timeTable, CostCalculator calculator) {
		initialSolver = new InitialSolver(timeTable, calculator);
		random = new Random();
		this.calculator = calculator;
	}

	@Override
	public Solution getSolution(Base... bases) {
		currentSolution = initialSolver.getSolution(bases);
		improveCurrentSolution(bases);
		return currentSolution;
	}

	private void improveCurrentSolution(Base... bases) {
		int iteration = 0;
		System.out.println(iteration + '\t' + currentSolution.getCost());
		while (iteration++ < maxIterations) {
			doIteration(bases);
			System.out.println(iteration + '\t' + currentSolution.getCost());
		}
	}

	private void doIteration(Base... bases) {
		setOldAndNewPairings(bases);
		double oldCost = getCost(oldPairings);
		double newCost = getCost(newPairings);
		if (newCost < oldCost)
			updateCurrentSolution(oldCost, newCost);
	}

	private void setOldAndNewPairings(Base... bases) {
		oldPairings = getRandomSample();
		coverSover = new SetCoverSolver(getOldPairingsLegs(), calculator);
		newPairings = coverSover.getSolution(bases).getPairings();
		setNewPairingsDeadHeads();
	}

	private List<Pairing> getRandomSample() {
		List<Pairing> list = new ArrayList<Pairing>();
		int[] randomIndexes = getRandomIndexes();
		for (int i: randomIndexes)
			list.add(currentSolution.getPairings().get(i));
		return list;
	}
	
	private int[] getRandomIndexes() {
		List<Integer> allIndexes = new ArrayList<Integer>();
		int solutionSize = currentSolution.getPairings().size();
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
	
	private List<Leg> getOldPairingsLegs() {
		// TODO Auto-generated method stub
		List<Leg> legs = new ArrayList<Leg>();	
		return legs;
	}
	
	private void setNewPairingsDeadHeads() {
		// TODO Auto-generated method stub	
	}
	
	private double getCost(List<Pairing> pairings) {
		double cost = 0.0;
		for (Pairing pairing: pairings) 
			cost += pairing.getCostWithDeadHeads();
		return cost;
	}
	
	private void updateCurrentSolution(double oldCost, double newCost) {
		double cost = currentSolution.getCost() - oldCost + newCost;
		currentSolution.setCost(cost);
		currentSolution.getPairings().removeAll(oldPairings);
		currentSolution.getPairings().addAll(newPairings);
	}
}
