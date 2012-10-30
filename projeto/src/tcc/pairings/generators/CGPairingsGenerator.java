package tcc.pairings.generators;

import java.util.LinkedList;
import java.util.Queue;

import com.sun.tools.javac.util.Pair;

import tcc.pairings.Leg;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.graph.Node;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.graph.networks.FlightNetworkPath;

public class CGPairingsGenerator extends PairingsGenerator {
	private Queue<Pair<Node<Leg>, FlightNetworkPath>> queue;

	public CGPairingsGenerator(FlightNetwork net, CostCalculator calculator) {
		super(net, calculator);
		queue = new LinkedList<Pair<Node<Leg>,FlightNetworkPath>>();
	}

	@Override
	protected void findPairings(Node<Leg> node) {
		//queue.offer();
		while (!queue.isEmpty()) {
			for (Node<Leg> neighbor : node.getNeighbors()) {
			//	if ()
			}
		}
	}
}
