package ar.edu.ungs.tesina.micp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

public class Instance<T extends Vertex, U extends Color> extends Observable {

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
		mColors = new ArrayList<U>(colors);
		EdgeFactory<T> edgeFactory = new EdgeFactory<T>();

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
		} else {
			return false;
		}
	}

	public boolean addRelacion(T c1, T c2) {

		if (mRelationshipGraph.addEdge(c1, c2) != null) {
			setChanged();
			notifyObservers();
			return true;
		} else {
			return false;
		}
	}

	public List<U> getColors() {
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

}