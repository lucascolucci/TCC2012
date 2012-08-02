package tcc.pairings.tests;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Leg;
import tcc.pairings.Rules;
import tcc.pairings.io.TimeTableReader;

public class TimeTableReaderTest {
	private TimeTableReader timeTableReader;
	private List<Leg> legs;
	private DateFormat df;
	
	@Before
	public void setUp() throws Exception {
		df = new SimpleDateFormat(Rules.DATE_FORMAT);
	}
	
	@Test
	public void itShouldRead62NoTailLegs() {
		timeTableReader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_notail_62.txt");		
		legs = timeTableReader.getLegs();
		assertEquals(62, legs.size());
	}
	
	@Test
	public void itShouldRead62TailLegs() {
		timeTableReader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_tail_62.txt");		
		legs = timeTableReader.getLegs();
		assertEquals(62, legs.size());
	}
	
	@Test
	public void itShouldMatchFirstNoTailLeg() {
		timeTableReader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_notail_62.txt");		
		legs = timeTableReader.getLegs();
		Leg firstLeg = legs.get(0); 
		assertEquals(1500, firstLeg.getNumber());
		assertEquals("CGH", firstLeg.getFrom());
		assertEquals("SDU", firstLeg.getTo());
		assertEquals("27/08/2012 06:10", df.format(firstLeg.getDeparture()));
		assertEquals("27/08/2012 07:08", df.format(firstLeg.getArrival()));
		assertEquals(null, firstLeg.getTail());
	}
	
	@Test
	public void itShouldMatchFirstTailLeg() {
		timeTableReader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_tail_62.txt");		
		legs = timeTableReader.getLegs();
		Leg firstLeg = legs.get(0); 
		assertEquals(1500, firstLeg.getNumber());
		assertEquals("CGH", firstLeg.getFrom());
		assertEquals("SDU", firstLeg.getTo());
		assertEquals("27/08/2012 06:10", df.format(firstLeg.getDeparture()));
		assertEquals("27/08/2012 07:08", df.format(firstLeg.getArrival()));
		assertEquals("GOA", firstLeg.getTail());
	}
	
	@Test
	public void itShouldMatchLastNoTailLeg(){
		timeTableReader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_notail_62.txt");		
		legs = timeTableReader.getLegs();
		Leg lastLeg = legs.get(legs.size() - 1); 
		assertEquals(1561, lastLeg.getNumber());
		assertEquals("SDU", lastLeg.getFrom());
		assertEquals("CGH", lastLeg.getTo());
		assertEquals("27/08/2012 21:10", df.format(lastLeg.getDeparture()));
		assertEquals("27/08/2012 22:01", df.format(lastLeg.getArrival()));
		assertEquals(null, lastLeg.getTail());
	}
	
	@Test
	public void itShouldMatchLastTailLeg(){
		timeTableReader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_tail_62.txt");		
		legs = timeTableReader.getLegs();
		Leg lastLeg = legs.get(legs.size() - 1); 
		assertEquals(1561, lastLeg.getNumber());
		assertEquals("SDU", lastLeg.getFrom());
		assertEquals("CGH", lastLeg.getTo());
		assertEquals("27/08/2012 21:10", df.format(lastLeg.getDeparture()));
		assertEquals("27/08/2012 22:01", df.format(lastLeg.getArrival()));
		assertEquals("GOD", lastLeg.getTail());
	}
}
