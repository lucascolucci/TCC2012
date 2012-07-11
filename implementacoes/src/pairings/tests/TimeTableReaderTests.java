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
		timeTableReader = new TimeTableReader("./src/pairings/tests/time_table.txt");		
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
		assertEquals(1500, firstLeg.getNumber());
		assertEquals("CGH", firstLeg.getFrom());
		assertEquals("SDU", firstLeg.getTo());
		assertEquals("27/08/2012 06:10", df.format(firstLeg.getDeparture()));
		assertEquals("27/08/2012 07:08", df.format(firstLeg.getArrival()));
	}
	
	@Test
	public void itShouldMatchLastFlight(){
		Leg firstLeg = legs.get(legs.size() - 1); 
		assertEquals(1561, firstLeg.getNumber());
		assertEquals("SDU", firstLeg.getFrom());
		assertEquals("CGH", firstLeg.getTo());
		assertEquals("27/08/2012 21:10", df.format(firstLeg.getDeparture()));
		assertEquals("27/08/2012 22:01", df.format(firstLeg.getArrival()));
	}
}
