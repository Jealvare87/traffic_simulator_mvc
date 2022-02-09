package es.ucm.fdi.model;

import java.util.Comparator;
import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.launcher.SortedArrayList;

public class Road extends SimulatedObject{
	protected int length; // longitud de la carretera 
	protected int maxSpeed; // limite máximo de velocidad 
	protected Junction srcJunc; // cruce origen de la carretera 
	protected Junction destJunc; // cruce destino de la carretera
	protected List<Vehicle> vehicles;
	protected Comparator<Vehicle> compa;
	
	public Road(String id, int length, int maxSpeed, Junction src, Junction dest) {
		super(id);
		this.length = length;
		this.maxSpeed = maxSpeed;
		this.srcJunc = src;
		this.destJunc = dest;
		this.compa = new Comparator<Vehicle>() {

			@Override
			public int compare(Vehicle arg0, Vehicle arg1) {
				if(arg0.getLocation() > arg1.getLocation()) {
					return -1;
				}
				else if(arg0.getLocation() < arg1.getLocation()) {
					return 1;
				}
				else {
					return 0;
				}
			}};
		this.vehicles = new SortedArrayList<Vehicle>(this.compa);
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
					int y = (this.calculateBaseSpeed() / this.reduceSpeedFactor(this.vehiclesFaulty(v)));
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
	
	protected int vehiclesFaulty(Vehicle v) {
		int i = 0;
		int j = 0;
		int k = 0;
		j = this.vehicles.indexOf(v);
		while(k < j) {
			if(this.vehicles.get(k).faultyTime > 0) {
				i++;
			}
			k++;
		}
		return i;
	}
	
	public Junction getSource() {
		return this.srcJunc;
	}
	
	public Junction getDestination() {
		return this.destJunc;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public int getMaxSpeed() {
		return this.maxSpeed;
	}
	
	public List<Vehicle> getVehicles(){
		return this.vehicles;
	}
	
	protected void enter(Vehicle v) {
		if(!this.vehicles.contains(v)) {
			this.vehicles.add(v);
		}	
	}
	
	public void exit(Vehicle exit) {
		if(this.vehicles.contains(exit)) {
			this.vehicles.remove(exit);
		}
	}
	
	private int calculateBaseSpeed() {
		int aux = 0;
	
		if(this.vehicles.size() > 1) {
			aux = (this.maxSpeed / this.vehicles.size());
		}
		else {
			aux = this.maxSpeed;
		}
		aux++;
		if(this.maxSpeed < aux) {
			aux = this.maxSpeed;
		}
		return aux;
	}
	
	private int reduceSpeedFactor(int x) {
		int factor = 0;
		if(x == 0) {
			factor = 1;
		}
		else if(x > 0) {
			factor = 2;
		}
		return factor;
	}
}
