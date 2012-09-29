package tcc.pairings.solvers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ResultsBuffer {
	private StringBuilder sb;
	private BufferedWriter writer;
	
	public ResultsBuffer() {
		sb = new StringBuilder();
	}
	
	public void output(String line) {
		sb.append(line).append('\n');
		System.out.println(line);
	}
	
	public void writeToFile(String file) {
		try {
			tryToWriteToFile(file);
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			writer = null;
		}
	}

	private void tryToWriteToFile(String file) throws IOException {
		writer = new BufferedWriter(new FileWriter(file));
		writer.write(sb.toString());
		writer.flush();
		writer.close();
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}
}
