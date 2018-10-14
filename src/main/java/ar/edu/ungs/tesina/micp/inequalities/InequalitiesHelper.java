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
	private List<Integer> mInequalitiesEnabled;

	public InequalitiesHelper(SolverConfig solverConfig) {
		super(solverConfig);
		mConfig = solverConfig;
		mInequalitiesEnabled = solverConfig.getInequalitiesEnabled();

	}

	@Override
	public void addInequalities(MicpScipSolver<T,U> micpSolver, List<T> vertices, List<U> colors,
			Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {
		// Si no se selecciono ninguna inequality, se termina el metodo.
		if (mInequalitiesEnabled.isEmpty()) {
			return;
		}

		CustomInequalities<T,U> ineq = null;

		if ( PartitionedInequalities.mustAddInequalities(mInequalitiesEnabled)) {
			ineq = new PartitionedInequalities<T,U>(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

		if ( CliqueInequalities.mustAddInequalities(mInequalitiesEnabled) ) {
			ineq = new CliqueInequalities<T,U>(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

		if ( TriangleDiamondInequalities.mustAddInequalities(mInequalitiesEnabled) ) {
			ineq = new TriangleDiamondInequalities<T,U>(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

		if ( ValidInequalities.mustAddInequalities(mInequalitiesEnabled) ) {
			ineq = new ValidInequalities<T,U>(mConfig);
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
			ineq = null;
		}

	}
}