package tcc.pairings;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import tcc.pairings.graph.Edge;
import tcc.pairings.graph.networks.EdgeType;
import tcc.pairings.graph.networks.FlightNetworkPath;
import tcc.pairings.rules.Rules;
import tcc.util.DateUtil;

public class Pairing {
	private int number;
	private double cost;
	private double costWithDeadHeads;
	private List<Duty> duties;
	
	public int getNumber() {
		return number;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public double getCostWithDeadHeads() {
		return costWithDeadHeads;
	}

	public void setCostWithDeadHeads(double costWithDeadHeads) {
		this.costWithDeadHeads = costWithDeadHeads;
	}

	public List<Duty> getDuties() {
		return duties;
	}
	
	public Pairing() {
		duties = new ArrayList<Duty>();
	}
	
	public Pairing(int number, FlightNetworkPath path) {
		this.number = number;
		cost = 1;
		duties = new ArrayList<Duty>();
		build(path);
	}
	
	private void build(FlightNetworkPath path) {
		Duty duty = new Duty();
		for (Edge<Leg> edge: path.getEdges()) {			
			if (edge.getType() == EdgeType.OVERNIGHT) {
				duties.add(duty);
				duty = new Duty();	
			}
			duty.addLeg(new DutyLeg(edge.getIn().getInfo()));
		}
		duties.add(duty);
	}
	
	public void addDuty(Duty duty) {
		duties.add(duty);
	}
	
	public int getNumberOfDuties() {
		return duties.size();
	}
	
	public Leg getFirstLeg() {
		if (!duties.isEmpty() && !duties.get(0).getLegs().isEmpty()) 
			return duties.get(0).getLegs().get(0);
		return null;
	}
	
	public Leg getLastLeg() {
		if (!duties.isEmpty()) {
			Duty lastDuty = duties.get(duties.size() - 1);
			int numberOfLegs = lastDuty.getLegs().size();
			if (numberOfLegs > 0)
				return lastDuty.getLegs().get(numberOfLegs - 1);
		}
		return null;
	}
	
	public boolean containsAny(List<DutyLeg> legs) {
		for (Leg leg: legs)
			if (contains(leg))
				return true;
		return false;
	}

	public boolean contains(Leg leg) {
		for (Duty duty: duties)
			if (duty.contains(leg))
				return true;
		return false;
	}
		
	public int getExcessTime() {
		int excess = 0;
		for (int i = 0; i < duties.size(); i++)
			excess += duties.get(i).getExcessTime() + getOvernightExcessTime(i);
		return excess;
	}
	
	private int getOvernightExcessTime(int index) {
		if (index < duties.size() - 1) {
			Duty previous = duties.get(index);
			Duty next = duties.get(index + 1);
			int rest = DateUtil.difference(previous.getLastLeg().getArrival(), next.getFirstLeg().getDeparture());
			return (rest - Rules.MIN_REST_TIME);
		}
		return 0;
	}
	
	public int getFlightTime() {
		int flight = 0;
		for (Duty duty: duties)
			flight += duty.getFlightTime();			
		return flight;
	}
	
	public int getDutyTime() {
		int dutyTime = 0;
		for (Duty duty: duties)
			dutyTime += duty.getDutyTime();			
		return dutyTime;
	}
	
	public int getDuration() {
		Leg first = getFirstLeg();
		Leg last = getLastLeg();
		return DateUtil.difference(first.getDeparture(), last.getArrival());
	}
	
	public void setAllLegsAsDH() {
		for (Duty duty: duties)
			duty.setAllLegsAsDH();
	}
	
	public int getNumberOfLegs() {
		return getLegs().size();
	}
	
	public List<DutyLeg> getLegs() {
		List<DutyLeg> legs = new ArrayList<DutyLeg>();
		for (Duty duty: duties)
			legs.addAll(duty.getLegs());
		return legs;
	}
	
	public void setDHIfContains(Leg leg) {
		List<DutyLeg> legs = getLegs();
		for (DutyLeg dutyLeg: legs)
			if (dutyLeg.equals(leg)) {
				dutyLeg.setDeadHead(true);
				return;
			}
	}

	private void appendDuties(StringBuilder sb) {
		int dutyNumber = 0;
		for(Duty duty: duties) {
			sb.append("\tDuty ").append(++dutyNumber).append('\n');
			sb.append(duty.toString());
		}
	}
	
	public double getMeanLegsPerDuty() {
		int total = 0;
		for (Duty duty: duties)
			total += duty.getNumberOfNonDHLegs();
		return (double) total / duties.size();
	}
	
	public double getMeanFlightTimePerDuty() {
		double total = 0.0;
		for (Duty duty: duties)
			total += duty.getFlightTime() / 60.0;
		return total / duties.size();
	}
	
	public int getNumberOfDHLegs() {
		int total = 0;
		for (Duty duty: duties)
			total += duty.getNumberOfDHLegs();
		return total;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((duties == null) ? 0 : duties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pairing other = (Pairing) obj;
		if (Double.doubleToLongBits(cost) != Double.doubleToLongBits(other.cost))
			return false;
		if (duties == null) {
			if (other.duties != null)
				return false;
		} else if (!duties.equals(other.duties))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuilder sb = new StringBuilder("Pairing ");
		sb.append(number).append(" - Cost ").append(df.format(getCost())).append('\n');
		appendDuties(sb);
		return sb.toString();
	}
}
