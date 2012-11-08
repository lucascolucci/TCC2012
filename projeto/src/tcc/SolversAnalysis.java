package tcc;

import tcc.pairings.Base;
import tcc.pairings.costs.*;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.exacts.SetCoverSolver;
import tcc.pairings.solvers.exacts.SetPartitionSolver;
import tcc.pairings.solvers.heuristics.CGSolver;
import tcc.pairings.solvers.heuristics.LocalSearchSolver;
import tcc.pairings.solvers.heuristics.genetic.GeneticSolver;
import tcc.pairings.solvers.heuristics.genetic.LocalSearchGeneticSolver;

public class SolversAnalysis {
	public static final String TIME_TABLES_PATH = "./time_tables/";
	public static final String OUTPUTS_PATH = "./outputs/";
	private static String file;
	private static CostCalculator calc;
	private static Base[] bases;
	
	public static void main(String[] args) {
		SolversAnalysis sa = new SolversAnalysis();
		file = "73H_26.txt";
		calc = new DutyToFlightCalculator();
		bases = new Base[] { new Base("CGH", "GRU") };
		//sa.doInitialSolution();
		//sa.doSetPartition();
		sa.doSetCover();
		//sa.doLocalSearch();
		//sa.doGeneticSolver();
		//sa.doLocalSearchGeneticSolver();
		//sa.doColumnGeneration();
	}
	
	public void doInitialSolution() {
		InitialSolver solver = new InitialSolver(TIME_TABLES_PATH + file, calc);
		System.out.println(solver.getSolution(bases));
		System.out.println(solver.getSolutionTime());
	}
	
	public void doSetPartition() {
		SetPartitionSolver solver = new SetPartitionSolver(TIME_TABLES_PATH + file, calc);
		System.out.println(solver.getSolution(bases));
		System.out.println(solver.getSolutionTime());
	}
	
	public void doSetCover() {
		SetCoverSolver solver = new SetCoverSolver(TIME_TABLES_PATH + file, calc);
		System.out.println(solver.getSolution(bases));
		System.out.println(solver.getSolutionTime());
	}
	
	public void doLocalSearch() {
		LocalSearchSolver solver = new LocalSearchSolver(TIME_TABLES_PATH + file, calc);
		System.out.println(solver.getSolution(bases));
		System.out.println(solver.getSolutionTime());
	}
	
	public void doGeneticSolver() {
		GeneticSolver solver = new GeneticSolver(TIME_TABLES_PATH + file, calc);
		System.out.println(solver.getSolution(bases));
		System.out.println(solver.getSolutionTime());
	}
	
	public void doLocalSearchGeneticSolver() {
		LocalSearchGeneticSolver solver = new LocalSearchGeneticSolver(TIME_TABLES_PATH + file, calc);
		System.out.println(solver.getSolution(bases));
		System.out.println(solver.getSolutionTime());
	}
	
	public void doColumnGeneration() {
		CGSolver solver = new CGSolver(TIME_TABLES_PATH + file, calc);
		solver.getInitialSolver().setMaxDuties(3);
		System.out.println(solver.getSolution(bases));
		System.out.println(solver.getSolutionTime());		
	}
}