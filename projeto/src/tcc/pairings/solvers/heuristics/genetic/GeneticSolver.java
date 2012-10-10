package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.generators.PairingsGenerator;
import tcc.pairings.io.outputers.MemoryOutputer;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.solvers.BasicSolver;
import tcc.pairings.solvers.Solution;

public class GeneticSolver extends BasicSolver {
	protected static double deadheadingPenalty = 1.0;
	protected static int mutationSize = 3;
	protected int populationSize = 1000;
	protected long maxGenerations = 100000;
	protected int maxPairings = 500000;
	protected int outputStep = 1000;
	protected Population population;
	protected static HashMap<Leg, List<Pairing>> hash;
	
	public static double getDeadheadingPenalty() {
		return deadheadingPenalty;
	}

	public static void setDeadheadingPenalty(double deadheadingPenalty) {
		GeneticSolver.deadheadingPenalty = deadheadingPenalty;
	}

	public static int getMutationSize() {
		return mutationSize;
	}

	public static void setMutationSize(int mutationSize) {
		GeneticSolver.mutationSize = mutationSize;
	}
	
	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public long getMaxGenerations() {
		return maxGenerations;
	}

	public void setMaxGenerations(long maxGenerations) {
		this.maxGenerations = maxGenerations;
	}
	
	public int getMaxPairings() {
		return maxPairings;
	}

	public void setMaxPairings(int maxPairings) {
		this.maxPairings = maxPairings;
	}
	
	public Population getPopulation() {
		return population;
	}
	
	public static HashMap<Leg, List<Pairing>> getHash() {
		return hash;
	}

	public GeneticSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public GeneticSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}
	
	@Override
	protected void setOutputers() {
		memory = new MemoryOutputer(); 
		outputers = new Outputer[] { memory };
	}
	
	@Override
	protected void generatePairings() {
		PairingsGenerator generator = new PairingsGenerator(net, outputers, calculator);
		generator.setMaxPairings(maxPairings);
		generator.generate(bases);
		numberOfPairings = generator.getNumberOfPairings();
	}
	
	@Override
	protected void setOptimizer() {
		optimizer = null;
	}
	
	@Override
	protected Solution getOptimalSolution() {
		sort(memory.getPairings());
		buildHash();
		buildInitialPopulation();
		doGenerations();
		return getSolutionFromPopulation();
	}

	private void sort(List<Pairing> pairings) {
		Collections.sort(pairings, new Comparator<Pairing>() {  
            public int compare(Pairing p1, Pairing p2) {
            	if (Math.abs(p1.getCost() - p2.getCost()) < 0.000001)
            		return p1.getNumberOfLegs() > p2.getNumberOfLegs() ? -1 : 1;
            	return p1.getCost() < p2.getCost() ? -1 : 1;
            }  
        });
	}

	private void buildHash() {
		hash = new HashMap<Leg, List<Pairing>>();
		for (Leg leg: legs)
			for (Pairing pairing: memory.getPairings())
				if (pairing.contains(leg)) {
					if (!hash.containsKey(leg))
						hash.put(leg, new ArrayList<Pairing>());
					hash.get(leg).add(pairing);
				}	
	}
	
	private void buildInitialPopulation() {
		population = new Population();
		fillPopulation();
	}
	
	private void fillPopulation() {
		for (int i = 0; i < populationSize; i++) {
			Individue individue = new Individue(legs, memory.getPairings());
			individue.generateChromosome();
			individue.turnFeasible();
			individue.calculateFitness();
			population.add(individue);
			System.out.println(individue);
		}
	}
	
	protected void doGenerations() {
		population.sort();
		for (long generation = 0; generation < maxGenerations; generation++) {
			output(generation);
			Individue child;
			while (true) {
				Individue[] parents = population.getParents();
				child = parents[0].doCrossover(parents[1]);
				child.doMutation(population.getTheFittest());
				child.turnFeasible();
				if (!population.contains(child)) {
					break;
				}
			}
			child.calculateFitness();
			population.replace(child);
		}
	}
	
	protected void output(long generation) {
//		if (generation % outputStep == 0)
//			System.out.println(generation + "\t" + population.getTheFittest().getFitness());
		if (generation % outputStep == 0)
			System.out.println(generation + "\t" + population.getAverageFitness());
	}
	
	private Solution getSolutionFromPopulation() {
		Individue theFittest = population.getTheFittest();
		Solution solution = new Solution(theFittest.getChromosome());
		setDeadHeads(solution);
		setCostsWithDeadHeads(solution.getPairings());
		setSolutionCost(solution);
		
		System.out.println(solution.isAllLegsCovered(legs));
		System.out.println(solution.isCostRight());
		
		return solution;
	}
	
	protected void setDeadHeads(Solution solution) {
		for (Leg leg: legs) {
			int numberOfDeadHeads = getNumberOfDeadHeads(solution, leg);
			if (numberOfDeadHeads > 0)
				solution.setDeadHeads(leg, numberOfDeadHeads);
		}
	}

	private int getNumberOfDeadHeads(Solution solution, Leg leg) {
		int count = 0;
		for (Pairing pairing: solution.getPairings())
			if (pairing.contains(leg))
				count++;
		return count - 1;
	}
}
