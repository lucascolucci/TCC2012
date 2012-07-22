package pairings.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import pairings.Leg;
import pairings.Pairing;

public class MpsOutputer extends BasicOutputer {
	private List<Leg> legs;
	private String fileName;
	private BufferedWriter out;
	
	public String getFileName() {
		return fileName;
	}
	
	public MpsOutputer(List<Leg> legs, String fileName) {
		super();
		this.legs = legs;
		this.fileName = fileName;
		setUpBufferedWriter();
	}
	
	private void setUpBufferedWriter() {
		try {
			out = new BufferedWriter(new FileWriter(fileName));
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public void writeUntilColumns() {
		write("NAME CPP\n");
		writeRows();
		write("COLUMNS\n");
	}
	
	private void writeRows() {
		StringBuilder sb = new StringBuilder("ROWS\n");
		sb.append(" N COST").append('\n');
		for (Leg leg: legs)
			sb.append(" E ").append("F").append(leg.getNumber()).append('\n');
		write(sb.toString());
	}
	
	@Override
	public void output(Pairing pairing) {
		StringBuilder sb = new StringBuilder();
		sb.append(" X").append(numberOfPairings).append(" COST ").append(String.valueOf(pairing.getCost())).append('\n');
		for (Leg leg: legs) 
			if (pairing.contains(leg.getNumber()))
				sb.append(" X").append(numberOfPairings).append(" F").append(leg.getNumber()).append(" 1").append('\n');
		write(sb.toString());		
	}
	
	public void writeRhsBoundsAndEnd() {
		writeRhs();
		writeBounds();
		write("ENDATA\n");
	}
		
	private void writeRhs() {
		StringBuilder sb = new StringBuilder("RHS\n");
		for (Leg leg: legs)
			sb.append(" B F").append(leg.getNumber()).append(" 1").append('\n');
		write(sb.toString());
	}

	private void writeBounds() {
		StringBuilder sb = new StringBuilder("BOUNDS\n");
		for (int i = 1; i <= numberOfPairings; i++) {
			sb.append(" BV BND X").append(i).append('\n');
			write(sb.toString());
			sb = new StringBuilder();
		}
	}
	
	private void write(String content) {
		try {
			out.write(content);
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
