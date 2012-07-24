package tcc.pairings.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import tcc.pairings.Pairing;

public class TextOutputer extends Outputer {
	private String fileName;
	private BufferedWriter out;
	
	public String getFileName() {
		return fileName;
	}
	
	public TextOutputer(String fileName) {
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
		write(pairing.toString());
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
