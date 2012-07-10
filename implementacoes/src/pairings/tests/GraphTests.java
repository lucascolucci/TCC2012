package pairings.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pairings.graphs.Graph;
import pairings.graphs.Node;


public class GraphTests {
	private Graph graph;
	
	@Before
	public void setUp() throws Exception {
		graph = new Graph();
	}

	@Test
	public void itShouldHaveAnEdge() throws Exception {
		Node from = new Node(null, 1);
		Node to = new Node(null, 2);
		graph.addNode(from);
		graph.addNode(to);
		graph.addEdge(from, to);
		assertTrue(graph.hasEdge(from, to));
	}
	
	@Test(expected = Exception.class)
	public void itShouldNotAddANullNode() throws Exception {
		graph.addNode(null);
	}
	
	@Test(expected = Exception.class)
	public void itShouldNotAddAnEdgeForNodesNotInGraph() throws Exception {
		Node from = new Node(null, 1);
		Node to = new Node(null, 2);
		graph.addEdge(from, to);
	}
	
	@Test(expected = Exception.class)
	public void itShouldNotAddAnEdgeForSameNode() throws Exception {
		Node from = new Node(null, 1);
		graph.addNode(from);
		graph.addEdge(from, from);
	}
}
