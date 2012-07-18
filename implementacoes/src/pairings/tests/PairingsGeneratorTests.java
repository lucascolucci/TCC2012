package pairings.tests;

import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pairings.Pairing;
import pairings.Rules;
import pairings.generators.MpsFilePairingsGenerator;
import pairings.generators.MemoryPairingsGenerator;
import pairings.graph.networks.FlightNetwork;
import pairings.io.PairingsOutputer;
import pairings.io.TimeTableReader;

public class PairingsGeneratorTests {
	private MemoryPairingsGenerator memoryGenerator;
	private MpsFilePairingsGenerator mpsGenerator;

	@Before
	public void setUp() throws Exception {
		TimeTableReader reader = new TimeTableReader("./src/pairings/tests/time_table_pairings_test.txt");
		FlightNetwork net = new FlightNetwork(reader.getLegs());
		net.build();
		memoryGenerator = new MemoryPairingsGenerator(net);
		mpsGenerator = new MpsFilePairingsGenerator(net);
	}

	@Test
	public void itShouldGiveLegalPairings(){
		String base = "CGH";
		memoryGenerator.generate(base);
		List<Pairing> pairings = memoryGenerator.getPairings();
		PairingsOutputer.setPairingNumber(1);		
		for (Pairing pairing: pairings)
			assertTrue(Rules.isPairingLegal(pairing, base));
		base = "SDU";
		memoryGenerator.generate(base);
		pairings = memoryGenerator.getPairings();
		PairingsOutputer.setPairingNumber(1);		
		for (Pairing pairing: pairings)
			assertTrue(Rules.isPairingLegal(pairing, base));
	}
	
	@Test
	public void itShouldPrintCplexFile(){
		String base = "CGH";
		mpsGenerator.generate(base);
	}
}
