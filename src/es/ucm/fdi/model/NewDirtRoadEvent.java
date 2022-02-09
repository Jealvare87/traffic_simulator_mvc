package es.ucm.fdi.model;


public class NewDirtRoadEvent extends NewRoadEvent{
	
	protected String type;
	
	public NewDirtRoadEvent(int time, String id, String src, String dest, int maxSpeed, int kilometrage, String type) {
		super(time, id, src, dest, maxSpeed, kilometrage);
		this.type = type;
	}

	public void execute(RoadMap map, int x) throws SimulatorError {
		if (super.getScheduledTime() == time) {
			if(super.checkIfRoadExists(map, this.id) == null) {	
				map.addRoad(new DirtRoad(this.id, this.kilometrage, this.max_speed, map.getJunction(this.src),
						map.getJunction(this.dest), this.type));
				boolean ok = false;
				if (map.getJunction(this.dest).exist(this.id)) {
					ok = true;
				}
				if (ok == false) {
					map.getJunction(this.dest).addIncommingRoad(map.getRoad(this.id));
				}
				map.getJunction(this.src).road = map.getRoad(this.id);
			}
		}
	}
	
	public String toString() {
		return "New Dirt Road " + this.id;
	}
}
