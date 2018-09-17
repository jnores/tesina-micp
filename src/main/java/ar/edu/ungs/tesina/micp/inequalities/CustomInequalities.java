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

abstract public class CustomInequalities {
	
	public static final long WITHOUT_INEQUALITIES = 0;

	protected SolverConfig mConfig;
	protected long mInequalitiesEnabled;

	public CustomInequalities(SolverConfig solverConfig) {
		mConfig = solverConfig;
		mInequalitiesEnabled = solverConfig.getInequalitiesEnabled();
	}
	
	abstract public void addInequalities(MicpScipSolver micpSolver, List<Vertex> vertices, List<Color> colors,
			Graph<Vertex, Edge> conflictGraph, Graph<Vertex, Edge> relationshipGraph);

	
	static public Set<Color> generateColorsSubset(Collection<Color> colors, int len) {

		if (len < 1)
			throw new RuntimeException("MicpScipSolver.generateColorsSubset La cantidad de colores solicitada debe ser mayor a 1");
		
		if (len > colors.size())
			throw new RuntimeException("MicpScipSolver.generateColorsSubset La cantidad de colores es mayor a la cantidad disponible");

		Set<Color> conj = new TreeSet<Color>();
		int i = 0;
		for (Color c : colors) {
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
	static public Set<Color> generateComplement(Collection<Color> colors, Collection<Color> set) {
		Set<Color> comp = new TreeSet<Color>();
		for (Color elem : set)
			if (!colors.contains(elem))
				comp.add(elem);
		return comp;
	}
}
