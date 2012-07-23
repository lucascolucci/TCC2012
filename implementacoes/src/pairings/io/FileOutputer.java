package pairings.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import pairings.Pairing;

public class FileOutputer extends Outputer {
	private String fileName;
	private BufferedWriter out;
	
	public String getFileName() {
		return fileName;
	}
	
	public FileOutputer(String fileName) {
		super();
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

	@Override
	public void output(Pairing pairing) {
		StringBuilder sb = new StringBuilder("Pairing ");
		sb.append(numberOfPairings).append('\n');
		sb.append(pairing.toString());
		write(sb.toString());
	}
	
	private void write(String content) {
		try {
			out.write(content);
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
