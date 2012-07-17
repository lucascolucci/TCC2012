package pairings.tests;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pairings.Leg;
import pairings.Pairing;
import pairings.PairingsGenerator;
import pairings.Rules;
import pairings.graph.networks.FlightNetwork;
import pairings.io.PairingsOutputer;

public class PairingsGeneratorTests {
	private FlightNetwork net;

	@Before
	public void setUp() throws Exception {
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

		net = new FlightNetwork(legsList);
		net.build();
	}

	@Test
	public void itShouldHave2PairingsForCGH() {
		PairingsGenerator generator = new PairingsGenerator(net);
		PairingsOutputer.setPairingNumber(1);
		List<Pairing> pairings = generator.getPairings("CGH");
		assertEquals(2, pairings.size());
	}
	
	@Test
	public void itShouldHave2PairingsForUDI() {
		PairingsGenerator generator = new PairingsGenerator(net);
		PairingsOutputer.setPairingNumber(1);
		List<Pairing> pairings = generator.getPairings("UDI");
		assertEquals(2, pairings.size());
	}
	
//	@Test(expected=java.lang.OutOfMemoryError.class)
//	public void it() throws Exception {
//		TimeTableReader reader = new TimeTableReader("./src/pairings/tests/time_table.txt");
//		FlightNetwork net = new FlightNetwork(reader.getLegs());
//		net.build();
//		PairingsGenerator generator = new PairingsGenerator(net);
//		PairingsOutputer.setPairingNumber(0);
//		generator.getPairings("CGH");	
//	}
}
