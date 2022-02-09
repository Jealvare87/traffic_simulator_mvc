package es.ucm.fdi.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.model.ArrivesVehicles;
import es.ucm.fdi.model.Junction;
import es.ucm.fdi.model.Junction.IncomingRoad;
import es.ucm.fdi.model.RoadMap;
import es.ucm.fdi.model.SimulatorError;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.Vehicle;
import es.ucm.fdi.view.MainWindow;

public class ExampleMain {

	private final static Integer _timeLimitDefaultValue = 10;
	private static Integer _timeLimit = null;
	private static String _inFile = null;
	private static String _outFile = null;
	private static boolean _batchMode;

	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseHelpOption(line, cmdLineOptions);
			parseInFileOption(line);
			parseModeOption(line);
			parseOutFileOption(line);
			parseStepsOption(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			// new Piece(...) might throw GameError exception
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message").build());
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Events input file").build());
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc("’batch’ for batch mode and ’gui’ for GUI mode").build());
		cmdLineOptions.addOption(
				Option.builder("o").longOpt("output").hasArg().desc("Output file, where reports are written.").build());
		cmdLineOptions.addOption(Option.builder("t").longOpt("ticks").hasArg()
				.desc("Ticks to execute the simulator's main loop (default value is " + _timeLimitDefaultValue + ").")
				.build());

		return cmdLineOptions;
	}
	
	private static void parseModeOption(CommandLine line) throws ParseException {
		if(line.hasOption("m")) {
			String x = line.getOptionValue("m");
			if(x.equals("batch")) {
				_batchMode = true;
			}
			else if(x.equals("gui")){
				_batchMode = false;
			}
			else {
				throw new ParseException("Bad spelling");
			}
		}
		else {
			_batchMode = false;
		}
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(ExampleMain.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
		if (_inFile == null) {
			throw new ParseException("An events file is missing");
		}
	}

	private static void parseOutFileOption(CommandLine line) throws ParseException {
		_outFile = line.getOptionValue("o");
	}

	private static void parseStepsOption(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _timeLimitDefaultValue.toString());
		try {
			_timeLimit = Integer.parseInt(t);
			assert (_timeLimit < 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time limit: " + t);
		}
	}

	/**
	 * This method run the simulator on all files that ends with .ini if the given
	 * path, and compares that output to the expected output. It assumes that for
	 * example "example.ini" the expected output is stored in "example.ini.eout".
	 * The simulator's output will be stored in "example.ini.out"
	 * 
	 * @throws IOException
	 * @throws SimulatorError 
	 */
	
	private static void test(String path) throws IOException, SimulatorError {

		File dir = new File(path);

		if ( !dir.exists() ) {
			throw new FileNotFoundException(path);
		}
		
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".ini");
			}
		});

		for (File file : files) {
			test(file.getAbsolutePath(), file.getAbsolutePath() + ".out", file.getAbsolutePath() + ".out-samir",10);
		}

	}

	private static void test(String inFile, String outFile, String expectedOutFile, int timeLimit) throws IOException, SimulatorError {
		_outFile = outFile;
		_inFile = inFile;
		_timeLimit = timeLimit;
		startBatchMode();
		boolean equalOutput = (new Ini(_outFile)).equals(new Ini(expectedOutFile));
		System.out.println("Result for: '" + _inFile + "' : "
				+ (equalOutput ? "OK!" : ("not equal to expected output +'" + expectedOutFile + "'")));
	}
	
	/**
	 * Run the simulator in batch mode
	 * 
	 * @throws IOException
	 * @throws SimulatorError 
	 */
	private static void startBatchMode() throws IOException, SimulatorError {
		InputStream is = new FileInputStream(new File(ExampleMain._inFile));
		OutputStream os = ExampleMain._outFile == null ? System.out : new FileOutputStream(new File(ExampleMain._outFile));
		TrafficSimulator sim = new TrafficSimulator(os);
		Controller control = new Controller (sim,ExampleMain._timeLimit,is,os);
		ArrivesVehicles av = new ArrivesVehicles();
		sim.addObserver(av);
		control.run();
		System.out.println(getFinalText(av, control.getTraffic().getMap()));
		is.close();
		System.out.println("Done!");

	}
	
	private static String getFinalText(ArrivesVehicles e, RoadMap map){
		String finalText = "\n[vehicles_info]\narrival = ";
		Map<String, Integer> aux = e.getArrived();
	
		for(int i = 0; i < map.getVehicles().size(); i++){
			if(i == 0){
				if(map.getVehicles().get(i).atDestination() == true){
					finalText += "(" + map.getVehicles().get(i).getId() + "," + aux.get(map.getVehicles().get(i).getId()) + ")";
				}else{
					finalText += "(" + map.getVehicles().get(i).getId() + ", NO)";
				}
			}else{
				if(map.getVehicles().get(i).atDestination() == true){
					finalText += " ,  (" + map.getVehicles().get(i).getId() + "," + aux.get(map.getVehicles().get(i).getId()) + ")";
				}else{
					finalText += " ,  (" + map.getVehicles().get(i).getId() + ", NO)";
				}
			}
		}
		
		return finalText;
	}
	
	public static void startGUIMode() throws IOException, SimulatorError, InvocationTargetException, InterruptedException{
		InputStream is = new FileInputStream(new File(ExampleMain._inFile)); 
		OutputStream os = ExampleMain._outFile == null ? System.out : new FileOutputStream(new File(ExampleMain._outFile));
		TrafficSimulator sim = new TrafficSimulator(os);
		Controller control = new Controller (sim,ExampleMain._timeLimit,is,os);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					new MainWindow(sim, ExampleMain._inFile, control);
				} catch (IOException | SimulatorError e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void start(String[] args) throws IOException, SimulatorError, InvocationTargetException, InterruptedException {
		parseArgs(args);
		if(_batchMode == true) {
			startBatchMode();
			//test("examples/advanced");
		}
		else {
			startGUIMode();
		}
	}

	public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException, SimulatorError {

		// example command lines:
		//
		// -i resources/examples/events/basic/ex1.ini
		// -i resources/examples/events/basic/ex1.ini -o ex1.out
		// -i resources/examples/events/basic/ex1.ini -t 20
		// -i resources/examples/events/basic/ex1.ini -o ex1.out -t 20
		// --help
		//

		// Call test in order to test the simulator on all examples in a directory.
		//
	    //	test("resources/examples/events/basic");

		// Call start to start the simulator from command line, etc.
		start(args);
		//test("examples/advanced");
	}

}
