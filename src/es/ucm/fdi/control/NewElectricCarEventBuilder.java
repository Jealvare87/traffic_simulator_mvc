package es.ucm.fdi.control;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;
import es.ucm.fdi.model.NewElectricCarEvent;

public class NewElectricCarEventBuilder extends EventBuilder {

	public NewElectricCarEventBuilder(){
		String [] k = {"time","id", "itinerary", "max_speed", "type", "capacity"};
		String [] x = {"", "", "", "", "", ""};
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
 		 return new NewElectricCarEvent(EventBuilder.parseNonNegInt(section, "time", 0), EventBuilder.validId(section, "id"),  words, Integer.parseInt(section.getValue("max_speed")), 
 				EventBuilder.validId(section, "type"), EventBuilder.parseInt(section, "capacity", 0));
		
	}
	
	public String toString(){
		return "New Electric Car";
	}
}
