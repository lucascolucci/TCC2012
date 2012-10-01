package tcc;

import tcc.pairings.Base;
import tcc.pairings.costs.*;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.Solver;
import tcc.pairings.solvers.exacts.SetCoverSolver;
import tcc.pairings.solvers.exacts.SetPartitionSolver;
import tcc.pairings.solvers.heuristics.LocalSearchSolver;
import tcc.pairings.solvers.heuristics.genetic.GeneticSolver;

public class SolversAnalysis {
	public static final String TIME_TABLES_PATH = "./time_tables/";
	public static final String OUTPUTS_PATH = "./outputs/";
	
	public static void main(String[] args) {
		SolversAnalysis sa = new SolversAnalysis();
		//sa.doInitialSolution();
		//sa.doSetPartition();
		//sa.doSetCover();
		//sa.doLocalSearch();
		sa.doGeneticSolver();
	}
	
	public void doInitialSolution() {
		Base sao = new Base("CGH", "GRU");
		Solver solver = new InitialSolver(TIME_TABLES_PATH + "73H_26.txt");
		System.out.println(solver.getSolution(sao));
	}
	
	public void doSetPartition() {
		Base sao = new Base("GRU", "CGH");
		Solver solver = new SetPartitionSolver(TIME_TABLES_PATH + "73H_26.txt");
		System.out.println(solver.getSolution(sao));
	}
	
	public void doSetCover() {
		Base sao = new Base("GRU", "CGH");
		CostCalculator calc = new MeanFlightPerDutyCalculator();
		Solver solver = new SetCoverSolver(TIME_TABLES_PATH + "733_92.txt", calc);
		System.out.println(solver.getSolution(sao));
		System.out.println(solver.getNumberOfPairings());
	}
	
	public void doLocalSearch() {
		Base sao = new Base("GRU", "CGH");
		CostCalculator calc = new MeanFlightPerDutyCalculator();
		Solver solver = new LocalSearchSolver(TIME_TABLES_PATH + "73H_26.txt", calc);
		System.out.println(solver.getSolution(sao));
	}
	
	public void doGeneticSolver() {
		Base sao = new Base("GRU", "CGH");
		CostCalculator calc = new MeanFlightPerDutyCalculator();
		Solver solver = new GeneticSolver(TIME_TABLES_PATH + "733_92.txt", calc);
		System.out.println(solver.getSolution(sao));
		System.out.println(solver.getNumberOfPairings());
	}
}