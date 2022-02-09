package es.ucm.fdi.control;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;
import es.ucm.fdi.model.NewRoundRobinJunctionEvent;

public class NewRoundRobinJunctionEventBuilder extends EventBuilder{

	public NewRoundRobinJunctionEventBuilder() {
		String [] k = {"id", "time", "max_time_slice", "min_time_slice"};
		keys = k;
		String[] x = {"", "", "", ""};
		defaultValues = x;	
	}
	
	public Event parse(IniSection section){
		 if (!section.getTag().equals("new_junction")) {
			 return null;
		 }
		 return new NewRoundRobinJunctionEvent(EventBuilder.parseNonNegInt(section, "time", 0), EventBuilder.validId(section, "id"), Integer.parseInt(section.getValue("max_time_slice")), 
				 Integer.parseInt(section.getValue("min_time_slice")));
	}
	
	public String toString(){
		return "New Junction";
	}
}
