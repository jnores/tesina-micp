package ar.edu.ungs.tesina.micp.inequalities;

import java.util.List;

import org.jgrapht.Graph;

import ar.edu.ungs.tesina.micp.Color;
import ar.edu.ungs.tesina.micp.Edge;
import ar.edu.ungs.tesina.micp.MicpScipSolver;
import ar.edu.ungs.tesina.micp.SolverConfig;
import ar.edu.ungs.tesina.micp.Vertex;

public class InequalitiesHelper {
	private SolverConfig mConfig;
	private long mInequalitiesEnabled;

	public InequalitiesHelper(SolverConfig solverConfig) {
		mConfig = solverConfig;
		mInequalitiesEnabled = solverConfig.getInequalitiesEnabled();

	}

	public void addInequalities(MicpScipSolver micpSolver, List<Vertex> vertices,
			List<Color> colors, Graph<Vertex, Edge> conflictGraph,
			Graph<Vertex, Edge> relationshipGraph) {
		// Si no se selecciono ninguna inequality, se termina el metodo.
		if (mInequalitiesEnabled <= CustomInequalities.WITHOUT_INEQUALITIES) {
			return;
		}

		CustomInequalities ineq = null;

		if ((mInequalitiesEnabled & PartitionedInequalities.ALL_INEQUALITIES) != 0) {
			ineq = new PartitionedInequalities(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

		if ((mInequalitiesEnabled & CliqueInequalities.ALL_INEQUALITIES) != 0) {
			ineq = new CliqueInequalities(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

		if ((mInequalitiesEnabled & TriangleDiamondInequalities.ALL_INEQUALITIES) != 0) {
			ineq = new TriangleDiamondInequalities(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

		if ((mInequalitiesEnabled & ValidInequalities.ALL_INEQUALITIES) != 0) {
			ineq = new ValidInequalities(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

	}
}