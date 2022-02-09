package es.ucm.fdi.model;


public class NewRoundRobinJunctionEvent extends NewJunctionEvent{

	protected int maxTimeSlice;
	protected int minTimeSlice;
	
	public NewRoundRobinJunctionEvent(int time, String id, int maxTimeSlice, int minTimeSlice) {
		super(time, id);
		this.maxTimeSlice = maxTimeSlice;
		this.minTimeSlice = minTimeSlice;		
	}
	
	@Override
	public void execute(RoadMap map, int time) throws SimulatorError {
		if(super.getScheduledTime() == time) {
			if(!map.getJunctions().contains(new Junction(this.id))) {
					map.addJunction(new RoundRobinJunction(this.id, this.maxTimeSlice, this.minTimeSlice));
			}
			else {
				throw new SimulatorError("Ya esta contenido");
			}
		}
	 }
	@Override
	public String toString() {
		return "New Round Robin " + this.id;
	}
}
