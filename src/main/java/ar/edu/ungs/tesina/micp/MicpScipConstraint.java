package ar.edu.ungs.tesina.micp;

import java.util.List;

import org.jgrapht.Graph;

import jscip.Scip;

/**
 * 
 * 
 * @author yoshknight
 *
 */
public interface MicpScipConstraint {

	/**
	 * 
	 * @param solver
	 * @param vectors
	 * @param conflictGraph
	 * @param relationshipGraph
	 * @param colors
	 */
	void addConstraints(Scip solver, List<Vertex> vectors, Graph<Vertex, Edge> conflictGraph,
			Graph<Vertex, Edge> relationshipGraph, List<Color> colors);
}
