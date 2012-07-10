package pairings.tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pairings.Leg;
import pairings.Rules;
import pairings.input.TimeTableReader;


public class TimeTableReaderTests {
	private TimeTableReader timeTableReader;
	private List<Leg> legs;
	private DateFormat df;
	
	@Before
	public void setUp() throws Exception {
		timeTableReader = new TimeTableReader("./src/readFlights/tests/time_table.txt");		
		legs = timeTableReader.getLegs();
		df = new SimpleDateFormat(Rules.DATE_FORMAT);
	}
	
	@Test
	public void itShouldRead62Legs() {
		assertEquals(62, legs.size());
	}
	
	@Test
	public void itShouldMatchFirstLeg(){
		Leg firstLeg = legs.get(0); 
		assertEquals(firstLeg.getNumber(), 1500);
		assertEquals(firstLeg.getFrom(), "CGH");
		assertEquals(firstLeg.getTo(), "SDU");
		assertEquals(df.format(firstLeg.getDeparture()), "27/08/2012 06:10");
		assertEquals(df.format(firstLeg.getArrival()), "27/08/2012 07:08");
	}
	
	@Test
	public void itShouldMatchLastFlight(){
		Leg firstLeg = legs.get(legs.size() - 1); 
		assertEquals(firstLeg.getNumber(), 1561);
		assertEquals(firstLeg.getFrom(), "SDU");
		assertEquals(firstLeg.getTo(), "CGH");
		assertEquals(df.format(firstLeg.getDeparture()), "27/08/2012 21:10");
		assertEquals(df.format(firstLeg.getArrival()), "27/08/2012 22:01");
	}
}
