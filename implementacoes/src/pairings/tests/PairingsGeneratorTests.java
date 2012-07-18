package pairings.tests;

import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pairings.Pairing;
import pairings.PairingsGenerator;
import pairings.Rules;
import pairings.graph.networks.FlightNetwork;
import pairings.io.MemoryOutputer;
import pairings.io.MpsOutputer;
import pairings.io.TimeTableReader;

public class PairingsGeneratorTests {
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
	public void itShouldGiveLegalPairings(){
		String base = "CGH";
		MemoryOutputer outputer = new MemoryOutputer();
		generator.generate(base, outputer);
		List<Pairing> pairings = outputer.getPairings();
		for (Pairing pairing: pairings)
			assertTrue(Rules.isPairingLegal(pairing, base));
		
		base = "SDU";
		outputer.clear();
		generator.generate(base, outputer);
		pairings = outputer.getPairings();				
		for (Pairing pairing: pairings)
			assertTrue(Rules.isPairingLegal(pairing, base));
	}
	
	@Test
	public void itShouldOutputTheMpsFile() {
		String base = "CGH";
		MpsOutputer outputer = new MpsOutputer(net.getLegs(), base, null);
		generator.generate(base, outputer);
		outputer.completeFile();
	}
}
