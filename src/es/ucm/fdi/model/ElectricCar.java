package es.ucm.fdi.model;

import java.util.List;
import java.util.Random;

import es.ucm.fdi.ini.IniSection;

public class ElectricCar extends Vehicle {
	protected Random random;
	protected int capacity;
	protected int charging;
	protected int battery;

	public ElectricCar(String x, int i, Random random, List<Junction> list, Road r, String type, int capacity) {
		super(x, i, list, r);
		this.random = random;
		this.capacity = capacity;
		super.type = type;
		this.charging = 0;
		this.battery = capacity;
		this.faultyTime = 0;
	}
	
	protected void advance(int i) throws SimulatorError {
		int oldKm = 0;
		int newKm = 0;
		int aux = 0;
		int recarg = 0;
		
		if(super.faultyTime == 0) {
			oldKm = super.getKilometrage();
			super.advance(i);
			newKm = super.getKilometrage();
			aux = this.battery;
			aux -= newKm + oldKm;	// Si al restarle los kilometros avanzados nos quedamos sin bateria actúa de qué manera.
			if(aux > 0){
				this.battery -= newKm + oldKm;
			}
			else{
				this.battery = 0;
				this.setSpeed(0);
				this.faultyTime = this.getRandomValue();
			}
		}
		else {
			this.currSpeed = 0;
			this.faultyTime--;
			recarg = this.battery + 10;
			if(recarg > this.capacity){
				this.battery = this.capacity;
				this.faultyTime = 0;
				this.charging = 0;
			}else{
				int aux3 = this.capacity - this.battery;
				this.charging = aux3/10;
				this.battery += 10;
			}
		}
	}
	
	protected String getReportSectionTag() {
		return "vehicle_report";
	}
	
	private int getRandomValue(){
		int tiempo = 0;
		int suma = (this.capacity / 10) + 1;
		int ran = this.random.nextInt(suma);
		
		while(ran < 1 && ran > suma){
			ran = this.random.nextInt(suma);
		}
		tiempo = ran;
		return tiempo;
	}
	
	protected void fillReportDetails(IniSection section) {
		section.setValue("speed", this.currSpeed);
		section.setValue("kilometrage", this.kilometrage);
		section.setValue("faulty", this.faultyTime);
		section.setValue("location", this.arrived ? "arrived" : "(" + super.getRoad() + "," + super.getLocation() + ")");
		section.setValue("type", this.type);
		section.setValue("battery", this.battery);
		section.setValue("charging", this.charging);
	}

}
