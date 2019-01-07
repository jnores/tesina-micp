package ar.edu.ungs.tesina.micp.example.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import ar.edu.ungs.tesina.micp.example.MicpApp;
import ar.edu.ungs.tesina.micp.example.model.Aula;
import ar.edu.ungs.tesina.micp.example.ui.uimodel.InstanciaTableModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class MainFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final String APP_NAME = "MicpSolver";

	static final String ACTION_LOAD_CURSOS = "loadCursosAction";
	static final String ACTION_LOAD_AULAS = "loadAulasAction";
	static final String ACTION_OPTIMIZAR = "optimizarAction";
	static final String ACTION_EXPORTAR_SOLUCION = "exportarSolucionAction";

	private MicpApp mApp;

	private JPanel contentPane;
	private JTable mCursosTable;
	private JFileChooser mFileChooser;

	private JSpinner mSpinner;

	private List<Aula> mAulasCargadas;
	private JButton mBtnImportAulas;
	private JButton mBtnOptimizar;
	private JButton mBtnExportar;
	private JButton mBtnImportarCursos;

	private InstanciaTableModel mCursosTableModel;

	/**
	 * Create the frame.
	 */
	public MainFrame(MicpApp app) {
		mApp = app;
		try {
			// Cambiamos el Look&Feel
			JFrame.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		setTitle(getTitleName());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);

		JMenuItem mntmAbrir = new JMenuItem("Abrir");
		mntmAbrir.setMnemonic('A');
		mntmAbrir.setActionCommand(ACTION_LOAD_CURSOS);
		mntmAbrir.addActionListener(this);
		mnArchivo.add(mntmAbrir);

		JMenuItem mntmGuardar = new JMenuItem("Guardar");
		mntmGuardar.setMnemonic('G');
		mntmGuardar.setEnabled(false);
		mnArchivo.add(mntmGuardar);

		JSeparator separator = new JSeparator();
		mnArchivo.add(separator);

		JMenuItem mntmSalir = new JMenuItem("Salir");
		mntmSalir.setMnemonic('S');
		mntmSalir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				app.close();
				dispose();
			}
		});

		mnArchivo.add(mntmSalir);
		
		JMenu mnDesigualdades = new JMenu("Desigualdades");
		menuBar.add(mnDesigualdades);
		
		JCheckBoxMenuItem chckbxmntmPartitioned = new JCheckBoxMenuItem("Partitioned");
		mnDesigualdades.add(chckbxmntmPartitioned);
		chckbxmntmPartitioned.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_PARTITIONED));
		chckbxmntmPartitioned.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_PARTITIONED, isSelected);
			}
		});

		JCheckBoxMenuItem chckbxmntm3Partitioned = new JCheckBoxMenuItem("3-partitioned");
		mnDesigualdades.add(chckbxmntm3Partitioned);
		chckbxmntm3Partitioned.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_3_PARTITIONED));
		chckbxmntm3Partitioned.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_3_PARTITIONED, isSelected);
			}
		});

		
		JCheckBoxMenuItem chckbxmntmKpartitioned = new JCheckBoxMenuItem("k-partitioned");
		mnDesigualdades.add(chckbxmntmKpartitioned);
		app.disableInequality(MicpApp.INEQ_K_PARTITIONED);
		chckbxmntmKpartitioned.setSelected(false);
		chckbxmntmKpartitioned.setEnabled(false);
		
		
		JSeparator separator_1 = new JSeparator();
		mnDesigualdades.add(separator_1);
		
		JCheckBoxMenuItem chckbxmntmVertexclique = new JCheckBoxMenuItem("Vertex-clique");
		mnDesigualdades.add(chckbxmntmVertexclique);
		chckbxmntmVertexclique.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_VERTEX_CLIQUE));
		chckbxmntmVertexclique.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_VERTEX_CLIQUE, isSelected);
			}
		});
		
		JCheckBoxMenuItem chckbxmntmCliquepartitioned = new JCheckBoxMenuItem("Clique-partitioned");
		mnDesigualdades.add(chckbxmntmCliquepartitioned);
		chckbxmntmCliquepartitioned.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_CLIQUE_PARTITIONED));
		chckbxmntmCliquepartitioned.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_CLIQUE_PARTITIONED, isSelected);
			}
		});

		
		JCheckBoxMenuItem chckbxmntmSubclique = new JCheckBoxMenuItem("Sub-clique");
		mnDesigualdades.add(chckbxmntmSubclique);
		chckbxmntmSubclique.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_SUB_CLIQUE));
		chckbxmntmSubclique.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_SUB_CLIQUE, isSelected);
			}
		});
		
		JCheckBoxMenuItem chckbxmntmTwocolorSubclique = new JCheckBoxMenuItem("Two-color sub-clique");
		mnDesigualdades.add(chckbxmntmTwocolorSubclique);
		chckbxmntmTwocolorSubclique.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_2_COLOR_SUB_CLIQUE));
		chckbxmntmTwocolorSubclique.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_2_COLOR_SUB_CLIQUE, isSelected);
			}
		});
		
		JSeparator separator_2 = new JSeparator();
		mnDesigualdades.add(separator_2);
		
		JCheckBoxMenuItem chckbxmntmSemitriangle = new JCheckBoxMenuItem("Semi-triangle");
		mnDesigualdades.add(chckbxmntmSemitriangle);
		chckbxmntmSemitriangle.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_SEMI_TRIANGLE));
		chckbxmntmSemitriangle.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_SEMI_TRIANGLE, isSelected);
			}
		});
		
		JCheckBoxMenuItem chckbxmntmSemidiamond = new JCheckBoxMenuItem("Semi-diamond");
		mnDesigualdades.add(chckbxmntmSemidiamond);
		chckbxmntmSemidiamond.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_SEMI_DIAMOND));		
		chckbxmntmSemidiamond.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_SEMI_DIAMOND, isSelected);
			}
		});
		
		JSeparator separator_3 = new JSeparator();
		mnDesigualdades.add(separator_3);
		
		JCheckBoxMenuItem chckbxmntmBounding = new JCheckBoxMenuItem("Bounding");
		mnDesigualdades.add(chckbxmntmBounding);
		chckbxmntmBounding.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_BOUNDING));
		chckbxmntmBounding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_BOUNDING, isSelected);
			}
		});
		
		JCheckBoxMenuItem chckbxmntmReinforcedBounding = new JCheckBoxMenuItem("Reinforced bounding");
		mnDesigualdades.add(chckbxmntmReinforcedBounding);
		chckbxmntmReinforcedBounding.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_REINFORCED_BOUNDING));
		chckbxmntmReinforcedBounding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_REINFORCED_BOUNDING, isSelected);
			}
		});

		JMenu mnAyuda = new JMenu("Ayuda");
		menuBar.add(mnAyuda);

		JMenuItem mntmAcercaDe = new JMenuItem("Acerca de...");
		mnAyuda.add(mntmAcercaDe);

		JPanel panel = new JPanel();
		panel.setToolTipText("Cantidad de aulas");
		contentPane.add(panel, BorderLayout.NORTH);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Aulas", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		panel.add(panel_2);

		mSpinner = new JSpinner();
		mSpinner.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
		panel_2.add(mSpinner);

		mBtnImportAulas = new JButton("Cargar");
		mBtnImportAulas.setActionCommand(ACTION_LOAD_AULAS);
		mBtnImportAulas.addActionListener(this);
		panel_2.add(mBtnImportAulas);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Cursos",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_1.setToolTipText("");
		panel.add(panel_1);

		mBtnImportarCursos = new JButton("Cargar");
		mBtnImportarCursos.setActionCommand(ACTION_LOAD_CURSOS);
		mBtnImportarCursos.addActionListener(this);
		panel_1.add(mBtnImportarCursos);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(
				new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Optimizaci\u00F3n",
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.add(panel_3);

		mBtnOptimizar = new JButton("Optimizar");
		mBtnOptimizar.setEnabled(false);
		mBtnOptimizar.setActionCommand(ACTION_OPTIMIZAR);
		mBtnOptimizar.addActionListener(this);
		panel_3.add(mBtnOptimizar);

		mBtnExportar = new JButton("Exportar");
		mBtnExportar.setEnabled(false);
		mBtnExportar.setActionCommand(ACTION_EXPORTAR_SOLUCION);
		mBtnExportar.addActionListener(this);
		panel_3.add(mBtnExportar);

		setContentPane(contentPane);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Listado de cursos", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		contentPane.add(panel_4);

		mCursosTable = new JTable();
		mCursosTableModel = new InstanciaTableModel(null);
		mCursosTable.setModel(mCursosTableModel);
		panel_4.add(mCursosTable.getTableHeader());
		panel_4.setLayout(new BorderLayout(0, 0));
		panel_4.add(new JScrollPane(mCursosTable));

	}
	
	private void toggleInequality(int ineq, boolean isEnabled) {
		if (isEnabled) 
			mApp.enableInequality(ineq);
		else
			mApp.disableInequality(ineq);
	}

	private static String getTitleName() {
		return APP_NAME;
	}

	private static String getTitleName(String nombre) {
		if (nombre == null || nombre.length() == 0)
			return getTitleName();
		else
			return APP_NAME + " - " + nombre;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case ACTION_LOAD_CURSOS:
			loadFileCursos();
			break;
		case ACTION_LOAD_AULAS:
			loadFileAulas();
			break;
		case ACTION_OPTIMIZAR:
			optimizar();
			break;
		case ACTION_EXPORTAR_SOLUCION:
			exportarSolucion();
			break;

		default:
			break;
		}

	}

	private void loadFileCursos() {
		if (mFileChooser == null)
			mFileChooser = new JFileChooser();
		// Con esto solamente podamos abrir archivos
		mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int seleccion = mFileChooser.showOpenDialog(this);

		if (seleccion == JFileChooser.APPROVE_OPTION) {
			File f = mFileChooser.getSelectedFile();
			try {
				String nombre = f.getName();
				String path = f.getAbsolutePath();
				boolean result;
				if (mAulasCargadas == null)
					result = mApp.loadInstanceFCEN(path, (int) mSpinner.getValue());
				else
					result = mApp.loadInstanceFCEN(path, mAulasCargadas);

				if (result) {
					// Colocamos en el titulo de la aplicacion el
					// nombre del archivo
					setTitle(getTitleName(nombre));
					mBtnOptimizar.setEnabled(true);
					mCursosTableModel.setInstancia(mApp.getInstancia());
				} else {
					JOptionPane.showMessageDialog(this,
							"No se pudo parsear el archivo de Cursos seleccionado.");
				}
			} catch (Exception exp) {
				JOptionPane.showMessageDialog(this, "No se pudo cargar el archivo seleccionado");
				System.out.println("ERROR: " + exp);
			}
		}
	}

	private void loadFileAulas() {
		if (mFileChooser == null)
			mFileChooser = new JFileChooser();
		// Con esto solamente podamos abrir archivos
		mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int seleccion = mFileChooser.showOpenDialog(this);

		if (seleccion == JFileChooser.APPROVE_OPTION) {
			File f = mFileChooser.getSelectedFile();
			try {
				String path = f.getAbsolutePath();
				List<Aula> result;

				result = mApp.loadAulasFile(path);

				if (result != null && result.size() > 0) {
					mAulasCargadas = result;
					mSpinner.setValue(mAulasCargadas.size());
				} else {
					JOptionPane.showMessageDialog(this,
							"No se pudo parsear el archivo seleccionado.");
				}
			} catch (Exception exp) {
				JOptionPane.showMessageDialog(this,
						"No se pudo cargar el archivo de Aulas seleccionado");
				System.out.println("ERROR: " + exp);
			}
		}
	}

	private void optimizar() {
		JFrame f = new SolvingFrame(this, mApp.getRunnableSolver());
		setEnabled(false);
		f.setVisible(true);
	}

	private void exportarSolucion() {
		if (mFileChooser == null)
			mFileChooser = new JFileChooser();
		// Con esto solamente podamos abrir archivos
		mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int seleccion = mFileChooser.showSaveDialog(this);

		if (seleccion == JFileChooser.APPROVE_OPTION) {
			File f = mFileChooser.getSelectedFile();
			try {
				String path = f.getAbsolutePath();

				if (mApp.saveSolution(path)) {
					JOptionPane.showMessageDialog(this, "Solucion Guardada exitosamente.");
				} else {
					JOptionPane.showMessageDialog(this, "No se pudo guardar la solucion.");
				}
			} catch (Exception exp) {
				JOptionPane.showMessageDialog(this, "Hubo un error guardando la solucion.");
				System.out.println("ERROR: " + exp);
			}
		}
	}

	public void resumeFromOptimization(String message, boolean isCancelled) {
		setEnabled(true);
		mBtnExportar.setEnabled(false);
		int icon = JOptionPane.WARNING_MESSAGE;
		
		if (message != null && !message.trim().isEmpty()) {
			// muestro el mensaje como está!
			icon = JOptionPane.ERROR_MESSAGE;
			
		} else if (isCancelled) {
			message = "Asignación cancelada.";
			
		} else if (mApp.getInstancia().hasSolution()) {
			mBtnExportar.setEnabled(true);
			message = "Asignación optimizada.";
			icon = JOptionPane.INFORMATION_MESSAGE;
			
		} else {
			message = "La instancia no tiene solución.";
			
		}
		
		JOptionPane.showMessageDialog(this, message,"Optimización finalizada!",icon);
	}
}
