/**
 * 
 */
package ar.edu.ungs.tesina.micp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.jgrapht.Graph;

import ar.edu.ungs.tesina.micp.inequalities.InequalitiesHelper;
import jscip.Constraint;
import jscip.SCIP_Vartype;
import jscip.Scip;
import jscip.Solution;
import jscip.Variable;

/**
 * @author yoshknight
 *
 */
public class MicpScipSolver {

	private Scip mSolver;

	private Map<ComparablePair<Vertex, Color>, Variable> mVarX;
	private Map<ComparablePair<Vertex, Vertex>, Variable> mVarY;

	private int mNSols = 0;
	private String mName = "";
	private long mInitSearchingTime, mInitSolvingTime;

	private InequalitiesHelper mInequalitiesHelper;

	private boolean mIsVerbose = true;

	/**
	 * Verifica que el Solver pasado por parametro no sea nulo
	 * 
	 * Si es nulo Lanza una excepción En caso de que no sea null devuelve una
	 * instancia de la clase.
	 * 
	 * @param solver
	 *            una implementacion de la interfaz Solver
	 * @return
	 */
	public static MicpScipSolver createMicp(String name, Properties prop) throws RuntimeException {
		try {
			System.loadLibrary("jscip");

		} catch (UnsatisfiedLinkError ex) {
			throw new RuntimeException("No se encontro la libreria jscip.", ex);
		} catch (Exception ex) {
			throw new RuntimeException("No se pudo cargar la libreria jscip.", ex);
		}
		SolverConfig solverConfig = new SolverConfig(prop);

		Scip solver = new Scip();
		solver.create("micp_app-" + name);

		solver.hideOutput(!solverConfig.isVerbose());

		solver.setRealParam("limits/time", solverConfig.getTimeLimit());
		solver.setRealParam("limits/gap", solverConfig.getGapLimit());
		
		InequalitiesHelper ineqHelper = new InequalitiesHelper(solverConfig); 

		return new MicpScipSolver(solver, name, solverConfig, ineqHelper);
	}

	/**
	 * 
	 * @param solver
	 */
	private MicpScipSolver(Scip solver, String name, SolverConfig solverConfig, InequalitiesHelper inequalitiesHelper) {
		mIsVerbose = solverConfig.isVerbose();
		mSolver = solver;
		mName = name;
		mInequalitiesHelper = inequalitiesHelper;
	}

	/**
	 * Aplica el modelo lineal sobre el solver cargado en el constructor de la
	 * clase.
	 * 
	 * @param conflictGraph
	 *            grafo de conflictos entre vértises
	 * @param relationshipGraph
	 *            Grafo de relaciones entre vértices.
	 * @param colors
	 *            Lista de colores disponibles
	 */
	public Map<Vertex, Color> searchOptimal(Graph<Vertex, Edge> conflictGraph,
			Graph<Vertex, Edge> relationshipGraph, List<Color> colors) {

		mInitSearchingTime = System.currentTimeMillis();

		List<Vertex> vertices = new ArrayList<Vertex>(conflictGraph.vertexSet());
		Collections.sort(vertices);

		// System.out.print( "Vertices: [ " );
		// for (Vertex v:vertices)
		// System.out.print( v+" " );
		// System.out.println( "]" );
		// System.out.print( "Colors: [ " );
		// for (Color c: colors)
		// System.out.print( c+" " );
		// System.out.println( "]" );
		// System.out.println( "CONFLICTO: "+conflictGraph );
		// System.out.println( "RELACION: "+relationshipGraph );
		// Variable[][] varX = new Variable[V.size()][colors.size()];
		mVarX = new TreeMap<ComparablePair<Vertex, Color>, Variable>();
		mVarY = new TreeMap<ComparablePair<Vertex, Vertex>, Variable>();

		// Se crean las variables binarias e implicitamente se define la función
		// objetivo.

		/**
		 * 5. varX is Binary {0,1}
		 */
		for (Vertex v : vertices) {
			for (Color c : colors) {
				Variable var = mSolver.createVar(v + "-" + c, 0, 1, 0,
						SCIP_Vartype.SCIP_VARTYPE_BINARY);
				putVarX(v, c, var);
			}
		}

		/**
		 * 6. varY is Binary {0,1}
		 */
		for (Edge e : relationshipGraph.edgeSet()) {
			Vertex s = relationshipGraph.getEdgeSource(e);
			Vertex t = relationshipGraph.getEdgeTarget(e);

			if (s.compareTo(t) < 0) {
				Variable var;
				var = mSolver.createVar(s + "-" + t, 0, 1, 1, SCIP_Vartype.SCIP_VARTYPE_BINARY);

				putVarY(s, t, var);
			}
			// else {
			// var = mSolver.createVar(s+"-"+t, 0, 1, 0,
			// SCIP_Vartype.SCIP_VARTYPE_BINARY);
			// }

		}

		// Se agregan las restricciones Basicas.
		addMandatoryConstraints(vertices, colors, conflictGraph, relationshipGraph);

		// Se agregan las inecuaciones a probar. Estas de deben habilitar y
		// deshabilitar por
		// configuracion. por default de usar la primera.

		if ( mInequalitiesHelper != null )
			mInequalitiesHelper.addInequalities(this, vertices, colors, conflictGraph, relationshipGraph);

		// Se busca una solucion optima.
		Solution sol = solve();

		// Se genera un Map que asocia vertice con color para devolver como
		// solucion encontrada.
		Map<Vertex, Color> optimal = readSolution(sol, vertices, colors);

		// Se imprime informacion de muestreo de la solucion obtenida.
		printExecutionData(sol, vertices, colors, conflictGraph, relationshipGraph);

		// Libero las variables creadas para definir el modelo.
		for (Variable v : mVarX.values())
			mSolver.releaseVar(v);
		for (Variable v : mVarY.values())
			mSolver.releaseVar(v);

		mSolver.free();
		mSolver = null;

		return optimal;
	}

	/**
	 * 
	 * 
	 * @param varX
	 * @param varY
	 * @param vertices
	 * @param colors
	 * @param conflictGraph
	 * @param relationshipGraph
	 */
	private void addMandatoryConstraints(List<Vertex> vertices, List<Color> colors,
			Graph<Vertex, Edge> conflictGraph, Graph<Vertex, Edge> relationshipGraph) {
		/**
		 * 1. Constraint to assure that each vertex has one color. colorFactor
		 * Verifica: SUMc(Xic) <=1
		 */
		double[] colorFactor = new double[colors.size()];
		for (int i = 0; i < colorFactor.length; i++)
			colorFactor[i] = 1;
		for (Vertex v : vertices) {
			Variable[] varsFromV = new Variable[colors.size()];
			int indexC = 0;
			for (Color c : colors) {
				varsFromV[indexC++] = getVarX(v, c);
			}

			Constraint oneColorByV = mSolver.createConsLinear("OnlyOneColor-" + v, varsFromV,
					colorFactor, 1, 1);
			mSolver.addCons(oneColorByV);
			mSolver.releaseCons(oneColorByV);
		}

		/**
		 * 2. conflicted vertex can't have the same color. conflictFactors
		 * verifica: Xic + Xjc <= 1
		 */
		double[] conflictFactors = { 1, 1 };
		for (Color c : colors) {
			for (Edge e : conflictGraph.edgeSet()) {
				Vertex s = conflictGraph.getEdgeSource(e);
				Vertex t = conflictGraph.getEdgeTarget(e);

				Variable[] vars = new Variable[2];
				vars[0] = getVarX(s, c);
				vars[1] = getVarX(t, c);
				Constraint diferentColorsOnConflict = mSolver.createConsLinear(
						"diferentColorsOnConflict-" + s + "-" + t, vars, conflictFactors, 0, 1);
				mSolver.addCons(diferentColorsOnConflict);
				mSolver.releaseCons(diferentColorsOnConflict);
			}
		}

		/**
		 * 3 & 4. Force that Yij = 0 if Vi and Vj has diferent colors
		 * relationFactor1 verifica: Yij <= 1 + Xic - Xjc relationFactor2
		 * verifica: Yij <= 1 - Xic + Xjc
		 */
		double[] relationFactor1 = { -1, 1, 1 };
		double[] relationFactor2 = { 1, -1, 1 };
		for (Color c : colors) {
			for (Edge e : relationshipGraph.edgeSet()) {
				Vertex s = relationshipGraph.getEdgeSource(e);
				Vertex t = relationshipGraph.getEdgeTarget(e);
				if (s.compareTo(t) < 0) {

					Variable[] vars = new Variable[3];
					vars[0] = getVarX(s, c);
					vars[1] = getVarX(t, c);
					vars[2] = getVarY(s, t);
					Constraint varToOptimize = mSolver.createConsLinear(
							"varToOptimize1-" + s + "-" + t, vars, relationFactor1, -1, 1);
					mSolver.addCons(varToOptimize);
					mSolver.releaseCons(varToOptimize);

					varToOptimize = mSolver.createConsLinear("varToOptimize1-" + s + "-" + t, vars,
							relationFactor2, -1, 1);
					mSolver.addCons(varToOptimize);
					mSolver.releaseCons(varToOptimize);
				}
			}
		}
	}


	private Solution solve() {
		mInitSolvingTime = System.currentTimeMillis();
		mSolver.setMaximize();
		mSolver.solve();
		mNSols = mSolver.getNSols();
		Solution bestSol = null;

		if (mNSols > 0) {

			if (mIsVerbose) {
				mSolver.printBestSol(false);
				mSolver.printStatistics();
			}

			bestSol = mSolver.getBestSol();

		}

		return bestSol;
	}

	private Map<Vertex, Color> readSolution(Solution sol, List<Vertex> vertices,
			List<Color> colors) {

		Map<Vertex, Color> optimal = null;

		if (sol != null) {
			optimal = new TreeMap<Vertex, Color>();

			for (Entry<ComparablePair<Vertex, Color>, Variable> entry : mVarX.entrySet()) {
				Variable var = entry.getValue();
				if (mSolver.getSolVal(sol, var) == 1) {
					ComparablePair<Vertex, Color> key = entry.getKey();

					optimal.put(key.getFirst(), key.getSecond());
				}
			}
		}
		return optimal;
	}

	private void printExecutionData(Solution sol, List<Vertex> vertices, List<Color> colors,
			Graph<Vertex, Edge> conflictGraph, Graph<Vertex, Edge> relationshipGraph) {

		String msg = "No Se encontró solución!";
		String gap = "infinite";
		String objValue = "unknown";
		String searchingTime = "" + (System.currentTimeMillis() - mInitSearchingTime) / 1000;
		String solvingTime = "" + (System.currentTimeMillis() - mInitSolvingTime) / 1000;

		if (sol != null) {
			msg = "Solución encontrada!";
			gap = "" + mSolver.getGap();
			objValue = "" + mSolver.getSolOrigObj(sol);
		}
		// No pude conseguir leer la cantidad de cortes usados al momento de
		// encontrar la solución.

		System.out.println("################################ " + msg);
		System.out.println(
				"# instance;|V|;|Eg|;|Eh|;|C|;Finding Time; Solving Time; Solver Time;GAP;Nodes;Obj");
		System.out.print("# " + mName);
		System.out.print(";" + vertices.size());
		System.out.print(";" + conflictGraph.edgeSet().size());
		System.out.print(";" + relationshipGraph.edgeSet().size());
		System.out.print(";" + colors.size());
		System.out.print(";" + searchingTime);
		System.out.print(";" + solvingTime);
		System.out.print(";" + mSolver.getSolvingTime());
		System.out.print(";" + gap);
		System.out.print(";" + mSolver.getNNodes());
		System.out.println(";" + objValue);
		System.out.println("################################");
	}

	public void free() {
		if (mSolver != null) {
			System.out.println(" - MicpScipSolver.free() -");
			if (mIsVerbose) {
				mSolver.printBestSol(false);
				mSolver.printStatistics();
			}

			mSolver.free();
			mSolver = null;
		}
	}

	public Scip getSolver()
	{
		return mSolver;
	}
	
	public void putVarX(Vertex v, Color c, Variable var) {
		ComparablePair<Vertex, Color> p = new ComparablePair<Vertex, Color>(v, c);
		mVarX.put(p, var);
	}

	public Variable getVarX(Vertex v, Color c) {
		return mVarX.get(new ComparablePair<Vertex, Color>(v, c));
	}

	public void putVarY(Vertex v1, Vertex v2, Variable var) {
		ComparablePair<Vertex, Vertex> p = new ComparablePair<Vertex, Vertex>(v1, v2);
		mVarY.put(p, var);
	}

	public Variable getVarY(Vertex v1, Vertex v2) {
		if (v1.compareTo(v2) < 0)
			return mVarY.get(new ComparablePair<Vertex, Vertex>(v1, v2));
		else
			return mVarY.get(new ComparablePair<Vertex, Vertex>(v2, v1));
	}

}
