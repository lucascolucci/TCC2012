package tcc.pairings.tests;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Base;
import tcc.pairings.Pairing;
import tcc.pairings.costs.ExcessToFlightCalculator;
import tcc.pairings.generators.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.io.outputers.MemoryOutputer;
import tcc.pairings.io.outputers.Outputer;

public class PairingTest {
	private List<Pairing> pairings1;
	private List<Pairing> pairings2;
	
	
	@Before
	public void setUp() throws Exception {
		pairings1 = getPairings();
		pairings2 = getPairings();
	}
	
	private List<Pairing> getPairings() {
		TimeTableReader reader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_10.txt");
		FlightNetwork net = new FlightNetwork(reader.getLegs());
		net.build();
		MemoryOutputer memory = new MemoryOutputer();
		Outputer[] outputers = new Outputer[] { memory };
		ExcessToFlightCalculator calc = new ExcessToFlightCalculator();
		PairingsGenerator generator = new PairingsGenerator(net, outputers, calc);
		Base sao = new Base("CGH");
		generator.generate(sao);
		return memory.getPairings();
	}

	@Test
	public void bothListsShouldBeEqual() {
		assertFalse(pairings1 == pairings2);
		assertTrue(pairings1.equals(pairings2));
	}
	
	@Test
	public void bothHashesShouldBeEqual() {
		HashSet<Pairing> hash1 = new HashSet<Pairing>(pairings1);
		HashSet<Pairing> hash2 = new HashSet<Pairing>(pairings2);
		assertFalse(hash1 == hash2);
		assertTrue(hash1.equals(hash2));
	}
}
