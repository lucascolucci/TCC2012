package pairings.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pairings.PairingsGenerator;
import pairings.graph.networks.FlightNetwork;
import pairings.io.MpsOutputer;
import pairings.io.Outputable;
import pairings.io.TimeTableReader;

public class PairingsOutputersTests {
	private FlightNetwork net;
	private PairingsGenerator generator;
	
	@Before
	public void setUp() throws Exception {
		TimeTableReader reader = new TimeTableReader("./src/pairings/tests/time_table_pairings_tests.txt");
		net = new FlightNetwork(reader.getLegs());
		net.build();
		generator = new PairingsGenerator(net);
	}

	@Test
	public void itShouldOutputTheMpsFile() {
		MpsOutputer outputer = new MpsOutputer(net.getLegs(), "CGH", null);
		Outputable[] outputers =  new Outputable[] { outputer };
		generator.generate("CGH", outputers);
		outputer.complete();
		assertTrue(true);
	}
}
