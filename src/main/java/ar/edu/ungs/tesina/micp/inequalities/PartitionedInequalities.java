/**
 * 
 */
package ar.edu.ungs.tesina.micp.inequalities;

import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import ar.edu.ungs.tesina.micp.Color;
import ar.edu.ungs.tesina.micp.Edge;
import ar.edu.ungs.tesina.micp.MicpScipSolver;
import ar.edu.ungs.tesina.micp.SolverConfig;
import ar.edu.ungs.tesina.micp.Vertex;
import jscip.Constraint;
import jscip.Variable;

/**
 * @author yoshknight
 *
 */
public class PartitionedInequalities extends CustomInequalities {

	public static final long PARTITIONED_INEQUALITIES = 1;
	public static final long THREE_PARTITIONED_INEQUALITIES = 2;
	public static final long K_PARTITIONED_INEQUALITIES = 4;

	public static final long ALL_INEQUALITIES = PARTITIONED_INEQUALITIES
			+ THREE_PARTITIONED_INEQUALITIES + K_PARTITIONED_INEQUALITIES;

	public PartitionedInequalities(SolverConfig solverConfig) {
		super(solverConfig);
	}

	@Override
	public void addInequalities(MicpScipSolver micpSolver, List<Vertex> vertices,
			List<Color> colors, Graph<Vertex, Edge> conflictGraph,
			Graph<Vertex, Edge> relationshipGraph) {

		// Si no se selecciono ninguna inequality, se termina el metodo.
		if ((mInequalitiesEnabled & ALL_INEQUALITIES) == 0)
			return;

		if ((mInequalitiesEnabled & PARTITIONED_INEQUALITIES) != 0) {
			addPartitionedInequality(micpSolver, vertices, colors, conflictGraph,
					relationshipGraph);
		}

		if ((mInequalitiesEnabled & THREE_PARTITIONED_INEQUALITIES) != 0) {
			addThreePartitionedInequality(micpSolver, vertices, colors, conflictGraph,
					relationshipGraph);
		}

		if ((mInequalitiesEnabled & K_PARTITIONED_INEQUALITIES) != 0) {
			addKPartitionedInequality(micpSolver, vertices, colors, conflictGraph,
					relationshipGraph);
		}

	}

	/**
	 * Para cada relacion en H Agrega na partitioned inequality
	 * 
	 * @param micpSolver
	 * @param vertices
	 * @param colors
	 * @param conflictGraph
	 * @param relationshipGraph
	 */
	private void addPartitionedInequality(MicpScipSolver micpSolver, List<Vertex> vertices,
			List<Color> colors, Graph<Vertex, Edge> conflictGraph,
			Graph<Vertex, Edge> relationshipGraph) {

		int len = colors.size() - 1; // 1 <= len <= |colors|-1
		Set<Color> D = generateColorsSubset(colors, len);
		Set<Color> Dcomplement = generateComplement(colors, D);

		int cantFactors = 1 + colors.size();

		System.out.println("Agrego PartitionedInequality para cada relaion de H: " + cantFactors);

		// Yij <= sum Xid' + sum Xjd
		Variable[] vars = new Variable[cantFactors];
		double[] factors = new double[cantFactors];
		int i;

		for (Edge e : relationshipGraph.edgeSet()) {
			Vertex vi = e.getSource();
			Vertex vj = e.getTarget();
			i = 0;
			factors[i] = 1;
			vars[i++] = micpSolver.getVarY(vi, vj);

			for (Color c : D) {
				factors[i] = -1;
				vars[i++] = micpSolver.getVarX(vj, c);
			}

			for (Color c : Dcomplement) {
				factors[i] = -1;
				vars[i++] = micpSolver.getVarX(vi, c);
			}

			Constraint diferentColorsOnConflict = micpSolver.getSolver().createConsLinear(
					"VertexCliqueInequality-" + vi + "-" + vj, vars, factors, -2, 0);
			micpSolver.getSolver().addCons(diferentColorsOnConflict);
			micpSolver.getSolver().releaseCons(diferentColorsOnConflict);
		}

	}

	private void addThreePartitionedInequality(MicpScipSolver micpSolver, List<Vertex> vertices,
			List<Color> colors, Graph<Vertex, Edge> conflictGraph,
			Graph<Vertex, Edge> relationshipGraph) {

		int len = colors.size() - 1; // 2 <= len <= |colors|-1
		Set<Color> D = generateColorsSubset(colors, len);
		Set<Color> D1 = generateColorsSubset(D, D.size() / 2);
		Set<Color> D2 = generateComplement(D, D1);

		int cantFactors = 3 + 2 * D.size();

		System.out.println("Agrego 3-PartitionedInequality para cada relaion de H: " + cantFactors);

		// Yij + Yjk + Yik <= 3 + 2sum( Xjd1 - Xid1 ) + 2sum( Xk2 - Xid2 )
		Variable[] vars = new Variable[cantFactors];
		double[] factors = new double[cantFactors];
		int i;

		for (Edge e : relationshipGraph.edgeSet()) {
			Vertex vi = e.getSource();
			Vertex vj = e.getTarget();

			for (Vertex vk : Graphs.neighborListOf(relationshipGraph, vj)) {
				if (!vk.equals(vi) && !relationshipGraph.containsEdge(vk, vi)) {

					i = 0;
					factors[i] = 1;
					vars[i++] = micpSolver.getVarY(vi, vj);
					factors[i] = 1;
					vars[i++] = micpSolver.getVarY(vj, vk);
					factors[i] = 1;
					vars[i++] = micpSolver.getVarY(vi, vk);

					for (Color c : D1) {
						factors[i] = -2;
						vars[i++] = micpSolver.getVarX(vj, c);
						factors[i] = 2;
						vars[i++] = micpSolver.getVarX(vi, c);
					}

					for (Color c : D2) {
						factors[i] = -2;
						vars[i++] = micpSolver.getVarX(vk, c);
						factors[i] = 2;
						vars[i++] = micpSolver.getVarX(vi, c);
					}

					Constraint diferentColorsOnConflict = micpSolver.getSolver().createConsLinear(
							"ThreePartitionedInequality-" + vi + "-" + vj + "-" + vk, vars, factors,
							-4, 3);
					micpSolver.getSolver().addCons(diferentColorsOnConflict);
					micpSolver.getSolver().releaseCons(diferentColorsOnConflict);
				}
			}
		}

	}

	private void addKPartitionedInequality(MicpScipSolver micpSolver, List<Vertex> vertices,
			List<Color> colors, Graph<Vertex, Edge> conflictGraph,
			Graph<Vertex, Edge> relationshipGraph) {

		throw new RuntimeException(
				"ERROR - Not Implemented Method PartitionedInequalities::addKPartitionedInequality ");
	}

}
