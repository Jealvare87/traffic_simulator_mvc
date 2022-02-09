package es.ucm.fdi.model;

import java.util.List;
import java.util.Random;

import es.ucm.fdi.ini.IniSection;

public class Car extends Vehicle {
	
	protected int last_faulty_km;
	protected int resistance;
	protected int maxFaultDuration; 
	protected double probability; 
	protected Random random;
	protected String type;
	
	public Car(String id, int max_speed, int maxFduration, int resistance, double probability, Random random, List<Junction> junctions, Road road, String type) {
		super(id, max_speed, junctions, road);
		this.maxFaultDuration = maxFduration;
		this.resistance = resistance;
		this.probability = probability;
		this.random = random;
		this.last_faulty_km = 0;
		this.type = type;
	}
	
	@Override
	protected void advance(int i) throws SimulatorError {
		if(super.faultyTime == 0 && ((super.getLocation() - this.last_faulty_km) > this.resistance)) {
			double ran = this.random.nextDouble();
			if(ran < this.probability) {
				int c = this.random.nextInt(this.maxFaultDuration) + 1;
				super.faultyTime = c;
				this.last_faulty_km = super.getLocation();
			}
		}
		super.advance(i);		
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
