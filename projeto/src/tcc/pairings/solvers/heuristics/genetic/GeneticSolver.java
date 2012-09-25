package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.io.outputers.MemoryOutputer;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.solvers.BasicSolver;
import tcc.pairings.solvers.Solution;

public class GeneticSolver extends BasicSolver {
	private static final int INITIAL_POPULATION_SIZE = 100; 
	private static final int MAX_GENERATIONS = 1000;

	private HashMap<Leg, List<Pairing>> hash;
	private Population population;
	
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
		// TODO gerar um nœmero limitado de pairings
	}
	
	@Override
	protected void setOptimizer() {
		optimizer = null;
	}
	
	@Override
	protected Solution getOptimalSolution() {
		createHash();
		createInitialPopulation();
		doGenerations();
		return null;
	}
	
	private void createHash() {
		hash = new HashMap<Leg, List<Pairing>>();
		for (Leg leg: legs)
			for (Pairing pairing: memory.getPairings())
				if (pairing.contains(leg)) {
					if (!hash.containsKey(leg))
						hash.put(leg, new ArrayList<Pairing>());
					hash.get(leg).add(pairing);
				}		
	}
	
	private void createInitialPopulation() {
		population = new Population();
		fillPopulation();
	}
	
	private void fillPopulation() {
		for (int i = 0; i < INITIAL_POPULATION_SIZE; i++) {
			Individue individue = new Individue(legs, memory.getPairings());
			individue.born();
			individue.turnFeasible(hash);
			population.add(individue);
		}
	}
	
	private void doGenerations() {
		for (int i = 0; i < MAX_GENERATIONS; i++) {
			
		}
	}
}
