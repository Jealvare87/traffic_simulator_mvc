package es.ucm.fdi.model;

import es.ucm.fdi.ini.IniSection;

public class DirtRoad extends Road {

	protected String type;
	
	public DirtRoad(String id, int kilometrage, int max_speed, Junction src, Junction dest, String type) {
		super(id, kilometrage, max_speed, src, dest);
		this.type = type;
	}
	
	protected void fillReportDetails(IniSection s) {
		String aux= "";
		int cont = 0;
		if(this.vehicles != null) {
			for(Vehicle v: this.vehicles) {
				if(cont == 0) {
					aux += "(" + v.getId() + "," + v.getLocation() + ")";
					cont++;
				}
				else {
					aux += ",(" + v.getId() + "," + v.getLocation() + ")";
				}
			}
		}
		else {
			aux += "()";
		}
		s.setValue("type", this.type);
		s.setValue("state", aux);
	}
	
	protected String getReportSectionTag() {
		return "road_report";
	}
	
	protected void advance(int x) throws SimulatorError {
		int contador = 0;
		boolean removed = false;
		
		if (this.reduceSpeedFactor(x) != 0) {
			if (this.vehicles != null) {
				for (Vehicle v : this.vehicles) {
					int y = (this.calculateBaseSpeed() / this.reduceSpeedFactor(super.vehiclesFaulty(v)));
					v.setSpeed(y);
					v.advance(x);
				}
			}
		}
		while (contador < this.vehicles.size() && !removed) {
			if (this.vehicles.get(contador).arrived == true) {
				this.vehicles.remove(contador);
				removed = true;
			} else if (contador == 0 && this.vehicles.get(contador).atJunction == true) {
				if (this.vehicles.get(contador).getLocation() != this.vehicles.get(contador).getRoad().length) {
					this.vehicles.remove(contador);
					removed = true;
				}
			}
			contador++;
		}
		this.vehicles.sort(this.compa);
	}
	
	private int calculateBaseSpeed() {
		return this.maxSpeed;
	}
	
	private int reduceSpeedFactor(int i) {
		return (1 + i);
	}
}
