package tcc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ResultsWriter {
	private String fileName;
	private BufferedWriter writer;
	
	private static final String DATA_PATH = "./data/";

	public String getFileName() {
		return fileName;
	}
	
	public ResultsWriter(String fileName) {
		this.fileName = fileName;
		setUpWriter();
	}
	
	private void setUpWriter() {
		try {
			writer = new BufferedWriter(new FileWriter(DATA_PATH + getFileName()));		
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			writer = null;
		}
	}
	
	public void write(String line) {
		try {
			tryToWrite(line);
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private void tryToWrite(String line) throws IOException {
		if (writer != null) {
			writer.write(line);
			writer.newLine();
			writer.flush();
		}
	}
	
	public void close() {
		try {
			tryToClose();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private void tryToClose() throws IOException {
		if (writer != null)
			writer.close();
	}
}
