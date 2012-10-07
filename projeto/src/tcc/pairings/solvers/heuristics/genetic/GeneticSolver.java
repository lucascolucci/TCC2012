package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.generators.PairingsGenerator;
import tcc.pairings.io.outputers.MemoryOutputer;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.solvers.BasicSolver;
import tcc.pairings.solvers.Solution;

public class GeneticSolver extends BasicSolver {
	public static double DEADHEADING_PENALTY = 1.0;
	public static int MUTATION_SIZE = 10;
	public static double CUTOFF_FACTOR = 0.50;
	
	private int populationSize = 10;
	private int maxGenerations = 500000;
	private int maxPairings = 500000;
	private int outputStep = 1000;
	
	private Population population;
	private static HashMap<Leg, List<Pairing>> hash;
	
	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public int getMaxGenerations() {
		return maxGenerations;
	}

	public void setMaxGenerations(int maxGenerations) {
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
	protected void generatePairings(Base... bases) {
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
		preprocessHash();
		buildInitialPopulation();
		doGenerations();
		return getSolutionFromPopulation();
	}

	private void preprocessHash() {
		buildHash();
		sortHash();
		cutoffHash();
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
	
	private void sortHash() {
		for (List<Pairing> pairings: hash.values())
			sortByCost(pairings);
	}

	private void sortByCost(List<Pairing> pairings) {
		Collections.sort(pairings, new Comparator<Pairing>() {  
            public int compare(Pairing p1, Pairing p2) {  
                return p1.getCost() < p2.getCost() ? -1 : 1;  
            }  
        });  
	}
	
	private void cutoffHash() {
		for (List<Pairing> pairings: hash.values()) {
			int cutoffSize = (int) Math.round(pairings.size() * CUTOFF_FACTOR);
			int size = Math.min(pairings.size() - 1, cutoffSize);
			for (int i = 0; i < size; i++)
				pairings.remove(pairings.size() - 1);
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
	
	private void doGenerations() {
		for (int generation = 0; generation < maxGenerations; generation++) {
			population.sort();
			output(generation);
			Individue[] parents = population.getParents();
			Individue child = parents[0].doCrossover(parents[1]);
			child.doMutation(population.getTheFittest());
			child.turnFeasible();
			child.calculateFitness();
			population.replace(child);
		}
	}
	
	private void output(int generation) {
		if (generation % outputStep == 0)
			System.out.println(generation + "\t" + population.getTheFittest().getFitness());
	}
	
	private Solution getSolutionFromPopulation() {
		Individue theFittest = population.getTheFittest();
		Solution solution = new Solution(theFittest.getChromosome());
		setDeadHeads(solution);
		setSolutionCost(solution);
		return solution;
	}
	
	private void setDeadHeads(Solution solution) {
		for (Leg leg: legs) {
			int numberOfDeadHeads = getNumberOfDeadHeads(solution, leg);
			if (numberOfDeadHeads > 0)
				solution.setDeadHeads(leg, numberOfDeadHeads);
		}
		setCostsWithDeadHeads(solution.getPairings());
	}

	private int getNumberOfDeadHeads(Solution solution, Leg leg) {
		int count = 0;
		for (Pairing pairing: solution.getPairings())
			if (pairing.contains(leg))
				count++;
		return count - 1;
	}
	
	public void printHash() {
		for (Leg leg: hash.keySet()) {
			System.out.println(leg);
			for (Pairing pairing: hash.get(leg))
				System.out.println(pairing.getCost());
		}
	}
}
