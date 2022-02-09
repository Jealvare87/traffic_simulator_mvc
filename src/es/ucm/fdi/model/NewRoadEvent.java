package es.ucm.fdi.model;

public class NewRoadEvent extends Event{

	protected String id;
	protected int max_speed;
	protected int kilometrage;
	protected String src;
	protected String dest;
	
	public NewRoadEvent(int time, String id, String src, String dest, int maxSpeed, int kilometrage) {
		 super(time);
		 this.id = id;
		 this.src = src;
		 this.dest = dest;
		 this.max_speed = maxSpeed;
		 this.kilometrage = kilometrage;
	 }
	

	@Override
	public void execute(RoadMap map, int time) throws SimulatorError {
		if (super.getScheduledTime() == time) {
			if(super.checkIfRoadExists(map, this.id) == null) {	
				map.addRoad(new Road(this.id, this.kilometrage, this.max_speed, map.getJunction(this.src),
						map.getJunction(this.dest)));
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
	@Override
	public String toString() {
		return "New Road " + this.id;
	 }

}
