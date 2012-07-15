package pairings.tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import pairings.DateUtil;
import pairings.Rules;

public class DateUtilTests {
	private DateFormat df; 
	private Date before;
	private Date after;
	
	@Before
	public void setUp() {
		df = new SimpleDateFormat(Rules.DATE_FORMAT);
	}
	
	@Test
	public void itShouldGive0Minute() throws ParseException {
		before = (Date) df.parse("15/01/2010 19:04");
		after = (Date) df.parse("15/01/2010 19:04");
		assertEquals(0, DateUtil.difference(before, after));
	}

	@Test
	public void itShouldGive1Minute() throws ParseException {
		before = (Date) df.parse("05/10/2012 00:00");
		after = (Date) df.parse("05/10/2012 00:01");
		assertEquals(1, DateUtil.difference(before, after));
	}
	
	@Test
	public void itShouldGive30Minutes() throws ParseException {
		before = (Date) df.parse("05/07/2012 10:15");
		after = (Date) df.parse("05/07/2012 10:45");
		assertEquals(30, DateUtil.difference(before, after));
	}
	
	@Test
	public void itShouldGive45Minutes() throws ParseException {
		before = (Date) df.parse("31/12/2012 23:30");
		after = (Date) df.parse("01/01/2013 00:15");
		assertEquals(45, DateUtil.difference(before, after));
	}
	
	@Test
	public void itShouldGive2881Minutes() throws ParseException {
		before = (Date) df.parse("31/01/2012 10:20");
		after = (Date) df.parse("02/02/2012 10:21");
		assertEquals(2881, DateUtil.difference(before, after));
	}
	
	@Test
	public void itShouldAddOneDay() throws ParseException {
		before = (Date) df.parse("15/01/2010 19:04");
		after = (Date) df.parse("16/01/2010 19:04");
		assertEquals(after, DateUtil.addOneDay(before));
	}
}
