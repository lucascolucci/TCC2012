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
import tcc.pairings.rules.Rules;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.Solution;

public class InitialSolverTest {
	private Base base;
	private InitialSolver solver;
	private Solution solution;
	
	@Before
	public void setUp() {
		Rules.MAX_DUTIES = 4;
		base = new Base("CGH", "GRU");
	}

	@Test
	public void itShouldCoverAllLegs() {
		legCoverageTest("73H_26.txt");
		legCoverageTest("738_48.txt");
		legCoverageTest("733_92.txt");
	}
	
	private void legCoverageTest(String testFile) {
		solver = new InitialSolver(FilePaths.TIME_TABLES + testFile);
		solution = solver.getSolution(base);
		List<DutyLeg> nonDHLegs = getNonDHLegs();
		assertEquals(solver.getLegs().size(), nonDHLegs.size());
		for (Leg leg: solver.getLegs()) {
			boolean duplicatedLegFound = false;
			for (DutyLeg dutyLeg: nonDHLegs) 
				if (dutyLeg.isDuplicate(leg)) {
					duplicatedLegFound = true;
					break;
				}
			assertTrue(duplicatedLegFound);
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
	public void itShouldHaveAtLeastOneNonDHLeg() {
		oneNonDHLegTest("73H_26.txt");
		oneNonDHLegTest("738_48.txt");
		oneNonDHLegTest("733_92.txt");
	}
	
	private void oneNonDHLegTest(String testFile) {
		solver = new InitialSolver(FilePaths.TIME_TABLES + testFile);
		solution = solver.getSolution(base);
		for (Pairing pairing: solution.getPairings()) {
			boolean hasNonDHLeg = false;
			for (DutyLeg leg: pairing.getLegs()) 
				if (!leg.isDeadHead()) {
					hasNonDHLeg = true;
					break;
				}
			assertTrue(hasNonDHLeg);
		}	
	}
}

