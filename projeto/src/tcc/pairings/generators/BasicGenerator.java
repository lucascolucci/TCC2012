package tcc.pairings.generators;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.graph.Edge;
import tcc.pairings.graph.Node;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.graph.networks.FlightNetworkEdgeLabel;
import tcc.pairings.graph.networks.FlightNetworkNodeLabel;
import tcc.pairings.graph.networks.FlightNetworkPath;
import tcc.pairings.graph.networks.SpecialNode;
import tcc.pairings.io.outputers.Outputer;

public abstract class BasicGenerator {
	protected FlightNetwork net;
	protected CostCalculator calculator;
	protected Outputer[] outputers;
	protected int numberOfPairings;
	protected Base base;
	protected SpecialNode source;
	protected SpecialNode sink;
	
	public int getNumberOfPairings() {
		return numberOfPairings;
	}
	
	public void setNumberOfPairings(int numberOfPairings) {
		this.numberOfPairings = numberOfPairings;
	}
		
	public BasicGenerator(FlightNetwork net, Outputer[] outputers, CostCalculator calculator) {
		this.net = net;
		this.outputers = outputers;
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
		setSourceAndSink();
	}

	protected void setSourceAndSink() {
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
	
	protected void addNewDutyToPath(FlightNetworkPath path, Edge<Leg> edge) {
		int flightTime = ((FlightNetworkNodeLabel) edge.getIn().getLabel()).getFlightTime();
		short track = edge.getIn().getInfo().getTrack();
		path.addNewDuty(flightTime, track);
		path.addEdge(edge);
	}
	
	protected void addConnectionToPath(FlightNetworkPath path, Edge<Leg> edge) {
		int flightTime = ((FlightNetworkNodeLabel) edge.getIn().getLabel()).getFlightTime();
		int sitTime = ((FlightNetworkEdgeLabel) edge.getLabel()).getSitTime();
		short track = edge.getIn().getInfo().getTrack();
		path.addConnection(flightTime, sitTime, track);
		path.addEdge(edge);
	}
}
