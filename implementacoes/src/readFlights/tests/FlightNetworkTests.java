package readFlights.tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import readFlights.Leg;
import readFlights.FlightNetwork;
import readFlights.Rules;

public class FlightNetworkTests {
	private FlightNetwork net;
	
	@Before
	public void setUp() throws Exception {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		Date leg1Departure = (Date) df.parse("27/08/2012 06:10");
		Date leg1Arrival = (Date) df.parse("27/08/2012 07:08");
		Date leg2Departure = (Date) df.parse("27/08/2012 10:10");
		Date leg2Arrival = (Date) df.parse("27/08/2012 12:08");

		Leg leg1 = new Leg(1234, "CGH", "UDI", leg1Departure, leg1Arrival);
		Leg leg2 = new Leg(1235, "UDI", "CGH", leg2Departure, leg2Arrival);
		
		List<Leg> legsList = new ArrayList<Leg>();
		legsList.add(leg1);
		legsList.add(leg2);

		net = new FlightNetwork(legsList);
		net.build();
	}
	
	@Test
	public void itShouldHave10Nodes() throws Exception {		
		assertEquals(10, net.getNumberOfNodes());
	}

	@Test
	public void itShouldHave25Edges() throws Exception {
		assertEquals(25, net.getNumberOfEdges());
	}
}
