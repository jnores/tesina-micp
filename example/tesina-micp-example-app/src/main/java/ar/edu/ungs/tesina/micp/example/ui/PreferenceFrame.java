package ar.edu.ungs.tesina.micp.example.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

import ar.edu.ungs.tesina.micp.example.MicpApp;
import javax.swing.JToggleButton;

public class PreferenceFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6890758497251529463L;
	private JPanel contentPane;
	private MicpApp mApp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PreferenceFrame frame = new PreferenceFrame(new MicpApp());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PreferenceFrame(MicpApp app) {
		mApp = app;
		try {
			// Cambiamos el Look&Feel
			JFrame.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		setAlwaysOnTop(true);
		setType(Type.NORMAL);
		setTitle("Preferencias");
		setResizable(false);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 393);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(5, 12, 211, 337);
		panel.setBorder(new TitledBorder(null, "Desigualdades", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JCheckBox chckbxmntmPartitioned = new JCheckBox("Partitioned");
		panel.add(chckbxmntmPartitioned);
		chckbxmntmPartitioned.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_PARTITIONED));
		chckbxmntmPartitioned.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_PARTITIONED, isSelected);
			}
		});

		JCheckBox chckbxmntm3Partitioned = new JCheckBox("3-partitioned");
		panel.add(chckbxmntm3Partitioned);
		chckbxmntm3Partitioned.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_3_PARTITIONED));
		chckbxmntm3Partitioned.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_3_PARTITIONED, isSelected);
			}
		});

		JCheckBox chckbxmntmKpartitioned = new JCheckBox("k-partitioned");
		panel.add(chckbxmntmKpartitioned);
		app.disableInequality(MicpApp.INEQ_K_PARTITIONED);
		chckbxmntmKpartitioned.setSelected(false);
		chckbxmntmKpartitioned.setEnabled(false);

		JSeparator separator_1 = new JSeparator();
		panel.add(separator_1);

		JCheckBox chckbxmntmVertexclique = new JCheckBox("Vertex-clique");
		panel.add(chckbxmntmVertexclique);
		chckbxmntmVertexclique.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_VERTEX_CLIQUE));
		chckbxmntmVertexclique.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_VERTEX_CLIQUE, isSelected);
			}
		});

		JCheckBox chckbxmntmCliquepartitioned = new JCheckBox("Clique-partitioned");
		panel.add(chckbxmntmCliquepartitioned);
		chckbxmntmCliquepartitioned.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_CLIQUE_PARTITIONED));
		chckbxmntmCliquepartitioned.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_CLIQUE_PARTITIONED, isSelected);
			}
		});

		JCheckBox chckbxmntmSubclique = new JCheckBox("Sub-clique");
		panel.add(chckbxmntmSubclique);
		chckbxmntmSubclique.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_SUB_CLIQUE));
		chckbxmntmSubclique.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_SUB_CLIQUE, isSelected);
			}
		});

		JCheckBox chckbxmntmTwocolorSubclique = new JCheckBox("Two-color sub-clique");
		panel.add(chckbxmntmTwocolorSubclique);
		chckbxmntmTwocolorSubclique.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_2_COLOR_SUB_CLIQUE));
		chckbxmntmTwocolorSubclique.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_2_COLOR_SUB_CLIQUE, isSelected);
			}
		});

		JSeparator separator_2 = new JSeparator();
		panel.add(separator_2);

		JCheckBox chckbxmntmSemitriangle = new JCheckBox("Semi-triangle");
		panel.add(chckbxmntmSemitriangle);
		chckbxmntmSemitriangle.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_SEMI_TRIANGLE));
		chckbxmntmSemitriangle.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_SEMI_TRIANGLE, isSelected);
			}
		});

		JCheckBox chckbxmntmSemidiamond = new JCheckBox("Semi-diamond");
		panel.add(chckbxmntmSemidiamond);
		chckbxmntmSemidiamond.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_SEMI_DIAMOND));
		chckbxmntmSemidiamond.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_SEMI_DIAMOND, isSelected);
			}
		});

		JSeparator separator_3 = new JSeparator();
		panel.add(separator_3);

		JCheckBox chckbxmntmBounding = new JCheckBox("Bounding");
		panel.add(chckbxmntmBounding);
		chckbxmntmBounding.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_BOUNDING));
		chckbxmntmBounding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_BOUNDING, isSelected);
			}
		});

		JCheckBox chckbxmntmReinforcedBounding = new JCheckBox("Reinforced bounding");
		panel.add(chckbxmntmReinforcedBounding);
		chckbxmntmReinforcedBounding.setSelected(mApp.isInequalityEnabled(MicpApp.INEQ_REINFORCED_BOUNDING));
		chckbxmntmReinforcedBounding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				boolean isSelected = itemEvent.getStateChange() == ItemEvent.SELECTED;
				toggleInequality(MicpApp.INEQ_REINFORCED_BOUNDING, isSelected);
			}
		});

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Solver", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(228, 100, 204, 212);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblTiempoMximomin = new JLabel("Tiempo Máximo (min):");
		lblTiempoMximomin.setBounds(12, 28, 160, 15);
		panel_1.add(lblTiempoMximomin);

		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(30), new Integer(1), new Integer(120), new Integer(1)));
		spinner.setBounds(119, 50, 73, 20);
		JComponent comp = spinner.getEditor();
		JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
		DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
		formatter.setCommitsOnValidEdit(true);
		spinner.setValue(mApp.getTimeLimit());
		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mApp.setTimeLimit((Integer) spinner.getValue());
			}
		});
		panel_1.add(spinner);

		JLabel lblGapDeCorte = new JLabel("GAP de corte:");
		lblGapDeCorte.setBounds(12, 80, 115, 15);
		panel_1.add(lblGapDeCorte);

		JSpinner spinner_1 = new JSpinner();
		spinner_1.setModel(new SpinnerNumberModel(new Float(0), new Float(0), new Float(1), new Float(0.01)));
		spinner_1.setBounds(119, 102, 73, 20);
		JComponent comp_1 = spinner_1.getEditor();
		JFormattedTextField field_1 = (JFormattedTextField) comp_1.getComponent(0);
		DefaultFormatter formatter_1 = (DefaultFormatter) field_1.getFormatter();
		formatter_1.setCommitsOnValidEdit(true);
		spinner_1.setValue(mApp.getGapLimit());
		spinner_1.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mApp.setGapLimit((Float) spinner_1.getValue());
			}
		});
		panel_1.add(spinner_1);
		
		JLabel lblVerbose = new JLabel("Verbose:");
		lblVerbose.setBounds(12, 180, 70, 15);
		panel_1.add(lblVerbose);
		
		JToggleButton tglbtnActivo = new JToggleButton("Activo");
		tglbtnActivo.setSelected(mApp.isVerboseMode());
		tglbtnActivo.addItemListener(new ItemListener() {
			   public void itemStateChanged(ItemEvent ev) {
				  if(ev.getStateChange()==ItemEvent.SELECTED){
					  mApp.setVerboseMode(true);
			      } else if(ev.getStateChange()==ItemEvent.DESELECTED){
			    	  mApp.setVerboseMode(false);
			      }
			   }
			});
		tglbtnActivo.setBounds(100, 175, 89, 25);
		panel_1.add(tglbtnActivo);
		
		JLabel lblMemoryLimit = new JLabel("Memoria máxima:");
		lblMemoryLimit.setBounds(12, 126, 115, 15);
		panel_1.add(lblMemoryLimit);
		
		JSpinner spinner_2 = new JSpinner();
		spinner_2.setBounds(119, 148, 73, 20);
		spinner_2.setModel(new SpinnerNumberModel(new Double(1024), new Double(1), null, new Double(1)));
		
		JComponent comp_2 = spinner.getEditor();
		JFormattedTextField field_2 = (JFormattedTextField) comp_2.getComponent(0);
		DefaultFormatter formatter_2 = (DefaultFormatter) field_2.getFormatter();
		formatter_2.setCommitsOnValidEdit(true);
		spinner_2.setValue(mApp.getMemoryLimit());
		spinner_2.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mApp.setMemoryLimit((double) spinner_2.getValue());
			}
		});
		panel_1.add(spinner_2);

		JButton btnCerrar = new JButton("Cerrar");
		btnCerrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCerrar.setBounds(275, 324, 117, 25);
		contentPane.add(btnCerrar);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Instancia", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(228, 12, 204, 76);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblPabellon = new JLabel("Pabellon:");
		lblPabellon.setBounds(12, 31, 67, 15);
		panel_2.add(lblPabellon);

		JComboBox<Integer> comboBox = new JComboBox<Integer>();
		comboBox.setBounds(97, 26, 61, 24);
		comboBox.setModel(new DefaultComboBoxModel<Integer>(new Integer[] { 1, 2 }));
		comboBox.setSelectedItem(new Integer(mApp.getPabellon()));
		comboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent itemEvent) {
				if (itemEvent.getStateChange() == ItemEvent.SELECTED)
					mApp.setPabellon((Integer) itemEvent.getItem());

			}
		});
		panel_2.add(comboBox);

	}

	private void toggleInequality(int ineq, boolean isEnabled) {
		if (isEnabled)
			mApp.enableInequality(ineq);
		else
			mApp.disableInequality(ineq);
	}
}
