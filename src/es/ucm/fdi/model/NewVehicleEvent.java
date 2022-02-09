package es.ucm.fdi.model;

import java.util.List;

public class NewVehicleEvent extends Event{
	protected String id;
	protected Integer maxSpeed;
	protected String[] itinerary;
	
	
	public NewVehicleEvent(int time, String id, int maxSpeed, String[] itinerary) {
		super(time);
		this.id = id;
		this.maxSpeed = maxSpeed;
		this.itinerary = itinerary;
	 }
	
	@Override
	public void execute(RoadMap map, int time) throws SimulatorError {
		if(super.getScheduledTime() == time) {
			List<Junction> junctions = super.parseListOfJunctions(map, this.itinerary);
			if(junctions != null && junctions.size() >= 2) {
				if(super.checkIfVehicleExists(map, this.id) == null) {					
					Road x = this.getRoad(map, junctions);		
					map.addVehicle(new Vehicle(this.id, this.maxSpeed, junctions, x));	
					x.enter(map.getVehicle(this.id));
	 				map.getRoads().get(map.getRoads().indexOf(x)).enter(map.getVehicle(this.id)); 
				}
			}
			else {
				throw new SimulatorError("Tiene menos de dos cruces");
			}
		}
	}
	@Override
	public String toString() {
		return "New Vehicle " + this.id;
	}
	
	private Road getRoad(RoadMap map, List<Junction> j) {
		boolean found = false;
		Road r = null;
		int c = 0;
		while(c < map.getRoads().size() && found == false) {
			if(map.getRoadInt(c).srcJunc.id.equals(j.get(0).id)) {
				r = map.getRoadInt(c);
				found = true;
			}
			else {
				c++;
			}
		}
		return r;
	}
}
