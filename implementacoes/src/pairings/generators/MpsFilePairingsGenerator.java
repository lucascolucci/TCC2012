package pairings.generators;


import pairings.Leg;
import pairings.Pairing;
import pairings.graph.networks.FlightNetwork;

public class MpsFilePairingsGenerator extends PairingsGenerator {
	private StringBuilder sb;
	private int variableCount;
	
	public MpsFilePairingsGenerator(FlightNetwork net) {
		super(net);
		variableCount = 1;
	}

	@Override
	public void generate(String base) {
		this.base = base;
		sb = new StringBuilder();
		setHeader();
		setRows();
		setColumnsHeader();
		printOutput();
		super.generate(base);
		setRHS();
		setBounds();
		setEnd();
	}

	private void printOutput() {
		System.out.println(sb);
	}

	private void setHeader() {
		sb.append("NAME          ").append(base).append("\n");
	}
	
	private void setRows() {
		sb.append("ROWS").append("\n");
		sb.append(" N  COST").append("\n");
		for (Leg leg: net.getLegs())
			sb.append(" E  ").append("F").append(leg.getNumber()).append("\n");
	}
	
	private void setColumnsHeader() {
		sb.append("COLUMNS");
	}
	
	@Override
	protected void generateOutput(Pairing pairing) {
		sb = new StringBuilder();
		sb.append("                                                             ");
		String variable = "X" + variableCount++;
		sb.replace(4, 4 + variable.length(), variable);		
		sb.replace(14, 18, "COST");
		sb.replace(30, 30 + String.valueOf(pairing.getCost()).length(), String.valueOf(pairing.getCost()));
		int column = 1;
		for (Leg leg: net.getLegs()) {
			if (column == 0) {
				sb.append("                                                             ");
				sb.replace(14, 15 + String.valueOf(leg.getNumber()).length(), "F" + leg.getNumber());
				sb.replace(30, 31, pairing.contains(leg.getNumber()) ? "1" : "0");
			}
			else {
				sb.replace(39, 40 + String.valueOf(leg.getNumber()).length(), "F" + leg.getNumber());
				sb.replace(54, 55, pairing.contains(leg.getNumber()) ? "1" : "0");
				printOutput();
				sb = new StringBuilder();
			}
			column = 1-column;
		}
		printOutput();
	}
		
	private void setRHS() {
		sb = new StringBuilder();
		sb.append("RHS");
		printOutput();
		sb = new StringBuilder();
		sb.append("                                                             ");
		sb.replace(4, 8, "RHS1");		
		int column = 0;
		for (Leg leg: net.getLegs()) {
			if (column == 0) {
				sb.append("                                                             ");
				sb.replace(14, 15 + String.valueOf(leg.getNumber()).length(), "F" + leg.getNumber());
				sb.replace(30, 31, "1");
			}
			else {
				sb.replace(39, 40 + String.valueOf(leg.getNumber()).length(), "F" + leg.getNumber());
				sb.replace(54, 55, "1");
				printOutput();
				sb = new StringBuilder();
			}
			column = 1-column;
		}
	}

	private void setBounds() {
		sb = new StringBuilder();
		sb.append("BOUNDS");
		printOutput();
		for (int i = 1; i < variableCount; i++) {
			sb = new StringBuilder();
			sb.append(" BV BND1      X").append(i);
			printOutput();
		}
	}
	
	private void setEnd() {
		sb = new StringBuilder();
		sb.append("ENDATA");
		printOutput();
	}
}
