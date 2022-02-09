package es.ucm.fdi.model;

import java.util.List;

import es.ucm.fdi.ini.IniSection;

public class RoundRobinJunction extends JunctionWithTimeSlice{

	protected int maxTimeSlice;
	protected int minTimeSlice;
	protected String type;
	protected int timeUnits;
	
	public RoundRobinJunction(String id, int maxTimeSlice, int minTimeSlice) {
		super(id);
		this.maxTimeSlice = maxTimeSlice;
		this.minTimeSlice = minTimeSlice;
		this.type = "rr";
		this.timeUnits = 0;
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
		boolean ok = false;
		
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
		/////////////////////////////////////////////////////
		
		if(pos == -1 && this.roads.size() > 0) {
			this.roads.get(0).setGreen(true);
			((IncomingRoadWithTimeSlice) this.roads.get(0))._timeSlice = this.maxTimeSlice;
		}
		else if(pos != -1){
			if(((IncomingRoadWithTimeSlice) this.roads.get(pos))._usedTimeUnits == ((IncomingRoadWithTimeSlice) this.roads.get(pos))._timeSlice) {
				this.roads.get(pos).setGreen(false);
				if(((IncomingRoadWithTimeSlice) this.roads.get(pos))._fullyUsed == true) {
					if((((IncomingRoadWithTimeSlice) this.roads.get(pos))._timeSlice + 1) < this.maxTimeSlice) {
						((IncomingRoadWithTimeSlice) this.roads.get(pos))._timeSlice += 1;
					}
					else {
						((IncomingRoadWithTimeSlice) this.roads.get(pos))._timeSlice = this.maxTimeSlice;
					}
				}
				((IncomingRoadWithTimeSlice) this.roads.get(pos))._usedTimeUnits = 0;
				((IncomingRoadWithTimeSlice) this.roads.get(pos)).setFullyUsed(true);
				((IncomingRoadWithTimeSlice) this.roads.get(pos)).setUsed(false);
				
				if(pos == this.roads.size() - 1) {
					this.roads.get(0).setGreen(true);
				}
				else {
					this.roads.get(pos).setGreen(true);
				}
			}
			else {
				((IncomingRoadWithTimeSlice) this.roads.get(pos))._timeSlice--;
				if(((IncomingRoadWithTimeSlice) this.roads.get(pos))._timeSlice == 0) {
					if(this.timeUnits == 2) {
						((IncomingRoadWithTimeSlice) this.roads.get(pos)).setTimeSlice(1);
					}
					else {
						this.timeUnits++;
						((IncomingRoadWithTimeSlice) this.roads.get(pos)).setTimeSlice(this.maxTimeSlice - timeUnits);
					}
				}
			}			
		}
	}

	protected int posGreenIncoSlice(List<IncomingRoadWithTimeSlice> inco) {
		int cont = 0;
		int aux = 0;
		for(int i = 0; i < inco.size(); i++) {
			if(aux < inco.get(i)._queue.size()) {
				aux = inco.get(i)._queue.size();
				cont = i;
			}
		}
		return cont;
	}
	
	
	@Override
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
		is.setValue("type", "rr");
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
			if(ok == true &&!this.roads.get(pos)._queue.isEmpty()) {
				this.roads.get(pos).advanceFirstVehicle();
			}
			this.switchLights();
		}
	}
	
	 public String toStrings() {
		  return "Round Robin Junction";
	}
}
