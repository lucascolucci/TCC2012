package tcc.pairings.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GlpkSolutionReader {
	private String fileName;
		
	public String getFileName() {
		return fileName;
	}

	public GlpkSolutionReader(String fileName) {
		this.fileName = fileName;
	}
	
	public List<Integer> getOneVariables() {
		try {
			return tryToGetOneVariables();
		} 
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}

	private List<Integer> tryToGetOneVariables() throws Exception {
		List<Integer> variables = new ArrayList<Integer>();
		readFileAndAdd(variables);
		return variables;
	}

	private void readFileAndAdd(List<Integer> variables) throws Exception {
		DataInputStream in = new DataInputStream(new FileInputStream(fileName));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		int lineCount = 0; int rows = 0; String line;
		while ((line = br.readLine()) != null) { 
			if (++lineCount == 1)
				rows = Integer.parseInt(line.split(" ")[0]) - 1;
			else if (lineCount > rows + 3 && Integer.parseInt(line) == 1)
				variables.add(lineCount - rows - 3);
		}
		in.close();
	}
	
	public double getCost() {
		try {
			return tryToReadFileAndGetCost();
		} 
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	private double tryToReadFileAndGetCost() throws Exception {
		DataInputStream in = new DataInputStream(new FileInputStream(fileName));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		int lineCount = 0; double cost = 0; String line;
		while ((line = br.readLine()) != null)  
			if (++lineCount == 3) {
				cost = Double.parseDouble(line);
				break;
			}
		in.close();
		return cost;
	}
	
	public int getNumberOfOneVariables() {
		try {
			return tryToReadFileAndCountOneVariables();
		} 
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	private int tryToReadFileAndCountOneVariables() throws Exception {
		DataInputStream in = new DataInputStream(new FileInputStream(fileName));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		int lineCount = 0; int rows = 0; int count = 0; String line;
		while ((line = br.readLine()) != null) { 
			if (++lineCount == 1)
				rows = Integer.parseInt(line.split(" ")[0]) - 1;
			else if (lineCount > rows + 3 && Integer.parseInt(line) == 1)
				++count;
		}
		in.close();
		return count;
	}
}
