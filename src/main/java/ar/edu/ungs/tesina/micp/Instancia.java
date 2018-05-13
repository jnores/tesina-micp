package ar.edu.ungs.tesina.micp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

public class Instancia<T extends Vertex, U extends Color> extends Observable{

	private String mName;
	private List<T> mVertices;
	private List<U> mColors;
	private Graph<T, Edge> mConflictGraph;
	private Graph<T, Edge> mRelationshipGraph;
	private Map<T, U> mSolution;

	public Instancia(String name, List<T> vertices, List<U> colors) {

		mName = name;
		if (vertices == null || vertices.isEmpty())
			throw new InvalidParameterException("Error al crear la instancia. El listado de clases no puede ser vacio");
		if (colors == null || colors.isEmpty())
			throw new InvalidParameterException("Error al crear la instancia. El listado de aulas no puede ser vacio");

		mVertices = vertices;
		mColors = new ArrayList<U>( colors);

		mConflictGraph = new SimpleGraph<T, Edge>(Edge.class);
		;
		mRelationshipGraph = new SimpleGraph<T, Edge>(Edge.class);

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

	public Graph<T, Edge> getConflictGraph() {
		return mConflictGraph;
	}

	public Graph<T, Edge> getRelationshipGraph() {
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
		return mSolution.get(c);
	}
	
	public String getName() {
		return mName;
	}
	
}
