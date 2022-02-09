package es.ucm.fdi.model;

import java.util.List;

public class KilometrageView {
	private int time;
	private List<Vehicle> v;
	private int totalKm;
	
	public KilometrageView(List<Vehicle> vehicles, int t){
		this.time = t;
		this.v = vehicles;
		if(time != 0){
			this.totalKm = this.calculateKmTotal();
		}else{
			this.totalKm = 0;
		}
	}
	
	public int getKmTotal(){
		return this.totalKm;
	}
	
	private int calculateKmTotal(){
		int kmT = 0;
		for(Vehicle e: v){
			kmT += e.kilometrage;
		}
		return kmT;
	}
	
	public int getTime(){
		return this.time;
	}
}
