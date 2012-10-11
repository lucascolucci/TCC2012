package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.exacts.SetCoverSolver;

public class LocalSearchGeneticSolver extends GeneticSolver {
	private static int sampleSize = 3;
	private static Random random = new Random(0);
	
	public static int getSampleSize() {
		return sampleSize;
	}

	public static void setSampleSize(int sampleSize) {
		LocalSearchGeneticSolver.sampleSize = sampleSize;
	}

	public LocalSearchGeneticSolver(String timeTable) {
		super(timeTable);
	}
	
	public LocalSearchGeneticSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}
		
	@Override
	protected void doGenerations() {
		for (long generation = 0; generation < maxGenerations; generation++) {
			output(generation);
			Individue[] parents = population.getParents();
			Individue child = parents[0].doCrossover(parents[1]);
			child.doMutation(population.getTheFittest());
			child.turnFeasible();
			localOptimize(child);
			child.calculateFitness();
			population.replace(child);
		}
	}
	
	public void localOptimize(Individue individue) {
		Solution solution = new Solution(individue.getChromosome().getGenes());
		setDeadHeads(solution);
		setSolutionCost(solution);
		
		List<Pairing> pairings = new ArrayList<Pairing>();
		for (int i = 0; i < sampleSize; i++) {
			int randomIndex = random.nextInt(individue.getChromosome().size());
			Pairing selected = individue.getChromosome().get(randomIndex);
			if (!pairings.contains(selected))
				pairings.add(selected);	
		}
		
		List<Leg> pairingsLegs = new ArrayList<Leg>();
		for (Pairing pairing: pairings)
			for (DutyLeg leg: pairing.getLegs())
				if (!leg.isDeadHead())
					pairingsLegs.add(leg);
		
		SetCoverSolver solver = new SetCoverSolver(pairingsLegs);
		Solution sampleSolution = solver.getSolution(bases);
		
		if (sampleSolution != null) {		
			individue.getChromosome().removeAll(pairings);
			individue.getChromosome().addAll(sampleSolution.getPairings());
		}
		
		for (Pairing pairing: individue.getChromosome().getGenes())
			for (DutyLeg leg: pairing.getLegs())
				leg.setDeadHead(false);
					
		Solution newSolution = new Solution(individue.getChromosome().getGenes());
		System.out.println(newSolution.isAllLegsCovered(legs));
	}
}