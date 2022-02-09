package es.ucm.fdi.model;

public class NewJunctionEvent extends Event{
	protected String id;
	
	public NewJunctionEvent(int time, String id) {
	 super(time);
	 this.id = id;
	 }
	
	@Override
	public void execute(RoadMap map, int time) throws SimulatorError {
		if(super.getScheduledTime() == time) {
			if(!map.getJunctions().contains(new Junction(this.id))) {
					map.addJunction(new Junction(this.id));
			}
			else {
				throw new SimulatorError("Ya esta contenido");
			}
		}
	 }
	@Override
	public String toString() {
		return "New Junction " + this.id;
	}
}
