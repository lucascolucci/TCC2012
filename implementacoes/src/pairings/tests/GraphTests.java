package pairings.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pairings.graph.Graph;
import pairings.graph.Node;

public class GraphTests {
	private Graph<Object> graph;
	private Node<Object> node1;
	private Node<Object> node2;
	private Node<Object> node3;
	
	@Before
	public void setUp() {
		graph = new Graph<Object>();
		node1 = new Node<Object>(null);
		node2 = new Node<Object>(null);
		node3 = new Node<Object>(null);
		graph.addNode(node1);
		graph.addNode(node2);
		graph.addNode(node3);
		graph.addEdge(node1, node2);
		graph.addEdge(node1, node3);
	}

	@Test
	public void itShouldHave3Nodes() {
		assertEquals(graph.getNumberOfNodes(), 3);
	}
	
	@Test
	public void itShouldNotAddANullNode() {
		int before = graph.getNumberOfNodes();
		graph.addNode(null);
		assertEquals(before, graph.getNumberOfNodes());
	}
	
	@Test
	public void itShouldHave2Edges() {
		assertEquals(graph.getNumberOfEdges(), 2);
	}
	
	@Test
	public void itShouldNotAddAnEdgeForNodesNotInGraph() {
		Node<Object> other = new Node<Object>(null);
		int before = graph.getNumberOfEdges();
		graph.addEdge(node1, other);
		assertEquals(before, graph.getNumberOfEdges());
	}
	
	@Test
	public void itShouldNotAddAnEdgeForSameNode() {
		Node<Object> other = new Node<Object>(null);
		int before = graph.getNumberOfEdges();
		graph.addNode(other);
		graph.addEdge(other, other);
		assertEquals(before, graph.getNumberOfEdges());
	}

	@Test
	public void itShouldHaveTheRightEdges() {
		assertTrue(graph.hasEdge(node1, node2) && graph.hasEdge(node1, node3));
	}
	
	@Test
	public void itShouldNotHaveTheWrongEdge() {
		assertFalse(graph.hasEdge(node2, node1));
	}

	@Test
	public void itShouldHaveTheRightNeighbors() {
		Object[] neighbors = new Object[] {node2, node3};
		assertArrayEquals(neighbors, graph.getNeighbors(node1).toArray());
	}
}
