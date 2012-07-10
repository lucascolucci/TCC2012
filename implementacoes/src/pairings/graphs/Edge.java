package pairings.graphs;

public class Edge<T> {
	private Node<T> out;
	private Node<T> in;
	
	public Edge(Node<T> out, Node<T> in) {
		this.out = out;
		this.in = in;
	}

	public Node<T> getOut() {
		return out;
	}
	
	public Node<T> getIn() {
		return in;
	}
}
