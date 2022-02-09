package es.ucm.fdi.view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import es.ucm.fdi.model.KilometrageView;

public class HistoryKmTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	
	private List<KilometrageView> _vehicles;
	private String[] _colNames = { "Time", "Total km"};
	
	
	public void setKmList(List<KilometrageView> v) {
		this._vehicles = v;
		
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
	public int getRowCount() {
		return _vehicles == null ? 0 : _vehicles.size();
	}

	@Override
	public int getColumnCount() {
		return _colNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object s = null;
		switch (columnIndex){
		case 0:
			s = _vehicles.get(rowIndex).getTime();
			break;
		case 1:
			s = _vehicles.get(rowIndex).getKmTotal();
			break;
		}
		return s;
	}


	public void refresh() {
		fireTableDataChanged();
	}

}
