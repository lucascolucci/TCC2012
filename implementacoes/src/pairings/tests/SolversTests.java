package pairings.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import pairings.PairingsGenerator;
import pairings.graph.networks.FlightNetwork;
import pairings.io.FileOutputer;
import pairings.io.MpsOutputer;
import pairings.io.Outputer;
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
		String pairingsFile = "pairings.dat";
		MpsOutputer mps = new MpsOutputer(net.getLegs(), mpsFile);
		FileOutputer file = new FileOutputer(pairingsFile);
		Outputer[] outputers = new Outputer[] { mps, file };
		
		mps.writeUntilColumns();	
		generator.generate("CGH", outputers);
		mps.writeRhsBoundsAndEnd();
		mps.close();
		file.close();
		
		GlpkSolver solver = new GlpkSolver(mpsFile);
		assertTrue(solver.solve());
	}
}
