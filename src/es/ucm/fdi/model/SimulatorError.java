package es.ucm.fdi.model;

public class SimulatorError extends Exception{
	private static final long serialVersionUID = 1L;

	public SimulatorError(String x) {
		System.out.println(x);
	}
}
