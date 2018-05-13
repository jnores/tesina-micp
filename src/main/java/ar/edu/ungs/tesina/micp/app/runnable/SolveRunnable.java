package ar.edu.ungs.tesina.micp.app.runnable;

import ar.edu.ungs.tesina.micp.Instancia;
import ar.edu.ungs.tesina.micp.MicpScipSolver;
import jscip.SCIP_ParamEmphasis;
import jscip.Scip;

public class SolveRunnable implements Runnable {
	private MicpScipSolver mMicp;
	private Instancia mInstance;

	public SolveRunnable(Instancia instancia) {
		mInstance = instancia;
		Scip solver = new Scip();
		solver.create("micp_app-"+instancia.getName());
		solver.setRealParam("limits/time", 300);
		mMicp = MicpScipSolver.createMicp(solver);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			mInstance.setSolution(  mMicp.findOptimal(mInstance.getConflictGraph(), mInstance.getRelationshipGraph(), mInstance.getAulas()) );
		} catch (Exception e)
		{
			System.out.println("debug - La optimizacion fue interrumpida!");
			mMicp.free();
		}
		mMicp.free();
	}

}
