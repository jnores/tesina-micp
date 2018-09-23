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
	public void addInequalities(MicpScipSolver micpSolver, List<Vertex> vertices,
			List<Color> colors, Graph<Vertex, Edge> conflictGraph,
			Graph<Vertex, Edge> relationshipGraph) {

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
						addVertexCliqueInequality(micpSolver, v, cliqueVertex);
					}

					if ((mInequalitiesEnabled & CLIQUE_PARTITIONED_INEQUALITIES) != 0) {
						addCliquePartitionedInequality(micpSolver, v, cliqueVertex, colors);
					}

					if ((mInequalitiesEnabled & SUB_CLIQUE_INEQUALITIES) != 0) {
						addSubCliqueInequality(micpSolver, v, cliqueVertex, colors);
					}

					if ((mInequalitiesEnabled & TWO_COLOR_SUB_CLIQUE_INEQUALITIES) != 0) {
						addTwoColorSubCliqueInequality(micpSolver, v, cliqueVertex, colors);
					}

				}
			}
		}

	}

	private void addVertexCliqueInequality(MicpScipSolver micpSolver, Vertex v,
			Set<Vertex> clique) {

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

	private void addCliquePartitionedInequality(MicpScipSolver micpSolver, Vertex v,
			Set<Vertex> clique, List<Color> colors) {
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

	private void addSubCliqueInequality(MicpScipSolver micpSolver, Vertex vi, Set<Vertex> clique,
			List<Color> colors) {

		if (vi == null || clique == null || colors == null)
			return;

		// Si tengo menos de 2 elementos en la clique, no es necesario agregar
		// la restricción.
		if (clique == null || clique.size() <= 1)
			return;
		Set<Vertex> subclique = clique;
		System.out.println("Agrego SubCliqueInequality al vector: " + vi);
		int jPos = 0;
		Vertex vj = (Vertex) clique.toArray()[jPos];
		subclique.remove(vj);

		// Necesitamos |D| <= |C| - ( |K'| + 1 )
		Set<Color> D = generateColorsSubset(colors, colors.size() - subclique.size() + 1);
		Set<Color> Dcomplement = generateComplement(colors, D);
		Color c = (Color) Dcomplement.toArray()[0];

		int cantFactors = 3 + subclique.size() + D.size() * (1 + subclique.size());
		Variable[] vars = new Variable[cantFactors];
		double[] factors = new double[cantFactors];
		int i = 0;

		factors[i] = 1;
		vars[i++] = micpSolver.getVarY(vi, vj);

		for (Vertex auxV : subclique) {
			factors[i] = 2;
			vars[i++] = micpSolver.getVarY(vi, auxV);

		}

		// (Xic + Xjc)
		factors[i] = 1;
		vars[i++] = micpSolver.getVarX(vi, c);
		factors[i] = 1;
		vars[i++] = micpSolver.getVarX(vj, c);

		for (Color d : D) {
			for (Vertex auxV : subclique) {
				factors[i] = -2;
				vars[i++] = micpSolver.getVarX(auxV, d);
			}

			factors[i] = 2;
			vars[i++] = micpSolver.getVarX(vi, d);

		}

		Constraint constranint = micpSolver.getSolver().createConsLinear(
				"SubCliqueInequality-" + vi + "-" + vj + "-" + subclique.size(), vars, factors,
				-micpSolver.getSolver().infinity(), 3);
		micpSolver.getSolver().addCons(constranint);
		micpSolver.getSolver().releaseCons(constranint);
	}

	private void addTwoColorSubCliqueInequality(MicpScipSolver micpSolver, Vertex vi,
			Set<Vertex> clique, List<Color> colors) {
		// TODO Armar la desigualdad correspondiente en base de
		// CliquePartitionedInequality.
		// if clique.size() == 1, use el theorem 7
		// Else, use Theorem 12.
		//
		// COMPLICAO!!
		if (vi == null || clique == null || colors == null)
			return;
		if (clique.size() > colors.size())
			return;

		System.out.println("Agrego TwoColorSubCliqueInequality al vector: " + vi);

		Set<Vertex> subclique = clique;
		int jPos = 0;
		Vertex vj = (Vertex) clique.toArray()[jPos];
		subclique.remove(vj);

		// D != 0 y |D'| = |K'| Y |D| <= |C| - ( |D'| + 1 )
		Set<Color> Dprima = generateColorsSubset(colors, subclique.size());
		Set<Color> D = generateComplement(colors, Dprima); // Esto no deberia
															// ser un
															// complement.
															// deberia tener una
															// antidad random

		int cantFactors = 1 + subclique.size() + colors.size()
				+ Dprima.size() * (2 + subclique.size());

		// sum Yik <= 1
		Variable[] vars = new Variable[cantFactors];
		double[] factors = new double[cantFactors];
		int i = 0;

		factors[i] = 2;
		vars[i++] = micpSolver.getVarY(vi, vj);

		for (Vertex auxV : subclique) {
			factors[i] = 1;
			vars[i++] = micpSolver.getVarY(vi, auxV);

		}

		Vertex aux;
		for (Color c : colors) {
			factors[i] = -2;

			if (D.contains(c))
				aux = vi;
			else
				aux = vj;

			vars[i++] = micpSolver.getVarX(aux, c);
		}

		for (Color c : Dprima) {
			factors[i] = -1;
			vars[i++] = micpSolver.getVarX(vi, c);
			
			factors[i] = 2;
			vars[i++] = micpSolver.getVarX(vj, c);
			
			for (Vertex auxV : subclique) {
				factors[i] = 1;
				vars[i++] = micpSolver.getVarX(auxV, c);
			}
		}

		Constraint constranint = micpSolver.getSolver().createConsLinear(
				"TwoColorSubCliqueInequality-" + vi + "-" + cantFactors, vars, factors,
				-micpSolver.getSolver().infinity(), subclique.size());
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
