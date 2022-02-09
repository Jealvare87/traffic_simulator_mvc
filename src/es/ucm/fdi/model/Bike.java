package es.ucm.fdi.model;

import java.util.List;
import java.util.Random;

import es.ucm.fdi.ini.IniSection;

public class Bike extends Vehicle {

	protected int maxFaultDuration; 
	protected double probability; 
	protected Random random;
	
	public Bike(String id, int max_speed, Random random, List<Junction> junctions, Road road, String type) {
		super(id, max_speed, junctions, road);
		this.random = random;
		super.type = type;
	}
	
	public void makeFaulty(int faulty) {
		if(this.currSpeed > this.maxSpeed/2) {
			this.faultyTime = faulty;
		}
	}
	
	protected void advance(int i) throws SimulatorError {
		if(super.faultyTime == 0) {
			double ran = this.random.nextDouble();
			if(ran < this.probability) {
				this.makeFaulty(this.random.nextInt(this.maxFaultDuration));
			}
			else {
				super.advance(i);
			}
		}
		else {
			this.currSpeed = 0;
			this.faultyTime--;
		}
	}
	
	protected String getReportSectionTag() {
		return "vehicle_report";
	}
	
	protected void fillReportDetails(IniSection section) {
		section.setValue("type", this.type);
		section.setValue("speed", this.currSpeed);
		section.setValue("kilometrage", this.kilometrage);
		section.setValue("faulty", this.faultyTime);
		section.setValue("location", this.arrived ? "arrived" : "(" + super.getRoad() + "," + super.getLocation() + ")");
	}
}
