package pairings.tests;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import pairings.Pairing;
import pairings.PairingsGenerator;
import pairings.graph.networks.FlightNetwork;
import pairings.io.PairingsOutputer;
import pairings.io.TimeTableReader;

public class PairingsGeneratorTests {
	//private FlightNetwork net;

//	@Before
//	public void setUp() throws Exception {
//		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
//		List<Leg> legsList = new ArrayList<Leg>();
//		
//		Date leg1Dep = (Date) df.parse("27/08/2012 06:10");
//		Date leg1Arr = (Date) df.parse("27/08/2012 07:08");
//		Date leg2Dep = (Date) df.parse("27/08/2012 09:00");
//		Date leg2Arr = (Date) df.parse("27/08/2012 09:58");
//		
//		Leg leg1 = new Leg(1234, "CGH", "UDI", leg1Dep, leg1Arr);
//		Leg leg2 = new Leg(1235, "UDI", "CGH", leg2Dep, leg2Arr);
//		
//		legsList.add(leg1);
//		legsList.add(leg2);
//
//		net = new FlightNetwork(legsList);
//		net.build();
//	}

//	//@Test
//	public void itShouldHave2PairingsForCGH() {
//		PairingsGenerator generator = new PairingsGenerator(net);
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(2012, 8, 27);
//		List<Pairing> pairings = generator.getPairings("CGH", calendar.getTime());
//		assertEquals(2, pairings.size());
//	}
//	
//	//@Test
//	public void itShouldHave2PairingsForUDI() {
//		PairingsGenerator generator = new PairingsGenerator(net);
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(2012, 8, 27);
//		List<Pairing> pairings = generator.getPairings("UDI", calendar.getTime());
//		assertEquals(2, pairings.size());
//	}
	
	@Test
	public void test() {
		TimeTableReader reader = new TimeTableReader("./src/pairings/tests/time_table.txt");
		FlightNetwork xpto = new FlightNetwork(reader.getLegs());
		xpto.build();
		
		
		PairingsGenerator generator = new PairingsGenerator(xpto);
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 8, 27);
		
		generator.getPairings("CGH", calendar.getTime());	
	}
}
