package es.ucm.fdi.view;

import java.util.List;
import javax.swing.table.AbstractTableModel;

import es.ucm.fdi.model.Junction;


public class JunctionTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	
	private List<Junction> _junctions;
	private String[] _colNames = { "ID", "Green", "Red" };

	public void setJunctionsList(List<Junction> junctions) {
		_junctions = junctions;
		fireTableStructureChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public String getColumnName(int col) {
		return _colNames[col];
	}

	@Override
	public int getColumnCount() {
		return _colNames.length;
	}

	@Override
	public int getRowCount() {
		return _junctions == null ? 0 : _junctions.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object s = null;
		switch (columnIndex) {
		case 0:
			s = _junctions.get(rowIndex).getId();
			break;
		case 1:
			s = _junctions.get(rowIndex).getGreenQueue();
			break;
		case 2:
			s = _junctions.get(rowIndex).getRedQueue();
		}
		return s;
	}

	public void refresh() {
		fireTableDataChanged();
	}
}
