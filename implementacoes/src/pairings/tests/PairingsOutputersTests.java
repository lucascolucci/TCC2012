package pairings.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import pairings.PairingsGenerator;
import pairings.graph.networks.FlightNetwork;
import pairings.io.CplexOutputer;
import pairings.io.FileOutputer;
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
		FileOutputer file = new FileOutputer("pairings.dat");
		MpsOutputer mps = new MpsOutputer(net.getLegs(), "in.mps");
		CplexOutputer cplex = new CplexOutputer();
		Outputer[] outs = new Outputer[] { terminal, file, mps, cplex };
		
		mps.writeUntilColumns();	
		generator.generate("CGH", outs);
		mps.writeRhsBoundsAndEnd();
		mps.close();
		file.close();
		
		int last = -1;
		for (Outputer out: outs)
			if (last == -1)
				last = out.getNumberOfPairings();
			else {
				assertEquals(last, out.getNumberOfPairings());
				last = out.getNumberOfPairings();
			}
	}
}
