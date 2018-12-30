package ar.edu.ungs.tesina.micp;

import org.jgrapht.alg.util.Pair;

/**
 * Es una extension de la clase Paoir de jgrapht que implementa la interfaz Comparable de Java
 * para facilitar su uso.
 * 
 * @author yoshknight
 *
 * @param <A>
 * @param <B>
 */
class ComparableVertexPair<A extends Vertex> extends Pair<A, A>  implements Comparable< ComparableVertexPair<A> >{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7313302100585983151L;

	ComparableVertexPair(A a, A b) {
		super(a,b);
	}

	@Override
	public int compareTo(ComparableVertexPair<A> o) {
		int diff = first.compareTo(o.first);
		if ( diff == 0 )
			diff =  second.compareTo(o.second);
		
		return diff;
	}
}
