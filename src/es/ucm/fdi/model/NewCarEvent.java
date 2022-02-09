package es.ucm.fdi.model;

import java.util.List;
import java.util.Random;

public class NewCarEvent extends NewVehicleEvent{
	protected int last_faulty_km;
	protected int resistance;
	protected int maxFaultDuration; 
	protected double probability; 
	protected Random random;
	protected String type;

	public NewCarEvent(int time, String id, int max_speed, String[] itinerary, String type, int resistance, double faultProbab, int maxFaultDur, long seed) {
		super(time, id, max_speed, itinerary);
		this.type = type;
		this.resistance = resistance;
		this.probability = faultProbab;
		this.maxFaultDuration = maxFaultDur;
		this.random = new Random(seed);
	}
	
	@Override
	public void execute(RoadMap map, int time) throws SimulatorError {
		if(super.getScheduledTime() == time) {
			List<Junction> junctions = super.parseListOfJunctions(map, this.itinerary);
			if(junctions != null && junctions.size() >= 2) {
				if(super.checkIfVehicleExists(map, this.id) == null) {					
					Road x = this.getRoad(map, junctions);		
					map.addVehicle(new Car(this.id, this.maxSpeed, this.maxFaultDuration, this.resistance, this.probability, this.random, junctions, x, this.type));	
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
		return "New Car " + this.id;
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
