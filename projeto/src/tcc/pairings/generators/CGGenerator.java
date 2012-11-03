package tcc.pairings.generators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.graph.Edge;
import tcc.pairings.graph.Node;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.graph.networks.FlightNetworkNodeLabel;
import tcc.pairings.graph.networks.FlightNetworkPath;
import tcc.pairings.graph.networks.SpecialNode;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.rules.Rules;

public class CGGenerator extends BasicGenerator {
	private int maxPaths = 10; 
	private Queue<Node<Leg>> queue;
	private Node<Leg> current;
	private Node<Leg> neighbor;
	private FlightNetworkNodeLabel currentLabel;
	private FlightNetworkNodeLabel neighborLabel;
	
	public int getMaxPaths() {
		return maxPaths;
	}

	public void setMaxPaths(int maxPaths) {
		this.maxPaths = maxPaths;
	}
	
	public CGGenerator(FlightNetwork net, Outputer[] outputers) {
		this(net, outputers, null);
	}

	public CGGenerator(FlightNetwork net, Outputer[] outputers, CostCalculator calculator) {
		super(net, outputers, calculator);
		queue = new LinkedList<Node<Leg>>();
	}
	
	@Override
	protected void setSourceAndSink() {
		source = new SpecialNode(base);
		sink = new SpecialNode(base);
		sink.setLabel(new FlightNetworkNodeLabel());
	}

	@Override
	protected void findPairings(Node<Leg> node) {
		queue.offer(node);
		while (!queue.isEmpty())
			breadthFirstSearch();
		output();
	}

	private void breadthFirstSearch() {
		current = queue.poll();
		currentLabel = (FlightNetworkNodeLabel) current.getLabel();
		for (Edge<Leg> edge: current.getEdges()) {
			neighbor = edge.getIn(); 
			neighborLabel = (FlightNetworkNodeLabel) neighbor.getLabel();
			exploreTrough(edge);
		}
	}

	private void exploreTrough(Edge<Leg> edge) {
		switch (edge.getType()) {
		case FROM_SOURCE:
			exploreTroughSource(edge);
			break;
		case CONNECTION:
			exploreTroughConnection(edge);
			break;
		case OVERNIGHT:
			exploreTroughOvernight(edge);
			break;
		case TO_SINK:
			exploreTroughSink();
			break;
		}
	}
	
	private void exploreTroughSource(Edge<Leg> edge) {
		FlightNetworkPath path = new FlightNetworkPath();
		addNewDutyToPath(path, edge);
		setReducedCost(path);
		neighborLabel.addPath(path);
		queue.offer(neighbor);
	}
	
	private void exploreTroughConnection(Edge<Leg> edge) { 
		boolean flag = false;
		for (FlightNetworkPath path: currentLabel.getPaths())
			if (Rules.isPossibleToAppendConnection(path, edge))
				if (wasConnectionAdded(path, edge))
					flag = true;
		if (flag && !queue.contains(neighbor))
			queue.offer(neighbor);
	}

	private boolean wasConnectionAdded(FlightNetworkPath path, Edge<Leg> edge) {
		FlightNetworkPath clonedPath = path.clone();
		addConnectionToPath(clonedPath, edge);
		setReducedCost(clonedPath);
		if (dominanceCheck(clonedPath)) {
			neighborLabel.addPath(clonedPath);
			return true;
		}
		return false;
	}
	
	private void exploreTroughOvernight(Edge<Leg> edge) {
		boolean flag = false;
		for (FlightNetworkPath path: currentLabel.getPaths())
			if (Rules.isPossibleToAppendOvernight(path, edge, base))
				if (wasOvernightAdded(path, edge))
					flag = true;
		if (flag && !queue.contains(neighbor))
			queue.offer(neighbor);
	}

	private boolean wasOvernightAdded(FlightNetworkPath path, Edge<Leg> edge) {
		FlightNetworkPath clonedPath = path.clone();
		addNewDutyToPath(clonedPath, edge);
		setReducedCost(clonedPath);
		if (dominanceCheck(clonedPath)) {
			neighborLabel.addPath(clonedPath);
			return true;
		}
		return false;
	}

	private void exploreTroughSink() {
		boolean flag = false;
		for (FlightNetworkPath path: currentLabel.getPaths())
			if (dominanceCheck(path)) {
				neighborLabel.addPath(path);
				flag = true;
			}
		if (flag && !queue.contains(neighbor))
			queue.offer(neighbor);
	}

	private void setReducedCost(FlightNetworkPath path) {
		if (calculator != null) 
			calculator.setReducedCost(path);
		else
			path.updateReducedCost(neighborLabel.getDual());
	}
	
	private boolean dominanceCheck(FlightNetworkPath newPath) {
		for (FlightNetworkPath oldPath: neighborLabel.getPaths())
			if (oldPath.dominates(newPath))
				return false;
		removeDominatedPaths(newPath);
		return true;
	}

	private void removeDominatedPaths(FlightNetworkPath newPath) {
		List<FlightNetworkPath> list = new ArrayList<FlightNetworkPath>();
		for (FlightNetworkPath oldPath: neighborLabel.getPaths())
			if (newPath.dominates(oldPath))
				list.add(oldPath);
		neighborLabel.removePaths(list);
	}
	
	@Override
	protected void output() {
		FlightNetworkNodeLabel sinkLabel = (FlightNetworkNodeLabel) sink.getLabel();
		int count = 0;
		for (FlightNetworkPath path: sinkLabel.getPaths())
			if (path.getReducedCost() < 0)
				output(++count, path);
		numberOfPairings += count;
	}

	private void output(int count, FlightNetworkPath path) {
		Pairing pairing = new Pairing(count, path);
		if (calculator != null)
			calculator.setCost(pairing);
		for (Outputer outputer: outputers)
			outputer.output(pairing);
	}
}
