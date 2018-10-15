package ar.edu.ungs.tesina.micp.inequalities;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.Graph;

import ar.edu.ungs.tesina.micp.Color;
import ar.edu.ungs.tesina.micp.Edge;
import ar.edu.ungs.tesina.micp.MicpScipSolver;
import ar.edu.ungs.tesina.micp.SolverConfig;
import ar.edu.ungs.tesina.micp.Vertex;

abstract public class CustomInequalities<T extends Vertex,U extends Color> {
	
	public static final long WITHOUT_INEQUALITIES = 0;

	protected SolverConfig mConfig;
	protected List<Integer> mInequalitiesEnabled;

	public CustomInequalities(SolverConfig solverConfig) {
		mConfig = solverConfig;
		mInequalitiesEnabled = solverConfig.getInequalitiesEnabled();
	}
	
	abstract public void addInequalities(MicpScipSolver<T,U> micpSolver, List<T> vertices, List<U> colors,
			Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph);

	
	public Set<U> generateColorsSubset(Collection<U> colors, int len) {

		if (len < 1)
			throw new RuntimeException("MicpScipSolver.generateColorsSubset La cantidad de colores solicitada debe ser mayor a 1");
		
		if (len > colors.size())
			throw new RuntimeException("MicpScipSolver.generateColorsSubset La cantidad de colores es mayor a la cantidad disponible");

		Set<U> conj = new TreeSet<U>();
		int i = 0;
		for (U c : colors) {
			if (i >= len)
				break;
			conj.add(c);
			i++;
		}

		return conj;
	}
	
	/**
	 * Genera un conjunto complemento de un subconjunto de colores: _D = C - D
	 * 
	 * @See MicpScipSolver.generateD
	 * @param colors
	 * @param set
	 * @return
	 */
	public Set<U> generateComplement(Collection<U> colors, Collection<U> set) {
		Set<U> comp = new TreeSet<U>();
		for (U elem : colors)
			if (!set.contains(elem))
				comp.add(elem);
		return comp;
	}
}
