package tcc;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.TimeTableReader;

public class Main {
	private static final String TIME_TABLES_PATH = "./time_tables/";
	
	public static void main(String[] args) {
		TimeTableReader reader = new TimeTableReader(TIME_TABLES_PATH + "cgh_sdu_62.txt");
		List<Leg> allLegs = reader.getLegs();
		
		for (int totalLegs = 2; totalLegs <= 30; totalLegs += 2) {
			List<Leg> legs = getTrimmedList(allLegs, totalLegs);
			
			FlightNetwork net = new FlightNetwork(legs);
			net.build();
	
			PairingsGenerator generator = new PairingsGenerator(net, null);
			generator.generate("CGH");
	
			System.out.println(totalLegs + "\t" + generator.getNumberOfPairings());
		}		
	}
	
	private static List<Leg> getTrimmedList(List<Leg> legs, int totalLegs) {
		List<Leg> list = new ArrayList<Leg>();
		int size = legs.size();
		int half = totalLegs / 2;
		list.addAll(legs.subList(0, half));
		list.addAll(legs.subList(size/2, size/2 + half));
		return list;
	}
}