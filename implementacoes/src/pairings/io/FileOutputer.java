package pairings.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import pairings.Pairing;

public class FileOutputer implements Outputable {
	private String fileName;
	private int numberOfPairings;
	private BufferedWriter out;
	
	public String getFileName() {
		return fileName;
	}
	
	@Override
	public int getNumberOfPairings() {
		return numberOfPairings;
	}
	
	public FileOutputer(String fileName) {
		this.fileName = fileName;
		numberOfPairings = 0;
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
		++numberOfPairings;
		write("Pairing " + numberOfPairings + '\n');
		write(pairing.toString());
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
