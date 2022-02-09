package es.ucm.fdi.model;

public class MakeVehicleFaultyEvent extends Event{

	protected String vehicleId;
	protected int duration;
	
	
	public MakeVehicleFaultyEvent(int time, String vehicleId, int duration) {
		super(time);
		this.duration = duration;
		this.vehicleId = vehicleId;		
	}

	public void execute(RoadMap rm, int time) {
		if(super.getScheduledTime() == time) {
			rm.getVehicle(this.vehicleId).makeFaulty(this.duration);
		}
	}
	
	public String toString() {
		return "Vehicle faulty " + this.vehicleId;
	}

}
