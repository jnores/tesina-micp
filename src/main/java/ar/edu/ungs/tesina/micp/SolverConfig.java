package ar.edu.ungs.tesina.micp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// TODO: Cambiar la definiion de ls desigualdades y armarle un factory y/o algun Helper. ahora es id1|id2|id3... 
public class SolverConfig {
	static final double DEFAULT_GAP_LIMIT = 0.0;
	static final int DEFAULT_TIME_LIMIT = 3600;
	static final boolean DEFAULT_IS_VERBOSE = false;
	private double gapLimit = DEFAULT_GAP_LIMIT;
	private long timeLimit = DEFAULT_TIME_LIMIT;
	private boolean isVerbose = DEFAULT_IS_VERBOSE;
	private List<Integer> inequalitiesEnabled;

	public SolverConfig(Properties p) {
		inequalitiesEnabled = new ArrayList<Integer>();
		readGapLimit(p);
		readTimeLimit(p);
		readVerbose(p);
		readInequalitiesEnabled(p);
	}

	private void readGapLimit(Properties p) {
		String gap = p.getProperty("gap_limit");
		try {
			double gapValue = Double.parseDouble(gap);
			setGapLimit(gapValue);
		} catch (Exception e) {
			gapLimit = DEFAULT_GAP_LIMIT;
			if (gap == null) {
				System.out
						.println("debug - No se configuró un limite para el GAP. Uso por default: "
								+ DEFAULT_GAP_LIMIT);
			} else {
				System.out.println("debug - No se pudo parsear la configuración del GAP (" + gap
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
				System.out
						.println("debug - No se configuró un limite para el TIME. Uso por default: "
								+ DEFAULT_TIME_LIMIT);
			} else {
				System.out.println("debug - No se pudo parsear la configuración del TIME (" + time
						+ "). uso por default: " + DEFAULT_TIME_LIMIT);
			}

		}
	}

	private void readVerbose(Properties p) {
		String verbose = p.getProperty("verbose");
		try {
			if (verbose != null && !verbose.isEmpty()) {
				boolean verboseValue = Boolean.parseBoolean(verbose);
				isVerbose = verboseValue;
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
					System.out.println("debug - No se pudo parsear la inequality como entero con el parametro: "+s);
				}
			}
		}	
	}
	
	public void setGapLimit(double gap) {
		if (gap >= 0 && gap <1)
		gapLimit = gap;
	}

	public double getGapLimit() {
		return gapLimit;
	}

	public void setTimeLimit(long time) {
		if (time > 0)
			timeLimit = time;
	}


	public long getTimeLimit() {
		return timeLimit;
	}

	public boolean isVerbose() {
		return isVerbose;
	}
	
	public boolean isInequalityEnabled(Integer ineq) {
		return inequalitiesEnabled.contains(ineq);
	}
	
	public void enableInequality(Integer ineq) {
		if ( ! isInequalityEnabled(ineq) ) {
			inequalitiesEnabled.add(ineq);
		}
	}
	
	public void disableInequality(Integer ineq) {
		int pos;
		while( (pos = inequalitiesEnabled.indexOf(ineq)) >= 0 )
			inequalitiesEnabled.remove(pos);
	}
	
	public List<Integer> getInequalitiesEnabled() {
		return inequalitiesEnabled;
	}

}
