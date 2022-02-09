package es.ucm.fdi.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import es.ucm.fdi.launcher.SortedArrayList;

public class TrafficSimulator {


	private RoadMap map; // estructura que almacena los objetos simulados 
	private int time; // contador de pasos del simulador 
	private OutputStream outStream; // flujo de salida utilizado
	private List<Event> events;
	private List<TrafficSimulatorObserver> observers;

	
	public TrafficSimulator(OutputStream o) {
		this.map = new RoadMap();
		this.time = 0;
		Comparator<Event> cmp = new Comparator<Event>() {

			@Override
			public int compare(Event arg0, Event arg1) {
				int r;
				r = arg0.compareTo(arg1);
				
				return r;
			}};
		this.events = new SortedArrayList<>(cmp);
		this.outStream = o;
		this.observers = new ArrayList<TrafficSimulatorObserver>();
	}
	
	public void run(int ticks) throws IOException, SimulatorError {
		int limit = 0;
		limit = this.time + ticks - 1;
		while(time <= limit) {
			//
			for(Event e : this.events) {
				e.execute(this.map, this.time);
			}
			
			if (time == 0) {
				for (int i = 0; i < map.getJunctions().size(); i++) {
					if (!map.getJunctions().get(i).roads.isEmpty()) {
						for (int j = 0; j < map.getJunctions().get(i).roads.size(); j++) {
							if (map.getJunctions().get(i).toStrings() != "Most Crowded Junction"
									&&  map.getJunctions().get(i).toStrings() != "Round Robin Junction") {
								map.getJunctions().get(i).roads.get(0).setGreen(true);
							}
						}
					}
				}
			}
			
			//
			
			this.updateRoads();
			
			//
			
			this.updateJunction();
			
			//
			
			this.time++;

			//
			this.printReports();
			this.notifyAdvanced();
		}
	}
	
	public void addEvent(Event e) {
		this.events.add(e);
		this.notifyEventAdded();
	}
	
	public RoadMap getMap() {
		return this.map;
	}
	
	public void reset() {
		this.events.clear();
		this.map.clear();
		this.time = 0;
		this.notifyReset();
	}
	
	public void setOutputStream(OutputStream o) {
		this.outStream = o;
	}
	
	public String toString() {
		return "Traffic simulator";
	}
	
	private void updateRoads() throws SimulatorError {
		for(Road road: this.map.getRoads()) {
			road.advance(this.time);
			this.notifyAdvanced();
		}
	}
	
	public List<Event> getEvents(){
		return this.events;
	}
	
	private void updateJunction() throws SimulatorError {
		for(Junction j: this.map.getJunctions()) {
			j.advance(this.time);
			this.notifyAdvanced();
		}
	}
	
	private void printReports() throws IOException {		
		this.outStream.write(map.generateReport(this.time).getBytes());
	}
	
	public void printearReports() throws IOException {
		this.printReports();
	}
	
	///////////////
	public interface TrafficSimulatorObserver {

		public void registered(int time, RoadMap map, List<Event> events);
		
		public void reset(int time, RoadMap map, List<Event> events);
		
		public void eventAdded(int time, RoadMap map, List<Event> events);
		
		public void advanced(int time, RoadMap map, List<Event> events);
		
		public void simulatorError(int time, RoadMap map, List<Event> events, SimulatorError e);
	
	}
	///////////////
	private void notifyRegistered(TrafficSimulatorObserver o) {
		for(TrafficSimulatorObserver e: this.observers) {
			e.registered(this.time, this.map, this.events);
		}
	}
	
	private void notifyReset() {
		for(TrafficSimulatorObserver e: this.observers) {
			e.reset(this.time, this.map, this.events);
		}
	}
	
	private void notifyEventAdded() {
		for(TrafficSimulatorObserver e: this.observers) {
			e.eventAdded(this.time, this.map, this.events);
		}
	}
	
	private void notifyAdvanced() {
		for(TrafficSimulatorObserver e: this.observers) {
			e.advanced(this.time, this.map, this.events);
		}
	}
	
	private void notifyError(SimulatorError err) {
		for(TrafficSimulatorObserver e: this.observers) {
			e.simulatorError(time, map, events, err);
		}
	}
	
	public void notificaError(SimulatorError e) {
		this.notifyError(e);
	}
	
	public void addObserver(TrafficSimulatorObserver o) {
		this.observers.add(o);
		this.notifyRegistered(o);
	}
	
	public void removeObserver(TrafficSimulatorObserver o) {
		this.observers.remove(o);
	}
}
