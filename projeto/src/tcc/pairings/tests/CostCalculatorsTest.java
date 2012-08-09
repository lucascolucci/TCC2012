package tcc.pairings.tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Duty;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.Rules;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.costs.ExcessCalculator;
import tcc.pairings.costs.ExcessToFlightCalculator;

public class CostCalculatorsTest {
	private CostCalculator calc;
	
	@Before
	public void setUp() throws Exception {
		Rules.MIN_SIT_TIME = 25;
		Rules.MIN_REST_TIME = 720;
	}

	@Test
	public void excessCalculotorShouldGiveTheRightCostForOneDayPairing() throws ParseException {
		Pairing pairing = getOneDayPairing();
		calc = new ExcessCalculator();
		calc.setCost(pairing);
		assertEquals(87, pairing.getCost(), 0.001);
	}
	
	@Test
	public void excessToFlightCalculotorShouldGiveTheRightCostForOneDayPairing() throws ParseException {
		Pairing pairing = getOneDayPairing();
		calc = new ExcessToFlightCalculator();
		calc.setCost(pairing);
		assertEquals(0.75, pairing.getCost(), 0.001);
	}
	
	@Test
	public void excessCalculotorShouldGiveTheRightCostForTwoDaysPairing() throws ParseException {
		Pairing pairing = getTwoDaysPairing();
		calc = new ExcessCalculator();
		calc.setCost(pairing);
		assertEquals(832, pairing.getCost(), 0.001);
	}
	
	@Test
	public void excessToFlightCalculotorShouldGiveTheRightCostForTwoDaysPairing() throws ParseException {
		Pairing pairing = getTwoDaysPairing();
		calc = new ExcessToFlightCalculator();
		calc.setCost(pairing);
		assertEquals(7.172, pairing.getCost(), 0.001);
	}

	private Pairing getOneDayPairing() throws ParseException {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		Date dep1 = (Date) df.parse("27/08/2012 06:10");
		Date arr1 = (Date) df.parse("27/08/2012 07:08");
		Date dep2 = (Date) df.parse("27/08/2012 09:00");
		Date arr2 = (Date) df.parse("27/08/2012 09:58");
		
		Leg leg1 = new Leg((short) 1234, "CGH", "UDI", dep1, arr1, (short) 1);
		Leg leg2 = new Leg((short) 1235, "UDI", "CGH", dep2, arr2, (short) 1);
		
		Duty duty = new Duty();
		duty.addLeg(leg1);
		duty.addLeg(leg2);
		
		Pairing pairing = new Pairing();
		pairing.addDuty(duty);
		
		return pairing;
	}
	
	private Pairing getTwoDaysPairing() throws ParseException {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		Date dep1 = (Date) df.parse("27/08/2012 06:10");
		Date arr1 = (Date) df.parse("27/08/2012 07:08");
		Date dep2 = (Date) df.parse("28/08/2012 09:00");
		Date arr2 = (Date) df.parse("28/08/2012 09:58");
		
		Leg leg1 = new Leg((short) 1234, "CGH", "UDI", dep1, arr1, (short) 1);
		Leg leg2 = new Leg((short) 1235, "UDI", "CGH", dep2, arr2, (short) 1);
		
		Duty duty1 = new Duty();
		duty1.addLeg(leg1);
		
		Duty duty2 = new Duty();
		duty2.addLeg(leg2);
		
		Pairing pairing = new Pairing();
		pairing.addDuty(duty1);
		pairing.addDuty(duty2);
		
		return pairing;
	}
}
