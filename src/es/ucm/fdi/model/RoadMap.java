package es.ucm.fdi.model;

import java.util.ArrayList;
import java.util.List;

public class RoadMap {
	
	private List<Vehicle> vehicles; // lista de vehículos
	private List<Road> roads; // lista de carreteras
	private List<Junction> junction; // lista de junction
	
	RoadMap() {
		this.vehicles = new ArrayList<Vehicle>();
		this.roads = new ArrayList<Road>();
		this.junction = new ArrayList<Junction>();
	}
	
	public Vehicle getVehicle(String x) {
	int contador = 0;
		while(contador < this.vehicles.size() && !this.vehicles.get(contador).id.equals(x)) {
			contador++;
		}
		return this.vehicles.get(contador);
	}
	
	public Road getRoad(String x) {
		int contador = 0;
		boolean ok = false;
		while(contador < this.roads.size() && ok == false) {
			if(this.roads.get(contador).id.equals(x)) {
				ok = true;
			}
			else {
				contador++;
			}
		}
		if(ok == true) {
			return this.roads.get(contador);
		}
		else {
			return null;
		}
			
	}
	
	public Road getRoadInt(int x) {
		return this.roads.get(x);
	}
	
	public SimulatedObject getSimulatedObject(String x) {
		return null;
	}
		
	public Junction getJunction(String x) {
		int contador = 0;
		boolean ok = false;
		while(contador < this.junction.size() && ok == false) {
			if(this.junction.get(contador).id.equals(x)) {
				ok = true;
			}
			else {
				contador++;
			}
		}
		if(ok == true) {
			return this.junction.get(contador);
		}
		else {
			return null;
		}
	}
	
	public List<Vehicle> getVehicles(){
		return this.vehicles;
	}
	
	public List<Road> getRoads(){
		return this.roads;
	}
	
	public List<Junction> getJunctions(){
		return this.junction;
	}
	
	void addJunction(Junction j) throws SimulatorError {
		if(!this.getJunctions().contains(j)) {
			this.junction.add(j);
		}
		else {
			throw new SimulatorError("Solo se ejecuta una vez por Cruce. Cuando se procesa su evento");
		}
	}
	
	void addVehicle(Vehicle v) throws SimulatorError {
		if(!this.getVehicles().contains(v)) {
			this.vehicles.add(v);
		}
		else {
			throw new SimulatorError("Solo se ejecuta una vez por vehículo. Cuando se procesa el evento");
		}
	}
	
	void addRoad(Road r) throws SimulatorError {
		if(!this.getRoads().contains(r)) {
			this.roads.add(r);
		}
		else {
			throw new SimulatorError("Solo se ejecuta una vez por carretera. Cuando se procesa el evento");
		}
	}
	
	protected void clear() {
		this.vehicles.clear();
		this.junction.clear();
		this.roads.clear();
	}
	
	public String generateReport(int time) {
		String palabra = "";
		
		for(Junction j: this.junction) {
			palabra += j.generateReport(time) + "\n";
		}
		
		for(Road r: this.roads) {
			palabra += r.generateReport(time) + "\n";
		}
		
		for(Vehicle v: this.vehicles) {
			palabra += v.generateReport(time) + "\n";
		}
		return palabra;
	}
}
