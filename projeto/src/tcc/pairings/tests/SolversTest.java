package tcc.pairings.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Base;
import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.ExcessToFlightCalculator;
import tcc.pairings.rules.Rules;
import tcc.pairings.solvers.BasicSolver;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.exacts.SetCoverSolver;
import tcc.pairings.solvers.exacts.SetPartitionSolver;

public class SolversTest {
	private Base base;
	private Solution solution;
	
	@Before
	public void setUp() {
		Rules.MAX_DUTIES = 3;
		base = new Base("CGH", "GRU");
	}

	@Test
	public void setPartitionShouldCoverAllLegs() {
		SetPartitionSolver solver;
		solver = new SetPartitionSolver(FilePaths.TIME_TABLES + "73H_26.txt");
		legCoverageTest(solver);
		solver = new SetPartitionSolver(FilePaths.TIME_TABLES + "cgh_sdu_10.txt");
		legCoverageTest(solver);
	}
	
	@Test
	public void setCoverShouldCoverAllLegs() {
		SetCoverSolver solver;
		solver = new SetCoverSolver(FilePaths.TIME_TABLES + "73H_26.txt");
		legCoverageTest(solver);
		solver = new SetCoverSolver(FilePaths.TIME_TABLES + "cgh_sdu_10.txt");
		legCoverageTest(solver);
	}

	private void legCoverageTest(BasicSolver solver) {
		solution = solver.getSolution(base);
		List<DutyLeg> nonDHLegs = getNonDHLegs();
		assertEquals(solver.getLegs().size(), nonDHLegs.size());
		for (Leg leg: solver.getLegs()) {
			boolean legFound = false;
			for (DutyLeg dutyLeg: nonDHLegs) 
				if (dutyLeg.isDuplicate(leg)) {
					legFound = true;
					break;
				}
			assertTrue(legFound);
		}
	}
		
	private List<DutyLeg> getNonDHLegs() {
		List<DutyLeg> nonDHLegs = new ArrayList<DutyLeg>();
		for (Pairing pairing: solution.getPairings())
			for (DutyLeg leg: pairing.getLegs()) 
				if (!leg.isDeadHead())
					nonDHLegs.add(leg);
		return nonDHLegs;
	}
	
	@Test
	public void setPartitionShouldGiveTheRightCost() {
		ExcessToFlightCalculator calc = new ExcessToFlightCalculator();
		SetPartitionSolver solver;
		solver = new SetPartitionSolver(FilePaths.TIME_TABLES + "73H_26.txt", calc);
		costTest(solver);
		solver = new SetPartitionSolver(FilePaths.TIME_TABLES + "cgh_sdu_10.txt", calc);
		costTest(solver);
	}
	
	@Test
	public void setCoverShouldGiveTheRightCost() {
		ExcessToFlightCalculator calc = new ExcessToFlightCalculator();
		SetCoverSolver solver;
		solver = new SetCoverSolver(FilePaths.TIME_TABLES + "73H_26.txt", calc);
		costTest(solver);
		solver = new SetCoverSolver(FilePaths.TIME_TABLES + "cgh_sdu_10.txt", calc);
		costTest(solver);
	}

	private void costTest(BasicSolver solver) {
		solution = solver.getSolution(base);
		double plainCost = 0.0;
		double costWithDeadHeads = 0.0;
		for (Pairing pairing: solution.getPairings()) {
			plainCost += pairing.getCost();
			costWithDeadHeads += pairing.getCostWithDeadHeads();
		}
		assertEquals(plainCost, solution.getPairingsCost(), 0.001);
		assertEquals(costWithDeadHeads, solution.getCost(), 0.001);
	}
}