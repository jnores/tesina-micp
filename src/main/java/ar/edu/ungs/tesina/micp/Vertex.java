package ar.edu.ungs.tesina.micp;

public class Vertex implements Comparable<Vertex>{
	
	private String mName;
	
	public Vertex(String name)
	{
		mName = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Vertex) {
			Vertex v = (Vertex) obj;
			return mName.equals(v.mName);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return mName;
	}

	@Override
	public int compareTo(Vertex o) {
		return mName.compareTo(o.mName);
	}

}
