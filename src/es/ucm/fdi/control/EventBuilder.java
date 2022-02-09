package es.ucm.fdi.control;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;

public abstract class EventBuilder {

	protected String tag;
	protected String[] keys;
	protected String[] defaultValues;
	
	protected EventBuilder() {
		
	}
	
	protected static String validId(IniSection section, String key) {
		String s = section.getValue(key);
		if (!isValid(s))
		throw new IllegalArgumentException("El valor " + s + " para " + key + " no es un ID valido");
		else return s;
	}
	
	protected static String[] validItinerary(IniSection section, String[] key) {
		String aux = "";
		String [] s = {};
		int contador = 0;
		
		for(int i = 0; i < key.length; i++) {
			if(contador == 0) {
				aux += key[i];
				contador++;
			}
			else {
				aux += "," + key[i];
			}
		}
		
		
		if(section.getValue("itinerary").equals(aux)) {
			s = key;
		}
		
		if (!isValid(s))
		throw new IllegalArgumentException("El valor " + s + " para " + key + " no es un ID valido");
		else return null;
	}
	
	private static boolean isValid(String[] s) {
		boolean ok = true;
		for (int i = 0; i < s.length; i++) {
			if (s[i] == null || !s[i].matches("[a-z0-9_]+")) {
				ok = false;
			}
		}
		return ok;
	}

	private static boolean isValid(String id) {
		return id != null && id.matches("[a-z0-9_]+");
	}
	
	protected static int parseInt(IniSection section, String key, int defaultValue) {
		String v = section.getValue(key);
		return (v != null) ? Integer.parseInt(section.getValue(key)) : defaultValue;
	}
	
	protected static int parseNonNegInt(IniSection section, String key, int defaultValue) {
		int i = EventBuilder.parseInt(section, key, defaultValue);
		if (i < 0)
			throw new IllegalArgumentException("El valor " + i + " para " + key + " no es un ID valido");
		else return i;
	}
	
	protected static double parseNonNegDouble(IniSection section, String key, double defaultValue) {
		double i = EventBuilder.parseDouble(section, key, defaultValue);
		if (i < 0.0)
			throw new IllegalArgumentException("El valor " + i + " para " + key + " no es un ID valido");
		else return i;
	}
	
	protected static double parseDouble(IniSection section, String key, double defaultValue) {
		String v = section.getValue(key);
		return (v != null) ? Double.parseDouble(section.getValue(key)) : defaultValue;
	}
	
	public abstract Event parse(IniSection is);
	
	public String template(){
		return null;
	}
}
