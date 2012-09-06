package tcc.pairings;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import tcc.DateUtil;
import tcc.pairings.graph.Edge;
import tcc.pairings.graph.networks.EdgeType;
import tcc.pairings.graph.networks.FlightNetworkPath;

public class Pairing {
	private int number;
	private double cost;
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
	
	public boolean contains(Leg leg) {
		for (Duty duty: duties)
			if(duty.contains(leg))
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
	
	public void setAllDutiesAsDH() {
		for (Duty duty: duties)
			duty.setAllLegsAsDH();
	}
	
	public List<DutyLeg> getLegs() {
		List<DutyLeg> legs = new ArrayList<DutyLeg>();
		for (Duty duty: duties)
			legs.addAll(duty.getLegs());
		return legs;
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuilder sb = new StringBuilder("Pairing ");
		sb.append(number).append(" - Cost ").append(df.format(getCost())).append('\n');
		appendDuties(sb);
		return sb.toString();
	}

	private void appendDuties(StringBuilder sb) {
		int dutyNumber = 0;
		for(Duty duty: duties) {
			sb.append("\tDuty ").append(++dutyNumber).append('\n');
			sb.append(duty.toString());
		}
	}
}
