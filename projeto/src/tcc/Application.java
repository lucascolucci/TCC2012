package tcc;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.PairingsGenerator;
import tcc.pairings.Rules;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.MpsOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.solvers.GlpkSolver;

public class Application {
	private FlightNetwork net;
	private PairingsGenerator generator;
	
	private static final String TIME_TABLES_PATH = "./time_tables/";
	private static final String OUTPUTS_PATH = "./outputs/";
	private static final int TRIALS = 5;
	
	public static void main(String[] args) {
		Application app = new Application();
		app.findNumberOfPairings();
		app.findGenerationTime();
		app.findGlpkSolutionTime();
	}

	public void findNumberOfPairings() {
		System.out.print("Nœmero de pairings... ");
		Rules.MAX_DUTIES = 2;
		findNumberOfpairings(36, "number_of_pairings_2.dat");
		Rules.MAX_DUTIES = 3;
		findNumberOfpairings(36, "number_of_pairings_3.dat");
		Rules.MAX_DUTIES = 4;
		findNumberOfpairings(36, "number_of_pairings_4.dat");
		System.out.println("Feito!");
	}
	
	private void findNumberOfpairings(int maxLegs, String outputFile) {
		ResultsWriter writer = new ResultsWriter(outputFile);
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			buildNet(getTrimmedList(allLegs, numberOfLegs));
			generatePairings(new String[] { "CGH" }, null);
			writer.write(numberOfLegs + "\t" + generator.getNumberOfPairings());
		}
		writer.close();
	}
	
	public void findGenerationTime() {
		System.out.print("Generation time... ");
		Rules.MAX_DUTIES = 2;
		findGenerationTime(36, "generation_time_2.dat");
		Rules.MAX_DUTIES = 3;
		findGenerationTime(36, "generation_time_3.dat");
		Rules.MAX_DUTIES = 4;
		findGenerationTime(36, "generation_time_4.dat");
		System.out.println("Feito!");
	}
	
	private void findGenerationTime(int maxLegs, String outputFile) {
		ResultsWriter writer = new ResultsWriter(outputFile);
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			List<Leg> trimmedList = getTrimmedList(allLegs, numberOfLegs);
			double[] values = new double[TRIALS]; 
			for (int i = 0; i < TRIALS; i++) {
				long start = System.nanoTime();
				buildNet(trimmedList);
				generatePairings(new String[] { "CGH" }, null);
				long stop = System.nanoTime();
				values[i] = (double) (stop - start) / 1000000;
			}
			double mean = getMean(values);
			writer.write(numberOfLegs + "\t" + mean + "\t" + getSD(values, mean));
		}
		writer.close();
	}
	
	public void findGlpkSolutionTime() {
		System.out.print("GLPK solution time... ");
		Rules.MAX_DUTIES = 2;
		findGlpkSolutionTime(36, "glpk_solution_time_2.dat");
		Rules.MAX_DUTIES = 3;
		findGlpkSolutionTime(36, "glpk_solution_time_3.dat");
		Rules.MAX_DUTIES = 4;
		findGlpkSolutionTime(36, "glpk_solution_time_4.dat");
		System.out.println("Feito!");
	}
	
	private void findGlpkSolutionTime(int maxLegs, String outputFile) {
		ResultsWriter writer = new ResultsWriter(outputFile);
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			List<Leg> trimmedLegs = getTrimmedList(allLegs, numberOfLegs);
			buildNet(trimmedLegs);
			MpsOutputer mps = new MpsOutputer(trimmedLegs, OUTPUTS_PATH + "cgh_sdu.mps");
			mps.writeUntilColumns();
			generatePairings(new String[] { "CGH" }, new Outputer[] { mps });
			mps.writeRhsAndBounds(generator.getNumberOfPairings());
			mps.close();
			GlpkSolver solver = new GlpkSolver(OUTPUTS_PATH + "cgh_sdu.mps", OUTPUTS_PATH + "cgh_sdu.sol");
			double[] values = new double[TRIALS]; 
			for (int i = 0; i < TRIALS; i++) {
				solver.solve();
				values[i] = solver.getSolutionTime();
			}
			double mean = getMean(values);
			writer.write(numberOfLegs + "\t" + mean + "\t" + getSD(values, mean));
		}
		writer.close();	
	}
	
	private List<Leg> getLegsFromFile(String fileName) {
		return (new TimeTableReader(TIME_TABLES_PATH + fileName)).getLegs();
	}
	
	private List<Leg> getTrimmedList(List<Leg> legs, int numberOfLegs) {
		List<Leg> list = new ArrayList<Leg>();
		int size = legs.size();
		int half = numberOfLegs / 2;
		list.addAll(legs.subList(0, half));
		list.addAll(legs.subList(size/2, size/2 + half));
		return list;
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
	
	private double getMean(double[] values) {
		double total = 0;
		for (double value: values)
			total += value;
		return total / values.length;
	}
	
	private double getSD(double[] values, double mean) {
		double total = 0.0;
		for (double value: values)
			total += (value - mean) * (value - mean);
		return Math.sqrt(total / (values.length - 1));
	}
}