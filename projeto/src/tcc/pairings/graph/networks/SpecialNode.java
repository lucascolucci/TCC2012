package tcc.pairings.graph.networks;

import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.graph.Label;
import tcc.pairings.graph.Node;

public class SpecialNode extends Node<Leg> {
	private Base base;

	public Base getBase() {
		return base;
	}
	
	public void setLabel(Label label) {
		this.label = label;
	}
	
	public SpecialNode(Base base) {
		super();
		this.base = base;
	}
	
	public List<String> getAirports() {
		return base.getAirports();
	}
}
