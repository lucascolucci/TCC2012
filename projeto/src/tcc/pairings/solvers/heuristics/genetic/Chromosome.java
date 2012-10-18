package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Pairing;

public class Chromosome implements Cloneable {
	private List<Pairing> genes;

	public List<Pairing> getGenes() {
		return genes;
	}

	public void setGenes(List<Pairing> genes) {
		this.genes = genes;
	}
	
	public Chromosome() {
		genes = new ArrayList<Pairing>();
	}

	public Chromosome(List<Pairing> genes) {
		this.genes = genes;
	}
	
	public void addAll(List<Pairing> genes) {
		for (Pairing gene: genes)
			add(gene);
	}
	
	public void add(Pairing gene) {
		int i;
		for (i = 0; i < genes.size(); i++)
			if (genes.get(i).getCost() > gene.getCost())
				break;
		genes.add(i, gene);
	}
	
	public void remove(Pairing gene) {
		genes.remove(gene);
	}
	
	public void removeAll(List<Pairing> genes) {
		this.genes.removeAll(genes);
	}
	
	public boolean isEmpty() {
		return genes.isEmpty();
	}
	
	public int size() {
		return genes.size();
	}
	
	public Pairing get(int index) {
		return genes.get(index);
	}
	
	public boolean contains(Pairing gene) {
		return genes.contains(gene);
	}
	
	public boolean isDuplicate(Chromosome other) {
		if (size() != other.size())
			return false;
		for (Pairing gene : genes)
			if (!other.contains(gene))
				return false;
		return true;
	}
	
	@Override
	public Chromosome clone() {
		List<Pairing> clonedGenes = new ArrayList<Pairing>(genes);
		return new Chromosome(clonedGenes);
	}
}
