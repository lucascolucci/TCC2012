package tcc.pairings.graph.networks;

public class ColumnGenerationLabel extends FlightNetworkNodeLabel {
	private double dual;
	
	public double getDual() {
		return dual;
	}

	public void setDual(double dual) {
		this.dual = dual;
	}
	
	public ColumnGenerationLabel(int flightTime, int day) {
		super(flightTime, day);
		// TODO Auto-generated constructor stub
	}
}
