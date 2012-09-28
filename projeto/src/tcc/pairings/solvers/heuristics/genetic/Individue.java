package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;

public class Individue {
	private static final double DEADHEADING_PENALTY = 3.0;
	
	private int size;
	private List<Leg> toCoverLegs;
	private List<Pairing> pairings;
	private boolean[] chromosome;
	private static Random random = new Random(0);
	
	public Individue(List<Leg> toCoverLegs, List<Pairing> pairings) {
		this.toCoverLegs = toCoverLegs;
		this.pairings = pairings;
		size = pairings.size();
		chromosome = new boolean[size];
	}
	
	public void generateChromosome() {
		for (int i = 0; i < size; i++)
			chromosome[i] = random.nextBoolean();
	}
	
	public void turnFeasible(HashMap<Leg, List<Pairing>> hash) {
		List<Leg> uncoveredLegs = getUncoveredLegs();
		for (Leg uncoveredLeg: uncoveredLegs) {
			Pairing chosen = getChosen(hash.get(uncoveredLeg));
			int index = pairings.indexOf(chosen);
			chromosome[index] = true;
		}
	}
	
	// TODO Aplicar heur’stica melhor
	private Pairing getChosen(List<Pairing> pairings) {
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
	
	public double getFitness() {
		double fitness = 0.0;
		for (int i = 0; i < size; i++)
			if (chromosome[i])
				fitness += pairings.get(i).getCost(); 
		fitness += DEADHEADING_PENALTY * getNumberOfDeadheadedFlights(); 
		return fitness;
	}
	
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
}
