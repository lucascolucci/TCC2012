package tcc.pairings.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Base;
import tcc.pairings.Pairing;
import tcc.pairings.PairingsGenerator;
import tcc.pairings.Rules;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.CplexOutputer;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.MpsOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.optimizers.CplexOptimizer;
import tcc.pairings.optimizers.GlpkOptimizer;
import tcc.pairings.optimizers.Optimizer;

public class OptimizersTest {
	private FlightNetwork net;
	private MemoryOutputer memory;
	
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
		GlpkOptimizer solver = getGlpkOptimizer();
		solver.optimize();
		itShouldGiveRightSolutionCost(solver);
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
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
	
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
		CplexOptimizer solver = getCplexOptimizer();
		solver.optimize();
		itShouldGiveRightSolutionCost(solver);
	}
	
	@Test
	public void cplexFromFileShouldGiveRightSolutionCost() {	
		CplexOptimizer solver = getCplexOptimizerFromFile();
		solver.optimize();
		itShouldGiveRightSolutionCost(solver);
	}
	
	private CplexOptimizer getCplexOptimizer() {
		CplexOutputer cplex = new CplexOutputer(net.getLegs());
		Outputer[] outputers = new Outputer[] { memory, cplex };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		
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

	private void itShouldGiveRightSolutionCost(Optimizer optimizer) {
		List<Pairing> solution = optimizer.getOptimalPairings(memory.getPairings());
		double cost = 0;
		for (Pairing pairing: solution) 
			cost += pairing.getCost();
		assertEquals(optimizer.getOptimalCost(), cost, 0.1);
	}	
}
