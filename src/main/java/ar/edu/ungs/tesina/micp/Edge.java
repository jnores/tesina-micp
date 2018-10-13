package ar.edu.ungs.tesina.micp;

import org.jgrapht.graph.DefaultEdge;

/**
 * Clase Edge implementada para el modelo conde los puntos de la arista son de tipo Vertex.
 *  
 * @author yoshknight
 *
 */
public class Edge<T extends Vertex> extends DefaultEdge {

	T source;
	T target;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3823257067729647357L;
	
	public Edge(T source, T target) {
		this.source = source;
		this.target = target;
	}
	
	@SuppressWarnings("unchecked")
	public T getSource() {
		return (T) super.getSource();
	}
	
	@SuppressWarnings("unchecked")
	public T getTarget() {
		return (T) super.getTarget();
	}

}
