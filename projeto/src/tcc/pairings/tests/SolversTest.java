package tcc.pairings.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Pairing;
import tcc.pairings.PairingsGenerator;
import tcc.pairings.Rules;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.CplexOutputer;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.MpsOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.solvers.CplexSolver;
import tcc.pairings.solvers.GlpkSolver;
import tcc.pairings.solvers.Solvable;

public class SolversTest {
	private FlightNetwork net;
	private MemoryOutputer memory;
	
	@Before
	public void setUp() {
		Rules.MAX_DUTIES = 4;
		Rules.MIN_SIT_TIME = 25;
		Rules.MAX_LEGS = 5;
		Rules.MAX_TRACKS = 2;
		
		TimeTableReader reader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_10.txt");
		net = new FlightNetwork(reader.getLegs());
		net.build();
		
		memory = new MemoryOutputer();
	}
	
	@Test
	public void glpkShouldGiveRightSolutionCost() {	
		GlpkSolver solver = getGlpkSolver();
		solver.solve();
		itShouldGiveRightSolutionCost(solver);
	}
	
	@Test
	public void glpkShouldSolveInstance() {	
		assertTrue(getGlpkSolver().solve());
	}
	
	private GlpkSolver getGlpkSolver() {
		String mpsFile = FilePaths.OUTPUTS + "in.mps";
		String solutionFile = FilePaths.OUTPUTS + "out.sol";
		
		MpsOutputer mps = new MpsOutputer(net.getLegs(), mpsFile);
		Outputer[] outputers = new Outputer[] { memory, mps };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
	
		mps.writeUntilColumns();	
		generator.generate("CGH");
		mps.writeRhsAndBounds(generator.getNumberOfPairings());
		mps.close();

		return new GlpkSolver(mpsFile, solutionFile);
	}
	
	@Test
	public void cplexShouldSolveInstance() {	
		assertTrue(getCplexSolver().solve());
	}
	
	@Test
	public void cplexFromFileShouldSolveInstance() {	
		assertTrue(getCplexSolverFromFile().solve());
	}
	
	@Test
	public void cplexShouldGiveRightSolutionCost() {	
		CplexSolver solver = getCplexSolver();
		solver.solve();
		itShouldGiveRightSolutionCost(solver);
	}
	
	@Test
	public void cplexFromFileShouldGiveRightSolutionCost() {	
		CplexSolver solver = getCplexSolverFromFile();
		solver.solve();
		itShouldGiveRightSolutionCost(solver);
	}
	
	private CplexSolver getCplexSolver() {
		CplexOutputer cplex = new CplexOutputer(net.getLegs());
		Outputer[] outputers = new Outputer[] { memory, cplex };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		
		cplex.addRows();	
		generator.generate("CGH");
		
		return new CplexSolver(cplex.getModel());
	}
	
	private CplexSolver getCplexSolverFromFile() {
		String mpsFile = FilePaths.OUTPUTS + "in.mps";
		
		MpsOutputer mps = new MpsOutputer(net.getLegs(), mpsFile);
		Outputer[] outputers = new Outputer[] { memory, mps };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		
		mps.writeUntilColumns();
		generator.generate("CGH");
		mps.writeRhsAndBounds(generator.getNumberOfPairings());
		mps.close();
		
		return new CplexSolver(mpsFile);
	}

	private void itShouldGiveRightSolutionCost(Solvable solver) {
		List<Pairing> solution = solver.getSolution(memory.getPairings());
		int cost = 0;
		for (Pairing pairing: solution) 
			cost += pairing.getCost();
		assertEquals(solver.getSolutionCost(), cost);
	}	
}
