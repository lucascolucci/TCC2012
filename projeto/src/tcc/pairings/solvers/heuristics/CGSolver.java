package tcc.pairings.solvers.heuristics;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.generators.CGGenerator;
import tcc.pairings.io.outputers.CGCplexOutputer;
import tcc.pairings.io.outputers.DHCplexOutputer;
import tcc.pairings.io.outputers.MemoryOutputer;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.optimizers.CplexOptimizer;
import tcc.pairings.solvers.BasicSolver;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.Solution;

public class CGSolver extends BasicSolver {
	private InitialSolver initialSolver;
	private Solution initialSolution;
	private CGCplexOutputer cplex;
	private List<Pairing> generatedPairings;
	private double reducedCostCutoff = 1.0;
	
	public double getReducedCostCutoff() {
		return reducedCostCutoff;
	}

	public void setReducedCostCutoff(double reducedCostCutoff) {
		this.reducedCostCutoff = reducedCostCutoff;
	}

	public InitialSolver getInitialSolver() {
		return initialSolver;
	}
	
	public CGSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public CGSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
		initialSolver = new InitialSolver(timeTable);
	}

	@Override
	protected void setOutputers() {
		memory = new MemoryOutputer(); 
		cplex = new CGCplexOutputer(legs, calculator);
		cplex.addRows();
		cplex.addDHVariables();
		cplex.getModel().setOut(null);
		outputers = new Outputer[]{ memory, cplex }; 
	}

	@Override
	protected void generatePairings() {
		initialSolution = initialSolver.getSolution(bases);
		if (initialSolution != null) {
			System.out.println(initialSolution);
			for (Outputer outputer: outputers)
				outputer.output(initialSolution.getPairings());
			numberOfPairings = initialSolver.getNumberOfPairings();
		}
	}
	
	@Override
	protected void setOptimizer() {
		optimizer = new CplexOptimizer(cplex.getModel());
	}
	
	@Override
	protected Solution getSolution() {
		if (initialSolution == null)
			return null;
		generatedPairings = new ArrayList<Pairing>(memory.getPairings());
		return columnGeneration();
	}

	private Solution columnGeneration() {
		int newColumns, iteration = 0;
		do {
			optimizer.optimize();
			double obj = optimizer.getObjectiveValue();
			solvePricing();
			newColumns = addGeneratedPairings();
			output(++iteration, obj, newColumns);
		} while (newColumns > 0);		
		columnManagement();
		optimizer.endModel();
		return getIntegerSolution();
	}
	
	private void solvePricing() {
		setupFlightNetwork();
		generateMinReducedCostPairings();
		if (memory.getPairings().isEmpty())
			System.out.println("Menor custo reduzido encontrado é positivo!!");
	}

	private void setupFlightNetwork() {
		net.setDuals(optimizer.getDuals());
		net.clearLabels();
	}
	
	private void generateMinReducedCostPairings() {
		memory = new MemoryOutputer();
		outputers = new Outputer[] { memory };
		CGGenerator generator = new CGGenerator(net, outputers, calculator);
		generator.generate(bases);
	}
	
	private int addGeneratedPairings() {
		int count = 0;
		for (Pairing pairing: memory.getPairings())
			if (!generatedPairings.contains(pairing)) {
				cplex.output(pairing);
				generatedPairings.add(pairing);
				count++;
			}
		return count;
	}
	
	private void output(int iteration, double obj, int newColumns) {
		System.out.println(iteration + "\t" + obj + "\t" + newColumns);
	}
	
	private void columnManagement() {
		List<Pairing> remove = new ArrayList<Pairing>();
		double[] reducedCosts = optimizer.getReducedCosts();
		int i0 = legs.size() - 1;
		for (int i = 0; i < generatedPairings.size(); i++) {
			if (reducedCosts[i0 + i] > reducedCostCutoff)
				remove.add(generatedPairings.get(i));
		}
		System.out.println("Colunas removidas (custo reduzido): " + remove.size());
		generatedPairings.removeAll(remove);
	}

	private Solution getIntegerSolution() {
		System.out.println("Obtendo solução inteira (" + generatedPairings.size() + " colunas)... ");
		Solution solution = getSuperSolution();
		if (solution != null) {
			setDeadHeads(solution);
			setCostsWithDeadHeads(solution.getPairings());
			System.out.println("Cheque de cobertura: " + solution.isAllLegsCovered(legs));
			System.out.println("Cheque de custo: " + solution.isCostRight());
		}
		optimizer.endModel();
		return solution;
	}

	private Solution getSuperSolution() {
		resetMemoryOutputer();
		resetOptimizer();		
		return super.getSolution();
	}
	
	private void resetMemoryOutputer() {
		memory.clear();
		memory.output(generatedPairings);
	}

	private void resetOptimizer() {
		DHCplexOutputer dhCplex = new DHCplexOutputer(legs, calculator);
		dhCplex.addRows();
		dhCplex.output(memory.getPairings());
		dhCplex.addDHVariables();
		//dhCplex.getModel().setOut(null);
		optimizer = new CplexOptimizer(dhCplex.getModel());
	}
	
	private void setDeadHeads(Solution solution) {
		List<Integer> artificials = optimizer.getArtificialValues();
		for (int i = 0; i < legs.size(); i++) {
			int numberOfDeadHeads = artificials.get(i);
			if (numberOfDeadHeads > 0)
				solution.setDeadHeads(legs.get(i), numberOfDeadHeads);
		}
	}
	
	public void printDebug() {
		System.out.println("===============================================================");
		String status = optimizer.getStatus().toString();
		double[] duals = optimizer.getDuals();
		double[] vars = optimizer.getVars();
		double[] reds = optimizer.getReducedCosts();
		System.out.println("Status = " + status);
		System.out.println("Duais:");
		for (double dual: duals)
			System.out.println(dual);
		System.out.println("Variáveis:");
		for (double var: vars)
			System.out.println(var);
		System.out.println("Custos reduzidos:");
		for (double red: reds)
			System.out.println(red);		
	}
}
