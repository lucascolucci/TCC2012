package tcc.pairings.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Base;
import tcc.pairings.costs.ExcessToFlightCalculator;
import tcc.pairings.rules.Rules;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.Solver;
import tcc.pairings.solvers.exacts.SetCoverSolver;
import tcc.pairings.solvers.exacts.SetPartitionSolver;

public class SolversTest {
	private Base base;
	private Solution solution;
	private Solver solver;
	
	@Before
	public void setUp() {
		Rules.MAX_DUTIES = 3;
		base = new Base("CGH", "GRU");
	}

	@Test
	public void setPartitionShouldCoverAllLegs() {
		solver = new SetPartitionSolver(FilePaths.TIME_TABLES + "73H_26.txt");
		legCoverageTest();
		solver = new SetPartitionSolver(FilePaths.TIME_TABLES + "cgh_sdu_10.txt");
		legCoverageTest();
	}
	
	@Test
	public void setCoverShouldCoverAllLegs() {
		solver = new SetCoverSolver(FilePaths.TIME_TABLES + "73H_26.txt");
		legCoverageTest();
		solver = new SetCoverSolver(FilePaths.TIME_TABLES + "cgh_sdu_10.txt");
		legCoverageTest();
	}

	private void legCoverageTest() {
		solution = solver.getSolution(base);
		assertTrue(solution.isAllLegsCovered(solver.getLegs()));
	}
		
	@Test
	public void setPartitionShouldGiveTheRightCost() {
		ExcessToFlightCalculator calc = new ExcessToFlightCalculator();
		solver = new SetPartitionSolver(FilePaths.TIME_TABLES + "73H_26.txt", calc);
		costTest();
		solver = new SetPartitionSolver(FilePaths.TIME_TABLES + "cgh_sdu_10.txt", calc);
		costTest();
	}
	
	@Test
	public void setCoverShouldGiveTheRightCost() {
		ExcessToFlightCalculator calc = new ExcessToFlightCalculator();
		solver = new SetCoverSolver(FilePaths.TIME_TABLES + "73H_26.txt", calc);
		costTest();
		solver = new SetCoverSolver(FilePaths.TIME_TABLES + "cgh_sdu_10.txt", calc);
		costTest();
	}

	private void costTest() {
		solution = solver.getSolution(base);
		assertTrue(solution.isCostRight());
	}
}
