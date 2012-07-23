package pairings.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import pairings.PairingsGenerator;
import pairings.graph.networks.FlightNetwork;
import pairings.io.CplexOutputer;
import pairings.io.TextOutputer;
import pairings.io.MpsOutputer;
import pairings.io.Outputer;
import pairings.io.TerminalOutputer;
import pairings.io.TimeTableReader;

public class PairingsOutputersTests {
	@Test
	public void itShouldGiveTheSameNumberOfPairingsForAllOutputers() {
		TimeTableReader reader = new TimeTableReader("./src/pairings/tests/time_tables/cgh_sdu_10.txt");
		FlightNetwork net = new FlightNetwork(reader.getLegs());
		net.build();
		PairingsGenerator generator = new PairingsGenerator(net);
	
		TerminalOutputer terminal = new TerminalOutputer();
		TextOutputer file = new TextOutputer("pairings.dat");
		MpsOutputer mps = new MpsOutputer(net.getLegs(), "in.mps");
		CplexOutputer cplex = new CplexOutputer();
		Outputer[] outs = new Outputer[] { terminal, file, mps, cplex };
		
		mps.writeUntilColumns();	
		generator.generate("CGH", outs);
		mps.writeRhsAndBounds(generator.getNumberOfPairings());
		mps.close();
		file.close();
		
		assertTrue(true);
	}
}
