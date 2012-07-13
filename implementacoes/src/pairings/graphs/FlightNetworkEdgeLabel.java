package pairings.graphs;

public class FlightNetworkEdgeLabel extends Label{
	private int sitTime;
	
	public FlightNetworkEdgeLabel(int id, int sitTime) {
		super(id);
		this.sitTime = sitTime;
	}
	
	public int getSitTime() {
		return sitTime;
	}
}
