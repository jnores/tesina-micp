package ar.edu.ungs.tesina.micp.app.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ar.edu.ungs.tesina.micp.app.model.Instancia;
import ar.edu.ungs.tesina.micp.app.runnable.SolveRunnable;

import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class SolvingFrame extends JFrame {

	private JPanel contentPane;
	private Instancia mInstancia;
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
			showMessage("La instancia recibida es invalida!");
		}
	}
	
	private void initSolver(Runnable runnable ) {
		mExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
		mFuture = mExecutor.schedule(runnable, 10, TimeUnit.SECONDS);
	}
	
	private void verifySolverFinish() {
		final SwingWorker worker = new SwingWorker(){
			 
			@Override
			protected Object doInBackground() throws Exception {
				while ( ! mFuture.isDone() )
					Thread.sleep(10000);
				if ( mFuture.isCancelled() )
					showMessage("Trabajo cancelado");
				else
					showMessage("Optimización completa!");
				return null;
			}	
		};
		worker.execute();
	}
	
	private void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message,"Optimización finalizada!",JOptionPane.INFORMATION_MESSAGE);
		mParent.resumeFromOptimization();
		dispose();
	}
	
	

}
