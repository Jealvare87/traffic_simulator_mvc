package es.ucm.fdi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;

public class Junction extends SimulatedObject {
	
	protected Road road; // carretera 
	protected List<Vehicle> queue; // cola de vehículos 
	protected boolean green; // true si su semáforo está verde 
	protected List<IncomingRoad> roads; // lista de carreteras entrantes
	protected Map<Junction, Road> incomingRoads; // mapa de carreteras entrantes indicando cual es su cruce origen	

	public Junction(String x) {
		super(x);
		this.green = false;
		this.queue = new ArrayList<Vehicle>();
		this.roads = new ArrayList<IncomingRoad>();
		this.incomingRoads = new HashMap<Junction, Road>();
	}

	protected void fillReportDetails(IniSection is) {
		String aux = "";
		int contador = 0;
		for (IncomingRoad r : this.roads) {
			if (contador == 0) {
				if (r.hasGreenLight()) {
					aux += "(" + r.getRoad() + ",green," + "[" + r.printQuque() + "])";
				} else {
					aux += "(" + r.getRoad() + ",red," + "[" + r.printQuque() + "])";
				}
				contador++;
			}
			else {
				if (r.hasGreenLight()) {
					aux += ",(" + r.getRoad() + ",green," + "[" + r.printQuque() + "])";
				} else {
					aux += ",(" + r.getRoad() + ",red," + "[" + r.printQuque() + "])";
				}
			}
		}
		is.setValue("queues", aux);
	}
	
	public List<Vehicle> getQueue(){
		return this.queue;
	}
	
	public String getGreenQueue() {
		String aux = "[";
		int contador = 0;
		for (IncomingRoad r : this.roads) {
			if (contador == 0) {
				if (r.hasGreenLight()) {
					aux += "(" + r.getRoad() + ",green," + "[" + r.printQuque() + "])";
				contador++;
				}
			}
			else {
				if (r.hasGreenLight()) {
					aux += ",(" + r.getRoad() + ",green," + "[" + r.printQuque() + "])";
				} 
				contador++;
			}
		}
		aux += "]";
		return aux;
	}
	
	public String getRedQueue() {
		String aux = "[";
		int contador = 0;
		for (IncomingRoad r : this.roads) {
			if (contador == 0) {
				if (!r.hasGreenLight()) {
					aux += "(" + r.getRoad() + ",red," + "[" + r.printQuque() + "])";
				contador++;
				}
			}
			else {
				if (!r.hasGreenLight()) {
					aux += ",(" + r.getRoad() + ",red," + "[" + r.printQuque() + "])";
				} 
				contador++;
			}
		}
		aux += "]";
		return aux;
	}
	
	protected String getReportSectionTag() {
		return "junction_report";
	}

	void advance(int time) throws SimulatorError {
		if (this.roads != null) {
			int cont = 0;
			int conti = 1;
			boolean ok = false;

			while (cont < this.roads.size() && !ok) {
				if (this.posGreenInco(this.roads) == cont) {
					if (!this.roads.get(cont)._queue.isEmpty()) {
						ok = true;
						while (conti < this.roads.get(this.posGreenInco(this.roads))._queue.size()) {
							this.roads.get(this.posGreenInco(this.roads))._queue.get(conti).setSpeed(0);
							conti++;
						}
						this.roads.get(this.posGreenInco(this.roads)).advanceFirstVehicle();
					}
				}
				cont++;
			}
			if (this.roads.size() > 1 && time > 0) {
				this.switchLights();
			}
		}
	}

	public Road roadTo(Junction to, Junction from) {
		Road r = to.incomingRoads.get(from);
		return r;
	}
	
	public Road roadFrom(Junction from) {
		return this.incomingRoads.get(from);
	}
	
	public List<IncomingRoad> getRoadsInfo(){
		return this.roads;
	}
	
	void addIncommingRoad(Road inco) {
		inco.srcJunc.road = inco;
		boolean found = false;
		int cont = 0;
		while(cont < this.roads.size() && found == false) {
			if(this.roads.get(cont).road.equals(inco)) {
				found = true;
			}
			else {
				cont++;
			}
		}
		if(found == false) {
			this.createIncommingRoadQueue(inco);
		}		
	}
	
	void addOutGoingRoad(Road going) {
		this.incomingRoads.put(going.srcJunc, going);
	}
	
	void enter(Vehicle enter) {
		this.queue.add(enter);
	}
	
	protected void switchLights() {
		int pos = this.posGreenInco(this.roads);
		this.roads.get(pos).setGreen(false);
		if(pos < this.roads.size() - 1) {
			this.roads.get(pos + 1).setGreen(true);
		}
		else {
			this.roads.get(0).setGreen(true);
		}
	}
	
	protected int posGreenInco(List<IncomingRoad> inco) {
		int cont = 0;
		boolean ok = false;

		while (cont < inco.size() && !ok) {
			if (inco.get(cont).green == true) {
				ok = true;
			} else {
				cont++;
			}
		}

		return cont;
	}
	
	public int getPosIncRoad(Road p) {
		int pos = 0;
		boolean ok = false;
		while (ok == false && pos < this.roads.size()) {
			if (this.roads.get(pos).road.id.equals(p.id)) {
				ok = true;
			} else {
				pos++;
			}
		}
		if (ok == true) {
			return pos;
		} else {
			return -1;
		}
	}
	
	protected IncomingRoad createIncommingRoadQueue(Road queue) {
		IncomingRoad r = new IncomingRoad(queue);
		this.incomingRoads.put(queue.srcJunc, queue);
		for(Vehicle v : queue.vehicles) {
			this.roads.get(this.roads.indexOf(r)).addVehicle(v);
		}
		this.roads.add(r);
		return r;
	}
	
	public boolean exist(String id) {
		boolean ok = false;
		int contador = 0;
		while(contador < this.roads.size() && !ok) {
			if(this.roads.get(contador).getRoad().getId().equals(id)) {
				ok = true;
			}
			else {
				contador++;
			}
		}		
		return ok;
	}
	
	public class IncomingRoad{
		protected Road road;
		protected List<Vehicle> _queue;
		protected boolean green;
		
		public IncomingRoad(Road road) {
			this.road = road;
			this.green = false;
			this._queue = new ArrayList<Vehicle>();
		}
		
		public Road getRoad() {
			return this.road;
		}
		
		public boolean hasGreenLight() {
			return this.green;
		}
		
		protected void setGreen(boolean bool) {
			this.green = bool;
		}
		
		protected void advanceFirstVehicle() throws SimulatorError {
			if(roads.get(roads.indexOf(this)).green == true) {
				this._queue.get(0).moveToNextRoad();
			}
		}
		
		protected void addVehicle(Vehicle motor) {
			this._queue.add(motor);
		}
		
		protected String printQuque() {
			String palabra = "";
			int contador = 0;
			for(Vehicle x: this._queue) {
				if(contador == 0) {
					palabra +=  x.getId();
					contador++;
				}
				else {
					palabra += ","+ x.getId();
				}
			}
			return palabra;
		}
		
		public String toString(){
			return null;
		}
	}
	
	public String toStrings() {
		return this.id;
	}
}
