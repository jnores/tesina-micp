/**
 * 
 */
package ar.edu.ungs.tesina.micp;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Before;
import org.junit.Test;

import jscip.Scip;

/**
 * @author yoshknight
 *
 */
public class SimpleColoringTest {

	Micp mMicp;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		System.loadLibrary("jscip");
		
		Scip solver = new Scip();
		solver.create("SimpleColoringTest");
		
		mMicp = Micp.createMicp(solver);
		
	}

	@Test
	public void testCreatedObject() {
		assertNotNull(mMicp);
	}
	
	@Test
	public void testSimple() {
		int cantVertex = 6;
		Vertex[] V = new Vertex[cantVertex];
		
		Graph<Vertex,Edge> conflictGraph = new SimpleGraph<Vertex,Edge>(Edge.class);
		Graph<Vertex,Edge> relationshipGraph = new SimpleGraph<Vertex,Edge>(Edge.class);
		
		for(int i = 0 ; i < cantVertex ; i++ ) {
			Vertex v = new Vertex("v"+i) ;
			V[i] = v;
			conflictGraph.addVertex(v);
			relationshipGraph.addVertex(v);
		}
		
		conflictGraph.addEdge(V[0], V[1]);
		conflictGraph.addEdge(V[0], V[2]);
		conflictGraph.addEdge(V[0], V[3]);
		conflictGraph.addEdge(V[2], V[3]);
		conflictGraph.addEdge(V[4], V[3]);
		
		relationshipGraph.addEdge(V[1],V[5]);
		relationshipGraph.addEdge(V[2],V[4]);
		
		List<Color> colors = new ArrayList<Color>();
		colors.add(new Color("c1"));
		colors.add(new Color("c2"));
		
		
		mMicp.generateLPModel(conflictGraph, relationshipGraph, colors);
		
		mMicp.optimize();
		
	}

}
