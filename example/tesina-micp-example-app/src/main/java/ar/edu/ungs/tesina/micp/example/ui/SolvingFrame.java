package ar.edu.ungs.tesina.micp.example.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class SolvingFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnCancel;
	private ScheduledThreadPoolExecutor mExecutor;
	private ScheduledFuture<?> mFuture;
	private MainFrame mParent;

	/**
	 * Create the frame.
	 */
	public SolvingFrame(MainFrame parent, Runnable runnable) {
		mParent = parent;
		try {
			// Cambiamos el Look&Feel
			JFrame.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		setAlwaysOnTop(true);
		setType(Type.NORMAL);
		setTitle("Optimizando...");
		setResizable(false);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 289, 67);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		contentPane.add(progressBar);
		
		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mFuture != null);
					mFuture.cancel(true);
				dispose();
			}
		});
		contentPane.add(btnCancel);
		
		if (runnable != null ) {
			initSolver(runnable);
			verifySolverFinish();
		} else {
			showMessage("La instancia recibida es invalida!",false);
		}
	}
	
	private void initSolver(Runnable runnable ) {
		mExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
		mFuture = mExecutor.schedule(runnable, 1, TimeUnit.SECONDS);
	}
	
	private void verifySolverFinish() {
		final SwingWorker<Object,Object> worker = new SwingWorker<Object,Object>(){
			 
			@Override
			protected Object doInBackground() throws Exception {
				while ( ! mFuture.isDone() )
					Thread.sleep(2000);
				
				showMessage(null, mFuture.isCancelled());
				
				return null;
			}	
		};
		worker.execute();
	}
	
	private void showMessage(String message, boolean isCancelled) {
		// JOptionPane.showMessageDialog(this, message,"Optimización finalizada!",
		//                               JOptionPane.INFORMATION_MESSAGE);
		mParent.resumeFromOptimization( message, isCancelled);
		dispose();
	}
	
	

}
