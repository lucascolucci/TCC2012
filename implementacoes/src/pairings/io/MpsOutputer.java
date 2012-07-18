package pairings.io;

import java.util.List;

import pairings.Leg;
import pairings.Pairing;

public class MpsOutputer implements Outputable {
	private List<Leg> legs;
	private String base;
	private String fileName;
	private StringBuilder sb;
	private int varCount;
	
	public MpsOutputer(List<Leg> legs, String base, String fileName) {
		initialSetUp(legs, base, fileName);
		setUntilColumns();
		writeToFile();
	}
	
	public String getFileName() {
		return fileName;
	}

	private void initialSetUp(List<Leg> legs, String base, String fileName) {
		this.legs = legs;
		this.base = base;
		this.fileName = fileName;
		sb = new StringBuilder();
		varCount = 0;
	}
	
	private void setUntilColumns() {
		setHeader();
		setRows();
		setColumnsFirstLine();
	}
	
	private void setHeader() {
		sb.append("NAME          ").append(base).append("\n");
	}
	
	private void setRows() {
		sb.append("ROWS").append("\n");
		sb.append(" N  COST").append("\n");
		for (Leg leg: legs)
			sb.append(" E  ").append("F").append(leg.getNumber()).append("\n");
	}
	
	private void setColumnsFirstLine() {
		sb.append("COLUMNS");
	}
	
	@Override
	public void output(Pairing pairing) {
		sb = new StringBuilder();
		sb.append("                                                             ");
		String var = "X" + (++varCount);
		sb.replace(4, 4 + var.length(), var);		
		sb.replace(14, 18, "COST");
		sb.replace(30, 30 + String.valueOf(pairing.getCost()).length(), String.valueOf(pairing.getCost()));
		int column = 1;
		for (Leg leg: legs) {
			if (column == 0) {
				sb.append("                                                             ");
				sb.replace(14, 15 + String.valueOf(leg.getNumber()).length(), "F" + leg.getNumber());
				sb.replace(30, 31, pairing.contains(leg.getNumber()) ? "1" : "0");
			}
			else {
				sb.replace(39, 40 + String.valueOf(leg.getNumber()).length(), "F" + leg.getNumber());
				sb.replace(54, 55, pairing.contains(leg.getNumber()) ? "1" : "0");
				writeToFile();
				sb = new StringBuilder();
			}
			column = 1-column;
		}
		writeToFile();		
	}
	
	public void completeFile() {
		writeRHS();
		writeBounds();
		writeEnd();
	}
		
	private void writeRHS() {
		sb = new StringBuilder();
		sb.append("RHS");
		writeToFile();
		sb = new StringBuilder();
		sb.append("                                                             ");
		sb.replace(4, 8, "RHS1");		
		int column = 0;
		for (Leg leg: legs) {
			if (column == 0) {
				sb.append("                                                             ");
				sb.replace(14, 15 + String.valueOf(leg.getNumber()).length(), "F" + leg.getNumber());
				sb.replace(30, 31, "1");
			}
			else {
				sb.replace(39, 40 + String.valueOf(leg.getNumber()).length(), "F" + leg.getNumber());
				sb.replace(54, 55, "1");
				writeToFile();
				sb = new StringBuilder();
			}
			column = 1-column;
		}
	}

	private void writeBounds() {
		sb = new StringBuilder();
		sb.append("BOUNDS");
		writeToFile();
		for (int i = 1; i <= varCount; i++) {
			sb = new StringBuilder();
			sb.append(" BV BND1      X").append(i);
			writeToFile();
		}
	}
	
	private void writeEnd() {
		sb = new StringBuilder();
		sb.append("ENDATA");
		writeToFile();
	}
	
	private void writeToFile() {
		System.out.println(sb);
	}
}
