package ar.edu.ungs.tesina.micp;

import org.jgrapht.graph.DefaultEdge;

public class Edge extends DefaultEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3823257067729647357L;
	
	
	public Vertex getSource() {
		return (Vertex) super.getSource();
	}
	
	public Vertex getTarget() {
		return (Vertex) super.getTarget();
	}

}
