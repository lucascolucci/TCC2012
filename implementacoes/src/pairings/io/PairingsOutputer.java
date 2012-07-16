package pairings.io;

import java.util.List;

import pairings.Duty;
import pairings.Leg;
import pairings.Pairing;

public class PairingsOutputer {
	private List<Pairing> pairings;
	private static int pairingsCount = 0;
	
	public PairingsOutputer(List<Pairing> pairings) {
		this.pairings = pairings;
	}
	
	public void print() {
		int pairingsCount = 1;
		for(Pairing pairing: pairings) {
			System.out.println("\nPairing " + (pairingsCount++));
			int dutiesCount = 1;
			for(Duty duty: pairing.getDuties()) {
				System.out.println("\tDuty " + (dutiesCount++));
				for(Leg leg: duty.getLegs())
					System.out.println("\t\t" + leg.dataToString());
			}
		}
	}
	
	public static void print(Pairing pairing) {
		System.out.println("\nPairing " + (pairingsCount++));
		int dutiesCount = 1;
		for(Duty duty: pairing.getDuties()) {
			System.out.println("\tDuty " + (dutiesCount++));
			for(Leg leg: duty.getLegs())
				System.out.println("\t\t" + leg.dataToString());
		}
	}
}
