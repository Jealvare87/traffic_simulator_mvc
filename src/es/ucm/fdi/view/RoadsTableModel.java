package es.ucm.fdi.view;

import java.util.List;
import javax.swing.table.AbstractTableModel;

import es.ucm.fdi.model.Road;

public class RoadsTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	
	private List<Road> _roads;
	private String[] _colNames = { "ID", "Source", "Target", "Length", "Max Speed", "Vehicles" };

	public void setRoadsList(List<Road> roads) {
		_roads = roads;
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
		return _roads == null ? 0 : _roads.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object s = null;
		switch (columnIndex) {
		case 0:
			s = _roads.get(rowIndex).getId();
			break;
		case 1:
			s = _roads.get(rowIndex).getSource();
			break;
		case 2:
			s = _roads.get(rowIndex).getDestination();
			break;
		case 3:
			s = _roads.get(rowIndex).getLength();
			break;
		case 4:
			s = _roads.get(rowIndex).getMaxSpeed();
			break;
		case 5:
			s = _roads.get(rowIndex).getVehicles();
			break;
		}
		

		return s;
	}

	public void refresh() {
		fireTableDataChanged();
	}
}
