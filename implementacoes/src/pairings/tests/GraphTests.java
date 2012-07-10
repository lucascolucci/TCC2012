package pairings.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pairings.graphs.Graph;
import pairings.graphs.Node;


public class GraphTests {
	private Graph<Object> graph;
	
	@Before
	public void setUp() {
		graph = new Graph<Object>();
	}

	@Test
	public void itShouldHaveAnEdge() {
		Node<Object> out = new Node<Object>(1, null);
		Node<Object> in = new Node<Object>(2, null);
		graph.addNode(out);
		graph.addNode(in);
		graph.addEdge(out, in);
		assertTrue(graph.hasEdge(out, in));
	}
	
	@Test
	public void itShouldNotAddANullNode() {
		int before = graph.getNumberOfNodes();
		graph.addNode(null);
		assertEquals(before, graph.getNumberOfNodes());
	}
	
	@Test
	public void itShouldNotAddAnEdgeForNodesNotInGraph() {
		Node<Object> out = new Node<Object>(1, null);
		Node<Object> in = new Node<Object>(2, null);
		int before = graph.getNumberOfEdges();
		graph.addEdge(out, in);
		assertEquals(before, graph.getNumberOfEdges());
	}
	
	@Test
	public void itShouldNotAddAnEdgeForSameNode() {
		Node<Object> out = new Node<Object>(1, null);
		int before = graph.getNumberOfEdges();
		graph.addNode(out);
		graph.addEdge(out, out);
		assertEquals(before, graph.getNumberOfEdges());
	}
}
