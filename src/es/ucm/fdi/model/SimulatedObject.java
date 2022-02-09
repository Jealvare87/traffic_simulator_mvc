package es.ucm.fdi.model;

import es.ucm.fdi.ini.IniSection;


public abstract class SimulatedObject {
	public String id;
	
	public SimulatedObject(String x) {
		this.id = x;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String toString() {
		return this.id;
	}
	
	public String generateReport(int i) {
		IniSection is = new IniSection(getReportSectionTag());
		 is.setValue("id", this.id);
		 is.setValue("time", i);
		 fillReportDetails(is); 
		 return is.toString();
	}
	
	protected abstract void fillReportDetails(IniSection s);
	
	protected abstract String getReportSectionTag();
	
	abstract void advance(int x) throws SimulatorError;
	
}
