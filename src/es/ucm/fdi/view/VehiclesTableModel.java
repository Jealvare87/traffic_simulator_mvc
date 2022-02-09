package es.ucm.fdi.view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import es.ucm.fdi.model.Vehicle;

public class VehiclesTableModel extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	
	private List<Vehicle> _vehicles;
	private String[] _colNames = { "ID", "Road", "Location", "Speed", "Km", "Faulty Units", "Itinerary" };
	
	
	public void setVehiclesList(List<Vehicle> v) {
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
			s = _vehicles.get(rowIndex).getId();
			break;
		case 1:
			s = _vehicles.get(rowIndex).getRoad();
			break;
		case 2:
			s = _vehicles.get(rowIndex).getLocation();
			break;
		case 3:
			s = _vehicles.get(rowIndex).getSpeed();
			break;
		case 4:
			s = _vehicles.get(rowIndex).getKilometrage();
			break;
		case 5:
			s = _vehicles.get(rowIndex).getFaulyTime();
			break;
		case 6:
			s = _vehicles.get(rowIndex).getItinerary();
			break;		
		}
		return s;
	}


	public void refresh() {
		fireTableDataChanged();
	}
	
}
