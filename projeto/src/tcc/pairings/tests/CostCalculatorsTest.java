package tcc.pairings.tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Duty;
import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.costs.DutyToFlightCalculator;
import tcc.pairings.costs.ExcessCalculator;
import tcc.pairings.costs.ExcessToFlightCalculator;
import tcc.pairings.rules.Rules;
import tcc.util.DateUtil;

public class CostCalculatorsTest {
	private CostCalculator calc;
	
	@Before
	public void setUp() throws Exception {
		Rules.MIN_SIT_TIME = 25;
		Rules.MIN_REST_TIME = 720;
	}

	@Test
	public void excessCalculotorShouldGiveTheRightCostFor1DayPairing() throws ParseException {
		Pairing pairing = get1DayPairing();
		calc = new ExcessCalculator();
		calc.setCost(pairing);
		assertEquals(87, pairing.getCost(), 0.001);
	}
	
	@Test
	public void excessToFlightCalculotorShouldGiveTheRightCostFor1DayPairing() throws ParseException {
		Pairing pairing = get1DayPairing();
		calc = new ExcessToFlightCalculator();
		calc.setCost(pairing);
		assertEquals(0.750, pairing.getCost(), 0.001);
	}
	
	@Test
	public void dutyToFlightCalculotorShouldGiveTheRightCostFor1DayPairing() throws ParseException {
		Pairing pairing = get1DayPairing();
		calc = new DutyToFlightCalculator();
		calc.setCost(pairing);
		assertEquals(1.966, pairing.getCost(), 0.001);
	}
	
	private Pairing get1DayPairing() throws ParseException {
		DateFormat df = new SimpleDateFormat(DateUtil.DATE_FORMAT);
		Date dep1 = (Date) df.parse("27/08/2012 06:10");
		Date arr1 = (Date) df.parse("27/08/2012 07:08");
		Date dep2 = (Date) df.parse("27/08/2012 09:00");
		Date arr2 = (Date) df.parse("27/08/2012 09:58");
		
		Leg leg1 = new Leg((short) 1234, "CGH", "UDI", dep1, arr1, (short) 1);
		Leg leg2 = new Leg((short) 1235, "UDI", "CGH", dep2, arr2, (short) 1);
		
		Duty duty = new Duty();
		duty.addLeg(new DutyLeg(leg1));
		duty.addLeg(new DutyLeg(leg2));
		
		Pairing pairing = new Pairing();
		pairing.addDuty(duty);
		
		return pairing;
	}
	
	@Test
	public void excessCalculotorShouldGiveTheRightCostFor2DaysPairing() throws ParseException {
		Pairing pairing = get2DaysPairing();
		calc = new ExcessCalculator();
		calc.setCost(pairing);
		assertEquals(832, pairing.getCost(), 0.001);
	}
	
	@Test
	public void excessToFlightCalculotorShouldGiveTheRightCostFor2DaysPairing() throws ParseException {
		Pairing pairing = get2DaysPairing();
		calc = new ExcessToFlightCalculator();
		calc.setCost(pairing);
		assertEquals(7.172, pairing.getCost(), 0.001);
	}
	
	@Test
	public void dutyToFlightCalculotorShouldGiveTheRightCostFor2DaysPairing() throws ParseException {
		Pairing pairing = get2DaysPairing();
		calc = new DutyToFlightCalculator();
		calc.setCost(pairing);
		assertEquals(1.000, pairing.getCost(), 0.001);
	}
	
	private Pairing get2DaysPairing() throws ParseException {
		DateFormat df = new SimpleDateFormat(DateUtil.DATE_FORMAT);
		Date dep1 = (Date) df.parse("27/08/2012 06:10");
		Date arr1 = (Date) df.parse("27/08/2012 07:08");
		Date dep2 = (Date) df.parse("28/08/2012 09:00");
		Date arr2 = (Date) df.parse("28/08/2012 09:58");
		
		Leg leg1 = new Leg((short) 1234, "CGH", "UDI", dep1, arr1, (short) 1);
		Leg leg2 = new Leg((short) 1235, "UDI", "CGH", dep2, arr2, (short) 1);
		
		Duty duty1 = new Duty();
		duty1.addLeg(new DutyLeg(leg1));
		
		Duty duty2 = new Duty();
		duty2.addLeg(new DutyLeg(leg2));
		
		Pairing pairing = new Pairing();
		pairing.addDuty(duty1);
		pairing.addDuty(duty2);
		
		return pairing;
	}
}
