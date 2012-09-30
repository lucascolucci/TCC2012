package tcc.pairings.solvers.heuristics.genetic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;

public class Individue {
	private int size;
	private List<Leg> toCoverLegs;
	private List<Pairing> pairings;
	private boolean[] chromosome;
	private double fitness;
	private static Random random = new Random(0);
	
	public boolean[] getChromosome() {
		return chromosome;
	}

	public void setChromosome(boolean[] chromosome) {
		this.chromosome = chromosome;
	}
	
	public double getFitness() {
		return fitness;
	}
		
	public Individue(List<Leg> toCoverLegs, List<Pairing> pairings) {
		this.toCoverLegs = toCoverLegs;
		this.pairings = pairings;
		size = pairings.size();
		chromosome = new boolean[size];
		fitness = -1.0;
	}
	
	public void generateChromosome() {
		for (int i = 0; i < size; i++)
			chromosome[i] = random.nextBoolean();
	}
	
	public void turnFeasible() {
		List<Leg> uncoveredLegs = getUncoveredLegs();
		for (Leg uncoveredLeg: uncoveredLegs) {
			Pairing chosen = selectPairing(GeneticSolver.getHash().get(uncoveredLeg));
			int index = pairings.indexOf(chosen);
			chromosome[index] = true;
		}
	}
	
	// TODO Aplicar heur’stica melhor
	private Pairing selectPairing(List<Pairing> pairings) {
		int size = pairings.size();
		return pairings.get(random.nextInt(size));
	}
	
	public boolean isFeasible() {
		return getUncoveredLegs().size() == 0;
	}
	
	public List<Leg> getUncoveredLegs() {
		List<Leg> uncoveredLegs = new ArrayList<Leg>();
		List<Leg> legs = getLegs();
		for (Leg toCoverLeg: toCoverLegs) {
			boolean isCovered = false;
			for (Leg leg: legs)
				if (toCoverLeg.isDuplicate(leg)) {
					isCovered = true;
					break;
				}
			if (!isCovered)
				uncoveredLegs.add(toCoverLeg);
		}
		return uncoveredLegs;
	}
	
	public void calculateFitness() {
		fitness = 0.0;
		for (int i = 0; i < size; i++)
			if (chromosome[i])
				fitness += pairings.get(i).getCost(); 
		fitness += GeneticSolver.DEADHEADING_PENALTY * getNumberOfDeadheadedFlights(); 
	}
	
	// TODO - Gargalo
	private int getNumberOfDeadheadedFlights() {
		int total = 0;
		List<Leg> legs = getLegs();
		for (Leg toCoverLeg: toCoverLegs) { 
			List<Leg> duplicatedLegs = new ArrayList<Leg>();
			for (Leg leg: legs)
				if (toCoverLeg.isDuplicate(leg))
					duplicatedLegs.add(leg);
			total += duplicatedLegs.size() - 1;
			legs.removeAll(duplicatedLegs); // Pode dar problema.
		}
		return total;
	}
	
	private List<Leg> getLegs() {
		List<Leg> legs = new ArrayList<Leg>();
		for (int i = 0; i < size; i++)
			if (chromosome[i])
				legs.addAll(pairings.get(i).getLegs());
		return legs;
	}
	
	public Individue doCrossover(Individue other) {
		Individue individue = new Individue(toCoverLegs, pairings);
		individue.setChromosome(getCrossoverChromosome(other));
		return individue;
	}

	private boolean[] getCrossoverChromosome(Individue other) {
		boolean[] crossover = new boolean[size]; 
		for (int i = 0; i < size; i++)
			if (chromosome[i] == other.getChromosome()[i])
				crossover[i] = chromosome[i];
			else
				crossover[i] = random.nextBoolean();
		return crossover;
	}
	
	public void doMutation(Individue theFittest) {
		double prob = theFittest.getOnesDensity();
		for (int i = 0; i < GeneticSolver.MUTATION_SIZE; i++) {
			int index = random.nextInt(size);
			double r = random.nextDouble();
			if (r < prob) 
				chromosome[index] = true;
			else
				chromosome[index] = false;		
		}
	}
	
	public double getOnesDensity() {
		int count = 0;
		for (boolean gene: chromosome) {
			if (gene)
				count++;
		}
		return (double) count / size;
	}
	
	public List<Pairing> getSelectedPairings() {
		List<Pairing> selected = new ArrayList<Pairing>();
		for (int i = 0; i < size ; i++)
			if (chromosome[i])
				selected.add(this.pairings.get(i));
		return selected;
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		return "Fitness do indiv’duo = " + df.format(getFitness()); 
	}
}
