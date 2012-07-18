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
	
	public String getFileName() {
		return fileName;
	}
	
	public MpsOutputer(List<Leg> legs, String base, String fileName) {
		initialSetUp(legs, base, fileName);
		writeUntilColumns();
	}
	
	private void initialSetUp(List<Leg> legs, String base, String fileName) {
		this.legs = legs;
		this.base = base;
		this.fileName = fileName;
		varCount = 0;
	}
	
	private void writeUntilColumns() {
		writeHeader();
		writeRows();
		writeColumnsFirstLine();
	}
	
	private void writeHeader() {
		sb = new StringBuilder("NAME ");
		sb.append(base);
		write();
	}
	
	private void writeRows() {
		sb = new StringBuilder("ROWS\n");
		sb.append(" N COST").append('\n');
		for (Leg leg: legs)
			sb.append(" E ").append("F").append(leg.getNumber()).append('\n');
		sb.deleteCharAt(sb.length() - 1);
		write();
	}
	
	private void writeColumnsFirstLine() {
		sb = new StringBuilder("COLUMNS");
		write();
	}
	
	@Override
	public void output(Pairing pairing) {
		sb = new StringBuilder();
		sb.append(" X").append(++varCount).append(" COST ").append(String.valueOf(pairing.getCost())).append('\n');
		for (Leg leg: legs) 
			if (pairing.contains(leg.getNumber()))
				sb.append(" X").append(varCount).append(" F").append(leg.getNumber()).append(" 1").append('\n');
		sb.deleteCharAt(sb.length() - 1);
		write();		
	}
	
	public void complete() {
		writeRhs();
		writeBounds();
		writeEnd();
	}
		
	private void writeRhs() {
		sb = new StringBuilder("RHS\n");
		for (Leg leg: legs)
			sb.append(" B F").append(leg.getNumber()).append(" 1").append('\n');
		sb.deleteCharAt(sb.length() - 1);
		write();
	}

	private void writeBounds() {
		sb = new StringBuilder("BOUNDS\n");
		for (int i = 1; i <= varCount; i++)
			sb.append(" BV BND X").append(i).append('\n');
		sb.deleteCharAt(sb.length() - 1);
		write();
	}
	
	private void writeEnd() {
		sb = new StringBuilder("ENDATA");
		write();
	}
	
	private void write() {
		System.out.println(sb);
	}
}
