package pairings.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import pairings.Pairing;
import pairings.PairingsGenerator;
import pairings.graph.networks.FlightNetwork;
import pairings.io.MemoryOutputer;
import pairings.io.MpsOutputer;
import pairings.io.Outputer;
import pairings.io.TerminalOutputer;
import pairings.io.TimeTableReader;
import pairings.solvers.GlpkSolver;

public class SolversTests {
	@Test
	public void glpkShouldSolveInstance() {	
		TimeTableReader reader = new TimeTableReader("./src/pairings/tests/time_tables/cgh_sdu_10.txt");
		FlightNetwork net = new FlightNetwork(reader.getLegs());
		net.build();
		PairingsGenerator generator = new PairingsGenerator(net);
	
		String mpsFile = "in.mps";
		MemoryOutputer memory = new MemoryOutputer();
		MpsOutputer mps = new MpsOutputer(net.getLegs(), mpsFile);
		Outputer[] outputers = new Outputer[] { memory, mps };
		
		mps.writeUntilColumns();	
		generator.generate("CGH", outputers);
		mps.writeRhsAndBounds(generator.getNumberOfPairings());
		mps.close();

		GlpkSolver solver = new GlpkSolver(mpsFile);
		assertTrue(solver.solve());
		
		List<Pairing> solution = solver.getSolution(memory.getPairings());
		TerminalOutputer terminal = new TerminalOutputer();
		terminal.output(solution);
	}
}
