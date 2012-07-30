package tcc.pairings.solvers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Pairing;
import tcc.pairings.io.GlpkSolutionReader;

public class GlpkSolver implements Solvable {
	private String mpsFile;
	private String solutionFile;
	private double solutionTime;
	
	private static final String GLPSOL = "/usr/local/bin/glpsol";
	private static final String INFEASIBLE = "PROBLEM HAS NO PRIMAL FEASIBLE SOLUTION";
	private static final String SOLUTION_TIME = "Time used:";
	
	public String getMpsFile() {
		return mpsFile;
	}
	
	public String getSolutionFile() {
		return solutionFile;
	}
	
	public double getSolutionTime() {
		return solutionTime;
	}
	
	public GlpkSolver(String mpsFile, String solutionFile) {
		this.mpsFile = mpsFile;
		this.solutionFile = solutionFile;
		solutionTime = -1;
	}
	
	@Override
	public boolean solve() {
		try {
			return (runGlpsol().exitValue() == 0);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return false;
		}
	}
	
	private Process runGlpsol() throws Exception {
		String[] cmd = { GLPSOL, mpsFile, "-w", solutionFile };
		Process process = Runtime.getRuntime().exec(cmd);
		readGlpsolOutput(process);
		process.waitFor();
		return process;
	}

	private void readGlpsolOutput(Process process) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) 
			processOutput(in, line);
		in.close();
	}

	private void processOutput(BufferedReader in, String line) throws Exception {
		System.out.println(line);
		if (line.contentEquals(INFEASIBLE)) {
			in.close();
			throw new Exception(INFEASIBLE);
		}
		else if (line.contains(SOLUTION_TIME))
			setSolutionTime(line);
	}
	
	private void setSolutionTime(String line) {
		String time = line.substring(SOLUTION_TIME.length()).trim();
		solutionTime = Double.parseDouble(time.split(" ")[0]);
	}
	
	@Override
	public List<Pairing> getSolution(String pairingsFile) {
		// TODO
		return null;
	}
	
	@Override
	public List<Pairing> getSolution(List<Pairing> pairings) {
		List<Integer> oneVariables = (new GlpkSolutionReader(solutionFile)).getOneVariables();
		return getSolutionFromOneVariables(oneVariables, pairings);
	}

	private List<Pairing> getSolutionFromOneVariables(List<Integer> oneVariables, List<Pairing> pairings) {
		List<Pairing> solution = new ArrayList<Pairing>();
		if (oneVariables != null)
			for (int var: oneVariables)
				solution.add(pairings.get(var - 1));
		return solution;
	}
		
	@Override
	public int getSolutionCost() {
		return (new GlpkSolutionReader(solutionFile)).getCost();
	}
	
	public int getSolutionSize() {
		return (new GlpkSolutionReader(solutionFile).getNumberOfOneVariables());
	}
}
