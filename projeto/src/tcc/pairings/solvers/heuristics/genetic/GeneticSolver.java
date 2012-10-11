package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
	protected static int mutationSize = 5;
	protected int populationSize = 100;
	protected long maxGenerations = 10000;
	protected int maxPairings = 500000;
	protected int outputStep = 1000;
	protected static int cutoff = 5;
	protected Population population;
	protected static HashMap<Leg, List<Pairing>> coveringPairings;
	public static Random random = new Random(0);
	
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
	
	public static int getCutoff() {
		return cutoff;
	}
	
	public static void setCutOff(int cutoff) {
		GeneticSolver.cutoff = cutoff;
	}
	
	public Population getPopulation() {
		return population;
	}
	
	public static HashMap<Leg, List<Pairing>> getCoveringPairings() {
		return coveringPairings;
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
		sortGeneratedPairings();
		buildHash();
		buildInitialPopulation();
		doGenerations();
		return getSolutionFromPopulation();
	}

	private void sortGeneratedPairings() {
		Collections.sort(memory.getPairings(), new Comparator<Pairing>() {  
            public int compare(Pairing p1, Pairing p2) {
            	if (Math.abs(p1.getCost() - p2.getCost()) < 0.00001)
            		return p1.getNumberOfLegs() > p2.getNumberOfLegs() ? -1 : 1;
            	return p1.getCost() < p2.getCost() ? -1 : 1;
            }  
        });
	}

	private void buildHash() {
		coveringPairings = new HashMap<Leg, List<Pairing>>();
		for (Leg leg: legs)
			for (Pairing pairing: memory.getPairings())
				if (pairing.contains(leg)) {
					if (!coveringPairings.containsKey(leg))
						coveringPairings.put(leg, new ArrayList<Pairing>());
					coveringPairings.get(leg).add(pairing);
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
		}
		System.out.println(population);
	}
	
	protected void doGenerations() {
		for (long generation = 0; generation < maxGenerations; generation++) {
			output(generation);
			Individue child = getChild();
			child.calculateFitness();
			population.replace(child);
		}
	}

	private Individue getChild() {
		while (true) {
			Individue[] parents = population.getParents();
			Individue child = parents[0].doCrossover(parents[1]);
			child.doMutation(population.getTheFittest());
			child.turnFeasible();
			if (!population.contains(child))
				return child;
		}
	}
	
	protected void output(long generation) {
		if (generation % outputStep == 0)
			System.out.println(generation + "\t" + population.getAverageFitness());
	}
	
	private Solution getSolutionFromPopulation() {
		Individue theFittest = population.getTheFittest();
		Solution solution = new Solution(theFittest.getChromosome().getGenes());
		setDeadHeads(solution);
		setCostsWithDeadHeads(solution.getPairings());
		setSolutionCost(solution);		
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
