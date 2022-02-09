package es.ucm.fdi.model;

import java.util.List;

import es.ucm.fdi.ini.IniSection;

public class MostCrowdedJunction extends JunctionWithTimeSlice {

	
	public MostCrowdedJunction(String x) {
		super(x);
	}
	
	protected IncomingRoad createIncommingRoadQueue(Road road) {
		IncomingRoad r = new IncomingRoadWithTimeSlice(road);
		this.incomingRoads.put(road.srcJunc, road);
		for(Vehicle v : road.vehicles) {
			this.roads.get(this.roads.indexOf(r)).addVehicle(v);
		}
		this.roads.add((IncomingRoadWithTimeSlice) r);
		return r;
	}
	
	@Override
	protected void switchLights() {
		int pos = 0;
		int aux = 0;
		boolean ok = false;

		// Calcula si hay alguno con el semaforo en verde
		while (pos < this.roads.size() && !ok) {
			if (this.roads.get(pos).green == true) {
				ok = true;
			} else {
				pos++;
			}
		}
		
		if(ok == false) {
			pos = -1;
		}

		if (ok == true && ((IncomingRoadWithTimeSlice) this.roads.get(pos)).getTimeSlice() <= 1) {	//Semaforo en verde pero sin tiempo
			this.roads.get(pos).setGreen(false);
			((IncomingRoadWithTimeSlice) this.roads.get(pos)).setTimeSlice(1);
			aux = pos;
			pos = posGreenIncoSlice(this.roads, aux);
			this.roads.get(pos).setGreen(true);
			// Max(n/2, 1)
			if((this.roads.get(pos)._queue.size() / 2) > 1) {
				((IncomingRoadWithTimeSlice) this.roads.get(pos)).setTimeSlice(this.roads.get(pos)._queue.size() / 2);
			}
			else {
				((IncomingRoadWithTimeSlice) this.roads.get(pos)).setTimeSlice(1);
			}
		}	
		else if(ok == false) {												// Si ninguno está en verde
			pos = posGreenIncoSlice(this.roads, -1);
			this.roads.get(pos).setGreen(true);
			if((this.roads.get(pos)._queue.size() / 2) > 0) {
				((IncomingRoadWithTimeSlice) this.roads.get(pos)).setTimeSlice(this.roads.get(pos)._queue.size() / 2);
			}
			else {
				((IncomingRoadWithTimeSlice) this.roads.get(pos)).setTimeSlice(1);
			}
		}
		else if(((IncomingRoadWithTimeSlice) this.roads.get(pos)).getTimeSlice() > 1) {																// Si está en verde pero aún le queda tiempo de espera
			((IncomingRoadWithTimeSlice) this.roads.get(pos)).setTimeSlice(((IncomingRoadWithTimeSlice) this.roads.get(pos)).getTimeSlice()-1);	
		}
	}
	
	protected int posGreenIncoSlice(List<IncomingRoad> inco, int x) {
		int cont = 0;
		int aux = 0;
		boolean ok = false;
		
		/******************************************************************
		 * 																  *
		 * Calculate the position of the incoming road with more vehicles *
		 * 																  *
		 ******************************************************************/
		
		for(int i = 0; i < inco.size(); i++) {
			if(aux <= inco.get(i)._queue.size() && x != i && ok != true) {
				if(x == -1) {
					x = i;
				}
				aux = inco.get(i)._queue.size();
				cont = i;
				ok = true;
			}
		}

		return cont;
	}
	
	protected void turnLightOff(IncomingRoadWithTimeSlice irwts) {
		irwts.green = false;
	}
	
	protected void turnLightOn(IncomingRoadWithTimeSlice irwts) {
		irwts.green = true;
	}
	
	protected void fillReportDetails(IniSection is) {
		String aux = "";
		int contador = 0;
		for (IncomingRoad r : this.roads) {
			if (contador == 0) {
				if (r.hasGreenLight()) {
					aux += "(" + r.getRoad() + ",green:"+ ((IncomingRoadWithTimeSlice) r).getTimeSlice() + ",[" + r.printQuque() + "])";
				} else {
					aux += "(" + r.getRoad() + ",red," + "[" + r.printQuque() + "])";
				}
				contador++;
			}
			else {
				if (r.hasGreenLight()) {
					aux += ",(" + r.getRoad() +"," + "green:"+ ((IncomingRoadWithTimeSlice) r).getTimeSlice() + ",[" + r.printQuque() + "])";
				} else {
					aux += ",(" + r.getRoad() + "," + "red," + "[" + r.printQuque() + "])";
				}
			}
		}
		is.setValue("queues", aux);
		is.setValue("type", "mc");
	}
	
	void advance(int time) throws SimulatorError {
		int pos = 0;
		boolean ok = false;
		
		if (this.roads != null) {
		
			while (pos < super.roads.size() && !ok) {
				if (this.roads.get(pos).green == true) {
					ok = true;
				} else {
					pos++;
				}
			}
			
			if(ok == true && !this.roads.get(pos)._queue.isEmpty()) {
				this.roads.get(pos).advanceFirstVehicle();
			}
			this.switchLights();
		}
	}
	public String toStrings() {
		  return "Most Crowded Junction";
	}
}
