package tcc.pairings.optimizers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import tcc.pairings.io.GlpkSolutionReader;

public class GlpkOptimizer implements Optimizer {
	private String mpsFile;
	private String solutionFile;
	private double optmizationTime;
	
	private static final String GLPSOL = "/usr/local/bin/glpsol";
	private static final String INFEASIBLE = "PROBLEM HAS NO PRIMAL FEASIBLE SOLUTION";
	private static final String SOLUTION_TIME = "Time used:";
	
	public String getMpsFile() {
		return mpsFile;
	}
	
	@Override
	public double getOptimizationTime() {
		return optmizationTime;
	}
	
	public GlpkOptimizer(String mpsFile) {
		this.mpsFile = mpsFile;
		solutionFile = null;
		optmizationTime = -1;
	}
	
	public GlpkOptimizer(String mpsFile, String solutionFile) {
		this.mpsFile = mpsFile;
		this.solutionFile = solutionFile;
		optmizationTime = -1;
	}
	
	@Override
	public boolean optimize() {
		try {
			return (runGlpsol().exitValue() == 0);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return false;
		}
	}
	
	private Process runGlpsol() throws Exception {
		Process process = Runtime.getRuntime().exec(getCommand());
		readGlpsolOutput(process);
		process.waitFor();
		return process;
	}
	
	private String[] getCommand() {
		if (solutionFile != null)
			return new String[] { GLPSOL, mpsFile, "-w", solutionFile };
		return new String[] { GLPSOL, mpsFile };
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
			setOptimizationTime(line);
	}
	
	private void setOptimizationTime(String line) {
		String time = line.substring(SOLUTION_TIME.length()).trim();
		optmizationTime = Double.parseDouble(time.split(" ")[0]);
	}
	
	@Override
	public List<Integer> getOptimalVariables() {
		return (new GlpkSolutionReader(solutionFile)).getOneVariables();
	}
	
	@Override
	public List<Integer> getArtificialValues() {
		// TODO
		return null;
	}
		
	@Override
	public double getObjectiveValue() {
		return (new GlpkSolutionReader(solutionFile)).getCost();
	}
}
