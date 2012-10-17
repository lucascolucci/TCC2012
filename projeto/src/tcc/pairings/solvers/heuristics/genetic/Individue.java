package tcc.pairings.solvers.heuristics.genetic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;

public class Individue {
	private List<Leg> toCoverLegs;
	private List<Pairing> pairings;
	private Chromosome chromosome;
	private double fitness;

	public Chromosome getChromosome() {
		return chromosome;
	}

	public void setChromosome(Chromosome chromosome) {
		this.chromosome = chromosome;
	}

	public double getFitness() {
		return fitness;
	}

	public Individue(List<Leg> toCoverLegs, List<Pairing> pairings) {
		this.toCoverLegs = toCoverLegs;
		this.pairings = pairings;
		chromosome = new Chromosome();
	}

	public void generateChromosome() {
		HashMap<Leg, Integer> legCoverageCount = getNewLegCoverageCount();
		addPairingsToCoverLegs(legCoverageCount);
		removeRedundantPairingsRandomly(legCoverageCount);
	}

	private HashMap<Leg, Integer> getNewLegCoverageCount() {
		HashMap<Leg, Integer> legCoverageCount = new HashMap<Leg, Integer>();
		for (Leg leg : toCoverLegs)
			legCoverageCount.put(leg, 0);
		return legCoverageCount;
	}
	
	private void addPairingsToCoverLegs(HashMap<Leg, Integer> legCoverageCount) {
		for (Leg leg : toCoverLegs) {
			Pairing selected = getRandomPairingToCoverLeg(leg);
			chromosome.add(selected);
			incrementLegCoverageCount(legCoverageCount, selected);
		}
	}

	private Pairing getRandomPairingToCoverLeg(Leg leg) {
		List<Pairing> coveringPairings = GeneticSolver.getCoverPairings().get(leg);
		int size = Math.min(GeneticSolver.getCutoff(), coveringPairings.size());
		int randomIndex = GeneticSolver.random.nextInt(size);
		return coveringPairings.get(randomIndex);
	}
	
	private void incrementLegCoverageCount(HashMap<Leg, Integer> legCoverageCount, Pairing pairing) {
		for (Leg leg : pairing.getLegs()) {
			int value = legCoverageCount.get(leg);
			legCoverageCount.put(leg, value + 1);
		}
	}
	
	private void removeRedundantPairingsRandomly(HashMap<Leg, Integer> legCoverageCount) {
		Chromosome cloned = chromosome.clone();
		while (!cloned.isEmpty()) {
			int randomIndex = GeneticSolver.random.nextInt(cloned.size());
			Pairing selected = cloned.get(randomIndex);
			cloned.remove(selected);
			if (isRedundant(legCoverageCount, selected)) {
				chromosome.remove(selected);
				decrementLegCoverageCount(legCoverageCount, selected);
			}
		}
	}

	private boolean isRedundant(HashMap<Leg, Integer> legCoverageCount, Pairing pairing) {
		for (Leg leg : pairing.getLegs())
			if (legCoverageCount.get(leg) < 2)
				return false;
		return true;
	}
	
	private void decrementLegCoverageCount(HashMap<Leg, Integer> legCoverageCount, Pairing pairing) {
		for (Leg leg : pairing.getLegs()) {
			int value = legCoverageCount.get(leg);
			legCoverageCount.put(leg, value - 1);
		}
	}

	public void turnFeasible() {
		HashMap<Leg, Integer> legCoverageCount = getLegCovereageCount();
		List<Leg> uncoveredLegs = getUncoveredLegs(legCoverageCount);
		while (!uncoveredLegs.isEmpty()) {
			Pairing selected = getMinRatioPairing(uncoveredLegs);
			chromosome.add(selected);
			incrementLegCoverageCount(legCoverageCount, selected);
			updateUncoveredLegs(selected, uncoveredLegs);
			removeRedundatPairings(legCoverageCount);
		}
	}

	private HashMap<Leg, Integer> getLegCovereageCount() {
		HashMap<Leg, Integer> legCoverageCount = new HashMap<Leg, Integer>();
		for (Leg leg : toCoverLegs) {
			int value = 0;
			for (Pairing pairing : chromosome.getGenes())
				if (pairing.contains(leg))
					value++;
			legCoverageCount.put(leg, value);
		}
		return legCoverageCount;
	}
	
	private List<Leg> getUncoveredLegs(HashMap<Leg, Integer> legCoverageCount) {
		List<Leg> uncoveredLegs = new ArrayList<Leg>();
		for (Leg leg : legCoverageCount.keySet())
			if (legCoverageCount.get(leg) == 0)
				uncoveredLegs.add(leg);
		return uncoveredLegs;
	}
	
	private Pairing getMinRatioPairing(List<Leg> uncoveredLegs) {
		Leg leg = uncoveredLegs.get(0);
		Pairing selected = null; double min = Double.MAX_VALUE;
		List<Pairing> coverPairings = GeneticSolver.getCoverPairings().get(leg);
		for (Pairing pairing : coverPairings) {
			int numberOfCoveredLegs = getNumberOfCoveredLegs(pairing, uncoveredLegs);
			//int numberOfRepeatedLegs = getNumberOfRepeatedLegs(pairing);
			//double ratio = pairing.getCost() / numberOfCoveredLegs;
			double ratio = pairing.getCost() * (pairing.getNumberOfLegs() / numberOfCoveredLegs);
			//double ratio = pairing.getCost() * numberOfRepeatedLegs;
			//double ratio = (double) (numberOfRepeatedLegs / numberOfCoveredLegs) * pairing.getCost();
			if (ratio < min) {
				selected = pairing; min = ratio;
			}
		}
		return selected;
	}

	private int getNumberOfCoveredLegs(Pairing pairing, List<Leg> uncoveredLegs) {
		int count = 0;
		for (Leg leg : uncoveredLegs)
			if (pairing.contains(leg))
				count++;
		return count;
	}
	
//	private int getNumberOfRepeatedLegs(Pairing pairing) {
//		int count = 0;
//		for (Pairing chromosomePairng: chromosome.getGenes())
//			for (Leg leg: pairing.getLegs())
//				if (chromosomePairng.contains(leg))
//					count++;
//		return count;
//	}

	private void updateUncoveredLegs(Pairing pairing, List<Leg> uncoveredLegs) {
		List<Leg> clonedLegs = new ArrayList<Leg>(uncoveredLegs);
		for (Leg leg : clonedLegs)
			if (pairing.contains(leg))
				uncoveredLegs.remove(leg);
	}
	
	private void removeRedundatPairings(HashMap<Leg, Integer> legCoverageCount) {
		Chromosome cloned = chromosome.clone();
		for (int i = cloned.size() - 1; i >= 0; i--) {
			Pairing selected = cloned.get(i); 
			if (isRedundant(legCoverageCount, selected)) {
				chromosome.remove(selected);
				decrementLegCoverageCount(legCoverageCount, selected);
			}
		}
	}

	public Individue doCrossover(Individue other) {
		Individue individue = new Individue(toCoverLegs, pairings);
		individue.setChromosome(getCrossoverChromosome(other));
		return individue;
	}

	private Chromosome getCrossoverChromosome(Individue other) {
		Chromosome crossover = new Chromosome(); 
		for (Pairing pairing: chromosome.getGenes())
			if (other.getChromosome().contains(pairing))
				crossover.add(pairing);
			else if (GeneticSolver.random.nextBoolean())
				crossover.add(pairing);
		for (Pairing pairing: other.getChromosome().getGenes())
			if (!chromosome.contains(pairing))
				if (GeneticSolver.random.nextBoolean())
					crossover.add(pairing);
		return crossover;
	}
	
	public void doMutation(Individue theFittest, int k) {
		double prob = (double) chromosome.size() / pairings.size();
		for (int i = 0; i < k; i++) {
			List<Pairing> elite = GeneticSolver.getElite();
			int randomIndex = GeneticSolver.random.nextInt(elite.size());
			mutatePairing(elite.get(randomIndex), prob);
		}
	}
	
	private void mutatePairing(Pairing pairing, double prob) {
		double r = GeneticSolver.random.nextDouble();
		if (r < prob) {
			if (!chromosome.contains(pairing))
				chromosome.add(pairing);
		} else if (chromosome.contains(pairing))
			chromosome.remove(pairing);
	}
	
	public void doMutation(int k) {
		for (int i = 0; i < k; i++)
			mutateRandomElitePairing();
	}
	
	private void mutateRandomElitePairing() {
		List<Pairing> elite = GeneticSolver.getElite();
		int randomIndex = GeneticSolver.random.nextInt(elite.size());
		Pairing mutating = elite.get(randomIndex);
		if (chromosome.contains(mutating))
			chromosome.remove(mutating);
		else
			chromosome.add(mutating);
	}
	
	public void calculateFitness() {
		fitness = 0.0;
		for (Pairing pairing : chromosome.getGenes())
			fitness += pairing.getCost();
		fitness += GeneticSolver.getDeadheadingPenalty() * getNumberOfDeadheadedFlights();
	}

	private int getNumberOfDeadheadedFlights() {
		int total = 0;
		for (Leg leg : toCoverLegs)
			for (Pairing pairing : chromosome.getGenes())
				if (pairing.contains(leg))
					total++;
		return total - toCoverLegs.size();
	}

	public boolean isDuplicate(Individue other) {
		Chromosome otherChromosome = other.getChromosome();
		return chromosome.isDuplicate(otherChromosome);
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuilder sb = new StringBuilder();
		sb.append("\tIndiv’duo:\n");
		sb.append("\t\tColunas = ").append(chromosome.size()).append('\n');
		sb.append("\t\tFitness = ").append(df.format(getFitness()));
		return sb.toString();
	}
}
