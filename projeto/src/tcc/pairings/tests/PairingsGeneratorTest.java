package tcc.pairings.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.generators.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.rules.Rules;

public class PairingsGeneratorTest {
	private MemoryOutputer memory;
	
	@Test
	public void itShouldGive2PairingsForCGH() throws ParseException {
		Rules.MAX_DUTIES = 4;
		Rules.MIN_SIT_TIME = 25;
		Rules.MAX_LEGS = 5;
		Rules.MAX_TRACKS = 2;
		
		FlightNetwork net = getFlightNetworkWith2Legs();
		PairingsGenerator generator = new PairingsGenerator(net);
		
		generator.generate(new Base("CGH"));
		assertEquals(2, generator.getNumberOfPairings());
	}
	
	@Test
	public void itShouldGive1PairingForUDI() throws ParseException {
		FlightNetwork net = getFlightNetworkWith2Legs();
		PairingsGenerator generator = new PairingsGenerator(net);
		
		generator.generate(new Base("UDI"));
		assertEquals(1, generator.getNumberOfPairings());
	}
	
	@Test
	public void itShouldGive3PairingsForCGHAndUDI() throws ParseException {
		FlightNetwork net = getFlightNetworkWith2Legs();
		PairingsGenerator generator = new PairingsGenerator(net);
		
		generator.generate(new Base("CGH"));
		assertEquals(2, generator.getNumberOfPairings());
		
		generator.generate(new Base("UDI"));
		assertEquals(3, generator.getNumberOfPairings());
	}
	
	private FlightNetwork getFlightNetworkWith2Legs() throws ParseException {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		List<Leg> legsList = new ArrayList<Leg>();
		
		Date dep1 = (Date) df.parse("27/08/2012 06:10");
		Date arr1 = (Date) df.parse("27/08/2012 07:08");
		Date dep2 = (Date) df.parse("27/08/2012 09:00");
		Date arr2 = (Date) df.parse("27/08/2012 09:58");
		
		Leg leg1 = new Leg((short) 1234, "CGH", "UDI", dep1, arr1, (short) 1);
		Leg leg2 = new Leg((short) 1235, "UDI", "CGH", dep2, arr2, (short) 1);
		
		legsList.add(leg1);
		legsList.add(leg2);
		
		FlightNetwork net = new FlightNetwork(legsList);
		net.build();
		
		return net;
	}
	
	@Test
	public void itShouldGiveLegalPairingsForCGH() {
		PairingsGenerator generator = getGeneratorForCghSdu10();
		Base base = new Base("CGH");
		generator.generate(base);
		for (Pairing pairing: memory.getPairings())
			assertTrue(Rules.isPairingLegal(pairing, base));
	}
	
	@Test
	public void itShouldGiveLegalPairingsForSDU() {
		PairingsGenerator generator = getGeneratorForCghSdu10();
		Base base = new Base("SDU");
		generator.generate(base);
		for (Pairing pairing: memory.getPairings())
			assertTrue(Rules.isPairingLegal(pairing, base));
	}
	
	@Test
	public void itShouldGiveLegalPairingsForEachBase() {
		PairingsGenerator generator = getGeneratorForCghSdu10();
		
		Base base = new Base("CGH");
		generator.generate(base);
		for (Pairing pairing: memory.getPairings())
			assertTrue(Rules.isPairingLegal(pairing, base));
		
		memory.clear();
		base = new Base("SDU");
		generator.generate(base);
		for (Pairing pairing: memory.getPairings())
			assertTrue(Rules.isPairingLegal(pairing, base));
	}
	
	@Test
	public void itShouldGivePairingsWithPositiveCosts() {
		PairingsGenerator generator = getGeneratorForCghSdu10();
		generator.generate(new Base("CGH"), new Base("SDU"));
		for (Pairing pairing: memory.getPairings())
			assertTrue(pairing.getCost() >= 0);
	}
	
	private PairingsGenerator getGeneratorForCghSdu10() {
		TimeTableReader reader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_20.txt");
		FlightNetwork net = new FlightNetwork(reader.getLegs());
		net.build();
		memory = new MemoryOutputer();
		Outputer[] outputers = new Outputer[] { memory };
		return new PairingsGenerator(net, outputers);
	}
	
	@Test
	public void itShouldGiveLegalPairingsFor738() {
		PairingsGenerator generator = getGeneratorFor738();
		Base base = new Base("GIG", "SDU");
		generator.generate(base);
		for (Pairing pairing: memory.getPairings())
			assertTrue(Rules.isPairingLegal(pairing, base));
	}
	
	private PairingsGenerator getGeneratorFor738() {
		TimeTableReader reader = new TimeTableReader(FilePaths.TIME_TABLES + "738_48.txt");
		FlightNetwork net = new FlightNetwork(reader.getLegs());
		net.build();
		memory = new MemoryOutputer();
		Outputer[] outputers = new Outputer[] { memory };
		return new PairingsGenerator(net, outputers);
	}
}
