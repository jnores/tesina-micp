package ar.edu.ungs.tesina.micp.inequalities;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;

import ar.edu.ungs.tesina.micp.Color;
import ar.edu.ungs.tesina.micp.Edge;
import ar.edu.ungs.tesina.micp.MicpScipSolver;
import ar.edu.ungs.tesina.micp.Vertex;

public class CustomInequalitiesComposite<T extends Vertex, U extends Color> extends CustomInequalities<T, U> {
	
	private final List<CustomInequalities<T,U>> mAllInequalities = new ArrayList<CustomInequalities<T,U>>();;

	@Override
	public void addInequalities(MicpScipSolver<T, U> micpSolver, List<T> vertices, List<U> colors,
			Graph<T, Edge<T>> conflictGraph, Graph<T, Edge<T>> relationshipGraph) {
		for(CustomInequalities<T, U> ineq: mAllInequalities )
		{
			ineq.addInequalities(micpSolver, vertices, colors, conflictGraph, relationshipGraph);
		}
	}
	
	public void add(CustomInequalities<T, U> ineq) {
		if (ineq == null)
			throw new NullPointerException("La desigualdad a agregegar en el composite no puede ser nula");
		mAllInequalities.add(ineq);
	}
	
	public void remove(CustomInequalities<T, U> ineq) {
		if (ineq == null)
			throw new NullPointerException("La desigualdad a quitar del composite no puede ser nula");
		mAllInequalities.remove(ineq);
	}
	
	public int size() {
		return mAllInequalities.size();
	}
	
	public CustomInequalities<T,U> getChild(int pos) {
		if ( pos < 0 )
			throw new IndexOutOfBoundsException("La posicion " + pos + " no es valida. Debe ser >= 0");
		else if ( pos >= size() )
			throw new IndexOutOfBoundsException("La posicion " + pos + " no es valida. Debe ser menor que "+ size() );
		
		return mAllInequalities.get(pos);
	}
}
