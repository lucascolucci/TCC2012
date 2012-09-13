package tcc.pairings.solvers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.io.TerminalOutputer;

public class Solution {
	private List<Pairing> pairings;
	private double cost;
	
	public List<Pairing> getPairings() {
		return pairings;
	}
	
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Solution() {
		pairings = new ArrayList<Pairing>();
	}

	public Solution(List<Pairing> pairings) {
		this.pairings = pairings;
	}

	public void addPairing(Pairing pairing) {
		pairings.add(pairing);
	}

	public void removePairing(Pairing pairing) {
		pairings.remove(pairing);
	}

	public void clear() {
		pairings.clear();
	}

	public double getPairingsCost() {
		double total = 0.0;
		for (Pairing pairing: pairings)
			total += pairing.getCost();
		return total;
	}

	public int getSize() {
		return pairings.size();
	}
	
	public void setDeadHeads(Leg leg, int numberOfDeadHeads) {
		List<Pairing> list = getPairingsContaining(leg);
		for (int i = 0; i < numberOfDeadHeads; i++)
			list.get(i).setLegAsDH(leg);
	}
	
	private List<Pairing> getPairingsContaining(Leg leg) {
		List<Pairing> result = new ArrayList<Pairing>();
		for (Pairing pairing: pairings) {
			if (pairing.contains(leg))
				result.add(pairing);
		}
		return result;
	}

	public void print() {
		DecimalFormat df = new DecimalFormat("#.###");
		new TerminalOutputer().output(pairings);
		System.out.println("Número de pairings = " + getSize());
		System.out.println("Custo dos pairings = " + df.format(getPairingsCost()));
		System.out.println("Custo da sulução = " + df.format(cost));
	}
}