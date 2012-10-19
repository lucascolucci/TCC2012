package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.rules.Rules;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.exacts.SetCoverSolver;
import tcc.pairings.solvers.heuristics.Subproblem;
import tcc.pairings.solvers.heuristics.History;

public class LocalSearchGeneticSolver extends GeneticSolver {
	private int sampleSize = 3;
	private int initialMaxDuties = 4;
	private int individueImprovements = 100;
	private double optimizationProbability = 0.01;
	private InitialSolver initialSolver;
	private Solution initialSolution;
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
	
	public int getInitialMaxDuties() {
		return initialMaxDuties;
	}

	public void setInitialMaxDuties(int initialMaxDuties) {
		this.initialMaxDuties = initialMaxDuties;
	}
	
	public int getIndividueImprovements() {
		return individueImprovements;
	}

	public void setIndividueImprovements(int individueImprovements) {
		this.individueImprovements = individueImprovements;
	}

	public double getOptimizationProbability() {
		return optimizationProbability;
	}

	public void setOptimizationProbability(double optimizationProbability) {
		this.optimizationProbability = optimizationProbability;
	}

	public LocalSearchGeneticSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public LocalSearchGeneticSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
		initialSolver = new InitialSolver(timeTable, calculator);
		history = new History();
	}
	
	@Override
	protected void setOutputers() { 
		memory = null;
		outputers = null;
	}
	
	@Override
	protected void generatePairings() {
		int maxDuties = Rules.MAX_DUTIES;
		Rules.MAX_DUTIES = initialMaxDuties;
		initialSolution = initialSolver.getSolution(bases);
		Rules.MAX_DUTIES = maxDuties;
		numberOfPairings = initialSolver.getNumberOfPairings();
	}
	
	@Override
	protected void setOptimizer() {
		optimizer = null;
	}
	
	@Override
	protected Solution getOptimalSolution() {
		if (initialSolution != null) {
			System.out.println(initialSolution);
			return super.getOptimalSolution();
		}
		return null;
	}
	
	@Override
	protected void setPairings() {
		pairings = initialSolution.getPairings();
		setAllDeadheadsToFalse(pairings);
	}
	
	private void setAllDeadheadsToFalse(List<Pairing> pairings) {
		for (Pairing pairing: pairings)
			pairing.setAllDeadHeads(false);
	}
	
	@Override
	protected void fillPopulation() {
		int i = 0;
		while (i < populationSize) {
			Individue individue = getFeasibleIndividue();
			optimizeInitialIndividue(individue);
			if (!population.contains(individue))	
				addIndividueToPopulation(++i, individue);
		}
		System.out.println("Nœmero de pairings antes da evolu‹o = " + pairings.size());
	}

	private void optimizeInitialIndividue(Individue individue) {
		history.clear();
		for (int i = 0; i < individueImprovements; i++)
			doOptimization(individue);
	}
		
	@Override
	protected Individue getChild(long generation) {
		while (true) {
			Individue child = getFeasibleChild(generation);		
			if (random.nextDouble() < optimizationProbability)
				doOptimization(child);
			if (!population.contains(child))
				return child;
		}
	}
	
	private void doOptimization(Individue individue) {
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
		numberOfPairings += solver.getNumberOfPairings();
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
	
	public double getInitialSolutionTime() {
		return initialSolver.getSolutionTime();
	}
}
