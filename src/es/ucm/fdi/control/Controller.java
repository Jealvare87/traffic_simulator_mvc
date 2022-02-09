package es.ucm.fdi.control;

import java.io.*;
import java.util.List;

import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;
import es.ucm.fdi.model.SimulatorError;
import es.ucm.fdi.model.TrafficSimulator;

public class Controller {
	
	protected TrafficSimulator sim; // simulador que usa
	protected OutputStream outputStream; // flujo de salida
	protected InputStream inputStream; // flujo de entrada
	protected int ticks; // pasos de la simulación dados por el usuario
	EventBuilder[] eventBuilders = {}; // array de constructores de	eventos
	
	public EventBuilder[] initializeEB() {
		EventBuilder[] b = { new NewRoadEventBuilder(), new NewVehicleEventBuilder(), new NewJunctionEventBuilder(), new MakeVehicleFaultyEventBuilder(), 
				new NewCarEventBuilder(), new NewBikeEventBuilder()}; 
		return b;
	}
	
	public Controller(TrafficSimulator ts, int x, InputStream is, OutputStream os) {
		this.sim = ts;
		this.ticks = x;
		this.inputStream = is;
		this.outputStream = os;
		this.eventBuilders = this.initializeEB();
	}
	
	public void setEventBuilders(EventBuilder[] events){
		this.eventBuilders = events;
	}
	
	public EventBuilder[] getEventBuilders(){
		return this.eventBuilders;
	}
	
	public void run() throws IOException, SimulatorError{
		this.loadEvents(this.inputStream);
		this.run(this.ticks);
	}
	
	public void run(int x) throws IOException, SimulatorError{
		sim.run(x);
	}
	
	public void reset(){
		sim.reset();
	}
	
	public void setOutputStream(OutputStream os){
		sim.setOutputStream(os);
	}
	
	public void loadEvents(InputStream is) throws IOException, SimulatorError{		
		Ini ini = null;
		try {
			ini = new Ini(this.inputStream);
		}
		catch (IOException e) {
			sim.notificaError(new SimulatorError("Error en la lectura de eventos: " + e));
			//throw new SimulatorError("Error en la lectura de eventos: " + e);
		}
		for (IniSection sec : ini.getSections()) {
			Event e = this.parseEvent(sec);
		if (e != null) this.sim.addEvent(e);
		else
			sim.notificaError(new SimulatorError("Evento desconocido: " + sec.getTag()));
			//throw new SimulatorError("Evento desconocido: " + sec.getTag());
		}
	}
	
	public void loadPop(List<Event> events) {
		for(int i = 0; i < events.size(); i++) {
			this.sim.addEvent(events.get(i));
		}
	}
	
	public Event parseEvent(IniSection is){
		Event evento = null;
		EventBuilder aux = null;
				
		 switch(is.getTag()) {
			case "new_vehicle":
				if(is.getValue("type") == null) {
					aux = new NewVehicleEventBuilder();
					evento = aux.parse(is);
				}
				else if(is.getValue("type").equals("car")) {
					aux = new NewCarEventBuilder();
					evento = aux.parse(is);
				}
				else if(is.getValue("type").equals("bike")) {
					aux = new NewBikeEventBuilder();
					evento = aux.parse(is);
				}
				else if(is.getValue("type").equals("electric")) {
					aux = new NewElectricCarEventBuilder();
					evento = aux.parse(is);
				}
				
				break;
			case "new_junction":
				if(is.getValue("type") == null) {
					aux = new NewJunctionEventBuilder();
					evento = aux.parse(is);
				}
				else if(is.getValue("type").equals("rr")) {
					aux = new NewRoundRobinJunctionEventBuilder();
					evento = aux.parse(is);
				}
				else if(is.getValue("type").equals("mc")) {
					aux = new NewMostCrowdedJunctionEventBuilder();
					evento = aux.parse(is);
				}
				break;
			case "new_road":
				if(is.getValue("type") == null) {
					aux = new NewRoadEventBuilder();
					evento = aux.parse(is);
				}
				else if(is.getValue("type").equals("lanes")) {
					aux = new NewLanesRoadEventBuilder();
					evento = aux.parse(is);
				}
				else if(is.getValue("type").equals("dirt")) {
					aux = new NewDirtRoadEventBuilder();
					evento = aux.parse(is);
				}
				break;
			case "make_vehicle_faulty":
				aux = new MakeVehicleFaultyEventBuilder();
				evento = aux.parse(is);
				break;
			default:
				evento = null;
		}
		
		return evento;
	}
	
	public String[] getItinerary(IniSection ini) {
		String linea;
		String [] words;
		linea = ini.getValue("itinerary");
		words =linea.trim().split("[, ]+");
		return words;
	}
	
	public TrafficSimulator getTraffic() {
		return this.sim;
	}
}
