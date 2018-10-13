package ar.edu.ungs.tesina.micp.inequalities;

import java.util.List;

import org.jgrapht.Graph;

import ar.edu.ungs.tesina.micp.Color;
import ar.edu.ungs.tesina.micp.Edge;
import ar.edu.ungs.tesina.micp.MicpScipSolver;
import ar.edu.ungs.tesina.micp.SolverConfig;
import ar.edu.ungs.tesina.micp.Vertex;

public class InequalitiesHelper<T extends Vertex,U extends Color> extends CustomInequalities<T,U> {
	private SolverConfig mConfig;
	private long mInequalitiesEnabled;

	public InequalitiesHelper(SolverConfig solverConfig) {
		super(solverConfig);
		mConfig = solverConfig;
		mInequalitiesEnabled = solverConfig.getInequalitiesEnabled();

	}

	@Override
	public void addInequalities(MicpScipSolver<T,U> micpSolver, List<T> vertices, List<U> colors,
			Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {
		// Si no se selecciono ninguna inequality, se termina el metodo.
		if (mInequalitiesEnabled <= CustomInequalities.WITHOUT_INEQUALITIES) {
			return;
		}

		CustomInequalities<T,U> ineq = null;

		if ((mInequalitiesEnabled & PartitionedInequalities.ALL_INEQUALITIES) != 0) {
			ineq = new PartitionedInequalities<T,U>(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

		if ((mInequalitiesEnabled & CliqueInequalities.ALL_INEQUALITIES) != 0) {
			ineq = new CliqueInequalities<T,U>(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

		if ((mInequalitiesEnabled & TriangleDiamondInequalities.ALL_INEQUALITIES) != 0) {
			ineq = new TriangleDiamondInequalities<T,U>(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

		if ((mInequalitiesEnabled & ValidInequalities.ALL_INEQUALITIES) != 0) {
			ineq = new ValidInequalities<T,U>(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

	}
}