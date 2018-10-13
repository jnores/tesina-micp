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
class ComparableVertexColorPair<A extends Vertex, B extends Color> extends Pair<A, B>  implements Comparable< ComparableVertexColorPair<A, B> >{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7313302100585983151L;

	ComparableVertexColorPair(A a, B b) {
		super(a,b);
	}

	@Override
	public int compareTo(ComparableVertexColorPair<A, B> o) {
		int diff = first.compareTo(o.first);
		if ( diff == 0 )
			diff =  second.compareTo(o.second);
		
		return diff;
	}
}
