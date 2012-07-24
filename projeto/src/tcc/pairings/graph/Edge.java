package tcc.pairings.graph;

public class Edge<T> {
	private Node<T> out;
	private Node<T> in;
	private EdgeType type;
	private Label label;
	
	public Edge(Node<T> out, Node<T> in, EdgeType type) {
		this.out = out;
		this.in = in;
		this.type = type;
		label = null;
	}
	
	public Edge(Node<T> out, Node<T> in, EdgeType type, Label label) {
		this.out = out;
		this.in = in;
		this.type = type;
		this.label = label;
	}
	
	public Node<T> getOut() {
		return out;
	}
	
	public Node<T> getIn() {
		return in;
	}
	
	public EdgeType getType() {
		return type;
	}
	
	public Label getLabel() {
		return label;
	}
	
	public void setLabel(Label label) {
		this.label = label;
	}
}
