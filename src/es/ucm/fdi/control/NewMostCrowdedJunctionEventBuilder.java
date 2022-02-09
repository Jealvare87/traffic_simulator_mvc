package es.ucm.fdi.control;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;
import es.ucm.fdi.model.NewMostCrowdedJunctionEvent;

public class NewMostCrowdedJunctionEventBuilder extends EventBuilder{

	public NewMostCrowdedJunctionEventBuilder() {
		String [] k = {"id", "time", "type"};
		keys = k;
		String[] x = {"", "", ""};
		defaultValues = x;	
	}
	
	public Event parse(IniSection section){
		 if (!section.getTag().equals("new_junction")) {
			 return null;
		 }
		 return new NewMostCrowdedJunctionEvent(EventBuilder.parseNonNegInt(section, "time", 0), EventBuilder.validId(section, "id"), EventBuilder.validId(section, "type"));
		
	}
	
	public String toString(){
		return "New Junction";
	}
}
