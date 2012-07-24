package tcc;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TimeTableReader;

public class ResultsBuilder {
	private FlightNetwork net;
	private PairingsGenerator generator;
	
	private static final String TIME_TABLES_PATH = "./time_tables/";
	
	public static void main(String[] args) {
		ResultsBuilder rb = new ResultsBuilder();
		rb.buildNumberOfPairingsFor2Stations(30);
	}
	
	private void buildNumberOfPairingsFor2Stations(int maxLegs) {
		ResultsWriter writer = new ResultsWriter("number_of_pairings_2_stations.txt");
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			buildNet(getTrimmedList(allLegs, numberOfLegs));
			generatePairings(new String[] { "CGH" }, null);
			writer.write(numberOfLegs + "\t" + generator.getNumberOfPairings());
		}
		writer.close();
	}
	
	private List<Leg> getTrimmedList(List<Leg> legs, int numberOfLegs) {
		List<Leg> list = new ArrayList<Leg>();
		int size = legs.size();
		int half = numberOfLegs / 2;
		list.addAll(legs.subList(0, half));
		list.addAll(legs.subList(size/2, size/2 + half));
		return list;
	}
	
	private List<Leg> getLegsFromFile(String fileName) {
		return (new TimeTableReader(TIME_TABLES_PATH + fileName)).getLegs();
	}
	
	private void buildNet(List<Leg> legs) {
		net = new FlightNetwork(legs);
		net.build();
	}

	private void generatePairings(String[] bases, Outputer[] outputers) {
		generator = new PairingsGenerator(net, outputers);
		for (String base: bases)
			generator.generate(base);
	}
}