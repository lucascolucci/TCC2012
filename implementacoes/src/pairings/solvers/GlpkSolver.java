package pairings.solvers;

import java.io.*;
import java.util.List;

import pairings.Pairing;

public class GlpkSolver implements Solvable {
	private String mpsFile;
	
	private static final String SOLUTION_FILE = "solution.txt";
	private static final String GLPSOL = "/usr/local/bin/glpsol";
	private static final String INFEASIBLE = "PROBLEM HAS NO PRIMAL FEASIBLE SOLUTION";
	
	public String getMpsFile() {
		return mpsFile;
	}
	
	public GlpkSolver(String mpsFile) {
		this.mpsFile = mpsFile;
	}
	
	@Override
	public boolean solve() {
		try {
			return (runGlpsol().exitValue() == 0);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return false;
		}
	}
	
	private Process runGlpsol() throws Exception {
		String[] cmd = { GLPSOL, mpsFile, "-w", SOLUTION_FILE };
		Process process = Runtime.getRuntime().exec(cmd);
		readGlpsolOutput(process);
		process.waitFor();
		return process;
	}

	private void readGlpsolOutput(Process process) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) { 
			System.out.println(line);
			if (line.contentEquals(INFEASIBLE)) {
				in.close();
				throw new Exception(INFEASIBLE);
			}
		}
		in.close();
	}
	
	@Override
	public List<Pairing> getSolution(String pairingsFile) {
		// TODO
		return null;
	}
	
	@Override
	public List<Pairing> getSolution(List<Pairing> pairings) {
		// TODO
		return null;
	}
	
	@Override
	public int getSolutionCost() {
		// TODO
		return 0;
	}
}
