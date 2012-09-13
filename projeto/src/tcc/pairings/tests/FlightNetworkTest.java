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

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.graph.Edge;
import tcc.pairings.graph.Node;
import tcc.pairings.graph.networks.EdgeType;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.graph.networks.SpecialNode;
import tcc.pairings.rules.Rules;
import tcc.util.DateUtil;

public class FlightNetworkTest {
	private FlightNetwork net;
	
	@Before
	public void setUp() throws Exception {
		Rules.MAX_DUTIES = 4;
		Rules.MIN_SIT_TIME = 25;
		Rules.MAX_LEGS = 5;
		Rules.MAX_TRACKS = 2;
		
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		List<Leg> legsList = new ArrayList<Leg>();
		
		Date dep1 = (Date) df.parse("27/08/2012 06:10");
		Date arr1 = (Date) df.parse("27/08/2012 07:08");
		Date dep2 = (Date) df.parse("27/08/2012 09:00");
		Date arr2 = (Date) df.parse("27/08/2012 09:58");
		
		Leg leg1 = new Leg((short) 1234, "CGH", "UDI", dep1, arr1, (short) 1);
		Leg leg2 = new Leg((short) 1235, "UDI", "CGH", dep2, arr2, (short) 1);
		
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
		SpecialNode source = new SpecialNode(new Base("CGH"));
		net.addSource(source);
		for (Edge<Leg> edge: net.getOutwardEdges(source))
			assertEquals(EdgeType.FROM_SOURCE, edge.getType());
		assertEquals(1, net.numberOfOutwardEdges(source));
	}
	
	@Test
	public void sinkShouldHaveCorrectEdges() {
		SpecialNode sink = new SpecialNode(new Base("CGH"));
		net.addSink(sink);
		for (Edge<Leg> edge: net.getInwardEdges(sink))
			assertEquals(EdgeType.TO_SINK, edge.getType());
		assertEquals(4, net.numberOfInwardEdges(sink));
	}
	
	@Test
	public void itShouldSortEdges() {
		SpecialNode source = new SpecialNode(new Base("CGH"));
		SpecialNode sink = new SpecialNode(new Base("CGH"));
		net.addSource(source);
		net.addSink(sink);
		net.sort();
		for (Node<Leg> node: net.getNodes()) 
			nodeEdgesShouldBeSorted(node);
	}

	private void nodeEdgesShouldBeSorted(Node<Leg> node) {
		int[] count = getTypesCount(node);
		for (int i = 0; i < count[0]; i++)
			assertTrue(node.getEdges().get(i).getType() == EdgeType.TO_SINK);
		for (int i = 0; i < count[1]; i++)
			assertTrue(node.getEdges().get(count[0] + i).getType() == EdgeType.CONNECTION);
		for (int i = 0; i < count[2]; i++)
			assertTrue(node.getEdges().get(count[0] + count[1] + i).getType() == EdgeType.OVERNIGHT);
	}
	
	private int[] getTypesCount(Node<Leg> node) {
		int[] count = new int[] { 0, 0, 0 };
		for (Edge<Leg> edge: node.getEdges())
			if (edge.getType() == EdgeType.TO_SINK)
				++count[0];
			else if (edge.getType() == EdgeType.CONNECTION)
				++count[1];
			else if (edge.getType() == EdgeType.OVERNIGHT)
				++count[2];
		return count;
	}
}
