/**
 * 
 */
package ar.edu.ungs.tesina.micp;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

	MicpScipSolver mMicp;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		System.loadLibrary("jscip");
		
		Scip solver = new Scip();
		solver.create("SimpleColoringTest");
		solver.hideOutput(true);
		
		mMicp = MicpScipSolver.createMicp("Test 1", new Properties());
		
	}

	@Test
	public void testCreatedObject() {
		assertNotNull(mMicp);
	}
	
	@Test
	public void testSimple() {
		int cantVertex = 6;
		Vertex[] vertices = new Vertex[cantVertex];
		
		Graph<Vertex,Edge> conflictGraph = new SimpleGraph<Vertex,Edge>(Edge.class);
		Graph<Vertex,Edge> relationshipGraph = new SimpleGraph<Vertex,Edge>(Edge.class);
		
		for(int i = 0 ; i < cantVertex ; i++ ) {
			Vertex v = new Vertex("v"+i) ;
			vertices[i] = v;
			conflictGraph.addVertex(v);
			relationshipGraph.addVertex(v);
		}
		
		conflictGraph.addEdge(vertices[0], vertices[1]);
		conflictGraph.addEdge(vertices[0], vertices[3]);
		conflictGraph.addEdge(vertices[2], vertices[3]);
		conflictGraph.addEdge(vertices[4], vertices[3]);
		
		relationshipGraph.addEdge(vertices[1],vertices[5]);
		relationshipGraph.addEdge(vertices[2],vertices[4]);
		
		assertTrue(relationshipGraph.containsEdge(vertices[1],vertices[5]));
		assertTrue(relationshipGraph.containsEdge(vertices[5],vertices[1]));
		
		List<Color> colors = new ArrayList<Color>();
		colors.add(new Color("c1"));
		colors.add(new Color("c2"));
		
		
		Map<Vertex,Color> optimal = mMicp.searchOptimal(conflictGraph, relationshipGraph, colors);
		
		assertNotNull(optimal);
		
		assertEquals(cantVertex, optimal.size());
		System.out.println("================================ ");
		System.out.println("SOLUCION ENCONTRADA POR EL MICP: ");
		for(int i = 0 ; i < cantVertex ; i++ ) {
			System.out.println(vertices[i]+"-"+optimal.get(vertices[i]));
			
		}
		
	}

}
