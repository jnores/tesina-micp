package ar.edu.ungs.tesina.micp.app.runnable;

import ar.edu.ungs.tesina.micp.Micp;
import ar.edu.ungs.tesina.micp.app.model.Instancia;
import jscip.SCIP_ParamEmphasis;
import jscip.Scip;

public class SolveRunnable implements Runnable {
	private Micp mMicp;
	private Instancia mInstance;

	public SolveRunnable(Instancia instancia) {
		mInstance = instancia;
		Scip solver = new Scip();
		solver.create("micp_app-"+instancia.getName());
		solver.setEmphasis(SCIP_ParamEmphasis.SCIP_PARAMEMPHASIS_EASYCIP, true);
		mMicp = Micp.createMicp(solver);
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
