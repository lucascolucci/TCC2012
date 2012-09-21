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

public class LocalSearchSolver implements Solver {
	private static final int MAX_ITERATIONS = 250000;
	private static final int SAMPLE_SIZE = 6;
	
	private CostCalculator calculator;
	private int maxIterations = MAX_ITERATIONS;
	private int sampleSize = SAMPLE_SIZE;
	private InitialSolver initialSolver;
	private SetCoverSolver coverSolver;
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
		random = new Random(0);
		this.calculator = calculator;
	}

	@Override
	public Solution getSolution(Base... bases) {
		currentSolution = initialSolver.getSolution(bases);
		Rules.MAX_DUTIES = 4;
		if (currentSolution != null) {
			improveCurrentSolution(bases);
			System.out.println("*** CHECK ***");
			System.out.println(currentSolution.isAllLegsCovered(initialSolver.getLegs()));
			System.out.println(currentSolution.isCostRight());
		}
		return currentSolution;
	}

	private void improveCurrentSolution(Base... bases) {
		int iteration = 0;
		double firstCost = currentSolution.getCost();
		System.out.println(iteration + "\t" + firstCost + "\t" + currentSolution.getCost());
		while (iteration++ < maxIterations) {
			doIteration(bases);
			System.out.println(iteration + "\t" + firstCost + "\t" + currentSolution.getCost());
		}
	}

	private void doIteration(Base... bases) {
		setOldAndNewPairings(bases);
		if (newPairings != null) {
			double oldCost = getCost(oldPairings);
			double newCost = getCost(newPairings);
			if (newCost < oldCost)
				updateCurrentSolution(oldCost, newCost);
		}
	}

	private void setOldAndNewPairings(Base... bases) {
		oldPairings = getRandomSample();
		List<Leg> oldLegs = getOldLegsToBeCovered();
		coverSolver = new SetCoverSolver(oldLegs, calculator);
		Solution newSolution = coverSolver.getSolution(bases);
		coverSolver.endOptimizerModel();
		if (newSolution != null) {
			// Para fins de testes
			//if (!newSolution.isAllLegsCovered(oldLegs))
			//	throw new RuntimeException("Pernas n‹o cobertas.");	
			// Para finst de testes
			//if (!newSolution.isCostRight())
			//	throw new RuntimeException("Custo incorreto.");
			newPairings = newSolution.getPairings();
		}
		else
			newPairings = null;
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
	
	private void updateCurrentSolution(double oldCost, double newCost) {
		double cost = currentSolution.getCost() - oldCost + newCost;
		currentSolution.setCost(cost);
		currentSolution.getPairings().removeAll(oldPairings);
		currentSolution.getPairings().addAll(newPairings);
	}
}
