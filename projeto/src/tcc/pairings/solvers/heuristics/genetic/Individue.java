package tcc.pairings.solvers.heuristics.genetic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.sun.tools.javac.util.Pair;

import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;

public class Individue {
	private int size;
	private List<Leg> toCoverLegs;
	private List<Pairing> pairings;
	private List<Pairing> chromosome;
	private double fitness;
	private static long number = 0;
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
		number++;
	}
	
	public void generateChromosome() {
		List<Pairing> possiblePairings = new ArrayList<Pairing>(pairings);
		while (!possiblePairings.isEmpty()) {
			int size = possiblePairings.size();
			Pairing selected = possiblePairings.get(random.nextInt(size));
			chromosome.add(selected);
			updatePossiblePairings(possiblePairings, selected.getLegs());
		}
	}
	
	private void updatePossiblePairings(List<Pairing> possiblePairings, List<DutyLeg> legs) {
		List<Pairing> toBeRemoved = new ArrayList<Pairing>();
		for (Pairing pairing: possiblePairings)
			if (pairing.containsAny(legs))
				toBeRemoved.add(pairing);
		possiblePairings.removeAll(toBeRemoved);
	}

	public void turnFeasible() {
		List<Leg> uncoveredLegs = getUncoveredLegs();
		while (!uncoveredLegs.isEmpty()) {
			int randomIndex = random.nextInt(uncoveredLegs.size());
			Leg uncoveredLeg = uncoveredLegs.get(randomIndex);
			List<Pairing> pairings = GeneticSolver.getHash().get(uncoveredLeg);
			Pairing selected = selectPairingToCoverLeg(pairings, uncoveredLegs);
			chromosome.add(selected);
			updateUncoveredLegs(uncoveredLegs, selected);
		}
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
	
	private Pairing selectPairingToCoverLeg(List<Pairing> pairings, List<Leg> uncoveredLegs) {
		List<Pair<Pairing, Integer>> list = getDHCountList(pairings);
		sortPairs(list); 	
		cutoffPairs(list);
		list = getCoverageCountList(getPairingsFromPairs(list), uncoveredLegs);
		sortPairs(list);
		cutoffPairs(list);
		return list.get(random.nextInt(list.size())).fst;		
	}
	
	private List<Pair<Pairing, Integer>> getDHCountList(List<Pairing> pairings) {
		List<Pair<Pairing, Integer>> list = new ArrayList<Pair<Pairing, Integer>>();
		for (Pairing pairing: pairings)
			list.add(new Pair<Pairing, Integer>(pairing, getNumberOfRepeatedLegs(pairing)));
		return list;
	}
		
	private int getNumberOfRepeatedLegs(Pairing pairing) {
		int total = 0;
		for (Leg leg: pairing.getLegs())
			for (Pairing chromosomePairing: chromosome)
				if (chromosomePairing.contains(leg))
					total++;
		return total;
	}
	
	private List<Pair<Pairing, Integer>> getCoverageCountList(List<Pairing> pairings, List<Leg> uncoveredLegs) {
		List<Pair<Pairing, Integer>> list = new ArrayList<Pair<Pairing, Integer>>();
		for (Pairing pairing: pairings)
			list.add(new Pair<Pairing, Integer>(pairing, getNumberOfCoveredLegs(uncoveredLegs, pairing)));
		return list;
	}

	private int getNumberOfCoveredLegs(List<Leg> uncoveredLegs, Pairing pairing) {
		int count = 0;
		for (Leg leg: uncoveredLegs)
			if (pairing.contains(leg))
				count++;
		return count;
	}
	
	private List<Pairing> getPairingsFromPairs(List<Pair<Pairing, Integer>> list) {
		List<Pairing> listPairings = new ArrayList<Pairing>();
		for (Pair<Pairing, Integer> pair: list)
			listPairings.add(pair.fst);
		return listPairings;
	}
		
	private void sortPairs(List<Pair<Pairing, Integer>> list) {
		Collections.sort(list, new Comparator<Pair<Pairing, Integer>>() {  
            public int compare(Pair<Pairing, Integer> p1, Pair<Pairing, Integer> p2) {  
                return p1.snd < p2.snd ? -1 : 1;  
            }  
        });
	}
	
	private void cutoffPairs(List<Pair<Pairing, Integer>> list) {
		int cutoffSize = (int) Math.round(list.size() * GeneticSolver.cutoffFactor);
		int size = Math.min(list.size() - 1, cutoffSize);
		for (int i = 0; i < size; i++)
			list.remove(list.size() - 1);
	}

	private void updateUncoveredLegs(List<Leg> uncoveredLegs, Pairing pairing) {
		List<Leg> cloneLegs = new ArrayList<Leg>(uncoveredLegs);
		for (Leg leg: cloneLegs)
			if (pairing.contains(leg))
				uncoveredLegs.remove(leg);
	}
			
	public void calculateFitness() {
		fitness = 0.0;
		for (Pairing pairing: chromosome)
			fitness += pairing.getCost(); 
		fitness += GeneticSolver.deadheadingPenalty * getNumberOfDeadheadedFlights(); 
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
		for (Pairing pairing: other.getChromosome())
			if (!chromosome.contains(pairing))
				if (random.nextBoolean())
					crossover.add(pairing);
		return crossover;
	}
	
	public void doMutation(Individue theFittest) {
		double prob = theFittest.getOnesDensity();
		for (int i = 0; i < GeneticSolver.mutationSize; i++)
			mutatePairing(pairings.get(random.nextInt(size)), prob);
	}

	private void mutatePairing(Pairing pairing, double prob) {
		double r = random.nextDouble();
		if (r < prob) {
			if (!chromosome.contains(pairing))
				chromosome.add(pairing);
		} else
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
		sb.append("Indiv’duo ").append(number).append('\n');
		sb.append("- Pairings no cromossomo = ").append(chromosome.size()).append('\n');
		sb.append("- Fitness = ").append(df.format(getFitness()));
		return sb.toString(); 
	}
}
