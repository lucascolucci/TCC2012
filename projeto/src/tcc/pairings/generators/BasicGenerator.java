package tcc.pairings.generators;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.graph.Node;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.graph.networks.FlightNetworkPath;
import tcc.pairings.graph.networks.SpecialNode;

public abstract class BasicGenerator {
	protected FlightNetwork net;
	protected CostCalculator calculator;
	protected int numberOfPairings;
	protected Base base;
	protected FlightNetworkPath path;
	protected SpecialNode source;
	protected SpecialNode sink;
	
	public int getNumberOfPairings() {
		return numberOfPairings;
	}
	
	public void setNumberOfPairings(int numberOfPairings) {
		this.numberOfPairings = numberOfPairings;
	}
		
	public BasicGenerator(FlightNetwork net) {
		this(net, null);
	}
	
	public BasicGenerator(FlightNetwork net, CostCalculator calculator) {
		this.net = net;
		this.calculator = calculator;
		numberOfPairings = 0;
	}
	
	public void generate(Base... bases) {
		for (Base base: bases)
			generate(base);
	}
		
	public void generate(Base base) {
		initialSetUp(base);
		addSourceAndSink();
		findPairings(source);
		removeSourceAndSink();
	}

	private void initialSetUp(Base base) {
		this.base = base;
		path = new FlightNetworkPath();
		setSourceAndSink();
	}

	private void setSourceAndSink() {
		source = new SpecialNode(base);
		sink = new SpecialNode(base);
	}
	
	private void addSourceAndSink() {
		net.addSource(source);
		net.addSink(sink);
	}
	
	private void removeSourceAndSink() {
		net.removeNode(source);
		net.removeNode(sink);
	}

	protected abstract void findPairings(Node<Leg> node);
	
	protected abstract void output();
}
