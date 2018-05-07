package ar.edu.ungs.tesina.micp;

import org.jgrapht.alg.util.Pair;

public class ComparablePair<A extends Comparable<A>, B extends Comparable<B>> extends Pair<A, B>  implements Comparable< ComparablePair<A, B> >{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7313302100585983151L;

	public ComparablePair(A a, B b) {
		super(a,b);
	}

	@Override
	public int compareTo(ComparablePair<A, B> o) {
		int diff = first.compareTo(o.first);
		if ( diff == 0 )
			diff =  second.compareTo(o.second);
		
		return diff;
	}
}
