package tcc;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.PairingsGenerator;
import tcc.pairings.Rules;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.CplexOutputer;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.MpsOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TerminalOutputer;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.solvers.CplexSolver;
import tcc.pairings.solvers.GlpkSolver;

public class Application {
	private FlightNetwork net;
	private PairingsGenerator generator;
	
	private static final String TIME_TABLES_PATH = "./time_tables/";
	private static final String OUTPUTS_PATH = "./outputs/";
	private static final int GENERATION_TRIALS = 5;
	private static final int SOLUTION_TRIALS = 3;
	
	public static void main(String[] args) {
		Application app = new Application();
		app.doPairings();
		//app.doNumberOfPairings();
		//app.doGenerationTime();
		//app.doGlpkSolutionTime();
		//app.doCplexSolutionTime();
	}
	
	public void doPairings() {
		List<Leg> allLegs = getLegsFromFile("738_48.txt");
		buildNet(allLegs);
		Rules.MAX_DUTIES = 4;
		Rules.MAX_TRACKS = 2;
		Rules.MAX_LEGS = 5;
		Rules.MIN_SIT_TIME = 30;
		MemoryOutputer memory = new MemoryOutputer();
		CplexOutputer cplex = new CplexOutputer(net.getLegs());
		cplex.addRows();
		generatePairings(new String[] { "GRU", "GIG" }, new Outputer[] { cplex, memory });
		CplexSolver solver = new CplexSolver(cplex.getModel());
		if (solver.solve()) {
			new TerminalOutputer().output(solver.getSolution(memory.getPairings()));
			System.out.println("Custo = " + solver.getSolutionCost());
			System.out.println("Tamanho = " + solver.getSolutionSize());
		}
		System.out.println(generator.getNumberOfPairings());
	}

	public void doNumberOfPairings() {
		System.out.print("Nœmero de pairings... ");
		Rules.MAX_DUTIES = 2;
		doNumberOfpairings(36, "number_of_pairings_2.dat");
		Rules.MAX_DUTIES = 3;
		doNumberOfpairings(36, "number_of_pairings_3.dat");
		Rules.MAX_DUTIES = 4;
		doNumberOfpairings(36, "number_of_pairings_4.dat");
		System.out.println("Feito!");
	}
	
	private void doNumberOfpairings(int maxLegs, String dataFile) {
		ResultsWriter writer = new ResultsWriter(dataFile);
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_notail_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			buildNet(getTrimmedList(allLegs, numberOfLegs));
			generatePairings(new String[] { "CGH" }, null);
			writer.write(numberOfLegs + "\t" + generator.getNumberOfPairings());
		}
		writer.close();
	}
	
	public void doGenerationTime() {
		System.out.print("Generation time... ");
		Rules.MAX_DUTIES = 2;
		doGenerationTime(36, "generation_time_2.dat");
		Rules.MAX_DUTIES = 3;
		doGenerationTime(36, "generation_time_3.dat");
		Rules.MAX_DUTIES = 4;
		doGenerationTime(36, "generation_time_4.dat");
		System.out.println("Feito!");
	}
	
	private void doGenerationTime(int maxLegs, String dataFile) {
		ResultsWriter writer = new ResultsWriter(dataFile);
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_notail_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			List<Leg> trimmedList = getTrimmedList(allLegs, numberOfLegs);
			double[] values = new double[GENERATION_TRIALS]; 
			for (int i = 0; i < GENERATION_TRIALS; i++) {
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
	
	public void doGlpkSolutionTime() {
		System.out.print("GLPK solution time... ");
		Rules.MAX_DUTIES = 2;
		doGlpkSolutionTime(36, "glpk_solution_time_2.dat");
		Rules.MAX_DUTIES = 3;
		doGlpkSolutionTime(36, "glpk_solution_time_3.dat");
		Rules.MAX_DUTIES = 4;
		doGlpkSolutionTime(36, "glpk_solution_time_4.dat");
		System.out.println("Feito!");
	}
	
	private void doGlpkSolutionTime(int maxLegs, String dataFile) {
		ResultsWriter writer = new ResultsWriter(dataFile);
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_notail_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			List<Leg> trimmedLegs = getTrimmedList(allLegs, numberOfLegs);
			buildNet(trimmedLegs);
			MpsOutputer mps = new MpsOutputer(trimmedLegs, OUTPUTS_PATH + "cgh_sdu.mps");
			mps.writeUntilColumns();
			generatePairings(new String[] { "CGH" }, new Outputer[] { mps });
			mps.writeRhsAndBounds(generator.getNumberOfPairings());
			mps.close();
			GlpkSolver solver = new GlpkSolver(OUTPUTS_PATH + "cgh_sdu.mps");
			double[] values = new double[SOLUTION_TRIALS]; 
			for (int i = 0; i < SOLUTION_TRIALS; i++) {
				solver.solve();
				values[i] = solver.getSolutionTime();
			}
			double mean = getMean(values);
			writer.write(numberOfLegs + "\t" + mean + "\t" + getSD(values, mean));
		}
		writer.close();	
	}
	
	public void doCplexSolutionTime() {
		System.out.print("GLPK solution time... ");
		Rules.MAX_DUTIES = 2;
		doCplexSolutionTime(36, "cplex_solution_time_2.dat");
		Rules.MAX_DUTIES = 3;
		doCplexSolutionTime(36, "cplex_solution_time_3.dat");
		Rules.MAX_DUTIES = 4;
		doCplexSolutionTime(36, "cplex_solution_time_4.dat");
		System.out.println("Feito!");
	}
	
	private void doCplexSolutionTime(int maxLegs, String dataFile) {
		// TODO	
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