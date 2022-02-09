package es.ucm.fdi.model;

import java.util.List;
import java.util.Random;

public class NewBikeEvent extends NewVehicleEvent {

	protected int last_faulty_km;
	protected Random random;
	protected String type;

	public NewBikeEvent(int time, String id, String[] itinerary, int max_speed, String type) {
		super(time, id, max_speed, itinerary);
		this.type = type;
		this.random = new Random(System.currentTimeMillis());
	}
	
	@Override
	public void execute(RoadMap map, int time) throws SimulatorError {
		if(super.getScheduledTime() == time) {
			List<Junction> junctions = super.parseListOfJunctions(map, this.itinerary);
			if(junctions != null && junctions.size() >= 2) {
				if(super.checkIfVehicleExists(map, this.id) == null) {					
					Road x = this.getRoad(map, junctions);		
					map.addVehicle(new Bike(this.id, this.maxSpeed, this.random, junctions, x, this.type));	
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
		return "New Bike " + this.id;
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
