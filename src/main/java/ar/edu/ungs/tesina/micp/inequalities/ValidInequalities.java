package ar.edu.ungs.tesina.micp.inequalities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.clique.BronKerboschCliqueFinder;
import org.jgrapht.graph.AsSubgraph;

import ar.edu.ungs.tesina.micp.Color;
import ar.edu.ungs.tesina.micp.Edge;
import ar.edu.ungs.tesina.micp.MicpScipSolver;
import ar.edu.ungs.tesina.micp.SolverConfig;
import ar.edu.ungs.tesina.micp.Vertex;
import jscip.Constraint;
import jscip.Variable;

public class ValidInequalities<T extends Vertex, U extends Color> extends CustomInequalities<T,U> {

	public static final int BOUNDING_INEQUALITIES = 11;
	public static final int REINFORCED_BOUNDING_INEQUALITIES = 12;

	public ValidInequalities(SolverConfig solverConfig) {
		super(solverConfig);
	}

	@Override
	public void addInequalities(MicpScipSolver<T,U> micpSolver, List<T> vertices, List<U> colors,
			Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {

		// Si no se selecciono ninguna inequality, se termina el metodo.
		if ( mInequalitiesEnabled.isEmpty() )
			return;
		
		if ( mInequalitiesEnabled.contains( BOUNDING_INEQUALITIES) ) {
			addBoundigInequalities(micpSolver, vertices, colors, conflictGraph,
					relationshipGraph);
		}

		if ( mInequalitiesEnabled.contains( REINFORCED_BOUNDING_INEQUALITIES) ) {
			addReinforcedBoundigInequalities(micpSolver, vertices, colors, conflictGraph,
					relationshipGraph);
		}


	}

	private void addBoundigInequalities(MicpScipSolver<T,U> micpSolver, List<T> vertices,
			List<U> colors, Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {
	
		for (T j: vertices) {
			List<T> setOfI = Graphs.neighborListOf(relationshipGraph, j);
			if ( setOfI.isEmpty() )
				continue;
			List<T> neighborOfJ = Graphs.neighborListOf(conflictGraph, j);
			
			if (neighborOfJ.isEmpty() ) 
				continue;
			
			neighborOfJ.add(j);
			
			TreeSet<T> vertexSubset = new TreeSet<T>(neighborOfJ);
			Graph<T, Edge<T>> g = new AsSubgraph<T, Edge<T>>(conflictGraph, vertexSubset);
			
			BronKerboschCliqueFinder<T, Edge<T>> cliqueFinder;
			cliqueFinder = new BronKerboschCliqueFinder<T, Edge<T>>(g);
			Iterator<Set<T>> it = cliqueFinder.iterator();

			while (it.hasNext()) {
				System.out.print("---- clique: [");
				Set<T> clique= it.next();
				if ( clique.contains(j) ) {
					Collection<T> k2 = clique;
					Collection<T> k1 = new ArrayList<T>();
					k1.add(j);
					k2.removeAll(k1);
					for(T i: setOfI) {
						addInequalities(micpSolver, i,k1,k2,colors);
					}
				}
				
			}	
		}
	}

	private void addInequalities(MicpScipSolver<T,U> micpSolver, T vi,
			Collection<T> k1, Collection<T> k2, Collection<U> D) {
		
		
		int cant = k1.size() + D.size()*(k2.size() + 1 );
		int min = (D.size() < (k2.size() + 1 ))? D.size() : (k2.size() + 1 ); 
		
		Variable[] vars = new Variable[cant];
		double[] factors = new double[cant];
		int i = 0;
		for (T k : k1) {
			factors[i] = 1;
			vars[i++] = micpSolver.getVarY(vi, k);
		}
		
		for (U d : D) {
			for (T k : k2) {
				
				factors[i] = 1;
				vars[i++] = micpSolver.getVarX(k, d);
			}
			
			factors[i] = 1;
			vars[i++] = micpSolver.getVarX(vi, d);
		}
		
		
		Constraint constranint = micpSolver.getSolver().createConsLinear(
				"ReinforcedBoundigInequalities-" + vi + "-" + k1.size(), vars, factors, 0, 1 + min);
		micpSolver.getSolver().addCons(constranint);
		micpSolver.getSolver().releaseCons(constranint);
		
	}

	private void addReinforcedBoundigInequalities(MicpScipSolver<T,U> micpSolver, List<T> vertices,
			List<U> colors, Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {
		for (T j: vertices) {
			List<T> setOfI = Graphs.neighborListOf(relationshipGraph, j);
			
			if ( setOfI.isEmpty() )
				continue;
			
			List<T> neighborOfJ = Graphs.neighborListOf(conflictGraph, j);
			
			if (neighborOfJ.isEmpty() ) 
				continue;
			
			neighborOfJ.add(j);
			
			TreeSet<T> vertexSubset = new TreeSet<T>(neighborOfJ);
			Graph<T, Edge<T>> g = new AsSubgraph<T, Edge<T>>(conflictGraph, vertexSubset);
			
			BronKerboschCliqueFinder<T, Edge<T>> cliqueFinder;
			cliqueFinder = new BronKerboschCliqueFinder<T, Edge<T>>(g);
			Iterator<Set<T>> it = cliqueFinder.iterator();

			while (it.hasNext()) {

				Set<T> clique= it.next();
				if ( clique.contains(j) ) {
					Collection<T> k2 = clique;
					Collection<T> k1 = new ArrayList<T>();
					for(T i: setOfI) {
						for ( T v : clique)
							if (relationshipGraph.containsEdge(v, i))
								k1.add(v);
						k2.removeAll(k1);
						if ( !k2.isEmpty() )
							addInequalities(micpSolver, i,k1,k2,colors);
					}
				}
			}	
		}
		
	}

	public static boolean mustAddInequalities(List<Integer> mInequalitiesEnabled) {
		if (mInequalitiesEnabled.isEmpty())
			return false;
		return mInequalitiesEnabled.contains(BOUNDING_INEQUALITIES)
				|| mInequalitiesEnabled.contains(REINFORCED_BOUNDING_INEQUALITIES);
	}

}
