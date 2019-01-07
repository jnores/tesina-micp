package ar.edu.ungs.tesina.micp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import ar.edu.ungs.tesina.micp.inequalities.InequalitiesHelper;
import jscip.Scip;

public class Instance<T extends Vertex, U extends Color> extends Observable{

	private String mName;
	private List<T> mVertices;
	private List<U> mColors;
	private Graph<T, Edge<T>> mConflictGraph;
	private Graph<T, Edge<T>> mRelationshipGraph;
	private Map<T, U> mSolution;

	public Instance(String name, List<T> vertices, List<U> colors) {

		mName = name;
		if (vertices == null || vertices.isEmpty())
			throw new InvalidParameterException("Error al crear la instancia. El listado de clases no puede ser vacio");
		if (colors == null || colors.isEmpty())
			throw new InvalidParameterException("Error al crear la instancia. El listado de aulas no puede ser vacio");

		mVertices = vertices;
		mColors = new ArrayList<U>( colors);
		EdgeFactory<T> edgeFactory= new EdgeFactory<T>();

		mConflictGraph = new SimpleGraph<T, Edge<T>>(edgeFactory);
		mRelationshipGraph = new SimpleGraph<T, Edge<T>>(edgeFactory);

		for (T c : vertices) {
			mConflictGraph.addVertex(c);
			mRelationshipGraph.addVertex(c);
		}

	}

	public boolean addConflicto(T c1, T c2) {
		if (mConflictGraph.addEdge(c1, c2) != null) {
			setChanged();
			notifyObservers();
			return true;
		}
		else
			return false;
	}
	
	public boolean addRelacion(T c1, T c2) {
		if (mRelationshipGraph.addEdge(c1, c2) != null)
		{
			setChanged();
			notifyObservers();
			return true;
		}
		else
			return false;
	}

	public List<U> getAulas() {
		return mColors;
	}

	public Graph<T, Edge<T>> getConflictGraph() {
		return mConflictGraph;
	}

	public Graph<T, Edge<T>> getRelationshipGraph() {
		return mRelationshipGraph;
	}

	public void setSolution(Map<T, U> optimal) {
		mSolution = optimal;
		setChanged();
		notifyObservers();
	}

	public boolean hasSolution() {
		return mSolution != null;
	}

	public List<T> getVertices() {
		return mVertices;
	}

	public U getOptimal(T c) {
		return (mSolution != null) ? mSolution.get(c) : null;
	}
	
	public String getName() {
		return mName;
	}

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
	public MicpScipSolver<T,U> createMicp(SolverConfig solverConfig) {
		String name = getName();
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
		
		InequalitiesHelper<T,U> ineqHelper = new InequalitiesHelper<T,U>(solverConfig); 

		return new MicpScipSolver<T,U>(solver, name, solverConfig, ineqHelper);
	}
	
}
