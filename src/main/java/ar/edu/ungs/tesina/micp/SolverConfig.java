package ar.edu.ungs.tesina.micp;

import java.util.Properties;

public class SolverConfig {
	static final double DEFAULT_GAP_LIMIT = 0.0;
	static final int DEFAULT_TIME_LIMIT = 240; // 3600;
	static final boolean DEFAULT_IS_VERBOSE = true; // false;
	static final long DEFAULT_INEQUALITIES_ENABLED = 24; // WITHOUT_INEQUALITIES
	private double gapLimit = DEFAULT_GAP_LIMIT;
	private long timeLimit = DEFAULT_TIME_LIMIT;
	private boolean isVerbose = DEFAULT_IS_VERBOSE;
	private long inequalitiesEnabled = DEFAULT_INEQUALITIES_ENABLED;

	public SolverConfig(Properties p) {
		readGapLimit(p);
		readTimeLimit(p);
		readVerbose(p);
		readInequalitiesEnabled(p);
	}

	private void readGapLimit(Properties p) {
		String gap = p.getProperty("gap_limit");
		try {
			double gapValue = Double.parseDouble(gap);
			if (gapValue > 0)
				gapLimit = gapValue;
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
			if (timeValue > 0)
				timeLimit = timeValue;
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
		String ineqEnabled = p.getProperty("inequalities_enabled");
		try {
			int ineqEnabledValue = Integer.parseInt(ineqEnabled);
			if (ineqEnabledValue > 0)
				inequalitiesEnabled = ineqEnabledValue;
		} catch (Exception e) {
			inequalitiesEnabled = DEFAULT_INEQUALITIES_ENABLED;
			if (ineqEnabled == null) {
				System.out
						.println("debug - No se configuró un las Inequalities a usar. Uso por default: "
								+ DEFAULT_INEQUALITIES_ENABLED);
			} else {
				System.out.println("debug - No se pudo parsear la configuración de Inequalities a usar (" + ineqEnabled
						+ "). uso por default: " + DEFAULT_INEQUALITIES_ENABLED);
			}

		}
	}

	public double getGapLimit() {
		return gapLimit;
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	public boolean isVerbose() {
		return isVerbose;
	}
	
	public long getInequalitiesEnabled() {
		return inequalitiesEnabled;
	}

	
}
