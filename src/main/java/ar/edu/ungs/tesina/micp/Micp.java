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

import jscip.Constraint;
import jscip.SCIP_Vartype;
import jscip.Scip;
import jscip.Solution;
import jscip.Variable;

/**
 * @author yoshknight
 *
 */
public class Micp {

	private Scip mSolver;
	private int mNSols = 0;
	
	/**
	 * Verifica que el Solver pasado por parametro no sea nulo
	 * 
	 * Si es nulo Lanza una excepción
	 * En caso de que no sea null devuelve una instancia de la clase. 
	 *  
	 * @param solver una implementacion de la interfaz Solver
	 * @return
	 */
	public static Micp createMicp(Scip solver) {
		
		if (solver == null)
			throw new NullPointerException("El parametro solver no puede ser null.");
		else
			return new Micp(solver);
	}
	
	/**
	 *  
	 * @param solver
	 */
	private Micp(Scip solver) {
		
		mSolver = solver;
	}
	
	/**
	 * Aplica el modelo lineal sobre el solver cargado en el constructor de la clase.
	 * 
	 * @param conflictGraph grafo de conflictos entre vértises
	 * @param relationshipGraph Grafo de relaciones entre vértices.
	 * @param colors Lista de colores disponibles
	 */
	public Map<Vertex,Color> findOptimal(Graph<Vertex,Edge> conflictGraph , Graph<Vertex,Edge> relationshipGraph, List<Color> colors) {
		List<Vertex> vertices = new ArrayList<>( conflictGraph.vertexSet() );
		Collections.sort(vertices);
//		System.out.print( "[ " );
//		for (Vertex v:V)
//			System.out.print( v+" " );
//		System.out.println( "]" );
//		Variable[][] varX = new Variable[V.size()][colors.size()];
		Map< ComparablePair<Vertex, Color> , Variable> varX = new TreeMap< ComparablePair<Vertex, Color> , Variable>();
		Map< ComparablePair<Vertex, Vertex> , Variable> varY = new TreeMap< ComparablePair<Vertex, Vertex> , Variable>();
		
		/**
		 * 5. varX is Binary {0,1} 
		 */
		for (Vertex v : vertices)
		{
			for (Color c: colors)
			{
				ComparablePair<Vertex, Color> p = new ComparablePair<Vertex, Color>(v,c);
				Variable var = mSolver.createVar(v+"-"+c, 0, 1, 0, SCIP_Vartype.SCIP_VARTYPE_BINARY);
				varX.put(p, var); 
				
			}
		}
		
		/**
		 * 6. varY is Binary {0,1}
		 */
		for ( Edge e: relationshipGraph.edgeSet() ) {
			Vertex s = relationshipGraph.getEdgeSource(e);
			Vertex t = relationshipGraph.getEdgeTarget(e);
			ComparablePair<Vertex, Vertex> p = new ComparablePair<Vertex, Vertex>(s,t);
			
			if (s.compareTo(t) < 0){
				Variable var;
				var = mSolver.createVar(s+"-"+t, 0, 1, 1, SCIP_Vartype.SCIP_VARTYPE_BINARY);
				varY.put(p, var);	
			}
//			else {
//				var = mSolver.createVar(s+"-"+t, 0, 1, 0, SCIP_Vartype.SCIP_VARTYPE_BINARY);
//			}
					
		}
		
		/**
		 * 1. Constraint to assure that each vertex has one color.
		 * colorFactor Verifica: SUMc(Xic) <=1 
		 */
		double[] colorFactor = new double[colors.size()];
		for (int i = 0; i< colorFactor.length; i++)
			colorFactor[i]=1;
		for (Vertex v : vertices)
		{
			Variable[] varsFromV = new Variable[colors.size()];
			int indexC=0;
			for (Color c: colors) {
				varsFromV[indexC++] = varX.get(new ComparablePair<Vertex, Color>(v,c));
			}
				
			
			Constraint oneColorByV = mSolver.createConsLinear("OnlyOneColor-"+v, varsFromV, colorFactor, 1, 1);
			mSolver.addCons(oneColorByV);
			mSolver.releaseCons(oneColorByV);	
		}
		
		/**
		 * 2. conflicted vertex can't have the same color.
		 * conflictFactors verifica: Xic + Xjc <= 1
		 */
		double[] conflictFactors = {1,1}; 
		for (Color c: colors) {
			for ( Edge e: conflictGraph.edgeSet() ) {
				Vertex s = conflictGraph.getEdgeSource(e);
				Vertex t = conflictGraph.getEdgeTarget(e);
				ComparablePair<Vertex, Color> ks = new ComparablePair<Vertex, Color>(s,c);
				ComparablePair<Vertex, Color> kt = new ComparablePair<Vertex, Color>(t,c);
				Variable[] vars = new Variable[2];
				vars[0] = varX.get(ks);
				vars[1] = varX.get(kt);
				Constraint diferentColorsOnConflict = mSolver.createConsLinear("diferentColorsOnConflict-"+s+"-"+t, vars, conflictFactors, 0, 1);
				mSolver.addCons(diferentColorsOnConflict);
				mSolver.releaseCons(diferentColorsOnConflict);
			}
		}
		
		
		/**
		 * 3 & 4. Force that Yij = 0 if Vi and Vj has diferent colors
		 * relationFactor1 verifica: Yij <= 1 + Xic - Xjc
		 * relationFactor2 verifica: Yij <= 1 - Xic + Xjc
		 */
		double[] relationFactor1 = {-1,1,1}; 
		double[] relationFactor2 = {1,-1,1};
		for (Color c: colors) {
			for ( Edge e: relationshipGraph.edgeSet() ) {
				Vertex s = relationshipGraph.getEdgeSource(e);
				Vertex t = relationshipGraph.getEdgeTarget(e);
				if (s.compareTo(t) < 0){
					ComparablePair<Vertex, Color> ks = new ComparablePair<Vertex, Color>(s,c);
					ComparablePair<Vertex, Color> kt = new ComparablePair<Vertex, Color>(t,c);
					ComparablePair<Vertex, Vertex> st = new ComparablePair<Vertex, Vertex>(s,t);
					
					Variable[] vars = new Variable[3];
					vars[0] = varX.get(ks);
					vars[1] = varX.get(kt);
					vars[2] = varY.get(st);
					Constraint varToOptimize = mSolver.createConsLinear("varToOptimize1-"+s+"-"+t, vars, relationFactor1, -1, 1);
					mSolver.addCons(varToOptimize);
					mSolver.releaseCons(varToOptimize);
					
					varToOptimize = mSolver.createConsLinear("varToOptimize1-"+s+"-"+t, vars, relationFactor2, -1, 1);
					mSolver.addCons(varToOptimize);
					mSolver.releaseCons(varToOptimize);
				}
			}
		}
		
		Map<Vertex,Color> optimal = solve(vertices,colors,varX);
		
		// Libero las variables creadas para definir el modelo.
		for(Variable v: varX.values())
			mSolver.releaseVar(v);
		for(Variable v: varY.values())
			mSolver.releaseVar(v);

		mSolver.free();
	
		return optimal;
		
	}
	
	private Map<Vertex, Color> solve(List<Vertex> vertices, List<Color> colors, Map<ComparablePair<Vertex, Color>, Variable> varX) {
		Map<Vertex, Color> optimal = null;
		mSolver.setMaximize();
		mSolver.solve();
		mNSols = mSolver.getNSols();
		
		if ( mNSols > 0  )
		{
			System.out.println("Tiene solucion y la mejor es: " + mSolver.getBestSol() );
			mSolver.printBestSol(false);
			optimal = new TreeMap<Vertex, Color>(); 
			
			Solution bestSol = mSolver.getBestSol();
			for (Entry<ComparablePair<Vertex, Color>, Variable> entry: varX.entrySet()) {
				
				Variable var = entry.getValue();
				
				if ( mSolver.getSolVal(bestSol, var) == 1 ) {
					ComparablePair<Vertex, Color> key = entry.getKey();
					
					optimal.put(key.getFirst(), key.getSecond());
				}
			}
			
		}
		else
		{
			System.out.println("No tiene solucion!");
		}
		
		return optimal;
	}
}
