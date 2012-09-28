package tcc;

import tcc.pairings.Base;
import tcc.pairings.costs.*;
import tcc.pairings.rules.Rules;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.Solver;
import tcc.pairings.solvers.exacts.SetCoverSolver;
import tcc.pairings.solvers.exacts.SetPartitionSolver;
import tcc.pairings.solvers.heuristics.LocalSearchSolver;
import tcc.pairings.solvers.heuristics.genetic.GeneticSolver;

public class SolversAnalysis {
	private static final String TIME_TABLES_PATH = "./time_tables/";
	
	public static void main(String[] args) {
		SolversAnalysis sa = new SolversAnalysis();
		//sa.doInitialSolution();
		//sa.doSetPartition();
		//sa.doSetCover();
		sa.doLocalSearch();
		//sa.doGeneticSolver();
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
		Base rio = new Base("SDU", "GIG");
		CostCalculator calc = new MeanFlightPerDutyCalculator();
		Solver solver = new SetCoverSolver(TIME_TABLES_PATH + "738_48.txt", calc);
		System.out.println(solver.getSolution(sao, rio));
	}
	
	public void doLocalSearch() {
		Base sao = new Base("GRU", "CGH");
		Solver solver = new LocalSearchSolver(TIME_TABLES_PATH + "73H_26.txt");
		System.out.println(solver.getSolution(sao));	
	}
	
	public void doGeneticSolver() {
		Rules.MAX_DUTIES = 1;
		Base sao = new Base("GRU", "CGH");
		Solver solver = new GeneticSolver(TIME_TABLES_PATH + "73H_26.txt");
		System.out.println(solver.getSolution(sao));	
	}
}