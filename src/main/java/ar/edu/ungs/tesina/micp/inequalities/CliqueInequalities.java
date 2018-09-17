package ar.edu.ungs.tesina.micp.inequalities;

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

public class CliqueInequalities extends CustomInequalities {
	public static final long VERTEX_CLIQUE_INEQUALITIES = 8;
	public static final long CLIQUE_PARTITIONED_INEQUALITIES = 16;
	public static final long SUB_CLIQUE_INEQUALITIES = 32;
	public static final long TWO_COLOR_SUB_CLIQUE_INEQUALITIES = 64;

	public static final long ALL_INEQUALITIES = VERTEX_CLIQUE_INEQUALITIES
			+ CLIQUE_PARTITIONED_INEQUALITIES + SUB_CLIQUE_INEQUALITIES
			+ TWO_COLOR_SUB_CLIQUE_INEQUALITIES;

	public CliqueInequalities(SolverConfig solverConfig) {
		super(solverConfig);
	}

	/**
	 * Add the inequalities enabled by configuration. At this moment only two
	 * are available: - Vertex-clique inequalities - clique-partitioned
	 * inequalities
	 * 
	 * @param micpSolver
	 * @param vertices
	 * @param colors
	 * @param conflictGraph
	 * @param relationshipGraph
	 */
	@Override
	public void addInequalities(MicpScipSolver micpSolver, List<Vertex> vertices, List<Color> colors,
			Graph<Vertex, Edge> conflictGraph, Graph<Vertex, Edge> relationshipGraph) {

		// Si no se selecciono ninguna inequality, se termina el metodo.
		if ((mInequalitiesEnabled & ALL_INEQUALITIES) == 0)
			return;

		// Busco todas las cliques que cumplan con Figure 1.
		for (Vertex v : vertices) {

			List<Vertex> neighborList = Graphs.neighborListOf(relationshipGraph, v);
			TreeSet<Vertex> vertexSubset = new TreeSet<Vertex>(neighborList);
			Graph<Vertex, Edge> g = new AsSubgraph<Vertex, Edge>(conflictGraph, vertexSubset);

			System.out.print("-- cliques from " + v + " -> vecinos: ");
			System.out.println(vertexSubset.size() + " ]");

			if (vertexSubset.size() > 0) {
				BronKerboschCliqueFinder<Vertex, Edge> cliqueFinder;
				cliqueFinder = new BronKerboschCliqueFinder<Vertex, Edge>(g);
				Iterator<Set<Vertex>> it = cliqueFinder.iterator();

				while (it.hasNext()) {

					System.out.print("---- clique: [");
					Set<Vertex> cliqueVertex = it.next();
					for (Vertex vFromClique : cliqueVertex) {
						System.out.print(" " + vFromClique);
					}
					System.out.println(" ]");

					if ((mInequalitiesEnabled & VERTEX_CLIQUE_INEQUALITIES) != 0) {
						addVertexCliqueInequality(micpSolver,v, cliqueVertex);

					}

					if ((mInequalitiesEnabled & CLIQUE_PARTITIONED_INEQUALITIES) != 0) {
						addCliquePartitionedInequality(micpSolver,v, cliqueVertex, colors);
					}

				}
			}
		}

	}
	
	private void addVertexCliqueInequality(MicpScipSolver micpSolver, Vertex v, Set<Vertex> clique) {

		// Si tengo menos de 2 elementos en la clique, no es necesario agregar
		// la restricción.
		if (clique == null || clique.size() <= 1)
			return;

		System.out.println("Agrego VertexCliqueInequality al vector: " + v);

		// sum Yik <= 1
		Variable[] vars = new Variable[clique.size()];
		double[] factors = new double[clique.size()];
		int i = 0;
		for (Vertex auxV : clique) {
			factors[i] = 1;
			vars[i++] = micpSolver.getVarY(v, auxV);

		}
		Constraint constranint = micpSolver.getSolver().createConsLinear(
				"VertexCliqueInequality-" + v + "-" + clique.size(), vars, factors, 0, 1);
		micpSolver.getSolver().addCons(constranint);
		micpSolver.getSolver().releaseCons(constranint);
	}

	private void addCliquePartitionedInequality(MicpScipSolver micpSolver, Vertex v, Set<Vertex> clique, List<Color> colors) {
		// TODO reescribir la funcion para poder armar la constraint
		// dinamicamente.
		// if clique.size() == 1, use el theorem 7
		// Else, use Theorem 12.
		//
		// COMPLICAO!!
		if (v == null || clique == null || colors == null)
			return;
		if (clique.size() > colors.size())
			return;

		Set<Color> D = generateD(colors, clique);
		Set<Color> Dcomplement = generateComplement(colors, D);

		int cantFactors = clique.size() * (D.size() + 1) + Dcomplement.size();

		System.out
				.println("Agrego CliquePartitionedInequality al vector: " + v + "-" + cantFactors);

		// sum Yik <= 1
		Variable[] vars = new Variable[cantFactors];
		double[] factors = new double[cantFactors];
		int i = 0;
		for (Vertex auxV : clique) {
			factors[i] = 1;
			vars[i++] = micpSolver.getVarY(v, auxV);
		}

		for (Color c : D) {
			for (Vertex auxV : clique) {
				factors[i] = -1;
				vars[i++] = micpSolver.getVarX(auxV, c);
			}
		}

		for (Color c : Dcomplement) {
			factors[i] = -1;
			vars[i++] = micpSolver.getVarX(v, c);
		}

		Constraint constranint = micpSolver.getSolver().createConsLinear(
				"VertexCliqueInequality-" + v + "-" + cantFactors, vars, factors,
				-micpSolver.getSolver().infinity(), 0);
		micpSolver.getSolver().addCons(constranint);
		micpSolver.getSolver().releaseCons(constranint);

	}

	/**
	 * Genera un subconjunto de colores D tal que:. |D| <= |C| - |K|
	 * 
	 * @param colors
	 * @param clique
	 * @return
	 */
	private Set<Color> generateD(Collection<Color> colors, Collection<Vertex> clique) {
		
		int len = colors.size() - clique.size();

		if (len < 1)
			throw new RuntimeException("MicpScipSolver.generateD colors less than clique size.");		

		return generateColorsSubset(colors, len);
	}
	

}
