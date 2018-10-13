package ar.edu.ungs.tesina.micp;

public class EdgeFactory<T extends Vertex> implements org.jgrapht.EdgeFactory<T, Edge<T>>{

	public EdgeFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Edge<T> createEdge(T sourceVertex, T targetVertex) {
		Edge<T> edge = new Edge<T>(sourceVertex,targetVertex);
		return edge;
	}

}
