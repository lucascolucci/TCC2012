package pairings.tests;

import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pairings.Pairing;
import pairings.PairingsGenerator;
import pairings.Rules;
import pairings.graph.networks.FlightNetwork;
import pairings.io.PairingsOutputer;
import pairings.io.TimeTableReader;

public class PairingsGeneratorTests {
	private PairingsGenerator generator;
	
	@Before
	public void setUp() throws Exception {
		TimeTableReader reader = new TimeTableReader("./src/pairings/tests/time_table_pairings_test.txt");
		FlightNetwork net = new FlightNetwork(reader.getLegs());
		net.build();
		generator = new PairingsGenerator(net);
	}

	@Test
	public void itShouldGiveLegalPairings(){
		String base = "CGH";
		List<Pairing> pairings = generator.getPairings(base);
		PairingsOutputer.setPairingNumber(1);		
		for (Pairing pairing: pairings)
			assertTrue(Rules.isPairingLegal(pairing, base));
		base = "SDU";
		pairings = generator.getPairings(base);
		PairingsOutputer.setPairingNumber(1);		
		for (Pairing pairing: pairings)
			assertTrue(Rules.isPairingLegal(pairing, base));
	}
}
