package pairings.tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pairings.Duty;
import pairings.Leg;
import pairings.Pairing;
import pairings.PairingsGenerator;
import pairings.Rules;
import pairings.graph.networks.FlightNetwork;

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
	public void test() throws ParseException {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		PairingsGenerator generator = new PairingsGenerator(net);
		List<Pairing> pairings = generator.getPairings("CGH", (Date) df.parse("27/08/2012 00:00"));
		int j = 1;
		for(Pairing pairing: pairings) {
			System.out.println();
			System.out.println("Pairing " + j++);
			int i = 1;
			for(Duty duty: pairing.getDuties()){
				System.out.println("Duty " + i++);
				for(Leg leg: duty.getLegs()){
					System.out.println(leg.getNumber() + " " + leg.getFrom() + " " + leg.getTo() + " " + df.format(leg.getDeparture()) + " " + df.format(leg.getArrival()));
				}
			}
		}
	}

}
