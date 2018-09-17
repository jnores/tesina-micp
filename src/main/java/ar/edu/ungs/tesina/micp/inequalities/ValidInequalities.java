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

public class ValidInequalities extends CustomInequalities {

	public static final long BOUNDING_INEQUALITIES = 512;
	public static final long REINFORCED_BOUNDING_INEQUALITIES = 1024;

	public static final long ALL_INEQUALITIES = BOUNDING_INEQUALITIES
			+ REINFORCED_BOUNDING_INEQUALITIES;

	public ValidInequalities(SolverConfig solverConfig) {
		super(solverConfig);
	}

	@Override
	public void addInequalities(MicpScipSolver micpSolver, List<Vertex> vertices, List<Color> colors,
			Graph<Vertex, Edge> conflictGraph, Graph<Vertex, Edge> relationshipGraph) {

		// Si no se selecciono ninguna inequality, se termina el metodo.
		if ((mInequalitiesEnabled & ALL_INEQUALITIES) == 0)
			return;
		
		if ((mInequalitiesEnabled & BOUNDING_INEQUALITIES) != 0) {
			addBoundigInequalities(micpSolver, vertices, colors, conflictGraph,
					relationshipGraph);
		}

		if ((mInequalitiesEnabled & REINFORCED_BOUNDING_INEQUALITIES) != 0) {
			addReinforcedBoundigInequalities(micpSolver, vertices, colors, conflictGraph,
					relationshipGraph);
		}


	}

	private void addBoundigInequalities(MicpScipSolver micpSolver, List<Vertex> vertices,
			List<Color> colors, Graph<Vertex, Edge> conflictGraph,
			Graph<Vertex, Edge> relationshipGraph) {
	
		for (Vertex j: vertices) {
			List<Vertex> setOfI = Graphs.neighborListOf(relationshipGraph, j);
			if ( setOfI.isEmpty() )
				continue;
			List<Vertex> neighborOfJ = Graphs.neighborListOf(conflictGraph, j);
			
			if (neighborOfJ.isEmpty() ) 
				continue;
			
			neighborOfJ.add(j);
			
			TreeSet<Vertex> vertexSubset = new TreeSet<Vertex>(neighborOfJ);
			Graph<Vertex, Edge> g = new AsSubgraph<Vertex, Edge>(conflictGraph, vertexSubset);
			
			BronKerboschCliqueFinder<Vertex, Edge> cliqueFinder;
			cliqueFinder = new BronKerboschCliqueFinder<Vertex, Edge>(g);
			Iterator<Set<Vertex>> it = cliqueFinder.iterator();

			while (it.hasNext()) {
				System.out.print("---- clique: [");
				Set<Vertex> clique= it.next();
				if ( clique.contains(j) ) {
					Collection<Vertex> k2 = clique;
					Collection<Vertex> k1 = new ArrayList<Vertex>();
					k1.add(j);
					k2.removeAll(k1);
					for(Vertex i: setOfI) {
						addInequalities(micpSolver, i,k1,k2,colors);
					}
				}
			}	
		}
	}

	private void addInequalities(MicpScipSolver micpSolver, Vertex vi,
			Collection<Vertex> k1, Collection<Vertex> k2, Collection<Color> D) {
		
		
		int cant = k1.size() + D.size()*(k2.size() + 1 );
		int min = (D.size() < (k2.size() + 1 ))? D.size() : (k2.size() + 1 ); 
		
		Variable[] vars = new Variable[cant];
		double[] factors = new double[cant];
		int i = 0;
		for (Vertex k : k1) {
			factors[i] = 1;
			vars[i++] = micpSolver.getVarY(vi, k);
		}
		
		for (Color d : D) {
			for (Vertex k : k2) {
				
				factors[i] = 1;
				vars[i++] = micpSolver.getVarX(k, d);
			}
			
			factors[i] = 1;
			vars[i++] = micpSolver.getVarX(vi, d);
		}
		
		
		Constraint constranint = micpSolver.getSolver().createConsLinear(
				"ReinforcedBoundigInequalities-" + vi + "-" + k1.size(), vars, factors, 0, 1 + min );
		micpSolver.getSolver().addCons(constranint);
		micpSolver.getSolver().releaseCons(constranint);
		
	}

	private void addReinforcedBoundigInequalities(MicpScipSolver micpSolver, List<Vertex> vertices,
			List<Color> colors, Graph<Vertex, Edge> conflictGraph,
			Graph<Vertex, Edge> relationshipGraph) {
		for (Vertex j: vertices) {
			List<Vertex> setOfI = Graphs.neighborListOf(relationshipGraph, j);
			if ( setOfI.isEmpty() )
				continue;
			List<Vertex> neighborOfJ = Graphs.neighborListOf(conflictGraph, j);
			
			if (neighborOfJ.isEmpty() ) 
				continue;
			
			neighborOfJ.add(j);
			
			TreeSet<Vertex> vertexSubset = new TreeSet<Vertex>(neighborOfJ);
			Graph<Vertex, Edge> g = new AsSubgraph<Vertex, Edge>(conflictGraph, vertexSubset);
			
			BronKerboschCliqueFinder<Vertex, Edge> cliqueFinder;
			cliqueFinder = new BronKerboschCliqueFinder<Vertex, Edge>(g);
			Iterator<Set<Vertex>> it = cliqueFinder.iterator();

			while (it.hasNext()) {

				Set<Vertex> clique= it.next();
				if ( clique.contains(j) ) {
					Collection<Vertex> k2 = clique;
					Collection<Vertex> k1 = new ArrayList<Vertex>();
					for(Vertex i: setOfI) {
						for ( Vertex v : clique)
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

}
