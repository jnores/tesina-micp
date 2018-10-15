/**
 * 
 */
package ar.edu.ungs.tesina.micp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jgrapht.Graph;

import ar.edu.ungs.tesina.micp.inequalities.CustomInequalities;
import jscip.Constraint;
import jscip.SCIP_Vartype;
import jscip.Scip;
import jscip.Solution;
import jscip.Variable;

/**
 * @author yoshknight
 *
 */
public class MicpScipSolver<T extends Vertex, U extends Color> {

	private Scip mSolver;

	private Map<ComparableVertexColorPair<T, U>, Variable> mVarX;
	private Map<ComparableVertexPair<T, T>, Variable> mVarY;

	private int mNSols = 0;
	private String mName = "";
	private long mInitSearchingTime, mInitSolvingTime;

	private CustomInequalities<T,U> mCustomInequalities;
	
	SolverConfig mSolverConfig;

	private boolean mIsVerbose = true;


	/**
	 * 
	 * @param solver
	 */
	MicpScipSolver(Scip solver, String name, SolverConfig solverConfig, CustomInequalities<T,U> inequalities) {
		mIsVerbose = solverConfig.isVerbose();
		mSolver = solver;
		mName = name;
		mCustomInequalities = inequalities;
		mSolverConfig = solverConfig;
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
	public Map<T, U> searchOptimal(Graph<T, Edge<T>> conflictGraph,
			Graph<T, Edge<T>> relationshipGraph, List<U> colors) {

		mInitSearchingTime = System.currentTimeMillis();

		List<T> vertices = new ArrayList<T>(conflictGraph.vertexSet());
		Collections.sort(vertices);

		// System.out.print( "Vertices: [ " );
		// for (T v:vertices)
		// System.out.print( v+" " );
		// System.out.println( "]" );
		// System.out.print( "Colors: [ " );
		// for (U c: colors)
		// System.out.print( c+" " );
		// System.out.println( "]" );
		// System.out.println( "CONFLICTO: "+conflictGraph );
		// System.out.println( "RELACION: "+relationshipGraph );
		// Variable[][] varX = new Variable[V.size()][colors.size()];
		mVarX = new TreeMap<ComparableVertexColorPair<T, U>, Variable>();
		mVarY = new TreeMap<ComparableVertexPair<T, T>, Variable>();

		// Se crean las variables binarias e implicitamente se define la función
		// objetivo.

		/**
		 * 5. varX is Binary {0,1}
		 */
		for (T v : vertices) {
			for (U c : colors) {
				Variable var = mSolver.createVar(v + "-" + c, 0, 1, 0,
						SCIP_Vartype.SCIP_VARTYPE_BINARY);
				putVarX(v, c, var);
			}
		}

		/**
		 * 6. varY is Binary {0,1}
		 */
		for (Edge<T> e : relationshipGraph.edgeSet()) {
			T s = relationshipGraph.getEdgeSource(e);
			T t = relationshipGraph.getEdgeTarget(e);

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

		if ( mCustomInequalities != null )
			mCustomInequalities.addInequalities(this, vertices, colors, conflictGraph, relationshipGraph);

		// Se busca una solucion optima.
		Solution sol = solve();

		// Se genera un Map que asocia vertice con color para devolver como
		// solucion encontrada.
		Map<T, U> optimal = readSolution(sol, vertices, colors);

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
	private void addMandatoryConstraints(List<T> vertices, List<U> colors,
			Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {
		/**
		 * 1. Constraint to assure that each vertex has one color. colorFactor
		 * Verifica: SUMc(Xic) <=1
		 */
		double[] colorFactor = new double[colors.size()];
		for (int i = 0; i < colorFactor.length; i++)
			colorFactor[i] = 1;
		for (T v : vertices) {
			Variable[] varsFromV = new Variable[colors.size()];
			int indexC = 0;
			for (U c : colors) {
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
		for (U c : colors) {
			for (Edge<T> e : conflictGraph.edgeSet()) {
				T s = conflictGraph.getEdgeSource(e);
				T t = conflictGraph.getEdgeTarget(e);

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
		for (U c : colors) {
			for (Edge<T> e : relationshipGraph.edgeSet()) {
				T s = relationshipGraph.getEdgeSource(e);
				T t = relationshipGraph.getEdgeTarget(e);
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

	private Map<T, U> readSolution(Solution sol, List<T> vertices,
			List<U> colors) {

		Map<T, U> optimal = null;

		if (sol != null) {
			optimal = new TreeMap<T, U>();

			for (Entry<ComparableVertexColorPair<T, U>, Variable> entry : mVarX.entrySet()) {
				Variable var = entry.getValue();
				if (mSolver.getSolVal(sol, var) == 1) {
					ComparableVertexColorPair<T, U> key = entry.getKey();

					optimal.put(key.getFirst(), key.getSecond());
				}
			}
		}
		return optimal;
	}

	private void printExecutionData(Solution sol, List<T> vertices, List<U> colors,
			Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {

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
				"# instance;|V|;|Eg|;|Eh|;|C|;Finding Time; Solving Time; Solver Time;GAP;Nodes;Obj;ineq");
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
		System.out.print(";" + objValue);
		System.out.println(";" + getInequalitiesEnabled());
		System.out.println("################################");
	}

	private String getInequalitiesEnabled() {
		String separator = ",";
		List<Integer> ineqs = mSolverConfig.getInequalitiesEnabled();
		if (null == ineqs || ineqs.isEmpty()) return "";

	    StringBuilder sb = new StringBuilder(256);
	    sb.append(ineqs.get(0));
	    for (int i = 1; i < ineqs.size(); i++) sb.append(separator).append(ineqs.get(i));

	    return sb.toString();
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
	
	public void putVarX(T v, U c, Variable var) {
		ComparableVertexColorPair<T, U> p = new ComparableVertexColorPair<T, U>(v, c);
		mVarX.put(p, var);
	}

	public Variable getVarX(T v, U c) {
		return mVarX.get(new ComparableVertexColorPair<T, U>(v, c));
	}

	public void putVarY(T v1, T v2, Variable var) {
		ComparableVertexPair<T, T> p;
		if (v1.compareTo(v2) < 0)
			p = new ComparableVertexPair<T, T>(v1, v2);
		else
			p = new ComparableVertexPair<T, T>(v2, v1);
		
		mVarY.put(p, var);
	}

	public Variable getVarY(T v1, T v2) {
		if (v1.compareTo(v2) < 0)
			return mVarY.get(new ComparableVertexPair<T, T>(v1, v2));
		else
			return mVarY.get(new ComparableVertexPair<T, T>(v2, v1));
	}

}
