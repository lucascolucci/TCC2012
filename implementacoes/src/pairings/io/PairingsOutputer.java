package pairings.io;

import java.util.List;

import pairings.Duty;
import pairings.Leg;
import pairings.Pairing;

public class PairingsOutputer {
	private static int pairingNumber = 1;
	
	public static int getPairingNumber() {
		return pairingNumber;
	}

	public static void setPairingNumber(int pairingNumber) {
		PairingsOutputer.pairingNumber = pairingNumber;
	}
	
	public static void print(List<Pairing> pairings) {
		for (Pairing pairing: pairings)
			print(pairing);
	}
	
	public static void print(Pairing pairing) {
		System.out.println("\nPairing " + (pairingNumber++));
		int dutyNumber = 1;
		for(Duty duty: pairing.getDuties()) {
			System.out.println("\tDuty " + (dutyNumber++));
			for(Leg leg: duty.getLegs())
				System.out.println("\t\t" + leg.toString());
		}
	}
}
