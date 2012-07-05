package readFlights.tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import readFlights.FlightLeg;
import readFlights.FlightNetwork;

public class FlightNetworkTest {
	FlightNetwork net;
	
	@Before
	public void setUp() throws Exception {
		net = new FlightNetwork();
	}

	@Test
	public void itShouldHas25Edges() throws Exception {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date departureLeg_1 = (Date) df.parse("27/08/2012 06:10");
		Date arrivalLeg_1 = (Date) df.parse("27/08/2012 07:08");
		Date departureLeg_2 = (Date) df.parse("27/08/2012 10:10");
		Date arrivalLeg_2 = (Date) df.parse("27/08/2012 12:08");

		FlightLeg leg_1 = new FlightLeg(1234, "CGH", "UDI", departureLeg_1, arrivalLeg_1);
		FlightLeg leg_2 = new FlightLeg(1234, "UDI", "CGH", departureLeg_2, arrivalLeg_2);
		
		List<FlightLeg> legsList = new ArrayList<FlightLeg>();
		legsList.add(leg_1);
		legsList.add(leg_2);
		
		net.build(legsList);
		
		assertEquals(25, net.getNumberOfEdges());
	}
	
	@Test
	public void itShouldHas10Nodes() throws Exception {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date departureLeg_1 = (Date) df.parse("27/08/2012 06:10");
		Date arrivalLeg_1 = (Date) df.parse("27/08/2012 07:08");
		Date departureLeg_2 = (Date) df.parse("27/08/2012 10:10");
		Date arrivalLeg_2 = (Date) df.parse("27/08/2012 12:08");

		FlightLeg leg_1 = new FlightLeg(1234, "CGH", "UDI", departureLeg_1, arrivalLeg_1);
		FlightLeg leg_2 = new FlightLeg(1234, "UDI", "CGH", departureLeg_2, arrivalLeg_2);
		
		List<FlightLeg> legsList = new ArrayList<FlightLeg>();
		legsList.add(leg_1);
		legsList.add(leg_2);
		
		net.build(legsList);
		
		assertEquals(10, net.getNumberOfNodes());
	}

}
