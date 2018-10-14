package ar.edu.ungs.tesina.micp;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigSolverTest {

	@Test
	public void testStringVacio() {
		String config = "", cif="", sif="";
		String[] aux = config.split(",");
		int n;
		System.out.println("Proceso "+config);
		
		for (String s: aux) {
			if ( !s.trim().isEmpty() ) {
				n = Integer.parseInt(s);
				cif += " " + n;
			}
			sif += " " + s;
		}
		System.out.println("SIN IF: ("+sif+" )");
		System.out.println("CON IF: ("+cif+" )");
	}
	
	@Test
	public void testStringConLetras() {
		String config = "1,2,3,4,5,r,t6,7,8,9,", cif="", sif="";
		String[] aux = config.split(",");
		int n;
		System.out.println("Proceso "+config);
		
		for (String s: aux) {
			if ( !s.trim().isEmpty() ) {
				try {
				n = Integer.parseInt(s);
				cif += " " + n;
				} catch(NumberFormatException ex) {
					System.out.println("No se pudo parsear como entero el parametro: "+s);
				}
			}
			sif += " " + s;
		}
		System.out.println("SIN IF: ("+sif+" )");
		System.out.println("CON IF: ("+cif+" )");
	}

}
