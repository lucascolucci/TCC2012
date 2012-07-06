package readFlights.tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import readFlights.FlightLeg;
import readFlights.FlightReader;
import readFlights.Rules;

public class FlightReaderTests {
	private FlightReader flightReader;
	private List<FlightLeg> list;
	private DateFormat df;
	
	@Before
	public void setUp() throws Exception {
		flightReader = new FlightReader("./src/readFlights/tests/time_table.txt");		
		df = new SimpleDateFormat(Rules.DATE_FORMAT);
	}
	
	@Test
	public void isReading62FlightLegs() {
		list = flightReader.getFlightsList();
		assertEquals(62, list.size());
	}
	
	@Test
	public void isFirstFlightMatching(){
		list = flightReader.getFlightsList();
		FlightLeg firstLeg = list.get(0); 
		assertEquals(firstLeg.getNumber(), 1500);
		assertEquals(firstLeg.getFrom(), "CGH");
		assertEquals(firstLeg.getTo(), "SDU");
		assertEquals(df.format(firstLeg.getDeparture()), "27/08/2012 06:10");
		assertEquals(df.format(firstLeg.getArrival()), "27/08/2012 07:08");
	}
	
	@Test
	public void isLastFlightMatching(){
		list = flightReader.getFlightsList();
		FlightLeg firstLeg = list.get(list.size()-1); 
		assertEquals(firstLeg.getNumber(), 1561);
		assertEquals(firstLeg.getFrom(), "SDU");
		assertEquals(firstLeg.getTo(), "CGH");
		assertEquals(df.format(firstLeg.getDeparture()), "27/08/2012 21:10");
		assertEquals(df.format(firstLeg.getArrival()), "27/08/2012 22:01");
	}
}
