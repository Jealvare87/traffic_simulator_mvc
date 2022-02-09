package es.ucm.fdi.model;

import es.ucm.fdi.ini.IniSection;

public class LanesRoad extends Road {
	
	protected int numLanes;
	protected String type;
	
	public LanesRoad(String id, int length, int maxSpeed, Junction src, Junction dest, String type, int lanes) {
		super(id, length, maxSpeed, src, dest);
		this.numLanes = lanes;
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
	
	public int getNumLanes() {
		return this.numLanes;
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
		int a = 0, total = 0;		
		if(this.vehicles.size() > 1) {
			a = ((this.maxSpeed * this.numLanes) / this.vehicles.size());
		}
		else {
			a = this.maxSpeed * this.numLanes;
		}
		a++;
		if(this.maxSpeed > a) {
			total = a;
		}
		else {
			total = this.maxSpeed;
		}		
		return total;
	}
	
	private int reduceSpeedFactor(int factor) {
		int x = 0;
		if(this.numLanes > factor) {
			x = 1;
		}
		else {
			x = 2;
		}		
		return x;
	}
}
