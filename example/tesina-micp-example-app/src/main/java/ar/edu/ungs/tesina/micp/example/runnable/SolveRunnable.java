package ar.edu.ungs.tesina.micp.example.runnable;

import java.util.Properties;

import ar.edu.ungs.tesina.micp.Instance;
import ar.edu.ungs.tesina.micp.MicpScipSolver;

public class SolveRunnable implements Runnable {
	private MicpScipSolver mMicp;
	private Instance mInstance;

	public SolveRunnable(Instance instancia,Properties prop) {
		mInstance = instancia;
		mMicp = MicpScipSolver.createMicp(instancia.getName(), prop);
	}
	@Override
	public void run() {
		try {
			
			mInstance.setSolution(		
					mMicp.searchOptimal(
							mInstance.getConflictGraph(),
							mInstance.getRelationshipGraph(),
							mInstance.getAulas()
							)
					);
		} catch (Exception e)
		{
			System.out.println("debug - La optimizacion fue interrumpida!");
			e.printStackTrace();
			mMicp.free();
		}
		mMicp.free();
	}

}
