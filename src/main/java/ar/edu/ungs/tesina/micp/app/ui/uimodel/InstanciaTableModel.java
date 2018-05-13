package ar.edu.ungs.tesina.micp.app.ui.uimodel;

import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import ar.edu.ungs.tesina.micp.Instancia;
import ar.edu.ungs.tesina.micp.app.model.Aula;
import ar.edu.ungs.tesina.micp.app.model.Clase;

public class InstanciaTableModel extends AbstractTableModel implements Observer{

	private static final long serialVersionUID = 5392712288776859215L;

	private String[] mHeaders = { "id", "Aula", "Materia", "Docente", "Dia", "Horario" };

	private Instancia<Clase,Aula> mInstancia;

	public InstanciaTableModel(Instancia<Clase,Aula> instancia) {
		mInstancia = instancia;
		if (instancia != null)
			instancia.addObserver(this);
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		if (mInstancia == null)
			return 0;
		return mInstancia.getVertices().size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return mHeaders.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		Clase c = mInstancia.getVertices().get(rowIndex);
		Object ret = null;
		switch (columnIndex)
		{
		case 0: // Nombre Materia
			ret = c.getId();
			break;

		case 1: // Aula
			if (mInstancia.hasSolution())
				ret = mInstancia.getOptimal(c);
			else
				ret = "--";
			break;
		case 2: // Nombre Materia
			ret = c.getNombre();
			break;
		case 3: // Docente
			ret = c.getDocente();
			break;
		case 4: // Dia
			ret = c.getDia();
			break;
		case 5: // Horario
			ret = ""+c.getHoraInicio()+"-"+c.getHoraFin();
			break;
		}
		return ret;
	}

	@Override
	public String getColumnName(int index) {
		return mHeaders[index];
	}

	public void setInstancia(Instancia<Clase,Aula> instancia) {
		mInstancia = instancia;
		if (instancia != null)
			instancia.addObserver(this);
		fireTableDataChanged();
	}

	@Override
	public void update(Observable o, Object arg) {
		fireTableDataChanged();
	}

}
