package es.ucm.fdi.control;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;
import es.ucm.fdi.model.NewCarEvent;

public class NewCarEventBuilder extends EventBuilder{

	public NewCarEventBuilder() {
		String [] k = {"time","id", "max_speed", "itinerary", "type", "resistance", "fault_probability", "max_fault_duration", "seed"};
		String [] x = {"", "", "", "", "", "", "", "", ""};
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
 		 return new NewCarEvent(EventBuilder.parseNonNegInt(section, "time", 0), EventBuilder.validId(section, "id"), Integer.parseInt(section.getValue("max_speed")), words, 
 				EventBuilder.validId(section, "type"), Integer.parseInt(section.getValue("resistance")), Double.parseDouble(section.getValue("fault_probability")), 
 						Integer.parseInt(section.getValue("max_fault_duration")), Long.parseLong(section.getValue("seed")));
		
	}
	
	public String toString(){
		return "New Vehicle";
	}
}
