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

public class CliqueInequalities<T extends Vertex, U extends Color> extends CustomInequalities<T,U> {
	public static final int VERTEX_CLIQUE_INEQUALITIES = 5;
	public static final int CLIQUE_PARTITIONED_INEQUALITIES = 6;
	public static final int SUB_CLIQUE_INEQUALITIES = 7;
	public static final int TWO_COLOR_SUB_CLIQUE_INEQUALITIES = 8;

	
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
	public void addInequalities(MicpScipSolver<T,U> micpSolver, List<T> vertices,
			List<U> colors, Graph<T, Edge<T>> conflictGraph,
			Graph<T, Edge<T>> relationshipGraph) {

		// Si no se selecciono ninguna inequality, se termina el metodo.
		if ( mInequalitiesEnabled.isEmpty() )
			return;

		// Busco todas las cliques que cumplan con Figure 1.
		for (T v : vertices) {

			List<T> neighborList = Graphs.neighborListOf(relationshipGraph, v);
			TreeSet<T> vertexSubset = new TreeSet<T>(neighborList);
			Graph<T, Edge<T>> g = new AsSubgraph<T, Edge<T>>(conflictGraph, vertexSubset);

			System.out.print("-- cliques from " + v + " -> vecinos: ");
			System.out.println(vertexSubset.size() + " ]");

			if (vertexSubset.size() > 0) {
				BronKerboschCliqueFinder<T, Edge<T>> cliqueFinder;
				cliqueFinder = new BronKerboschCliqueFinder<T, Edge<T>>(g);
				Iterator<Set<T>> it = cliqueFinder.iterator();

				while (it.hasNext()) {

					System.out.print("---- clique: [");
					Set<T> cliqueVertex = it.next();
					for (Vertex vFromClique : cliqueVertex) {
						System.out.print(" " + vFromClique);
					}
					System.out.println(" ]");

					if ( mInequalitiesEnabled.contains( VERTEX_CLIQUE_INEQUALITIES ) ) {
						addVertexCliqueInequality(micpSolver, v, cliqueVertex);
					}

					if ( mInequalitiesEnabled.contains( CLIQUE_PARTITIONED_INEQUALITIES ) ) {
						addCliquePartitionedInequality(micpSolver, v, cliqueVertex, colors);
					}

					if ( mInequalitiesEnabled.contains( SUB_CLIQUE_INEQUALITIES ) ) {
						addSubCliqueInequality(micpSolver, v, cliqueVertex, colors);
					}

					if ( mInequalitiesEnabled.contains( TWO_COLOR_SUB_CLIQUE_INEQUALITIES ) ) {
						addTwoColorSubCliqueInequality(micpSolver, v, cliqueVertex, colors);
					}

				}
			}
		}

	}

	private void addVertexCliqueInequality(MicpScipSolver<T,U> micpSolver, T v,
			Set<T> clique) {

		// Si tengo menos de 2 elementos en la clique, no es necesario agregar
		// la restricción.
		if (clique == null || clique.size() <= 1)
			return;

		System.out.println("Agrego VertexCliqueInequality al vector: " + v);

		// sum Yik <= 1
		Variable[] vars = new Variable[clique.size()];
		double[] factors = new double[clique.size()];
		int i = 0;
		for (T auxV : clique) {
			factors[i] = 1;
			vars[i++] = micpSolver.getVarY(v, auxV);

		}
		Constraint constranint = micpSolver.getSolver().createConsLinear(
				"VertexCliqueInequality-" + v + "-" + clique.size(), vars, factors, 0, 1);
		micpSolver.getSolver().addCons(constranint);
		micpSolver.getSolver().releaseCons(constranint);
	}

	private void addCliquePartitionedInequality(MicpScipSolver<T,U> micpSolver, T v,
			Set<T> clique, List<U> colors) {
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

		Set<U> D = generateD(colors, clique);
		Set<U> Dcomplement = generateComplement(colors, D);

		int cantFactors = clique.size() * (D.size() + 1) + Dcomplement.size();

		System.out
				.println("Agrego CliquePartitionedInequality al vector: " + v + "-" + cantFactors);

		// sum Yik <= 1
		Variable[] vars = new Variable[cantFactors];
		double[] factors = new double[cantFactors];
		int i = 0;
		for (T auxV : clique) {
			factors[i] = 1;
			vars[i++] = micpSolver.getVarY(v, auxV);
		}

		for (U c : D) {
			for (T auxV : clique) {
				factors[i] = -1;
				vars[i++] = micpSolver.getVarX(auxV, c);
			}
		}

		for (U c : Dcomplement) {
			factors[i] = -1;
			vars[i++] = micpSolver.getVarX(v, c);
		}

		Constraint constranint = micpSolver.getSolver().createConsLinear(
				"VertexCliqueInequality-" + v + "-" + cantFactors, vars, factors,
				-micpSolver.getSolver().infinity(), 0);
		micpSolver.getSolver().addCons(constranint);
		micpSolver.getSolver().releaseCons(constranint);

	}

	private void addSubCliqueInequality(MicpScipSolver<T,U> micpSolver, T vi, Set<T> clique,
			List<U> colors) {

		if (vi == null || clique == null || colors == null)
			return;

		// Si tengo menos de 2 elementos en la clique, no es necesario agregar
		// la restricción.
		// Es necesario que el tamaño de la clique mayor que 2 porque en la seleccion de DcC 
		// Como uso el borde, |C| - |K'| +1 es igual a |C|
		if (clique == null || clique.size() <= 2)
			return;
		
		
		Set<T> subclique = clique;
		System.out.println("Agrego SubCliqueInequality al vector: " + vi);
		int jPos = 0;
		// TODO: Corregir esto pasando un array donde recibir el contenido.
		T vj = (T) clique.toArray()[jPos];
		subclique.remove(vj);
		if (subclique.size() >= colors.size())
			return;

		// Necesitamos |D| <= |C| - ( |K'| + 1 )
		Set<U> D = generateColorsSubset(colors, colors.size() - subclique.size() + 1);
		Set<U> Dcomplement = generateComplement(colors, D);
		// TODO: Corregir esto pasando un array donde recibir el contenido.
		U c = (U) Dcomplement.toArray()[0];

		int cantFactors = 3 + subclique.size() + D.size() * (1 + subclique.size());
		Variable[] vars = new Variable[cantFactors];
		double[] factors = new double[cantFactors];
		int i = 0;

		factors[i] = 1;
		vars[i++] = micpSolver.getVarY(vi, vj);

		for (T auxV : subclique) {
			factors[i] = 2;
			vars[i++] = micpSolver.getVarY(vi, auxV);

		}

		// (Xic + Xjc)
		factors[i] = 1;
		vars[i++] = micpSolver.getVarX(vi, c);
		factors[i] = 1;
		vars[i++] = micpSolver.getVarX(vj, c);

		for (U d : D) {
			for (T auxV : subclique) {
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

	/**
	 * Let K c V a clique in G and i \in V-K : ik \in Eh \forall k \in K. 
	 * 
	 * @param micpSolver
	 * @param vi
	 * @param clique
	 * @param colors
	 */
	private void addTwoColorSubCliqueInequality(MicpScipSolver<T,U> micpSolver, T vi,
			Set<T> clique, List<U> colors) {
		// TODO Armar la desigualdad correspondiente en base de
		// CliquePartitionedInequality.
		// if clique.size() == 1, use el theorem 7
		// Else, use Theorem 12.
		//
		// COMPLICAO!!
		if (vi == null || clique == null || colors == null)
			return;
		if (clique.size() >=  colors.size())
			return;
		if (clique.size() <  2)
			return;

		System.out.println("Agrego TwoColorSubCliqueInequality al vector: " + vi);

		Set<T> subclique = clique;
		int jPos = 0;
		
		// TODO Ver como corregir este warning
		T vj = (T) clique.toArray()[jPos];
		subclique.remove(vj);

		// D != 0 y |D'| = |K'| Y |D| <= |C| - ( |D'| + 1 )
		Set<U> Dprima = generateColorsSubset(colors, subclique.size());
		Set<U> D = generateComplement(colors, Dprima); // Esto no deberia
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

		for (T auxV : subclique) {
			factors[i] = 1;
			vars[i++] = micpSolver.getVarY(vi, auxV);

		}

		T aux;
		for (U c : colors) {
			factors[i] = -2;

			if (D.contains(c))
				aux = vi;
			else
				aux = vj;

			vars[i++] = micpSolver.getVarX(aux, c);
		}

		for (U c : Dprima) {
			factors[i] = -1;
			vars[i++] = micpSolver.getVarX(vi, c);
			
			factors[i] = 2;
			vars[i++] = micpSolver.getVarX(vj, c);
			
			for (T auxV : subclique) {
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
	private Set<U> generateD(Collection<U> colors, Collection<T> clique) {

		int len = colors.size() - clique.size();

		if (len < 1)
			throw new RuntimeException("MicpScipSolver.generateD colors less than clique size.");

		return generateColorsSubset(colors, len);
	}

	public static boolean mustAddInequalities(List<Integer> mInequalitiesEnabled) {
		if (mInequalitiesEnabled.isEmpty())
			return false;
		return mInequalitiesEnabled.contains(CLIQUE_PARTITIONED_INEQUALITIES)
				|| mInequalitiesEnabled.contains(VERTEX_CLIQUE_INEQUALITIES)
				|| mInequalitiesEnabled.contains(SUB_CLIQUE_INEQUALITIES)
				|| mInequalitiesEnabled.contains(TWO_COLOR_SUB_CLIQUE_INEQUALITIES);
	}

}
