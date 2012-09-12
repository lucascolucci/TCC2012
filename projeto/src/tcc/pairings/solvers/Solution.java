package tcc.pairings.solvers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

	public void print() {
		DecimalFormat df = new DecimalFormat("#.###");
		new TerminalOutputer().output(pairings);
		System.out.println("Nœmero de pairings = " + getSize());
		System.out.println("Custo dos pairings = " + df.format(getPairingsCost()));
		System.out.println("Custo da sulução = " + df.format(cost));
	}
}