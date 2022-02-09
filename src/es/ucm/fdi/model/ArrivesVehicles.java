package es.ucm.fdi.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.model.TrafficSimulator.TrafficSimulatorObserver;

public class ArrivesVehicles implements TrafficSimulatorObserver{
	
	private Map<String, Integer> arrived;
	
	public ArrivesVehicles(){
		arrived = new HashMap<String, Integer>();
	}
	
	public Map<String, Integer> getArrived(){
		return this.arrived;
	}
	

	@Override
	public void registered(int time, RoadMap map, List<Event> events) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset(int time, RoadMap map, List<Event> events) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventAdded(int time, RoadMap map, List<Event> events) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void advanced(int time, RoadMap map, List<Event> events) {
		for(Vehicle x: map.getVehicles()){
			if(x.atDestination() && !this.arrived.containsKey(x.getId())){
				this.arrived.put(x.getId(), time);
			}
		}
	}

	@Override
	public void simulatorError(int time, RoadMap map, List<Event> events,
			SimulatorError e) {
		// TODO Auto-generated method stub
		
	}
}
