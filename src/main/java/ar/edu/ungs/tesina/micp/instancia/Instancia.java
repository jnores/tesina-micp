package ar.edu.ungs.tesina.micp.instancia;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import ar.edu.ungs.tesina.micp.Color;
import ar.edu.ungs.tesina.micp.Edge;
import ar.edu.ungs.tesina.micp.Vertex;

public class Instancia {

	private List<Clase> mClases;
	private List<Color> mAulas;
	private Graph<Vertex, Edge> mConflictGraph;
	private Graph<Vertex, Edge> mRelationshipGraph;
	private Map<Vertex, Color> mSolution;

	public Instancia(List<Clase> clases, List<Aula> aulas) {

		if (clases == null || clases.isEmpty())
			throw new InvalidParameterException("Error al crear la instancia. El listado de clases no puede ser vacio");
		if (aulas == null || aulas.isEmpty())
			throw new InvalidParameterException("Error al crear la instancia. El listado de aulas no puede ser vacio");

		mClases = clases;
		mAulas = new ArrayList<Color>( aulas);

		mConflictGraph = new SimpleGraph<Vertex, Edge>(Edge.class);
		;
		mRelationshipGraph = new SimpleGraph<Vertex, Edge>(Edge.class);

		for (Clase c : clases) {
			mConflictGraph.addVertex(c);
			mRelationshipGraph.addVertex(c);
		}

	}

	public boolean addConflicto(Clase c1, Clase c2) {
		if (mConflictGraph.addEdge(c1, c2) != null)
			return true;
		else
			return false;
	}
	
	public boolean addRelacion(Clase c1, Clase c2) {
		if (mRelationshipGraph.addEdge(c1, c2) != null)
			return true;
		else
			return false;
	}

	public List<Color> getAulas() {
		return mAulas;
	}

	public Graph<Vertex, Edge> getConflictGraph() {
		return mConflictGraph;
	}

	public Graph<Vertex, Edge> getRelationshipGraph() {
		return mRelationshipGraph;
	}

	public void setSolution(Map<Vertex, Color> optimal) {
		mSolution = optimal;
		
	}

	public boolean hasSolution() {
		return mSolution != null;
	}

	public List<Clase> getClases() {
		return mClases;
	}

	public Aula getOptimal(Clase c) {
		return (Aula) mSolution.get(c);
	}
	
	
}
