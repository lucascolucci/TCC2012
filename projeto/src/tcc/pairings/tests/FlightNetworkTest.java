package tcc.pairings.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tcc.DateUtil;
import tcc.pairings.Leg;
import tcc.pairings.Rules;
import tcc.pairings.graph.Edge;
import tcc.pairings.graph.EdgeType;
import tcc.pairings.graph.Node;
import tcc.pairings.graph.networks.FlightNetwork;

public class FlightNetworkTest {
	private FlightNetwork net;
	
	@Before
	public void setUp() throws Exception {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		List<Leg> legsList = new ArrayList<Leg>();
		
		Date leg1Dep = (Date) df.parse("27/08/2012 06:10");
		Date leg1Arr = (Date) df.parse("27/08/2012 07:08");
		Date leg2Dep = (Date) df.parse("27/08/2012 09:00");
		Date leg2Arr = (Date) df.parse("27/08/2012 09:58");
		
		Leg leg1 = new Leg((short) 1234, "CGH", "UDI", leg1Dep, leg1Arr);
		Leg leg2 = new Leg((short) 1235, "UDI", "CGH", leg2Dep, leg2Arr);
		
		legsList.add(leg1);
		legsList.add(leg2);

		net = new FlightNetwork(legsList);
		net.build();
	}
	
	@Test
	public void itShouldHave8Nodes() {		
		assertEquals(8, net.getNumberOfNodes());
	}

	@Test
	public void itShouldHave10Edges() {
		assertEquals(10, net.getNumberOfEdges());
	}
	
	@Test 
	public void itShouldHaveCorrectEdges() {
		for (Node<Leg> node: net.getNodes()) 
			for (Edge<Leg> edge: node.getEdges()) {
				Date arrival = edge.getOut().getInfo().getArrival();
				Date departure = edge.getIn().getInfo().getDeparture();
				assertTrue(arrival.before(departure));
				int sit = DateUtil.difference(arrival, departure);
				if (Rules.sitTimeCheck(sit))
					assertEquals(EdgeType.CONNECTION, edge.getType());
				else if (Rules.restTimeCheck(sit))
					assertEquals(EdgeType.OVERNIGHT, edge.getType());
			}
	}
	
	@Test
	public void sourceShouldHaveCorrectEdges() {
		Leg sourceLeg = new Leg((short) 0, "CGH", "CGH", null, null);
		Node<Leg> source = new Node<Leg>(sourceLeg);
		net.addSource(source);
		for (Edge<Leg> edge: net.getOutwardEdges(source))
			assertEquals(EdgeType.FROM_SOURCE, edge.getType());
		assertEquals(1, net.numberOfOutwardEdges(source));
	}
	
	@Test
	public void sinkShouldHaveCorrectEdges() {
		Leg sinkLeg = new Leg((short) 0, "CGH", "CGH", null, null);
		Node<Leg> sink = new Node<Leg>(sinkLeg);
		net.addSink(sink);
		for (Edge<Leg> edge: net.getInwardEdges(sink))
			assertEquals(EdgeType.TO_SINK, edge.getType());
		assertEquals(4, net.numberOfInwardEdges(sink));
	}
}
