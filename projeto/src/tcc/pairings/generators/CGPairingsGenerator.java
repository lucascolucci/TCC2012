package tcc.pairings.generators;

import java.util.LinkedList;
import java.util.Queue;

import tcc.pairings.Leg;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.graph.Node;
import tcc.pairings.graph.networks.FlightNetwork;

public class CGPairingsGenerator extends BasicGenerator {
	private Queue<Node<Leg>> queue;

	public CGPairingsGenerator(FlightNetwork net, CostCalculator calculator) {
		super(net, calculator);
		queue = new LinkedList<Node<Leg>>();
	}

	@Override
	protected void findPairings(Node<Leg> node) {
		queue.offer(node);
		while (!queue.isEmpty()) {
		
		}
	}
	
	@Override
	protected void output() {
		
	}
}
