package es.ucm.fdi.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Event {
	protected Integer time; // tiempo de ejecución del evento
	
	public Event(Integer x) {
		this.time = x;
	}
	
	public int getScheduledTime() {
		return this.time;
	}
	
	public int compareTo(Event e) {
		if(this.time < e.time) {
			return -1;
		}
		else if(this.time == e.time) {
			return 0;
		}
		else {
			return 1;
		}
	}
	
	protected Junction checkIfJunctionExists(RoadMap rm, String x) {
		Junction junc = null;
		for(Junction j : rm.getJunctions()) {
			if(j.id.equals(x)) {
				junc = rm.getJunction(x);
			}
		}
		return junc;
	}
	
	protected Vehicle checkIfVehicleExists(RoadMap rm, String x) {
		Vehicle c = null;
		for(Vehicle v : rm.getVehicles()) {
			if(v.id.equals(x)) {
				c = rm.getVehicle(x);
			}
		}
		return c;
	}
	
	protected Road checkIfRoadExists(RoadMap rm, String x) {
		Road junc = null;
		for(Road j : rm.getRoads()) {
			if(j.id.equals(x)) {
				junc = rm.getRoad(x);
			}
		}
		return junc;
	}
	
	protected SimulatedObject checkIfSimObjExists(RoadMap rm, String x) {
		if(rm.getSimulatedObject(x) == null) {
			return null;
		}
		else {
			return rm.getSimulatedObject(x);
		}
	}
	
	protected List<Junction> parseListOfJunctions(RoadMap rm, String[] x){
		List<Junction> j = new ArrayList<Junction>();
		for(int i = 0; i < x.length; i++) {
			if(this.checkIfJunctionExists(rm, x[i]) != null) {
			    j.add(this.checkIfJunctionExists(rm, x[i]));
			}
		}
		return j;
	}
	
	protected List<Road> parseListOfRoads(RoadMap rm, String[] x){
		List<Road> j = new ArrayList<Road>();
		
		for(int i = 0; i < x.length; i++) {
			if(this.checkIfRoadExists(rm, x[i]) != null) {
				j.add(this.checkIfRoadExists(rm, x[i]));
			}
		}
	
		return j;
	}
	
	protected List<Vehicle> parseListOfVehicles(RoadMap rm, String[] x){
		List<Vehicle> j = new ArrayList<Vehicle>();
		for(int i = 0; i < x.length; i++) {
			if(this.checkIfVehicleExists(rm, x[i]) != null) {
				j.add(this.checkIfVehicleExists(rm, x[i]));
			}
		}
		return j;
	}
	
	public abstract void execute(RoadMap rm, int x) throws SimulatorError;
}
