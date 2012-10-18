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
import tcc.util.ResultsBuffer;

public class GeneticSolver extends BasicSolver {
	protected static double deadheadingPenalty = 1.0;
	protected int populationSize = 100;
	protected long maxGenerations = 10000;
	protected int maxPairings = 50000;
	protected int outputStep = 100;
	protected static int cutoff = 5;
	protected int mutationSize = 5;
	protected int mf = 5;
	protected int mc = 200; 
	protected double mg = 2.0;
	protected ResultsBuffer buffer;
	protected Population population;
	protected Individue best;
	protected List<Pairing> pairings;
	protected static HashMap<Leg, List<Pairing>> coverPairings;
	protected static List<Pairing> elite;
	protected static final Random random = new Random(0);
	
	public static double getDeadheadingPenalty() {
		return deadheadingPenalty;
	}

	public static void setDeadheadingPenalty(double deadheadingPenalty) {
		GeneticSolver.deadheadingPenalty = deadheadingPenalty;
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
	
	public int getMutationSize() {
		return mutationSize;
	}

	public void setMutationSize(int mutationSize) {
		this.mutationSize = mutationSize;
	}
	
	public int getMf() {
		return mf;
	}

	public void setMf(int mf) {
		this.mf = mf;
	}

	public int getMc() {
		return mc;
	}

	public void setMc(int mc) {
		this.mc = mc;
	}

	public double getMg() {
		return mg;
	}

	public void setMg(double mg) {
		this.mg = mg;
	}
	
	public ResultsBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ResultsBuffer buffer) {
		this.buffer = buffer;
	}
	
	public Population getPopulation() {
		return population;
	}
	
	public static HashMap<Leg, List<Pairing>> getCoverPairings() {
		return coverPairings;
	}
	
	public static List<Pairing> getElite() {
		return elite;
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
		setPairings();
		setCoverPairings();
		setElite();
		setInitialPopulation();
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
	
	private void setPairings() {
		//pairings = new HashSet<Pairing>(memory.getPairings());
		pairings = memory.getPairings();
	}

	private void setCoverPairings() {
		coverPairings = new HashMap<Leg, List<Pairing>>();
		for (Leg leg: legs)
			for (Pairing pairing: pairings)
				if (pairing.contains(leg)) {
					if (!coverPairings.containsKey(leg))
						coverPairings.put(leg, new ArrayList<Pairing>());
					coverPairings.get(leg).add(pairing);
				}	
	}
	
	protected void setElite() {
		elite = new ArrayList<Pairing>();
		for (List<Pairing> pairings: coverPairings.values()) {
			int maxIndex = Math.min(cutoff, pairings.size()) - 1;
			elite.addAll(pairings.subList(0, maxIndex));
		}
	}
	
	private void setInitialPopulation() {
		population = new Population();
		fillPopulation();
		best = population.getTheFittest();
		System.out.println(population);
	}
	
	protected void fillPopulation() {
		int i = 0;
		while (i < populationSize) {
			Individue individue = new Individue(legs, pairings);
			individue.generateChromosome();
			individue.turnFeasible();
			if (!population.contains(individue)) {
				individue.calculateFitness();
				population.add(individue);
				i++;
			}		
		}
	}
	
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
	
	protected void output(long generation) {
		if (generation % outputStep == 0) {
			double average = population.getAverageFitness();
			System.out.println(generation + "\t" + average);
			if (buffer != null)
				buffer.output(generation + "\t" + average);
		}
	}

	protected Individue getChild(long generation) {
		while (true) {
			Individue[] parents = population.getParents();
			Individue child = parents[0].doCrossover(parents[1]);
			child.doMutation(population.getTheFittest(), getNumberOfMutatingGenes(generation));
			child.turnFeasible();
			if (!population.contains(child))
				return child;
		}
	}
	
	protected int getNumberOfMutatingGenes(long t) {
		double k = (double) mf / (1.0 + Math.exp(-4.0 * mg * (t - mc) / mf));
		return (int) Math.ceil(k);
	}
	
	private Solution getSolutionFromPopulation() {
		Solution solution = new Solution(best.getChromosome());
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
