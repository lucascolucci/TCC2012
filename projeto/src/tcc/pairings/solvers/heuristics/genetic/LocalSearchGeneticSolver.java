package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.exacts.SetCoverSolver;

public class LocalSearchGeneticSolver extends GeneticSolver {
	private int sampleSize = 3;
	private Solution individueSolution;
	private Solution sampleSolution;
	
	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
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
			Individue child = getChild(generation);
			//doLocalOptimization(child);
			child.calculateFitness();
			population.replace(child);
			if (population.getTheFittest().getFitness() < best.getFitness())
				best = population.getTheFittest();
		}
	}
	
	@Override
	protected Individue getChild(long generation) {
		while (true) {
			Individue[] parents = population.getParents();
			Individue child = parents[0].doCrossover(parents[1]);
			child.doMutation(population.getTheFittest(), getNumberOfMutatingGenes(generation));
			child.turnFeasible();
			doLocalOptimization(child);
			if (!population.contains(child))
				return child;
		}
	}
	
	private void doLocalOptimization(Individue individue) {
		setIndividueSolution(individue);
		setSampleSolution();
		replacePairingsIfImproved(individue);
		setAllDeadheadsToFalse(individue.getChromosome().getGenes());
	}
	
	private void setIndividueSolution(Individue individue) {
		individueSolution = new Solution(individue.getChromosome().getGenes());
		setDeadheadsAndCosts(individueSolution);
	}
	
	private void setDeadheadsAndCosts(Solution solution) {
		setDeadHeads(solution);
		setCostsWithDeadHeads(solution.getPairings());
		setSolutionCost(solution);
	}
	
	private void setSampleSolution() {
		List<Pairing> samplePairings = getSamplePairings(individueSolution.getPairings());
		List<Leg> sampleLegs = getSampleLegs(samplePairings);
		SetCoverSolver solver = new SetCoverSolver(sampleLegs, calculator);
		sampleSolution = solver.getSolution(bases);
		solver.endOptimizerModel();
	}
	
	private List<Pairing> getSamplePairings(List<Pairing> pairings) {
		List<Pairing> sample = new ArrayList<Pairing>();
		for (int i = 0; i < sampleSize; i++) {
			int randomIndex = random.nextInt(pairings.size());
			Pairing selected = pairings.get(randomIndex);
			if (!sample.contains(selected)) 
				sample.add(selected);	
		}
		return sample;
	}

	private List<Leg> getSampleLegs(List<Pairing> pairings) {
		List<Leg> cloned = new ArrayList<Leg>();
		for (Pairing pairing: pairings)
			for (DutyLeg dutyLeg: pairing.getLegs())
				for (Leg leg: legs)
					if (leg.equals(dutyLeg) && !dutyLeg.isDeadHead()) {
						cloned.add(leg.clone());
						break;
					}
		return cloned;
	}
	
	private void replacePairingsIfImproved(Individue individue) {
		if (sampleSolution != null) {
			setDeadheadsAndCosts(sampleSolution);
			if (sampleSolution.getCost() < getSampleCost()) {
				setAllDeadheadsToFalse(sampleSolution.getPairings());
				updateChromossome(individue);
				updatePairings();
			}
		}
	}

	private double getSampleCost() {
		double cost = 0.0;
		for (Pairing pairing: sampleSolution.getPairings()) 
			cost += pairing.getCostWithDeadHeads();
		return cost;
	}
	
	private void updateChromossome(Individue individue) {
		List<Pairing> samplePairings = sampleSolution.getPairings(); 
		individue.getChromosome().removeAll(samplePairings);		
		individue.getChromosome().addAll(samplePairings);
	}

	private void updatePairings() {
		for (Pairing pairing: sampleSolution.getPairings())
			if (!pairings.contains(pairing)) {
				pairings.add(pairing);
				updateCoverPairings(pairing);
				updateElite(pairing);
			}
	}
	
	private void updateCoverPairings(Pairing pairing) {
		for (Leg leg: pairing.getLegs()) {
			List<Pairing> coverList = coverPairings.get(leg);
			int i;
			for (i = 0; i < coverList.size(); i++)
				if (pairing.getCost() > coverList.get(i).getCost())
					break;
			coverList.add(i, pairing);
		}
	}
	
	private void updateElite(Pairing pairing) {
		
	}

	private void setAllDeadheadsToFalse(List<Pairing> pairings) {
		for (Pairing pairing: pairings)
			pairing.setAllDeadHeads(false);
	}
}
