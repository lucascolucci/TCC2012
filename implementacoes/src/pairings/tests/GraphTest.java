package pairings.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pairings.graph.Graph;
import pairings.graph.Node;

public class GraphTest {
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
		graph.addEdge(node2, node3);
	}

	@Test
	public void itShouldHave3Nodes() {
		assertEquals(3, graph.getNumberOfNodes());
	}
	
	@Test
	public void itShouldNotAddANullNode() {
		int before = graph.getNumberOfNodes();
		graph.addNode(null);
		assertEquals(before, graph.getNumberOfNodes());
	}
	
	@Test
	public void itShouldHave3Edges() {
		assertEquals(3, graph.getNumberOfEdges());
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
		assertTrue(graph.hasEdge(node1, node2));
		assertTrue(graph.hasEdge(node1, node3));
		assertTrue(graph.hasEdge(node2, node3));
	}
	
	@Test
	public void itShouldNotHaveTheWrongEdges() {
		assertFalse(graph.hasEdge(node2, node1));
		assertFalse(graph.hasEdge(node3, node1));
		assertFalse(graph.hasEdge(node3, node2));
	}

	@Test
	public void itShouldHaveTheRightNeighbors() {
		Object[] neighbors1 = new Object[] {node2, node3};
		Object[] neighbors2 = new Object[] {node3};
		Object[] neighbors3 = new Object[] {};
		assertArrayEquals(neighbors1, graph.getNeighbors(node1).toArray());
		assertArrayEquals(neighbors2, graph.getNeighbors(node2).toArray());
		assertArrayEquals(neighbors3, graph.getNeighbors(node3).toArray());
	}
	
	@Test
	public void itShouldHaveTheRightInwardEdges() {
		Object[] inwards1 = new Object[] {};
		Object[] inwards2 = new Object[] {graph.getEdge(node1, node2)};
		Object[] inwards3 = new Object[] {graph.getEdge(node1, node3), graph.getEdge(node2, node3)};
		assertArrayEquals(inwards1, graph.getInwardEdges(node1).toArray());
		assertArrayEquals(inwards2, graph.getInwardEdges(node2).toArray());
		assertArrayEquals(inwards3, graph.getInwardEdges(node3).toArray());
	}
	
	@Test
	public void itShouldHaveTheRightOutwardEdges() {
		Object[] outwards1 = new Object[] {graph.getEdge(node1, node2), graph.getEdge(node1, node3)};
		Object[] outwards2 = new Object[] {graph.getEdge(node2, node3)};
		Object[] outwards3 = new Object[] {};
		assertArrayEquals(outwards1, graph.getOutwardEdges(node1).toArray());
		assertArrayEquals(outwards2, graph.getOutwardEdges(node2).toArray());
		assertArrayEquals(outwards3, graph.getOutwardEdges(node3).toArray());
	}
	
	@Test
	public void itShouldRemoveNode1() {
		graph.removeNode(node1);
		assertEquals(2, graph.getNumberOfNodes());
		assertEquals(1, graph.getNumberOfEdges());
		assertTrue(graph.hasEdge(node2, node3));
		assertFalse(graph.hasEdge(node1, node2));
		assertFalse(graph.hasEdge(node1, node3));
	}
}
