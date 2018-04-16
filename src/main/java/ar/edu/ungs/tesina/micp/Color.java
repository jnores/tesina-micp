package ar.edu.ungs.tesina.micp;

public class Color implements Comparable<Color> {
	private String mName;
	public Color(String name) {
		mName = name;
	}
	
	
	@Override
	public String toString() {
		return mName;
	}


	@Override
	public int compareTo(Color o) {
		return mName.compareTo(o.mName);
	}
}
