package ar.edu.ungs.tesina.micp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Clase encargada de procesar la estructura de un Properties y generar un estado de configuración
 * válido para la ejecucion del solver. Para esto, se dispone de valores por default de cada 
 * parametro. Así, de este modo, se puede comenzar a operar sin tener que establecer una 
 * configuarcioninicial. 
 *  
 * @author yoshknight
 *
 */
public class SolverConfig {
	static final double DEFAULT_GAP_LIMIT = 0.0;
	static final int DEFAULT_TIME_LIMIT = 3600; // seg
	static final double DEFAULT_MEMORY_LIMIT = 1024; // MB
	static final boolean DEFAULT_IS_VERBOSE = false;
	
	/**
	 * solving stops, if the relative gap = |primal - dual|/MIN(|dual|,|primal|) is below the given value
	 * [Type: real, advanced: FALSE, range: [0,1.79769313486232e+308], default: 0]
	 */
	private double gapLimit = DEFAULT_GAP_LIMIT;
	
	/**
	 * maximal time in seconds to run
	 * [type: real, advanced: FALSE, range: [0,1e+20], default: 1e+20]
	 */
	private long timeLimit = DEFAULT_TIME_LIMIT;
	
	/**
	 * maximal memory usage in MB; reported memory usage is lower than real memory usage!
	 * [type: real, advanced: FALSE, range: [0,8796093022208], default: 8796093022208]
	 */
	private double memoryLimit = DEFAULT_MEMORY_LIMIT;
	
	/**
	 * Boolean que determina si se debe ejecutar el solver en modo verbose o no.
	 */
	private boolean isVerbose = DEFAULT_IS_VERBOSE;
	
	/**
	 * List que contiene los id de las desigualdades que se deben agregar al modelo.
	 */
	private List<Integer> inequalitiesEnabled;

	public SolverConfig(Properties p) {
		inequalitiesEnabled = new ArrayList<Integer>();
		readMemoryLimit(p);
		readGapLimit(p);
		readTimeLimit(p);
		readVerbose(p);
		readInequalitiesEnabled(p);
	}

	private void readMemoryLimit(Properties p) {
		String memory = p.getProperty("memory_limit");
		try {
			double memoryValue = Double.parseDouble(memory);
			setMemoryLimit(memoryValue);
		} catch (Exception e) {
			gapLimit = DEFAULT_MEMORY_LIMIT;
			if (memory == null) {
				System.out
						.println("debug - No se configuró el paremetor 'memory_limit'. Uso por default: "
								+ DEFAULT_MEMORY_LIMIT);
			} else {
				System.out.println("debug - No se pudo parsear el parametro 'memory_limit' (" 
						+ memory
						+ "). uso por default: " + DEFAULT_MEMORY_LIMIT);
			}

		}
	}

	private void readGapLimit(Properties p) {
		String gap = p.getProperty("gap_limit");
		try {
			double gapValue = Double.parseDouble(gap);
			setGapLimit(gapValue);
		} catch (Exception e) {
			gapLimit = DEFAULT_GAP_LIMIT;
			if (gap == null) {
				System.out.println("debug - No se configuró el parametro 'gap_limit'. Uso por"
						+ " default: " + DEFAULT_GAP_LIMIT);
			} else {
				System.out.println("debug - No se pudo parsear el parametro 'gap_limit' (" + gap
						+ "). uso por default: " + DEFAULT_GAP_LIMIT);
			}

		}
	}

	private void readTimeLimit(Properties p) {
		String time = p.getProperty("time_limit");
		try {
			int timeValue = Integer.parseInt(time);
			setTimeLimit(timeValue);
		} catch (Exception e) {
			timeLimit = DEFAULT_TIME_LIMIT;
			if (time == null) {
				System.out.println("debug - No se configuró el parametro 'time_limit'. Uso por"
						+ " default: " + DEFAULT_TIME_LIMIT);
			} else {
				System.out.println("debug - No se pudo parsear el parametro 'time_limit' (" + time
						+ "). uso por default: " + DEFAULT_TIME_LIMIT);
			}

		}
	}

	private void readVerbose(Properties p) {
		String verbose = p.getProperty("verbose");
		try {
			if (verbose != null && !verbose.isEmpty()) {
				boolean verboseValue = Boolean.parseBoolean(verbose);
				setVerbose(verboseValue);
			}
		} catch (Exception e) {
			isVerbose = DEFAULT_IS_VERBOSE;
		}
	}

	
	private void readInequalitiesEnabled(Properties p) {
		String ineqEnabled = p.getProperty("inequalities_enabled","");
		
		String[] aux = ineqEnabled.split(",");
		int n;
		System.out.println("Proceso "+ineqEnabled);
		
		for (String s: aux) {
			if ( !s.trim().isEmpty() ) {
				try {
					n = Integer.parseInt(s);
					enableInequality(n);
				} catch(NumberFormatException ex) {
					System.out.println("debug - Error en el parametro 'inequalities_enabled'. No se"
							+ " pudo parsear la inequality como entero con el parametro: "+s);
				}
			}
		}	
	}
	
	public void setMemoryLimit(double memory) {
		System.out.println("Spinner MEMORY value changed: " + memory);
		if ( memory > 0 )
			memoryLimit = memory;
	}
	
	public double getMemoryLimit() {
		return memoryLimit;
	}

	
	
	public void setGapLimit(double gap) {
		System.out.println("Spinner GAP value changed: " + gap);
		if (gap >= 0 && gap <1)
			gapLimit = gap;
	}

	public double getGapLimit() {
		return gapLimit;
	}

	
	
	public void setTimeLimit(long time) {
		System.out.println("Spinner TIME value changed (min): " + time);
		if (time > 0)
			timeLimit = time;
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	
	
	public void setVerbose(boolean isVerbose) {
		System.out.println("CAMBIO DE CONFIGURACION: VERBOSE MODE: " + (isVerbose ? "ON" : "OFF"));
		this.isVerbose = isVerbose;
	}

	public boolean isVerbose() {
		return isVerbose;
	}
	
	
	
	public boolean isInequalityEnabled(Integer ineq) {
		return inequalitiesEnabled.contains(ineq);
	}
	
	public void enableInequality(Integer ineq) {
		System.out.println("HABILITO " + ineq);
		if ( ! isInequalityEnabled(ineq) ) {
			inequalitiesEnabled.add(ineq);
		}
	}
	
	public void disableInequality(Integer ineq) {
		System.out.println("DESHABILITO " + ineq);
		int pos;
		while( (pos = inequalitiesEnabled.indexOf(ineq)) >= 0 )
			inequalitiesEnabled.remove(pos);
	}
	
	public List<Integer> getInequalitiesEnabled() {
		return inequalitiesEnabled;
	}	
}
