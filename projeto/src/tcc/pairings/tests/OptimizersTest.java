package tcc.pairings.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Base;
import tcc.pairings.Pairing;
import tcc.pairings.costs.ExcessCalculator;
import tcc.pairings.generators.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.CplexOutputer;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.MpsOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.optimizers.CplexOptimizer;
import tcc.pairings.optimizers.GlpkOptimizer;
import tcc.pairings.optimizers.Optimizer;
import tcc.pairings.rules.Rules;

public class OptimizersTest {
	private FlightNetwork net;
	private MemoryOutputer memory;
	private Optimizer optimizer;
	
	@Before
	public void setUp() {
		Rules.MAX_DUTIES = 4;
		Rules.MIN_SIT_TIME = 25;
		Rules.MAX_LEGS = 5;
		Rules.MAX_TRACKS = 2;
		
		TimeTableReader reader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_10.txt");
		net = new FlightNetwork(reader.getLegs());
		net.build();
		
		memory = new MemoryOutputer();
	}
	
	@Test
	public void glpkShouldGiveRightSolutionCost() {	
		optimizer = getGlpkOptimizer();
		optimizer.optimize();
		itShouldGiveRightSolutionCost();
	}
	
	@Test
	public void glpkShouldSolveInstance() {	
		assertTrue(getGlpkOptimizer().optimize());
	}
	
	private GlpkOptimizer getGlpkOptimizer() {
		String mpsFile = FilePaths.OUTPUTS + "in.mps";
		String solutionFile = FilePaths.OUTPUTS + "out.sol";
		
		MpsOutputer mps = new MpsOutputer(net.getLegs(), mpsFile);
		Outputer[] outputers = new Outputer[] { memory, mps };
		ExcessCalculator calculator = new ExcessCalculator();
		PairingsGenerator generator = new PairingsGenerator(net, outputers, calculator);
	
		mps.writeUntilColumns();	
		generator.generate(new Base("CGH"));
		mps.writeRhsAndBounds(generator.getNumberOfPairings());
		mps.close();

		return new GlpkOptimizer(mpsFile, solutionFile);
	}
	
	@Test
	public void cplexShouldSolveInstance() {	
		assertTrue(getCplexOptimizer().optimize());
	}
	
	@Test
	public void cplexFromFileShouldSolveInstance() {	
		assertTrue(getCplexOptimizerFromFile().optimize());
	}
	
	@Test
	public void cplexShouldGiveRightSolutionCost() {	
		optimizer = getCplexOptimizer();
		optimizer.optimize();
		itShouldGiveRightSolutionCost();
	}
	
	@Test
	public void cplexFromFileShouldGiveRightSolutionCost() {	
		optimizer = getCplexOptimizerFromFile();
		optimizer.optimize();
		itShouldGiveRightSolutionCost();
	}
	
	private CplexOptimizer getCplexOptimizer() {
		CplexOutputer cplex = new CplexOutputer(net.getLegs());
		Outputer[] outputers = new Outputer[] { memory, cplex };
		ExcessCalculator calculator = new ExcessCalculator();
		PairingsGenerator generator = new PairingsGenerator(net, outputers, calculator);
		
		cplex.addRows();	
		generator.generate(new Base("CGH"));
		
		return new CplexOptimizer(cplex.getModel());
	}
	
	private CplexOptimizer getCplexOptimizerFromFile() {
		String mpsFile = FilePaths.OUTPUTS + "in.mps";
		
		MpsOutputer mps = new MpsOutputer(net.getLegs(), mpsFile);
		Outputer[] outputers = new Outputer[] { memory, mps };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		
		mps.writeUntilColumns();
		generator.generate(new Base("CGH"));
		mps.writeRhsAndBounds(generator.getNumberOfPairings());
		mps.close();
		
		return new CplexOptimizer(mpsFile);
	}

	private void itShouldGiveRightSolutionCost() {
		List<Pairing> pairings = getOptimalPairings();
		double cost = 0;
		for (Pairing pairing: pairings) 
			cost += pairing.getCost();
		assertEquals(optimizer.getObjectiveValue(), cost, 0.001);
	}

	private List<Pairing> getOptimalPairings() {
		List<Pairing> list = new ArrayList<Pairing>();
		List<Pairing> pairings = memory.getPairings(); 
		List<Integer> vars = optimizer.getOptimalVariables();
		for (int i: vars)
			list.add(pairings.get(i - 1));
		return list;
	}	
}
