package es.ucm.fdi.control;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;
import es.ucm.fdi.model.MakeVehicleFaultyEvent;

public class MakeVehicleFaultyEventBuilder extends EventBuilder{

	public MakeVehicleFaultyEventBuilder() {
		tag = "make_vehicle_faulty";
		String [] k = {"time","vehicles", "duration"};
		String [] x = {"", "", ""};
		keys = k;
		defaultValues = x;
	}
	
	public Event parse(IniSection section){

		 if (!section.getTag().equals("make_vehicle_faulty")) {
			 return null;
		 }
		 return new MakeVehicleFaultyEvent(EventBuilder.parseNonNegInt(section, "time", 0), EventBuilder.validId(section, "vehicles"), EventBuilder.parseInt(section, "duration", 0));
	}
	
	public String toString(){
		return "Make Vehicle Faulty";
	}
}
