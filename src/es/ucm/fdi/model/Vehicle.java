package es.ucm.fdi.model;

import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Road;

public class Vehicle extends SimulatedObject{
	
	protected int maxSpeed; // máxima velocidad 
	protected int currSpeed; // velocidad actual 
	private Road road; // carretera por la que viaja 
	private int location; // localización en la carretera (desde 0) 
	protected List<Junction> itinerary; // lista de cruces 
	protected int kilometrage; // distancia recorrida por el vehículo 
	protected int faultyTime; // tiempo que resta en estado averiado 
	protected boolean atJunction; // true si ha entrado en cola de cruce 
	protected boolean arrived; // true cuando el coche llega a su destino
	protected String type;
	
	public Vehicle(String x, int i, List<Junction> list, Road r) {
		super(x);
		this.maxSpeed = i;
		this.itinerary = list;
		this.arrived = false;
		this.atJunction = false;
		this.faultyTime = 0;
		this.location = 0;
		this.currSpeed = 0;
		this.kilometrage = 0;
		this.road = r;
		this.type = null;
	}

	protected void fillReportDetails(IniSection section) {
		section.setValue("speed", this.currSpeed);
		section.setValue("kilometrage", this.kilometrage);
		section.setValue("faulty", this.faultyTime);
		section.setValue("location", this.arrived ? "arrived" : "(" + this.road + "," + this.getLocation() + ")");
	}

	protected String getReportSectionTag() {
		return "vehicle_report";
	}

	void advance(int x) throws SimulatorError {
		if (this.faultyTime > 0) {
			this.faultyTime--;
			this.currSpeed = 0;
		} else {
			if (this.arrived == false) {
				if (this.atJunction == false) {
					this.location += this.currSpeed;
					if (this.location < this.road.length) {
						this.kilometrage += this.currSpeed;
					} else {
						int i = this.road.length - (this.location - this.currSpeed);
						this.kilometrage += i;
					}
					if (this.location >= this.road.getLength()) {
						this.location = this.road.length;
						this.road.destJunc.queue.add(this);
						if (this.road.destJunc.getPosIncRoad(this.road) != -1) {
							this.road.destJunc.roads.get(this.road.destJunc.getPosIncRoad(this.road))._queue.add(this);
						}
						this.atJunction = true;
						this.currSpeed = 0;
					}
					
				} else {
					this.currSpeed = 0;
				}
			}
		}
	}
	
	public Road getRoad() {
		return this.road;
	}
	
	public int getMaxSpeed() {
		return this.maxSpeed;
	}
	
	public int getSpeed() {
		return this.currSpeed;
	}
	
	public int getLocation() {
		return this.location;
	}
	
	public int getKilometrage() {
		return this.kilometrage;
	}
	
	public int getFaulyTime() {
		return this.faultyTime;
	}
	
	public boolean atDestination() {
		return this.arrived;
	}
	
	public List<Junction> getItinerary(){
		return this.itinerary;
	}
	
	public void makeFaulty(int i) {
		if (this.road != null) {
			if (this.faultyTime == 0) {
				this.faultyTime = i;
			} else {
				this.faultyTime += i;
			}
			this.currSpeed = 0;
		}
	}
	
	public void setLocation(int j) {
		this.location = j;
	}
	
	public void setSpeed(int j) {
		if(j < 0) {
			this.currSpeed = 0;
		}
		else if(j <= this.maxSpeed) {
			this.currSpeed = j;
		}
		else {
			this.currSpeed = this.maxSpeed;
		}
	}
	
	public void moveToNextRoad() throws SimulatorError {
		int pos = 0;
		boolean ok = false;
		while(pos < this.itinerary.size() && !ok) {
			if(this.road.destJunc.equals(this.itinerary.get(pos))) {
				ok = true;
			}
			else {
				pos++;
			}
		}
		if(ok == false) {
			this.currSpeed = 0;
		}
		else {
			if(pos == (this.itinerary.size() - 1)) {///////////////
				this.arrived = true;
				this.location = 0;
				this.currSpeed = 0;
				this.atJunction = false;
				this.road.destJunc.queue.remove(this);
				this.road.vehicles.remove(this);
				this.road.destJunc.roads.get(this.road.destJunc.getPosIncRoad(this.road))._queue.remove(this);////////////////
				
			}
			else {
				this.location = 0;
				this.currSpeed = 0;
				this.atJunction = false;
				this.road.destJunc.queue.remove(this);
				this.road.vehicles.remove(this);
				this.road.destJunc.roads.get(this.road.destJunc.getPosIncRoad(this.road))._queue.remove(this);
				this.road = this.road.destJunc.roadTo(this.itinerary.get(pos + 1), this.itinerary.get(pos));
				if(this.road != null) {
					this.road.vehicles.add(this);
				}
			}
		}
	}
}
