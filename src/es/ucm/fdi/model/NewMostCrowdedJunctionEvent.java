package es.ucm.fdi.model;

public class NewMostCrowdedJunctionEvent extends NewJunctionEvent{

	protected String type;
	
	public NewMostCrowdedJunctionEvent(int time, String id, String type) {
		super(time, id);
		this.type = type;
	}

	@Override
	public void execute(RoadMap map, int time) throws SimulatorError {
		if(super.getScheduledTime() == time) {
			if(!map.getJunctions().contains(new Junction(this.id))) {
					map.addJunction(new MostCrowdedJunction(this.id));
			}
			else {
				throw new SimulatorError("Ya esta contenido");
			}
		}
	 }
	@Override
	public String toString() {
		return "New Most Crowded " + this.id;
	}
}
