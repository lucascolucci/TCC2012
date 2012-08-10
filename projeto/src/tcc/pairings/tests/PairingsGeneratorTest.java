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
import tcc.pairings.PairingsGenerator;
import tcc.pairings.Rules;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TimeTableReader;

public class PairingsGeneratorTest {
	@Test
	public void itShouldGive2PairingsForCGH() throws ParseException {
		Rules.MAX_DUTIES = 4;
		Rules.MIN_SIT_TIME = 25;
		Rules.MAX_LEGS = 5;
		Rules.MAX_TRACKS = 2;
		
		FlightNetwork net = getFlightNetworkWith2Legs();
		PairingsGenerator generator = new PairingsGenerator(net, null);
		
		generator.generate(new Base("CGH"));
		assertEquals(2, generator.getNumberOfPairings());
	}
	
	@Test
	public void itShouldGive1PairingForUDI() throws ParseException {
		FlightNetwork net = getFlightNetworkWith2Legs();
		PairingsGenerator generator = new PairingsGenerator(net, null);
		
		generator.generate(new Base("UDI"));
		assertEquals(1, generator.getNumberOfPairings());
	}
	
	@Test
	public void itShouldGive3PairingsForCGHAndUDI() throws ParseException {
		FlightNetwork net = getFlightNetworkWith2Legs();
		PairingsGenerator generator = new PairingsGenerator(net, null);
		
		generator.generate(new Base("CGH"));
		assertEquals(2, generator.getNumberOfPairings());
		
		generator.generate(new Base("UDI"));
		assertEquals(3, generator.getNumberOfPairings());
	}
	
	private FlightNetwork getFlightNetworkWith2Legs() throws ParseException {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		List<Leg> legsList = new ArrayList<Leg>();
		
		Date leg1Dep = (Date) df.parse("27/08/2012 06:10");
		Date leg1Arr = (Date) df.parse("27/08/2012 07:08");
		Date leg2Dep = (Date) df.parse("27/08/2012 09:00");
		Date leg2Arr = (Date) df.parse("27/08/2012 09:58");
		
		Leg leg1 = new Leg((short) 1234, "CGH", "UDI", leg1Dep, leg1Arr, (short) 1);
		Leg leg2 = new Leg((short) 1235, "UDI", "CGH", leg2Dep, leg2Arr, (short) 1);
		
		legsList.add(leg1);
		legsList.add(leg2);
		
		FlightNetwork net = new FlightNetwork(legsList);
		net.build();
		
		return net;
	}
	
	@Test
	public void itShouldGiveLegalPairingsForCGH() {
		MemoryOutputer memory = new MemoryOutputer();
		Outputer[] outputers = new Outputer[] { memory };
		PairingsGenerator generator = getGeneratorForCghSdu10(outputers);
		Base base = new Base("CGH");
		generator.generate(base);
		for (Pairing pairing: memory.getPairings())
			assertTrue(Rules.isPairingLegal(pairing, base));
	}
	
	@Test
	public void itShouldGiveLegalPairingsForSDU() {
		MemoryOutputer memory = new MemoryOutputer();
		Outputer[] outputers = new Outputer[] { memory };
		PairingsGenerator generator = getGeneratorForCghSdu10(outputers);
		Base base = new Base("SDU");
		generator.generate(base);
		for (Pairing pairing: memory.getPairings())
			assertTrue(Rules.isPairingLegal(pairing, base));
	}
	
	@Test
	public void itShouldGiveLegalPairingsForEachBase() {
		MemoryOutputer memory = new MemoryOutputer();
		Outputer[] outputers = new Outputer[] { memory };
		PairingsGenerator generator = getGeneratorForCghSdu10(outputers);
		
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
		MemoryOutputer memory = new MemoryOutputer();
		Outputer[] outputers = new Outputer[] { memory };
		PairingsGenerator generator = getGeneratorForCghSdu10(outputers);
		generator.generate(new Base("CGH"));
		generator.generate(new Base("SDU"));
		for (Pairing pairing: memory.getPairings())
			assertTrue(pairing.getCost() >= 0);
	}
	
	private PairingsGenerator getGeneratorForCghSdu10(Outputer[] outputers) {
		TimeTableReader reader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_20.txt");
		FlightNetwork net = new FlightNetwork(reader.getLegs());
		net.build();
		return new PairingsGenerator(net, outputers);
	}
}
