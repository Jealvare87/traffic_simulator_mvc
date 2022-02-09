package es.ucm.fdi.control;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;
import es.ucm.fdi.model.NewJunctionEvent;

public class NewJunctionEventBuilder extends EventBuilder{

	public NewJunctionEventBuilder() {
		String [] k = {"id", "time"};
		keys = k;
		String[] x = {"", ""};
		defaultValues = x;		
	}
	
	public Event parse(IniSection section) {
		 if (!section.getTag().equals("new_junction")) {
			 return null;
		 }
		 return new NewJunctionEvent(EventBuilder.parseNonNegInt(section, "time", 0), EventBuilder.validId(section, "id"));
	}
	public String toString(){
		return "New Junction";
	}
}
