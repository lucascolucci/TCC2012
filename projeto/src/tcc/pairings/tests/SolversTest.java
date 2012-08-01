package tcc.pairings.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Pairing;
import tcc.pairings.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.MpsOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.solvers.GlpkSolver;

public class SolversTest {
	private FlightNetwork net;
	private MemoryOutputer memory;
	
	@Before
	public void setUp() {
		TimeTableReader reader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_10.txt");
		net = new FlightNetwork(reader.getLegs());
		net.build();
		
		memory = new MemoryOutputer();
	}
	
	@Test
	public void glpkShouldSolveInstance() {	
		assertTrue(getGlpkSolver().solve());
	}
	
	@Test
	public void glpkShouldGiveRightSolutionCost() {	
		GlpkSolver solver = getGlpkSolver();
		List<Pairing> solution = solver.getSolution(memory.getPairings());
		int cost = 0;
		for (Pairing pairing: solution) 
			cost += pairing.getCost();
		assertEquals(solver.getSolutionCost(), cost);
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
}
