package pairings.io;

import pairings.Duty;
import pairings.Leg;
import pairings.Pairing;

public class TerminalOutputer implements Outputable {
	private int pairingNumber;
	
	public TerminalOutputer() {
		pairingNumber = 0;
	}
	
	public int getPairingNumber() {
		return pairingNumber;
	}
	
	public void setPairingNumber(int pairingNumber) {
		this.pairingNumber = pairingNumber;
	}
	
	@Override
	public void output(Pairing pairing) {
		System.out.println("Pairing " + (++pairingNumber));
		int dutyNumber = 1;
		for(Duty duty: pairing.getDuties()) {
			System.out.println("\tDuty " + (dutyNumber++));
			for(Leg leg: duty.getLegs())
				System.out.println("\t\t" + leg.toString());
		}
	}
}
