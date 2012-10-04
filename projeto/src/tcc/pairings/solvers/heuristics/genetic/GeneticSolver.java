package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
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
	public static double DEADHEADING_PENALTY = 5.0;
	public static int MUTATION_SIZE = 8;
	
	private int populationSize = 50;
	private int maxGenerations = 1000000;
	private int maxPairings = 50000;
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
		buildHash();
		buildInitialPopulation();
		doGenerations();
		Solution solution = getSolutionFromPopulation();
		// Para fins de testes
		System.out.println(solution.isAllLegsCovered(legs));
		System.out.println(solution.isCostRight());
		return solution;
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
			System.out.println("Indiv’duo " + (i + 1));
			System.out.println(individue);
		}
	}
	
	private void doGenerations() {
		for (int i = 0; i < maxGenerations; i++) {
			if (i % 100 == 0)
				System.out.println("Gera‹o " + i + ", Best Fitness = " + population.getTheFittest().getFitness());
			population.sort();
			Individue[] parents = population.getParents();
			Individue child = parents[0].doCrossover(parents[1]);
			child.doMutation(population.getTheFittest());
			child.turnFeasible();
			child.calculateFitness();
			population.replace(child);
		}
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
}
