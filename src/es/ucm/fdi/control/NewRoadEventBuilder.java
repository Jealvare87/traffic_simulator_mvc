package es.ucm.fdi.control;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;
import es.ucm.fdi.model.NewRoadEvent;

public class NewRoadEventBuilder extends EventBuilder{

	public NewRoadEventBuilder() {
		String [] k = {"id", "time", "src", "dest", "max_speed", "lenght"};
		String[] x = {"", "","","","", ""};
		tag = "new_road";
		keys = k;
		defaultValues = x;
		
	}
	
	public Event parse(IniSection section){
		 if (!section.getTag().equals("new_road")) {
			 return null;
		 }
		 return new NewRoadEvent(EventBuilder.parseNonNegInt(section, "time", 0), EventBuilder.validId(section, "id"), EventBuilder.validId(section, "src")
				 , EventBuilder.validId(section, "dest") , EventBuilder.parseNonNegInt(section, "max_speed", 0), EventBuilder.parseNonNegInt(section, "length", 0));
	}
	
	public String toString(){
		return "New Road";
	}
}
