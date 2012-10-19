package tcc.pairings.solvers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;

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
			list.get(i).setDHIfContains(leg);
	}
	
	private List<Pairing> getPairingsContaining(Leg leg) {
		List<Pairing> result = new ArrayList<Pairing>();
		for (Pairing pairing: pairings)
			if (pairing.contains(leg))
				result.add(pairing);
		return result;
	}
	
	public boolean isAllLegsCovered(List<Leg> legs) {
		List<DutyLeg> nonDHLegs = getNonDHLegs();
		if (legs.size() != nonDHLegs.size())
			return false;
		for (Leg leg: legs)
			if (!nonDHLegs.contains(leg))
				return false;
		return true;
	}
		
	private List<DutyLeg> getNonDHLegs() {
		List<DutyLeg> nonDHLegs = new ArrayList<DutyLeg>();
		for (Pairing pairing: pairings)
			for (DutyLeg leg: pairing.getLegs()) 
				if (!leg.isDeadHead())
					nonDHLegs.add(leg);
		return nonDHLegs;
	}
	
	public boolean isCostRight() {
		double plainCost = 0.0;
		double costWithDeadHeads = 0.0;
		for (Pairing pairing: pairings) {
			plainCost += pairing.getCost();
			costWithDeadHeads += pairing.getCostWithDeadHeads();
		}
		return (Math.abs(plainCost - this.getPairingsCost()) <=  0.001) 
				&& (Math.abs(costWithDeadHeads - cost) <= 0.001);
	}
	
	public double getMeanLegsPerDuty() {
		double total = 0.0;
		for (Pairing pairing: pairings)
			total += pairing.getMeanLegsPerDuty();
		return total / pairings.size();
	}
	
	public double getMeanFlightTimePerDuty() {
		double total = 0.0;
		for (Pairing pairing: pairings)
			total += pairing.getMeanFlightTimePerDuty();
		return total / pairings.size();
	}

	private int getNumberOfDeadHeads() {
		int total = 0;
		for (Pairing pairing: pairings)
			total += pairing.getNumberOfDHLegs();
		return total;		
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuilder sb = new StringBuilder();
		for (Pairing pairing: pairings)
			sb.append(pairing);
		sb.append("Nœmero de pairings = ").append(getSize()).append('\n');
		sb.append("Custo dos pairings = ").append(df.format(getPairingsCost())).append('\n');
		sb.append("Custo da solu‹o = ").append(df.format(cost)).append('\n');
		sb.append("Nœmero mŽdio de pernas por jornada = ").append(df.format(getMeanLegsPerDuty())).append('\n');
		sb.append("Horas mŽdia de voo por jornada = ").append(df.format(getMeanFlightTimePerDuty())).append('\n');
		sb.append("Nœmero de deadheads = ").append(getNumberOfDeadHeads());
		return sb.toString();
	}
}