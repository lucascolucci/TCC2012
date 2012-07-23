package pairings.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pairings.Leg;
import pairings.Pairing;
import pairings.PairingsGenerator;
import pairings.Rules;
import pairings.graph.networks.FlightNetwork;
import pairings.io.MemoryOutputer;
import pairings.io.Outputer;
import pairings.io.TimeTableReader;

public class PairingsGeneratorTests {
	private MemoryOutputer outputer;
	private Outputer[] outputers;
	
	@Before 
	public void setUp() {
		outputer = new MemoryOutputer();
		outputers = new Outputer[] { outputer };
	}
	
	@Test
	public void itShouldGive2PairingsForEachBase() throws ParseException {
		FlightNetwork net = getFlightNetworkWith2Legs();
		PairingsGenerator generator = new PairingsGenerator(net);
		
		generator.generate("CGH", outputers);
		assertEquals(2, generator.getNumberOfPairings());
		
		outputer.reset();
		generator.generate("UDI", outputers);
		assertEquals(2, generator.getNumberOfPairings());
	}
	
	private FlightNetwork getFlightNetworkWith2Legs() throws ParseException {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		List<Leg> legsList = new ArrayList<Leg>();
		
		Date leg1Dep = (Date) df.parse("27/08/2012 06:10");
		Date leg1Arr = (Date) df.parse("27/08/2012 07:08");
		Date leg2Dep = (Date) df.parse("27/08/2012 09:00");
		Date leg2Arr = (Date) df.parse("27/08/2012 09:58");
		
		Leg leg1 = new Leg(1234, "CGH", "UDI", leg1Dep, leg1Arr);
		Leg leg2 = new Leg(1235, "UDI", "CGH", leg2Dep, leg2Arr);
		
		legsList.add(leg1);
		legsList.add(leg2);
		
		FlightNetwork net = new FlightNetwork(legsList);
		net.build();
		
		return net;
	}
	
	@Test
	public void itShouldGiveLegalPairings() {
		TimeTableReader reader = new TimeTableReader("./src/pairings/tests/time_tables/cgh_sdu_10.txt");
		FlightNetwork net = new FlightNetwork(reader.getLegs());
		net.build();
		
		PairingsGenerator generator = new PairingsGenerator(net);
		
		generator.generate("CGH", outputers);
		for (Pairing pairing: outputer.getPairings())
			assertTrue(Rules.isPairingLegal(pairing, "CGH"));
		
		outputer.reset();
		generator.generate("SDU", outputers);	
		for (Pairing pairing: outputer.getPairings())
			assertTrue(Rules.isPairingLegal(pairing, "SDU"));
	}
}
