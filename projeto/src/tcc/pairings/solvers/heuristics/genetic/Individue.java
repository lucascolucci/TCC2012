package tcc.pairings.solvers.heuristics.genetic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;

public class Individue {
	private int size;
	private List<Leg> toCoverLegs;
	private List<Pairing> pairings;
	private List<Pairing> chromosome;
	private double fitness;
	private static Random random = new Random(0);
	
	public List<Pairing> getChromosome() {
		return chromosome;
	}

	public void setChromosome(List<Pairing> chromosome) {
		this.chromosome = chromosome;
	}
	
	public double getFitness() {
		return fitness;
	}
		
	public Individue(List<Leg> toCoverLegs, List<Pairing> pairings) {
		this.toCoverLegs = toCoverLegs;
		this.pairings = pairings;
		size = pairings.size();
		chromosome = new ArrayList<Pairing>();
	}
	
	public void generateChromosome() {
		List<Pairing> possiblePairings = new ArrayList<Pairing>(pairings);
		while (!possiblePairings.isEmpty()) {
			int size = possiblePairings.size();
			Pairing selected = possiblePairings.get(random.nextInt(size));
			chromosome.add(selected);
			trimPossiblePairings(possiblePairings, selected.getLegs());
		}
	}
	
	private void trimPossiblePairings(List<Pairing> possiblePairings, List<DutyLeg> legs) {
		List<Pairing> toBeRemoved = new ArrayList<Pairing>();
		for (Pairing pairing: possiblePairings)
			if (pairing.containsSome(legs))
				toBeRemoved.add(pairing);
		possiblePairings.removeAll(toBeRemoved);
	}

	public void turnFeasible() {
		List<Leg> uncoveredLegs = getUncoveredLegs();
		for (Leg uncoveredLeg: uncoveredLegs) {
			Pairing chosen = selectPairingToCoverLeg(GeneticSolver.getHash().get(uncoveredLeg));
			chromosome.add(chosen);
		}
	}
	
	// TODO Aplicar heur’stica melhor
	private Pairing selectPairingToCoverLeg(List<Pairing> pairings) {
		int size = pairings.size();
		return pairings.get(random.nextInt(size));
	}
	
	public List<Leg> getUncoveredLegs() {
		List<Leg> uncoveredLegs = new ArrayList<Leg>();
		for (Leg leg: toCoverLegs) {
			boolean isCovered = false;
			for (Pairing pairing: chromosome)
				if (pairing.contains(leg)) {
					isCovered = true;
					break;
				}
			if (!isCovered)
				uncoveredLegs.add(leg);
		}
		return uncoveredLegs;
	}
	
	public void calculateFitness() {
		fitness = 0.0;
		for (Pairing pairing: chromosome)
			fitness += pairing.getCost(); 
		fitness += GeneticSolver.DEADHEADING_PENALTY * getNumberOfDeadheadedFlights(); 
	}
	
	private int getNumberOfDeadheadedFlights() {
		int total = 0;
		for (Leg leg: toCoverLegs)
			for (Pairing pairing: chromosome)
				if (pairing.contains(leg))
					total++;
		return total - toCoverLegs.size();
	}
	
	public Individue doCrossover(Individue other) {
		Individue individue = new Individue(toCoverLegs, pairings);
		individue.setChromosome(getCrossoverChromosome(other));
		return individue;
	}

	private List<Pairing> getCrossoverChromosome(Individue other) {
		List<Pairing> crossover = new ArrayList<Pairing>(); 
		for (Pairing pairing: chromosome)
			if (other.getChromosome().contains(pairing))
				crossover.add(pairing);
			else
				if (random.nextBoolean())
					crossover.add(pairing);
		return crossover;
	}
	
	public void doMutation(Individue theFittest) {
		double prob = theFittest.getOnesDensity();
		for (int i = 0; i < GeneticSolver.MUTATION_SIZE; i++)
			mutatePairing(pairings.get(random.nextInt(size)), prob);
	}

	private void mutatePairing(Pairing pairing, double prob) {
		double r = random.nextDouble();
		if (r < prob) 
			if (!chromosome.contains(pairing))
				chromosome.add(pairing);
		else
			if (chromosome.contains(pairing))
				chromosome.remove(pairing);
	}
	
	public double getOnesDensity() {
		return (double) chromosome.size() / size;
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuilder sb = new StringBuilder();
		sb.append("- Fitness do indiv’duo = ").append(df.format(getFitness())).append('\n');
		sb.append("- Tamanho do cromossomo = ").append(chromosome.size());
		return sb.toString(); 
	}
}
