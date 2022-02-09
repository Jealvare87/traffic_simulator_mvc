package es.ucm.fdi.model;

import java.util.ArrayList;
import java.util.List;

public abstract class JunctionWithTimeSlice extends Junction {

	//protected List<IncomingRoadWithTimeSlice> roadsSlice;
	
	public JunctionWithTimeSlice(String x) {
		super(x);
		super.green = false;
		//this.roadsSlice = new ArrayList<IncomingRoadWithTimeSlice>();
	}
	
	public List<Vehicle> getQueue(){
		return this.queue;
	}
	@Override
	public abstract String toStrings();

	public class IncomingRoadWithTimeSlice extends IncomingRoad{
		protected int _timeSlice;
		protected int _usedTimeUnits;
		protected boolean _fullyUsed;
		protected boolean _used;
		public IncomingRoadWithTimeSlice(Road road) {
			super(road);
			super.green = false;
			super._queue = new ArrayList<Vehicle>();
			this._timeSlice = 0;
			this._usedTimeUnits = 0;	
			this._fullyUsed = false;
			this._used = false;
		}
		
		public void setGreen(boolean b) {
			this.green = b;
		}

		public String printQuque() {
			String palabra = "";
			int contador = 0;
			for(Vehicle x: this._queue) {
				if(contador == 0) {
					palabra +=  x.getId();
					contador++;
				}
				else {
					palabra += ","+ x.getId();
				}
			}
			return palabra;
		}

		public Road getRoad() {
			return this.road;
		}
		
		public int getTimeSlice() {
			return this._timeSlice;
		}
		
		protected void setTimeSlice(int timeSlice) {
			this._timeSlice = timeSlice;
		}

		public int getUsedTimeUnits() {
			return this._usedTimeUnits;
		}
		
		protected void setUsedTimeUnits(int usedTimeUnits) {
			this._usedTimeUnits = usedTimeUnits;
		}
		
		public boolean isFullyUsed() {
			return this._fullyUsed;
		}
		
		protected void setFullyUsed(boolean fullyUsed) {
			this._fullyUsed = fullyUsed;
		}
		
		public boolean isUsed() {
			return this._used;
		}
		
		protected void setUsed(boolean used) {
			this._used = used;
		}
		
		public String toString() {
			String palabra = "";
			int contador = 0;
			for(Vehicle x: this._queue) {
				if(contador == 0) {
					palabra +=  x.getId();
					contador++;
				}
				else {
					palabra += ","+ x.getId();
				}
			}	
			return palabra;
		}
	}
}
