package tcc;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.costs.ExcessToFlightCalculator;
import tcc.pairings.generators.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.io.outputers.MpsOutputer;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.optimizers.CplexOptimizer;
import tcc.pairings.optimizers.GlpkOptimizer;
import tcc.pairings.rules.Rules;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.Solver;
import tcc.pairings.solvers.exacts.SetCoverSolver;
import tcc.pairings.solvers.exacts.SetPartitionSolver;
import tcc.pairings.solvers.heuristics.LocalSearchSolver;

public class Application {
	private static final String TIME_TABLES_PATH = "./time_tables/";
	private static final String OUTPUTS_PATH = "./outputs/";
	private static final int GENERATION_TRIALS = 5;
	private static final int SOLUTION_TRIALS = 3;
	
	public static void main(String[] args) {
		Application app = new Application();
		app.doInitialSolution();
		//app.doSetPartition();
		//app.doSetCover();
		//app.doLocalSearch();
		//app.doNumberOfPairings();
		//app.doGenerationTime();
		//app.doGlpkSolutionTime();
		//app.doCplexSolutionTime();
	}
	
	public void doInitialSolution() {
		Rules.MAX_DUTIES = 3;
		Base sao = new Base("CGH", "GRU");
		Solver solver = new InitialSolver(TIME_TABLES_PATH + "738_521.txt");
		System.out.println(solver.getSolution(sao));
	}
	
	public void doSetPartition() {
		Rules.MAX_DUTIES = 4;
		Base sao = new Base("GRU", "CGH");
		Base rio = new Base("GIG", "SDU");
		Base poa = new Base("POA");
		ExcessToFlightCalculator calc = new ExcessToFlightCalculator();
		Solver solver = new SetPartitionSolver(TIME_TABLES_PATH + "733_92.txt", calc);
		System.out.println(solver.getSolution(sao, rio, poa));
		System.out.println("Total de pairings = " + solver.getNumberOfPairings());
	}
	
	public void doSetCover() {
		Rules.MAX_DUTIES = 2;
		Base sao = new Base("GRU", "CGH");
		Base rio = new Base("GIG", "SDU");
		Base poa = new Base("POA");
		Base ssa = new Base("SSA");
		Base cnf = new Base("CNF");
		ExcessToFlightCalculator calc = new ExcessToFlightCalculator();
		Solver solver = new SetCoverSolver(TIME_TABLES_PATH + "733_92.txt", calc);
		System.out.println(solver.getSolution(sao, rio, poa, ssa, cnf));
	}
	
	public void doLocalSearch() {
		Rules.MAX_DUTIES = 3;
		Base sao = new Base("GRU", "CGH");
		Base rio = new Base("GIG", "SDU");
		ExcessToFlightCalculator calc = new ExcessToFlightCalculator();
		Solver solver = new LocalSearchSolver(TIME_TABLES_PATH + "738_48.txt", calc);
		System.out.println(solver.getSolution(sao, rio));
		System.out.println("Total de pairings = " + solver.getNumberOfPairings());	
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
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			FlightNetwork net = new FlightNetwork(getTrimmedList(allLegs, numberOfLegs));
			net.build();
			Base base = new Base("CGH");
			PairingsGenerator generator = new PairingsGenerator(net);
			generator.generate(base);
			writer.write(numberOfLegs + "\t" + generator.getNumberOfPairings());
		}
		writer.close();
	}
	
	public void doGenerationTime() {
		System.out.print("Generation time... ");
		Rules.MAX_TRACKS = Rules.MAX_LEGS;
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
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			List<Leg> trimmedLegs = getTrimmedList(allLegs, numberOfLegs);
			double[] values = new double[GENERATION_TRIALS]; 
			for (int i = 0; i < GENERATION_TRIALS; i++) {
				long start = System.nanoTime();
				FlightNetwork net = new FlightNetwork(trimmedLegs);
				net.build();
				Base base = new Base("CGH");
				PairingsGenerator generator = new PairingsGenerator(net);
				generator.generate(base);
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
		Rules.MAX_TRACKS = Rules.MAX_LEGS;
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
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			List<Leg> trimmedLegs = getTrimmedList(allLegs, numberOfLegs);
			FlightNetwork net = new FlightNetwork(trimmedLegs);
			net.build();
			MpsOutputer mps = new MpsOutputer(trimmedLegs, OUTPUTS_PATH + "cgh_sdu.mps");
			mps.writeUntilColumns();
			Base base = new Base("CGH");
			ExcessToFlightCalculator calc = new ExcessToFlightCalculator();
			PairingsGenerator generator = new PairingsGenerator(net, new Outputer[] { mps }, calc);
			generator.generate(base);
			mps.writeRhsAndBounds(generator.getNumberOfPairings());
			mps.close();
			GlpkOptimizer optimizer = new GlpkOptimizer(OUTPUTS_PATH + "cgh_sdu.mps");
			double[] values = new double[SOLUTION_TRIALS]; 
			for (int i = 0; i < SOLUTION_TRIALS; i++) {
				optimizer.optimize();
				values[i] = optimizer.getOptimizationTime();
			}
			double mean = getMean(values);
			writer.write(numberOfLegs + "\t" + mean + "\t" + getSD(values, mean));
		}
		writer.close();	
	}
	
	public void doCplexSolutionTime() {
		System.out.print("Cplex solution time... ");
		Rules.MAX_TRACKS = Rules.MAX_LEGS;
		Rules.MAX_DUTIES = 2;
		doCplexSolutionTime(36, "cplex_solution_time_2.dat");
		Rules.MAX_DUTIES = 3;
		doCplexSolutionTime(36, "cplex_solution_time_3.dat");
		Rules.MAX_DUTIES = 4;
		doCplexSolutionTime(36, "cplex_solution_time_4.dat");
		System.out.println("Feito!");
	}
	
	private void doCplexSolutionTime(int maxLegs, String dataFile) {
		ResultsWriter writer = new ResultsWriter(dataFile);
		List<Leg> allLegs = getLegsFromFile("cgh_sdu_62.txt");
		for (int numberOfLegs = 2; numberOfLegs <= maxLegs; numberOfLegs += 2) {
			List<Leg> trimmedLegs = getTrimmedList(allLegs, numberOfLegs);
			FlightNetwork net = new FlightNetwork(trimmedLegs);
			net.build();
			MpsOutputer mps = new MpsOutputer(trimmedLegs, OUTPUTS_PATH + "cgh_sdu.mps");
			mps.writeUntilColumns();
			Base base = new Base("CGH");
			ExcessToFlightCalculator calc = new ExcessToFlightCalculator();
			PairingsGenerator generator = new PairingsGenerator(net, new Outputer[] { mps }, calc);
			generator.generate(base);
			mps.writeRhsAndBounds(generator.getNumberOfPairings());
			mps.close();
			double[] values = new double[SOLUTION_TRIALS]; 
			for (int i = 0; i < SOLUTION_TRIALS; i++) {
				CplexOptimizer optimizer = new CplexOptimizer(OUTPUTS_PATH + "cgh_sdu.mps");
				optimizer.optimize();
				values[i] = optimizer.getOptimizationTime();
				optimizer.endModel();
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