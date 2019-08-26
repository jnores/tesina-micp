package ar.edu.ungs.tesina.micp.inequalities;

import java.util.HashSet;
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

public class TriangleDiamondInequalities<T extends Vertex, U extends Color>
		extends CustomInequalities<T, U> {

	protected List<Integer> mInequalitiesEnabled;

	public TriangleDiamondInequalities(SolverConfig solverConfig) {
		mInequalitiesEnabled = solverConfig.getInequalitiesEnabled();
	}

	@Override
	public void addInequalities(MicpScipSolver<T, U> micpSolver, List<T> vertices, List<U> colors,
			Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {

		// Si no se selecciono ninguna inequality, se termina el metodo.
		if (mInequalitiesEnabled.isEmpty())
			return;

		if (mInequalitiesEnabled.contains(DefinedInequalitiesEnum.SEMI_TRIANGLE_INEQUALITIES)) {
			addSemiTriangleInequalities(micpSolver, vertices, colors, conflictGraph,
					relationshipGraph);
		}

		if (mInequalitiesEnabled.contains(DefinedInequalitiesEnum.SEMI_DIAMOND_INEQUALITIES)) {
			addSemiDiamondInequalities(micpSolver, vertices, colors, conflictGraph,
					relationshipGraph);
		}

	}

	public void addSemiTriangleInequalities(MicpScipSolver<T, U> micpSolver, List<T> vertices,
			List<U> colors, Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {

		for (Edge<T> e : relationshipGraph.edgeSet()) {
			T vi = e.getSource();
			T vj = e.getTarget();

			for (T vk : Graphs.neighborListOf(conflictGraph, vj)) {
				if (!vk.equals(vi) && conflictGraph.containsEdge(vi, vk)) {
					// aqui tengo un semi-triangulo.
					addSemiTriangleInequalitiesForTriangle(micpSolver, vi, vj, vk, colors);
				}
			}

		}
	}

	private void addSemiTriangleInequalitiesForTriangle(MicpScipSolver<T, U> micpSolver, T vi, T vj,
			T vk, List<U> colors) {
		final double[] factors = { 1, 1, 1, 1, 1, -1, -1 };
		Variable[] vars = new Variable[7];
		vars[0] = micpSolver.getVarY(vi, vj);
		for (U d1 : colors) {
			for (U d2 : colors) {
				if (!d1.equals(d2)) {
					// TODO: acá tengo vi, vj, vk, d1 y d2 . Debo agregar la
					// inequalitie.
					vars[1] = micpSolver.getVarX(vi, d1);
					vars[2] = micpSolver.getVarX(vj, d2);
					vars[3] = micpSolver.getVarX(vk, d1);
					vars[4] = micpSolver.getVarX(vk, d2);
					// minus Xjd1 ; minus Xid2
					vars[5] = micpSolver.getVarX(vj, d1);
					vars[6] = micpSolver.getVarX(vi, d2);

					Constraint diferentColorsOnConflict = micpSolver.getSolver()
							.createConsLinear("SemiTriangleInequality-V(" + vi + ";" + vj + ";" + vk
									+ ") C(" + d1 + ";" + d2 + ")", vars, factors, -2, 2);
					micpSolver.getSolver().addCons(diferentColorsOnConflict);
					micpSolver.getSolver().releaseCons(diferentColorsOnConflict);
				}
			}
		}

	}

	public void addSemiDiamondInequalities(MicpScipSolver<T, U> micpSolver, List<T> vertices,
			List<U> colors, Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {

		for (Edge<T> e : conflictGraph.edgeSet()) {
			T vj = e.getTarget();
			T vk = e.getSource();

			for (T vl : Graphs.neighborListOf(conflictGraph, vj)) {
				if (!vl.equals(vk) && conflictGraph.containsEdge(vk, vl)) {
					// Aqui tengo un triangulo en G y Busco un vector i que
					// forme el semi-Diamond.
					for (T vi : Graphs.neighborListOf(relationshipGraph, vj)) {
						if (!vi.equals(vk) && relationshipGraph.containsEdge(vi, vk)
								&& !conflictGraph.containsEdge(vi, vl)) {
							// Aqui tengo el Semi-Diamond
							addSemiDiamondInequalitiesForDiamond(micpSolver, vi, vj, vk, vl,
									colors);
						}
					}
				}
			}

		}
	}

	private void addSemiDiamondInequalitiesForDiamond(MicpScipSolver<T, U> micpSolver, T vi, T vj,
			T vk, T vl, List<U> colors) {

		Set<U> Daux = new HashSet<U>(colors);
		Set<U> D;
		int len = colors.size() - 3; // 1 <= len <= |colors|-3

		double[] factors;
		Variable[] vars;
		int i, cant;

		for (U d1 : colors) {
			for (U d2 : colors) {
				if (!d1.equals(d2)) {
					Daux.remove(d1);
					Daux.remove(d2);
					D = generateColorsSubset(Daux, len);

					cant = D.size() * 3 + 8;
					factors = new double[cant];
					vars = new Variable[cant];
					i = 0;

					factors[i] = 1;
					vars[i++] = micpSolver.getVarY(vi, vj);

					factors[i] = 2;
					vars[i++] = micpSolver.getVarY(vi, vk);

					factors[i] = -1;
					vars[i++] = micpSolver.getVarX(vi, d1);

					factors[i] = 1;
					vars[i++] = micpSolver.getVarX(vk, d1);

					factors[i] = 1;
					vars[i++] = micpSolver.getVarX(vi, d2);

					factors[i] = -1;
					vars[i++] = micpSolver.getVarX(vk, d2);

					factors[i] = 1;
					vars[i++] = micpSolver.getVarX(vl, d1);

					factors[i] = 1;
					vars[i++] = micpSolver.getVarX(vl, d2);

					for (U d : D) {
						factors[i] = -1;
						vars[i++] = micpSolver.getVarX(vj, d);

						factors[i] = -1;
						vars[i++] = micpSolver.getVarX(vk, d);

						factors[i] = 1;
						vars[i++] = micpSolver.getVarX(vi, d);
					}

					Constraint diferentColorsOnConflict = micpSolver.getSolver().createConsLinear(
							"SemiDiamondInequality-V(" + vi + ";" + vj + ";" + vk + ";" + vl
									+ ") C(" + d1 + ";" + d2 + ")",
							vars, factors, -micpSolver.getSolver().infinity(), 3);
					micpSolver.getSolver().addCons(diferentColorsOnConflict);
					micpSolver.getSolver().releaseCons(diferentColorsOnConflict);
					Daux.add(d1);
					Daux.add(d2);
				}
			}
		}
	}
}