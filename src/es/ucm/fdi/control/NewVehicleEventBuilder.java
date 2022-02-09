package es.ucm.fdi.control;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;
import es.ucm.fdi.model.NewVehicleEvent;

public class NewVehicleEventBuilder extends EventBuilder{
	
	public NewVehicleEventBuilder() {
		String [] k = {"time","id", "max_speed","itinerary"};
		String [] x = {"", "", "", ""};
		tag = "new_vehicle";
		keys = k;
		defaultValues = x;
	}
	
	public Event parse(IniSection section){
		String y;
		y= section.getValue("itinerary");
		String[] words = y.split("[, ]+");
		
		if (!section.getTag().equals("new_vehicle")) {
			 return null;
		 }
 		 return new NewVehicleEvent(EventBuilder.parseNonNegInt(section, "time", 0), EventBuilder.validId(section, "id"), Integer.parseInt(section.getValue("max_speed")), words);
	}
	
	public String toString(){
		return "New Vehicle";
	}
	
	public String[] getItinerary(IniSection ini) {
		String linea;
		String [] words;
		linea = ini.getValue("itinerary");
		words =linea.trim().split("[, ]+");
		return words;
	}
}
