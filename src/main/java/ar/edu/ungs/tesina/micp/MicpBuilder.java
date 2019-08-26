package ar.edu.ungs.tesina.micp;

import java.security.InvalidParameterException;
import java.util.List;

import ar.edu.ungs.tesina.micp.inequalities.CustomInequalities;
import ar.edu.ungs.tesina.micp.inequalities.InequalitiesFactory;
import jscip.Scip;

public class MicpBuilder<T extends Vertex, U extends Color> {

	public Instance<T,U> buildInstance(String name, List<T> vertices, List<U> colors){
		return new Instance<T, U>(name, vertices, colors);
	}
	
	/**
	 * Verifica que el Solver pasado por parametro no sea nulo
	 * 
	 * Si es nulo Lanza una excepción En caso de que no sea null devuelve una
	 * instancia de la clase.
	 * 
	 * @param solver Una implementacion de la interfaz Solver
	 * @param ineq   Inscancia de la interfaz CustomIneq. Este puede contener un
	 *               contenedor de desigualdades o el ineqHelper.
	 * 
	 * @return Instancia del MicpScipSolver con los tipos especificados al crear la
	 *         instancia.
	 */
	public MicpScipSolver<T, U> buildMicpSolver(String name, SolverConfig solverConfig, CustomInequalities<T, U> ineq) {
		if (solverConfig == null)
			throw new InvalidParameterException("El parametro solverCOnfig no puede ser nulo");

		try {
			System.loadLibrary("jscip");
		} catch (UnsatisfiedLinkError ex) {
			throw new RuntimeException("No se encontro la libreria jscip.", ex);
		} catch (Exception ex) {
			throw new RuntimeException("No se pudo cargar la libreria jscip.", ex);
		}

		Scip solver = new Scip();
		solver.create("micp_app-" + name);

		solver.hideOutput(!solverConfig.isVerbose());

		solver.setRealParam("limits/time", solverConfig.getTimeLimit());
		solver.setRealParam("limits/gap", solverConfig.getGapLimit());
		solver.setRealParam("limits/memory", solverConfig.getMemoryLimit());

		return new MicpScipSolver<T, U>(solver, name, solverConfig, ineq);
	}

	/**
	 * 
	 * @param solver Una implementacion de la interfaz Solver
	 * @return Instancia del MicpScipSolver con los tipos especificados al crear la
	 *         instancia.
	 */
	public MicpScipSolver<T, U> buildMicpSolver(String name,SolverConfig solverConfig) {

		InequalitiesFactory<T, U> ineqHelper = new InequalitiesFactory<T, U>(solverConfig);

		return buildMicpSolver(name, solverConfig, ineqHelper.createInequalities() );
	}

}
