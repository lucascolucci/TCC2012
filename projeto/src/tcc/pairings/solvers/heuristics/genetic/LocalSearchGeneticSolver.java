package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.exacts.SetCoverSolver;
import tcc.pairings.solvers.heuristics.Subproblem;
import tcc.pairings.solvers.heuristics.History;

public class LocalSearchGeneticSolver extends GeneticSolver {
	private int sampleSize = 3;
	private Solution individueSolution;
	private Solution sampleSolution;
	private List<Pairing> samplePairings;
	private History history;
	
	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public LocalSearchGeneticSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public LocalSearchGeneticSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
		history = new History();
	}
	
	@Override
	protected void fillPopulation() {
		int i = 0;
		while (i < populationSize) {
			Individue individue = new Individue(legs, pairings);
			individue.generateChromosome();
			individue.turnFeasible();
			
			//history.clear();
			//for (int j = 0; j < 20; j++)
			//	doLocalOptimization(individue);
			
			if (!population.contains(individue)) {
				individue.calculateFitness();
				population.add(individue);
				i++;
			}		
		}
	}
		
	@Override
	protected void doGenerations() {
		for (long generation = 0; generation < maxGenerations; generation++) {
			output(generation);
			Individue child = getChild(generation);
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
			
			if (random.nextDouble() < 0.10)
				doLocalOptimization(child);
			
			if (!population.contains(child))
				return child;
		}
	}
	
	private void doLocalOptimization(Individue individue) {
		setIndividueSolution(individue);
		setSamplePairings(individueSolution.getPairings());
		List<Leg> sampleLegs = getSampleLegs();
		Subproblem subproblem = new Subproblem(sampleLegs);
		if (!history.contains(subproblem)) {
			history.add(subproblem);
			setSampleSolution(sampleLegs);
			replacePairingsIfImproved(individue);
		}
		setAllDeadheadsToFalse(samplePairings);
		setAllDeadheadsToFalse(individue.getChromosome());
	}
	
	private void setIndividueSolution(Individue individue) {
		individueSolution = new Solution(individue.getChromosome());
		setDeadheadsAndCosts(individueSolution);
	}
	
	private void setDeadheadsAndCosts(Solution solution) {
		setDeadHeads(solution);
		setCostsWithDeadHeads(solution.getPairings());
		setSolutionCost(solution);
	}
	
	private void setSamplePairings(List<Pairing> pairings) {
		samplePairings = new ArrayList<Pairing>();
		for (int i = 0; i < sampleSize; i++) {
			int randomIndex = random.nextInt(pairings.size());
			Pairing selected = pairings.get(randomIndex);
			if (!samplePairings.contains(selected)) 
				samplePairings.add(selected);	
		}
	}
	
	private List<Leg> getSampleLegs() {
		List<Leg> cloned = new ArrayList<Leg>();
		for (Pairing pairing: samplePairings)
			for (DutyLeg dutyLeg: pairing.getLegs())
				for (Leg leg: legs)
					if (leg.equals(dutyLeg) && !dutyLeg.isDeadHead()) {
						cloned.add(leg.clone());
						break;
					}
		return cloned;
	}
	
	private void setSampleSolution(List<Leg> sampleLegs) {
		SetCoverSolver solver = new SetCoverSolver(sampleLegs, calculator);
		sampleSolution = solver.getSolution(bases);
		solver.endOptimizerModel();
	}
	
	private void replacePairingsIfImproved(Individue individue) {
		if (sampleSolution != null) {
			setDeadheadsAndCosts(sampleSolution);
			if (sampleSolution.getCost() < getSampleCost()) {
				updateChromossome(individue);
				updatePairings();
			}
		}
	}

	private double getSampleCost() {
		double cost = 0.0;
		for (Pairing pairing: samplePairings) 
			cost += pairing.getCostWithDeadHeads();
		return cost;
	}
	
	private void updateChromossome(Individue individue) { 
		individue.getChromosome().removeAll(samplePairings);		
		individue.getChromosome().addAll(sampleSolution.getPairings());
	}

	private void updatePairings() {
		for (Pairing pairing: sampleSolution.getPairings())
			if (!pairings.contains(pairing)) {
				addToSortedList(pairing, pairings);
				updateCoverPairings(pairing);
				setElite();
			}
	}
	
	private void updateCoverPairings(Pairing pairing) {
		for (Leg leg: pairing.getLegs())
			addToSortedList(pairing, coverPairings.get(leg));
	}
	
	private void addToSortedList(Pairing pairing, List<Pairing> pairings) {
		int i;
		for (i = 0; i < pairings.size(); i++)
			if (pairing.getCost() > pairings.get(i).getCost())
				break;
		pairings.add(i, pairing);
	}

	private void setAllDeadheadsToFalse(List<Pairing> pairings) {
		for (Pairing pairing: pairings)
			pairing.setAllDeadHeads(false);
	}
}
