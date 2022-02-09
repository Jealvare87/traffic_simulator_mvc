package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Event;
import es.ucm.fdi.model.KilometrageView;
import es.ucm.fdi.model.RoadMap;
import es.ucm.fdi.model.SimulatorError;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.TrafficSimulator.TrafficSimulatorObserver;
import es.ucm.fdi.view.RoadMapComponent;

public class MainWindow extends JFrame implements TrafficSimulatorObserver {
	private static final long serialVersionUID = 1L;
	
	private Controller ctrl; // la vista usa el controlador 
	private RoadMap map; // para los métodos update de Observer 
	private int time; // para los métodos update de Observer 
	private List<Event> events; // para los métodos update de Observer 
	private OutputStream reportsOutputStream;
	private JFrame popupFrame;
	
	private JPanel mainPanel; 
	private JPanel contentPanel_1; 
	private JPanel contentPanel_2;
	private JPanel contentPanel_3;
	private JPanel contentPanel_4;
	private JPanel contentPanel_5;
	
	// PopUp Menu Panels
	private JPanel rrJunctionP;
	private JPanel mcJunctionP;
	private JPanel junctionP;
	private JPanel carVehicleP;
	private JPanel bikeVehicleP;
	private JPanel vehicleP;
	private JPanel lanesRoadsP;
	private JPanel dirtRoadP;
	private JPanel roadP;
	private JPanel faultyP;
	private JPanel kmPanel;
	
	private JPanel panelEditor; 
	private JPanel eventView;
	private JPanel reportsAreaPanel;
	private JPanel vehiclesPanel;
	private JPanel roadsPanel;
	private JPanel junctionPanel;
	private JPanel statusBarPanel;
	
	private JLabel statusBarText;
	private JMenu fileMenu;
	private JMenu simulatorMenu; 
	private JMenu reportsMenu; 
	private JToolBar toolBar;
	private JFileChooser fc; 
	private File currentFile;
	private JButton loadButton; //open.png
	private JButton saveButton; //save.png
	private JButton clearEventsButton; //clear.png
	private JButton checkInEventsButton;  //events.png
	private JButton resetButton;  //reset.png
	private JButton runButton;   //play.png
	//private JButton stopButton;  //stop.png
	private JSpinner stepsSpinner; 
	private JTextField timeViewer; 
	private JButton genReportsButton; //report.png
	private JButton clearReportsButton;  //delete_report.png
	private JButton saveReportsButton;  //save_report.png
	private JButton quitButton;  //exit.png
	private JTextArea eventsEditor; // editor de eventos 
	private JTable eventsTable; // cola de eventos 
	private JTextArea reportsArea; // zona de informes 
	private JTable vehiclesTable; // tabla de vehiculos 
	private JTable roadsTable; // tabla de carreteras 
	private JTable junctionsTable; // tabla de cruces 
	private EventsTableModel eventsTableModel;
	private JunctionTableModel junctionTableModel;
	private VehiclesTableModel vehiclesTableModel;
	private HistoryKmTableModel kmTableModel;
	private JTable kmTable;
	private RoadsTableModel roadsTableModel;
	private String reports;
	private RoadMapComponent rm;
	private JPopupMenu popMenu;
	private boolean canCheckIn;
	private boolean canRun;
	private boolean canClearEvents;
	private boolean canGenerateReports;
	private boolean canClearReports;
	private boolean canSaveReports;
	private boolean redirect;
	private OutputStream os;
	private String aux;
	private List<Event> popEvents; 
	private List<KilometrageView> kmTotal;
	//private ReportDialog reportDialog; // opcion
	
  	public MainWindow(TrafficSimulator model, String inFileName, Controller ctrl) throws IOException, SimulatorError { 
		super("Traffic Simulator  v1.5");
		this.setIconImage(new ImageIcon("icons/semaforo.png").getImage());
		this.ctrl = ctrl;
		currentFile = inFileName != null ? new File(inFileName) : null;
		reportsOutputStream = new JTextAreaOutputStream(reportsArea,null); 
		ctrl.setOutputStream(reportsOutputStream); // ver sección 8
		initGUI(); 
		reports = "";
		aux = "";
		model.addObserver(this);
		this.canRun = false;
		this.canCheckIn = true;
		this.canClearEvents = true;
		this.canGenerateReports = false;
		this.canClearReports = false;
		this.canSaveReports = false;
		this.redirect = true;
		this.os = null;
		this.popEvents = new ArrayList<Event>();
		this.kmTotal = new ArrayList<KilometrageView>();
	}
	
	private String correctFile(File f){
		String updated = "";
		boolean encontrado = false;
		int cont = f.getName().length() - 1;
		while(cont >= 0 && !encontrado){
			if(f.getName().charAt(cont) == '_'){
				encontrado = true;
				cont = cont - 2;
			}
			else{
				cont--;
			}
		}
		if (encontrado == true){
			updated = f.getName().substring(cont);
		}
		else {
			updated = f.getName();
		}
		return updated;
	}

	private void initGUI() throws IOException, SimulatorError { 
		mainPanel = new JPanel(new BorderLayout());
		this.mainPanel.setVisible(true);
		this.setContentPane(mainPanel);
		contentPanel_1 = new JPanel();
		contentPanel_1.setLayout(new BoxLayout(contentPanel_1,BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel_1, BorderLayout.CENTER);
		contentPanel_2 = new JPanel();
		contentPanel_2.setLayout(new BoxLayout(contentPanel_2,BoxLayout.X_AXIS));
		contentPanel_3 = new JPanel();
		contentPanel_3.setLayout(new BoxLayout(contentPanel_3,BoxLayout.X_AXIS));
		contentPanel_4 = new JPanel();
		contentPanel_4.setLayout(new BoxLayout(contentPanel_4,BoxLayout.Y_AXIS));
		contentPanel_5 = new JPanel(new BorderLayout());
		contentPanel_1.add(contentPanel_2);
		this.contentPanel_2.setMinimumSize(new Dimension(100,90));
		contentPanel_1.add(contentPanel_3);
		this.contentPanel_3.setMinimumSize(new Dimension(100, 90));
		contentPanel_3.add(contentPanel_4);
		contentPanel_3.add(contentPanel_5);
		
		// Divide la ventana en mas paneles y configura layout 
		fc = new JFileChooser(); 
		addMenuBar(); // barra de menus 
		addToolBar(); // barra de herramientas 
		addEventsEditor(); // editor de eventos 
		addEventsView(); // cola de eventos
		addReportsArea(); // zona de informes 
		addVehiclesTable(); // tabla de vehiculos 
		addRoadsTable(); // tabla de carreteras 
		addJunctionsTable(); // tabla de cruces 
		addKmTotal();
		addMap(); // mapa de carreteras 
		addStatusBar(); // barra de estado //
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	private void addStatusBar() {
		this.statusBarPanel = new JPanel();
		this.mainPanel.add(this.statusBarPanel, BorderLayout.PAGE_END);
		statusBarText = new JLabel("Welcome to the simulator!");	
		this.statusBarPanel.add(this.statusBarText);
	}

	private void addMap() {
		this.rm = new RoadMapComponent();
		rm.setVisible(true);
		this.map = this.ctrl.getTraffic().getMap();
		this.contentPanel_5.add(rm);
		rm.setMap(this.map);
	}

	private void addJunctionsTable() {
		this.junctionPanel = new JPanel(new BorderLayout());
		this.junctionPanel.setBorder(new TitledBorder("Junctions"));
		this.junctionTableModel = new JunctionTableModel();
		this.junctionsTable = new JTable(junctionTableModel);
		this.junctionPanel.add(this.junctionsTable);
		this.junctionPanel.add(new JScrollPane(junctionsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		this.contentPanel_4.add(this.junctionPanel);
	}

	private void addRoadsTable() {
		this.roadsPanel = new JPanel(new BorderLayout());
		this.roadsPanel.setBorder(new TitledBorder("Roads"));
		this.roadsTableModel = new RoadsTableModel();
		roadsTable = new JTable(roadsTableModel);
		this.roadsPanel.add(this.roadsTable);
		this.roadsPanel.add(new JScrollPane(roadsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		this.contentPanel_4.add(this.roadsPanel);	
	}

	private void addVehiclesTable() {
		this.vehiclesPanel = new JPanel(new BorderLayout());
		this.vehiclesPanel.setBorder(new TitledBorder("Vehicles"));
		this.vehiclesTableModel = new VehiclesTableModel();
		vehiclesTable = new JTable(vehiclesTableModel);
		this.vehiclesPanel.add(this.vehiclesTable);
		this.vehiclesPanel.add(new JScrollPane(vehiclesTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		this.contentPanel_4.add(this.vehiclesPanel);		
	}
	
	private void addKmTotal(){
		this.kmPanel = new JPanel(new BorderLayout());
		this.kmPanel.setBorder(new TitledBorder("Total Kilometrage History"));
		this.kmTableModel = new HistoryKmTableModel();
		this.kmTable = new JTable(kmTableModel);
		this.kmPanel.add(this.kmTable);
		this.kmPanel.add(new JScrollPane(kmTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		this.contentPanel_4.add(this.kmPanel);
	}

	private void addReportsArea() {
		this.reportsAreaPanel = new JPanel(new BorderLayout());
		this.reportsAreaPanel.setBorder(new TitledBorder("Reports"));
		this.contentPanel_2.add(this.reportsAreaPanel);
		this.reportsArea = new JTextArea(40,30);
		this.reportsArea.setEditable(false);
		this.reportsAreaPanel.add(this.reportsArea);
		this.reportsAreaPanel.add(new JScrollPane(reportsArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
	}

	private void addEventsView() {
		this.eventView = new JPanel(new BorderLayout());
		this.eventView.setBorder(new TitledBorder("Events Queue"));
		this.contentPanel_2.add(this.eventView);
		this.eventsTableModel = new EventsTableModel();
		this.eventsTable = new JTable(eventsTableModel);
		this.eventView.add(eventsTable);
		this.eventView.add(new JScrollPane(eventsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
	}

	private void addEventsEditor() {
		this.panelEditor = new JPanel(new BorderLayout());
		this.panelEditor.setBorder(new TitledBorder("Events: "+ correctFile(this.currentFile)));
		this.eventsEditor = new JTextArea(40, 30);
		this.panelEditor.add(this.eventsEditor);
		this.panelEditor.add(new JScrollPane(this.eventsEditor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		if(this.currentFile != null) {
			eventsEditor.setText(readFile(this.currentFile));
		}
		this.contentPanel_2.add(this.panelEditor);
		this.createPopMenu();		
	}
	
	private void createPopMenu() {
		this.popMenu = new JPopupMenu();
		this.popupFrame = new JFrame("Add Event - Traffic Simulator");
		this.popupFrame.setSize(new Dimension(40,70));
		this.popupFrame.setVisible(false);
		JMenu addTemplate = new JMenu("Add Template");
		String[] options = { "New RR Junction", "New MC Junction", "New Junction", "New Dirt Road", "New Lanes Road", "New Road", "New Bike", "New Car", "New Vehicle", "New Vehicle Faulty"};
		for(String s : options) {
			JMenuItem menuItem = new JMenuItem(s);
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					eventsEditor.insert(setTemplate(s), eventsEditor.getCaretPosition());
				}
			});
			addTemplate.add(menuItem);
		}
		this.popMenu.add(addTemplate);
		this.popMenu.addSeparator();
		JMenuItem loadOption = new JMenuItem("Load");
		loadOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fc.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
				FileFilter filter = new FileNameExtensionFilter("Archivos .ini", "ini");
				fc.setFileFilter(filter);
				int op = fc.showOpenDialog(MainWindow.this);
				File file = fc.getSelectedFile();
				if(op == JFileChooser.APPROVE_OPTION) {
					
					if(file != null) {
						currentFile = file;
						try {
							inizializa();
						} catch (IOException | SimulatorError vv) {
							vv.printStackTrace();
						}
						statusBarText.setText("File has been load succesfully.");
					}
					else {
						statusBarText.setText("File load interrupted.");
					}
					
				}
			}
		});
		this.popMenu.add(loadOption);
		JMenuItem saveOption = new JMenuItem("Save");
		saveOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(canCheckIn == true || canRun == true) {
					fc.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
					int op = fc.showSaveDialog(MainWindow.this);
					if (op == JFileChooser.APPROVE_OPTION) {
						try {
							@SuppressWarnings("resource")
							OutputStream fw = new FileOutputStream(fc.getSelectedFile());
					        fw.write(eventsEditor.getText().getBytes());
					        statusBarText.setText("File saved.");
					     } catch (Exception ex) {
					        ex.printStackTrace();
					        statusBarText.setText("An unexpected error occurred.");
					     }
					}
				}
				else {
					statusBarText.setText("Load an ini file.");
				}
			}
		});
		this.popMenu.add(saveOption);
		JMenuItem clearOption = new JMenuItem("Clear");
		clearOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(canClearEvents == true) {
					if(events != null) {
						events.clear();
					}
					eventsEditor.setText(null);
					eventAdded(time, map, events);
					canRun = false;
					canCheckIn = false;
					canClearEvents = false;
					canGenerateReports = false;
					canClearReports = false;
					canSaveReports = false;
					panelEditor.setBorder(new TitledBorder("Events: please load an .ini file"));
					statusBarText.setText("Events have been cleared.");
				}
				else {
					statusBarText.setText("First load an .ini file.");
				}
				
			}
		});
		this.popMenu.add(clearOption);
		this.eventsEditor.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger() && popMenu.isEnabled()) {
					popMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
	}

	private String setTemplate(String s) {
		String x = "";
		this.popupFrame.setVisible(true);
		switch(s) {
		case "New RR Junction":
			x += this.showRRJunction();
			break;
		case "New MC Junction":
			x += this.showMCJunction();
			break;
		case "New Junction":
			x += this.showJunction();
			break;
		case "New Dirt Road":
			x += this.showDirtRoad();
			break;
		case "New Lanes Road":
			x += this.showLanesRoad();
			break;
		case "New Road":
			x += this.showRoad();
			break;
		case "New Bike":
			x += this.showBike();
			break;
		case "New Car":
			x += this.showCar();
			break;
		case "New Vehicle":
			x += this.showVehicle();
			break;
		case "New Vehicle Faulty":
			x += this.showVehicleFaulty();
			break;
		}
		return x;
	}
	
	private String showRRJunction() {
		JTextField id = new JTextField(10);
		JTextField time = new JTextField(10);
		JTextField maxTimeSlice = new JTextField(10);
		JTextField minTimeSlice = new JTextField(10);
		JButton acept = new JButton("Acept");
		IniSection is = new IniSection("new_junction");
		this.aux = "";
		
		this.rrJunctionP = new JPanel();
		this.rrJunctionP.setMinimumSize(new Dimension(50, 80));
		this.rrJunctionP.setLayout(new BoxLayout(this.rrJunctionP, BoxLayout.Y_AXIS));
		popupFrame.setContentPane(this.rrJunctionP);
		this.rrJunctionP.add(new JLabel("Time"));
		this.rrJunctionP.add(time);
		this.rrJunctionP.add(new JLabel("ID"));
		this.rrJunctionP.add(id);
		this.rrJunctionP.add(new JLabel("Max time slice"));
		this.rrJunctionP.add(maxTimeSlice);
		this.rrJunctionP.add(new JLabel("Min time slice"));
		this.rrJunctionP.add(minTimeSlice);
		this.rrJunctionP.add(new JLabel("Type = rr"));
		this.rrJunctionP.add(acept);
		acept.addActionListener(new ActionListener() {

			@SuppressWarnings({ "deprecation", "unused" })
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int x = Integer.parseInt(time.getText());
					int y = Integer.parseInt(maxTimeSlice.getText());
					int z = Integer.parseInt(minTimeSlice.getText());
					if(y < z) {
						throw new SimulatorError("Max time slice cant be more than min time slice.");
					}
					is.setValue("time", time.getText());
					is.setValue("id", id.getText());
					is.setValue("max_time_slice", maxTimeSlice.getText());
					is.setValue("min_time_slice", minTimeSlice.getText());
					is.setValue("type", "rr");
					//ctrl.getTraffic().addEvent(ctrl.parseEvent(is));
					popEvents.add(ctrl.parseEvent(is));
					
						//popJunctions.add(((Junction)new RoundRobinJunction(id.getText(), Integer.parseInt(maxTimeSlice.getText()), Integer.parseInt(minTimeSlice.getText()))));
						aux ="\n[new_junction]\n" + "time = " + time.getText()
						+"\n" + "id = "+ id.getText() +"\n" + "max_time_slice = "+ maxTimeSlice.getText() + "\n" 
								+ "min_time_slice = "+ minTimeSlice.getText() + "\n" + "type = rr\n";
						eventsEditor.insert(aux, eventsEditor.getCaretPosition());
						statusBarText.setText("Event created succesfuly.");
					
				}catch(NumberFormatException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(IllegalArgumentException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(SimulatorError excep) {
					statusBarText.setText("Max time slice cant be more than min time slice.");
				}finally {
					popupFrame.show(false);
				}
			}
			
		});
		this.popupFrame.pack();
		return aux;
	}

	private String showMCJunction() {
		JTextField id = new JTextField(10);
		JTextField time = new JTextField(10);
		JButton acept = new JButton("Acept");
		IniSection is = new IniSection("new_junction");
		this.aux = "";
		
		this.mcJunctionP = new JPanel();
		this.mcJunctionP.setMinimumSize(new Dimension(50, 80));
		this.mcJunctionP.setLayout(new BoxLayout(this.mcJunctionP, BoxLayout.Y_AXIS));
		popupFrame.setContentPane(this.mcJunctionP);
		this.mcJunctionP.add(new JLabel("Time"));
		this.mcJunctionP.add(time);
		this.mcJunctionP.add(new JLabel("ID"));
		this.mcJunctionP.add(id);
		this.mcJunctionP.add(new JLabel("Type = mc"));
		this.mcJunctionP.add(acept);
		acept.addActionListener(new ActionListener() {

			@SuppressWarnings({ "unused", "deprecation" })
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int x = Integer.parseInt(time.getText());
					is.setValue("time", time.getText());
					is.setValue("id", id.getText());
					is.setValue("type", "mc");
					//ctrl.getTraffic().addEvent(ctrl.parseEvent(is));
					popEvents.add(ctrl.parseEvent(is));
						//popJunctions.add(((Junction)new MostCrowdedJunction(id.getText())));
						aux ="\n[new_junction]\n" + "time = " + time.getText()
						+"\n" + "id = "+ id.getText() +"\n" + "type = mc\n";
						eventsEditor.insert(aux, eventsEditor.getCaretPosition());
						statusBarText.setText("Event created succesfuly.");
				}catch(NumberFormatException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(IllegalArgumentException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}finally {
					popupFrame.show(false);
				}
			}
			
		});
		this.popupFrame.pack();
		
		return aux;
	}

	private String showJunction() {
		JTextField id = new JTextField(10);
		JTextField time = new JTextField(10);
		JButton acept = new JButton("Acept");
		IniSection is = new IniSection("new_junction");
		this.aux = "";
		
		this.junctionP = new JPanel();
		this.junctionP.setMinimumSize(new Dimension(50, 80));
		this.junctionP.setLayout(new BoxLayout(this.junctionP, BoxLayout.Y_AXIS));
		popupFrame.setContentPane(this.junctionP);
		this.junctionP.add(new JLabel("Time"));
		this.junctionP.add(time);
		this.junctionP.add(new JLabel("ID"));
		this.junctionP.add(id);
		this.junctionP.add(acept);
		acept.addActionListener(new ActionListener() {

			@SuppressWarnings({ "unused", "deprecation" })
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int x = Integer.parseInt(time.getText());
					is.setValue("time", time.getText());
					is.setValue("id", id.getText());
					//ctrl.getTraffic().addEvent(ctrl.parseEvent(is));
					popEvents.add(ctrl.parseEvent(is));
						//popJunctions.add(new Junction(id.getText()));
						aux ="\n[new_junction]\n" + "time = " + time.getText()
						+"\n" + "id = "+ id.getText();
						eventsEditor.insert(aux, eventsEditor.getCaretPosition());
						statusBarText.setText("Event created succesfuly.");
				}catch(NumberFormatException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(IllegalArgumentException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}finally {
					popupFrame.show(false);
				}
			}
			
		});
		this.popupFrame.pack();
		
		return aux;
	}
	
	private String showDirtRoad() {
		JTextField id = new JTextField(10);
		JTextField time = new JTextField(10);
		JTextField src = new JTextField(10);
		JTextField dest = new JTextField(10);
		JTextField maxSped = new JTextField(10);
		JTextField lenght = new JTextField(10);
		JButton acept = new JButton("Acept");
		IniSection is = new IniSection("new_road");
		this.aux = "";
		
		this.dirtRoadP = new JPanel();
		this.dirtRoadP.setMinimumSize(new Dimension(50, 80));
		this.dirtRoadP.setLayout(new BoxLayout(this.dirtRoadP, BoxLayout.Y_AXIS));
		popupFrame.setContentPane(this.dirtRoadP);
		this.dirtRoadP.add(new JLabel("Time"));
		this.dirtRoadP.add(time);
		this.dirtRoadP.add(new JLabel("ID"));
		this.dirtRoadP.add(id);
		this.dirtRoadP.add(new JLabel("src"));
		this.dirtRoadP.add(src);
		this.dirtRoadP.add(new JLabel("dest"));
		this.dirtRoadP.add(dest);
		this.dirtRoadP.add(new JLabel("Max speed"));
		this.dirtRoadP.add(maxSped);
		this.dirtRoadP.add(new JLabel("Length"));
		this.dirtRoadP.add(lenght);
		this.dirtRoadP.add(new JLabel("Type = dirt"));
		this.dirtRoadP.add(acept);
		acept.addActionListener(new ActionListener() {

			@SuppressWarnings({ "unused", "deprecation" })
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int x = Integer.parseInt(time.getText());
					is.setValue("time", time.getText());
					is.setValue("id", id.getText());
					is.setValue("src", src.getText());
					is.setValue("dest", dest.getText());
					is.setValue("max_speed", maxSped.getText());
					is.setValue("length", lenght.getText());
					is.setValue("type", "dirt");
					//ctrl.getTraffic().addEvent(ctrl.parseEvent(is));
					popEvents.add(ctrl.parseEvent(is));
						
						aux ="\n[new_road]\n" + "time = " + time.getText()
						+"\n" + "id = "+ id.getText() +"\n" + "src = "+ src.getText() + "\n" 
						+ "dest = "+ dest.getText() + "\nmax_speed = "+ maxSped.getText()
						+"\n" + "length = "+ lenght.getText()+ "\n" + "type = dirt\n";
						eventsEditor.insert(aux, eventsEditor.getCaretPosition());
						statusBarText.setText("Event created succesfuly.");
					
				}catch(NumberFormatException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(IllegalArgumentException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}finally {
					popupFrame.show(false);
				}
			}
			
		});
		this.popupFrame.pack();
		
		return aux;
	}
	
	private String showLanesRoad() {
		JTextField id = new JTextField(10);
		JTextField time = new JTextField(10);
		JTextField src = new JTextField(10);
		JTextField dest = new JTextField(10);
		JTextField maxSped = new JTextField(10);
		JTextField lenght = new JTextField(10);
		JTextField lanes = new JTextField(10);
		JButton acept = new JButton("Acept");
		IniSection is = new IniSection("new_road");
		this.aux = "";
		
		this.lanesRoadsP = new JPanel();
		this.lanesRoadsP.setMinimumSize(new Dimension(50, 80));
		this.lanesRoadsP.setLayout(new BoxLayout(this.lanesRoadsP, BoxLayout.Y_AXIS));
		popupFrame.setContentPane(this.lanesRoadsP);
		this.lanesRoadsP.add(new JLabel("Time"));
		this.lanesRoadsP.add(time);
		this.lanesRoadsP.add(new JLabel("ID"));
		this.lanesRoadsP.add(id);
		this.lanesRoadsP.add(new JLabel("src"));
		this.lanesRoadsP.add(src);
		this.lanesRoadsP.add(new JLabel("dest"));
		this.lanesRoadsP.add(dest);
		this.lanesRoadsP.add(new JLabel("Max speed"));
		this.lanesRoadsP.add(maxSped);
		this.lanesRoadsP.add(new JLabel("Length"));
		this.lanesRoadsP.add(lenght);
		this.lanesRoadsP.add(new JLabel("Lanes"));
		this.lanesRoadsP.add(lanes);
		this.lanesRoadsP.add(new JLabel("Type = lanes"));
		this.lanesRoadsP.add(acept);
		acept.addActionListener(new ActionListener() {

			@SuppressWarnings({ "unused", "deprecation" })
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int x = Integer.parseInt(time.getText());
					is.setValue("time", time.getText());
					is.setValue("id", id.getText());
					is.setValue("src", src.getText());
					is.setValue("dest", dest.getText());
					is.setValue("max_speed", maxSped.getText());
					is.setValue("length", lenght.getText());
					is.setValue("lanes", lanes.getText());
					is.setValue("type", "lanes");
					//ctrl.getTraffic().addEvent(ctrl.parseEvent(is));
					popEvents.add(ctrl.parseEvent(is));
					
						aux ="\n[new_road]\n" + "time = " + time.getText()
						+"\n" + "id = "+ id.getText() +"\n" + "src = "+ src.getText() + "\n" 
						+ "dest = "+ dest.getText() + "\nmax_speed = "+ maxSped.getText()
						+"\n" + "length = "+ lenght.getText()+ "\n" + "lanes = "+ lanes.getText() 
						+ "\n" + "type = lanes\n";
						eventsEditor.insert(aux, eventsEditor.getCaretPosition());
						statusBarText.setText("Event created succesfuly.");
				}catch(NumberFormatException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(IllegalArgumentException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}finally {
					popupFrame.show(false);
				}
			}
			
		});
		this.popupFrame.pack();
		
		return aux;
	}
	
	private String showRoad(){
		JTextField id = new JTextField(10);
		JTextField time = new JTextField(10);
		JTextField src = new JTextField(10);
		JTextField dest = new JTextField(10);
		JTextField maxSped = new JTextField(10);
		JTextField lenght = new JTextField(10);
		JButton acept = new JButton("Acept");
		IniSection is = new IniSection("new_road");
		this.aux = "";
		
		this.roadP = new JPanel();
		this.roadP.setMinimumSize(new Dimension(50, 80));
		this.roadP.setLayout(new BoxLayout(this.roadP, BoxLayout.Y_AXIS));
		popupFrame.setContentPane(this.roadP);
		this.roadP.add(new JLabel("Time"));
		this.roadP.add(time);
		this.roadP.add(new JLabel("ID"));
		this.roadP.add(id);
		this.roadP.add(new JLabel("src"));
		this.roadP.add(src);
		this.roadP.add(new JLabel("dest"));
		this.roadP.add(dest);
		this.roadP.add(new JLabel("Max speed"));
		this.roadP.add(maxSped);
		this.roadP.add(new JLabel("Length"));
		this.roadP.add(lenght);
		this.roadP.add(acept);
		acept.addActionListener(new ActionListener() {

			@SuppressWarnings({ "deprecation", "unused" })
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int x = Integer.parseInt(time.getText());
					is.setValue("time", time.getText());
					is.setValue("id", id.getText());
					is.setValue("src", src.getText());
					is.setValue("dest", dest.getText());
					is.setValue("max_speed", maxSped.getText());
					is.setValue("length", lenght.getText());
					//ctrl.getTraffic().addEvent(ctrl.parseEvent(is));
					popEvents.add(ctrl.parseEvent(is));
					
						aux ="\n[new_road]\n" + "time = " + time.getText()
						+"\n" + "id = "+ id.getText() +"\n" + "src = "+ src.getText() + "\n" 
						+ "dest = "+ dest.getText() + "\nmax_speed = "+ maxSped.getText()
						+"\n" + "length = "+ lenght.getText()+ "\n";
						eventsEditor.insert(aux, eventsEditor.getCaretPosition());
						statusBarText.setText("Event created succesfuly.");
				}catch(NumberFormatException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(IllegalArgumentException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}finally {
					popupFrame.show(false);
				}
			}
			
		});
		this.popupFrame.pack();
		
		return aux;
	}
	
	private String showBike() {
		JTextField id = new JTextField(10);
		JTextField time = new JTextField(10);
		JTextField itinerary = new JTextField(10);
		JTextField maxSped = new JTextField(10);
		JButton acept = new JButton("Acept");
		IniSection is = new IniSection("new_vehicle");
		this.aux = "";
		
		this.bikeVehicleP = new JPanel();
		this.bikeVehicleP .setMinimumSize(new Dimension(50, 80));
		this.bikeVehicleP .setLayout(new BoxLayout(this.bikeVehicleP, BoxLayout.Y_AXIS));
		popupFrame.setContentPane(this.bikeVehicleP);
		this.bikeVehicleP .add(new JLabel("Time"));
		this.bikeVehicleP.add(time);
		this.bikeVehicleP.add(new JLabel("ID"));
		this.bikeVehicleP.add(id);
		this.bikeVehicleP.add(new JLabel("Itinerary"));
		this.bikeVehicleP.add(itinerary);
		this.bikeVehicleP.add(new JLabel("Max speed"));
		this.bikeVehicleP.add(maxSped);
		this.bikeVehicleP.add(new JLabel("Type = bike"));
		this.bikeVehicleP.add(acept);
		acept.addActionListener(new ActionListener() {

			@SuppressWarnings({ "deprecation", "unused" })
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int x = Integer.parseInt(time.getText());
					int y = Integer.parseInt(maxSped.getText());
					is.setValue("time", time.getText());
					is.setValue("id", id.getText());
					is.setValue("itinerary", itinerary.getText());
					is.setValue("max_speed", maxSped.getText());
					is.setValue("type", "bike");
					//ctrl.getTraffic().addEvent(ctrl.parseEvent(is));
					popEvents.add(ctrl.parseEvent(is));
					
					aux ="\n[new_vehicle]\n" + "time = "+ time.getText()
					+"\n" + "id = " + id.getText() +"\n" + "itinerary = " + itinerary.getText() + "\n" + "max_speed = " 
							+ maxSped.getText() + "\n" + "type = bike\n";
					eventsEditor.insert(aux, eventsEditor.getCaretPosition());
					statusBarText.setText("Event created succesfuly.");
				}catch(NumberFormatException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(IllegalArgumentException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}finally {
					popupFrame.show(false);
				}
			}
		});
		this.popupFrame.pack();
		return aux;
	}
	
	private String showCar() {
		JTextField id = new JTextField(10);
		JTextField time = new JTextField(10);
		JTextField itinerary = new JTextField(10);
		JTextField maxSped = new JTextField(10);
		JTextField resistance = new JTextField(10);
		JTextField fp = new JTextField(10);
		JTextField mfd = new JTextField(10);
		JTextField seed = new JTextField(10);
		JButton acept = new JButton("Acept");
		IniSection is = new IniSection("new_vehicle");
		this.aux = "";
		
		this.carVehicleP = new JPanel();
		this.carVehicleP.setMinimumSize(new Dimension(50, 80));
		this.carVehicleP.setLayout(new BoxLayout(this.carVehicleP, BoxLayout.Y_AXIS));
		popupFrame.setContentPane(this.carVehicleP);
		this.carVehicleP.add(new JLabel("Time"));
		this.carVehicleP.add(time);
		this.carVehicleP.add(new JLabel("ID"));
		this.carVehicleP.add(id);
		this.carVehicleP.add(new JLabel("Itinerary"));
		this.carVehicleP.add(itinerary);
		this.carVehicleP.add(new JLabel("Max speed"));
		this.carVehicleP.add(maxSped);
		this.carVehicleP.add(new JLabel("Type = car"));
		this.carVehicleP.add(new JLabel("Resistance"));
		this.carVehicleP.add(resistance);
		this.carVehicleP.add(new JLabel("Fault probability"));
		this.carVehicleP.add(fp);
		this.carVehicleP.add(new JLabel("Max fault duration"));
		this.carVehicleP.add(mfd);
		this.carVehicleP.add(new JLabel("Seed"));
		this.carVehicleP.add(seed);
		this.carVehicleP.add(acept);
		acept.addActionListener(new ActionListener() {

			@SuppressWarnings({ "unused", "deprecation" })
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int x = Integer.parseInt(time.getText());
					int y = Integer.parseInt(maxSped.getText());
					int z = Integer.parseInt(resistance.getText());
					Double a = Double.parseDouble(fp.getText());
					int f = Integer.parseInt(mfd.getText());
					Double b = Double.parseDouble(mfd.getText());
					is.setValue("time", time.getText());
					is.setValue("id", id.getText());
					is.setValue("itinerary", itinerary.getText());
					is.setValue("max_speed", maxSped.getText());
					is.setValue("type", "car");
					is.setValue("resistance", resistance.getText());
					is.setValue("fault_probability", fp.getText());
					is.setValue("max_fault_duration", mfd.getText());
					is.setValue("seed", seed.getText());
					//ctrl.getTraffic().addEvent(ctrl.parseEvent(is));
					popEvents.add(ctrl.parseEvent(is));
					
					aux ="\n[new_vehicle]\n" + "time = "+ time.getText()
					+"\n" + "id = " + id.getText() +"\n" + "itinerary = " + itinerary.getText() + "\n" + "max_speed = " 
							+ maxSped.getText() + "\n" + "type = car\n" + "resistance = " + resistance.getText() + "\nfault_probability = "
							+ fp.getText() + "\nmax_fault_duration = " + mfd.getText() + "\nseed = " + seed.getText() + "\n";
					eventsEditor.insert(aux, eventsEditor.getCaretPosition());
					statusBarText.setText("Event created succesfuly.");
				}catch(NumberFormatException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(IllegalArgumentException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}finally {
					popupFrame.show(false);
				}
			}
		});
		this.popupFrame.pack();
		return aux;		
	}
	
	private String showVehicle() {
		JTextField id = new JTextField(10);
		JTextField time = new JTextField(10);
		JTextField itinerary = new JTextField(10);
		JTextField maxSped = new JTextField(10);
		JButton acept = new JButton("Acept");
		IniSection is = new IniSection("new_vehicle");
		this.aux = "";
		
		this.vehicleP = new JPanel();
		this.vehicleP.setMinimumSize(new Dimension(50, 80));
		this.vehicleP.setLayout(new BoxLayout(this.vehicleP, BoxLayout.Y_AXIS));
		popupFrame.setContentPane(this.vehicleP);
		this.vehicleP.add(new JLabel("Time"));
		this.vehicleP.add(time);
		this.vehicleP.add(new JLabel("ID"));
		this.vehicleP.add(id);
		this.vehicleP.add(new JLabel("Itinerary"));
		this.vehicleP.add(itinerary);
		this.vehicleP.add(new JLabel("Max speed"));
		this.vehicleP.add(maxSped);
		this.vehicleP.add(acept);
		acept.addActionListener(new ActionListener() {

			@SuppressWarnings({ "unused", "deprecation" })
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int x = Integer.parseInt(time.getText());
					int y = Integer.parseInt(maxSped.getText());
					is.setValue("time", time.getText());
					is.setValue("id", id.getText());
					is.setValue("itinerary", itinerary.getText());
					is.setValue("max_speed", maxSped.getText());
					//ctrl.getTraffic().addEvent(ctrl.parseEvent(is));
					popEvents.add(ctrl.parseEvent(is));
					
					aux ="\n[new_vehicle]\n" + "time = "+ time.getText()
					+"\n" + "id = " + id.getText() +"\n" + "itinerary = " + itinerary.getText() + "\n" + "max_speed = " 
							+ maxSped.getText() + "\n";
					eventsEditor.insert(aux, eventsEditor.getCaretPosition());
					statusBarText.setText("Event created succesfuly.");
				}catch(NumberFormatException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(IllegalArgumentException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}finally {
					popupFrame.show(false);
				}
			}
		});
		this.popupFrame.pack();
		return aux;	
	}
	
	private String showVehicleFaulty() {
		JTextField time = new JTextField(10);
		JTextField vehicles = new JTextField(10);
		JTextField duration = new JTextField(10);
		JButton acept = new JButton("Acept");
		IniSection is = new IniSection("make_vehicle_faulty");
		this.aux = "";
		
		this.faultyP = new JPanel();
		this.faultyP.setMinimumSize(new Dimension(50, 80));
		this.faultyP.setLayout(new BoxLayout(this.faultyP, BoxLayout.Y_AXIS));
		popupFrame.setContentPane(this.faultyP);
		this.faultyP.add(new JLabel("Time"));
		this.faultyP.add(time);
		this.faultyP.add(new JLabel("Vehicles"));
		this.faultyP.add(vehicles);
		this.faultyP.add(new JLabel("Duration"));
		this.faultyP.add(duration);
		this.faultyP.add(acept);
		acept.addActionListener(new ActionListener() {

			@SuppressWarnings({ "unused", "deprecation" })
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int x = Integer.parseInt(time.getText());
					int y = Integer.parseInt(duration.getText());
					is.setValue("time", time.getText());
					is.setValue("vehicles", vehicles.getText());
					is.setValue("duration", duration.getText());
					//ctrl.getTraffic().addEvent(ctrl.parseEvent(is));
					popEvents.add(ctrl.parseEvent(is));
					aux ="\n[make_vehicle_faulty]\n" + "time = "+ time.getText()
					+"\n" + "vehicles = " + vehicles.getText() +"\n" + "duration = " + duration.getText();
					eventsEditor.insert(aux, eventsEditor.getCaretPosition());
					statusBarText.setText("Event created succesfuly.");
				}catch(NumberFormatException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}catch(IllegalArgumentException excep) {
					statusBarText.setText("Wrong format. Event cancel.");
				}finally {
					popupFrame.show(false);
				}
			}
		});
		this.popupFrame.pack();
		return aux;	
	}
	
	private void addToolBar() {
		this.toolBar = new JToolBar();
		this.mainPanel.add(this.toolBar, BorderLayout.PAGE_START);
		
		addLoadButton();
		addSaveButton();
		addClearEventsButton();
		
		this.toolBar.addSeparator();
		
		addCheckInEventsButton();
		addRunButton();
		addResetButton();
		
		addStepsSpinner();
		addTimeTextField();
		
		toolBar.addSeparator();
		
		addReportsButton();
		addClearReportsButton();
		addSaveReportsButton();
		
		toolBar.addSeparator(); 
		
		addQuitButton();
	}
	
	private void addLoadButton() {
		this.loadButton = new JButton(new ImageIcon("icons/open.png"));
		this.loadButton.setToolTipText("Load a new .ini file");
		this.loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				fc.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
				FileFilter filter = new FileNameExtensionFilter("Archivos .ini", "ini");
				fc.setFileFilter(filter);
				int op = fc.showOpenDialog(MainWindow.this);
				File file = fc.getSelectedFile();
				if(op == JFileChooser.APPROVE_OPTION) {
					
					if(file != null) {
						currentFile = file;
						try {
							inizializa();
						} catch (IOException | SimulatorError e) {
							e.printStackTrace();
						}
						statusBarText.setText("File has been load succesfully.");
					}
					else {
						statusBarText.setText("File load interrupted.");
					}
					
				}
			}
			
		});
		this.toolBar.add(this.loadButton);
	}

	public void inizializa() throws IOException, SimulatorError {
		TrafficSimulator tsim = new TrafficSimulator(null);
		InputStream inp = null;
		try {
			 inp = new FileInputStream(currentFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ctrl = new Controller(tsim, (int) stepsSpinner.getValue(), inp, null);
		reportsOutputStream = new JTextAreaOutputStream(reportsArea,null); 
		ctrl.setOutputStream(reportsOutputStream);
		initGUI(); 
		reports = "";
		tsim.addObserver(this);
		this.canRun = false;
		this.canCheckIn = true;
		this.canClearEvents = true;
		this.canGenerateReports = false;
		this.canClearReports = false;
		this.canSaveReports = false;
	}
	
	private void addSaveButton() {
		this.saveButton = new JButton(new ImageIcon("icons/save.png"));
		this.saveButton.setToolTipText("Saves the actual .ini file");
		this.saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(canCheckIn == true || canRun == true) {
					fc.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
					int op = fc.showSaveDialog(MainWindow.this);
					if (op == JFileChooser.APPROVE_OPTION) {
						try {
							@SuppressWarnings("resource")
							OutputStream fw = new FileOutputStream(fc.getSelectedFile());
					        fw.write(eventsEditor.getText().getBytes());
					        statusBarText.setText("File saved.");
					     } catch (Exception ex) {
					        ex.printStackTrace();
					        statusBarText.setText("An unexpected error occurred.");
					     }
					}
				}
				else {
					statusBarText.setText("Load an ini file.");
				}
			}
		});
		this.toolBar.add(this.saveButton);
	}
	
	private void addClearEventsButton() {
		this.clearEventsButton = new JButton(new ImageIcon("icons/clear.png"));
		this.clearEventsButton.setToolTipText("Clear events");
		this.toolBar.add(this.clearEventsButton);
		this.clearEventsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(canClearEvents == true) {
					if(events != null) {
						events.clear();
					}
					eventsEditor.setText(null);
					eventAdded(time, map, events);
					canRun = false;
					canCheckIn = false;
					canClearEvents = false;
					canGenerateReports = false;
					canClearReports = false;
					canSaveReports = false;
					panelEditor.setBorder(new TitledBorder("Events: please load an .ini file"));
					statusBarText.setText("Events have been cleared.");
				}
				else {
					statusBarText.setText("First load an .ini file.");
				}
			}
		});
	}
	
	private void addCheckInEventsButton() {
		this.checkInEventsButton = new JButton(new ImageIcon("icons/events.png"));
		this.toolBar.add(this.checkInEventsButton);
		this.checkInEventsButton.setToolTipText("Check in events");
		this.checkInEventsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(canCheckIn == true) {
						ctrl.loadEvents(new ByteArrayInputStream(eventsEditor.getText().getBytes()));
						if(!popEvents.isEmpty()) {
							ctrl.loadPop(popEvents);
						}
						canRun = true;
						canCheckIn = false;
						canClearEvents = true;
						canGenerateReports = false;
						canClearReports = false;
						canSaveReports = false;
						statusBarText.setText("Events have been loaded.");
					}
					else {
						statusBarText.setText("First load an .ini file if there are no events showed.");
					}
				} catch (IOException | SimulatorError e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void addRunButton() {
		this.runButton = new JButton(new ImageIcon("icons/play.png"));
		this.toolBar.add(this.runButton);
		this.runButton.setToolTipText("Run simulator");
		this.runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(canRun == true) {
						int x = (int) stepsSpinner.getValue();
						ctrl.run(x);
						canRun = true;
						canCheckIn = false;
						canClearEvents = true;
						canGenerateReports = true;
						canClearReports = true;
						canSaveReports = true;
						if(redirect == true) {
							reports += map.generateReport(time).toString();
							reportsArea.setText(reports);
						}
						else {
							os.write(reportsArea.getText().getBytes());
						}
						statusBarText.setText("Simulator is running in time: " + time + ".");
					}
					else {
						statusBarText.setText("First load events if simulator is not running.");
					}
					
				} catch (IOException | SimulatorError e) {
					e.printStackTrace();
				}
			}
			
		});
	}

	private void addResetButton() {
		this.resetButton = new JButton(new ImageIcon("icons/reset.png"));
		this.toolBar.add(this.resetButton);
		this.resetButton.setToolTipText("Reset simulator");
		this.resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ctrl.reset();
				eventsEditor.setText("");
				canRun = false;
				canCheckIn = false;
				canClearEvents = false;
				canGenerateReports = false;
				canClearReports = false;
				canSaveReports = false;
				statusBarText.setText("Simulator has been reset.");
				panelEditor.setBorder(new TitledBorder("Events: please load an .ini file"));
			}
			
		});
	}
	
	private void addStepsSpinner() {
		toolBar.add(new JLabel(" Steps: ")); 
		stepsSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
		stepsSpinner.setMaximumSize(new Dimension(70, 70));
		toolBar.add(stepsSpinner);
		this.time = (int) stepsSpinner.getValue();
	}
	
	private void addTimeTextField() {
		toolBar.add(new JLabel(" Time: "));
		timeViewer = new JTextField("0", 5);
		timeViewer.setMaximumSize(new Dimension(70,70));
		timeViewer.setEditable(false);
		toolBar.add(timeViewer);
	}
	
	private void addReportsButton() {
		genReportsButton = new JButton(new ImageIcon("icons/report.png"));
		this.genReportsButton.setToolTipText("Generate reports");
		genReportsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(canGenerateReports == true) {
				reports += map.generateReport(time).toString();
				reportsArea.setText(reports);
				canGenerateReports = true;
				canClearReports = true;
				canSaveReports = true;
				statusBarText.setText("Reports have been generated.");
				}
				else {
					statusBarText.setText("First run the simulator.");
				}
			}
		});
		toolBar.add(genReportsButton);
	}
	
	private void addClearReportsButton() {
		clearReportsButton = new JButton(new ImageIcon("icons/delete_report.png"));
		this.clearReportsButton.setToolTipText("Delete reports");
		clearReportsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(canClearReports == true) {
					reports = "";
					reportsArea.setText(reports);
					canGenerateReports = false;
					canClearReports = false;
					canSaveReports = false;
					statusBarText.setText("Reports have been cleared.");	
				}
				else {
					statusBarText.setText("First load reports.");
				}
			}
		});
		toolBar.add(clearReportsButton);
	}
	
	private void addSaveReportsButton() {
		saveReportsButton = new JButton(new ImageIcon("icons/save_report.png"));
		this.saveReportsButton.setToolTipText("Save reports");
		saveReportsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(canSaveReports == true) {
					canGenerateReports = true;
					canClearReports = true;
					canSaveReports = true;
					String x = currentFile.getName() + ".out";
					try {
						@SuppressWarnings("resource")
						OutputStream o = new FileOutputStream(new File(x));
						o.write(reportsArea.getText().getBytes());
						statusBarText.setText("Reports have been saved");
					} catch (IOException e1) {
						statusBarText.setText("Reports saved has been cancelled.");
					}
				}
				else {
					statusBarText.setText("First generate reports.");
				}
			}
		});
		toolBar.add(saveReportsButton);
	}
	
	private void addQuitButton() {
		quitButton = new JButton(new ImageIcon("icons/exit.png"));
		this.quitButton.setToolTipText("Close simulator");
		quitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(confirmExit() == true) {
					ctrl.getTraffic().removeObserver(MainWindow.this);
					System.exit(0);
				}
			}
		});
		toolBar.add(quitButton);
	}
	
	private boolean confirmExit() {
		boolean out = false;
		String[] options = { "Yes", "No" };
		int election = JOptionPane.showOptionDialog(mainPanel, "Do you really want to exit?", "Confirm message", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, "Yes");
		if(election == JOptionPane.YES_OPTION) {
			out = true;
		}
		else {
			out = false;
		}
		return out;
	}
	
 	private void addMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		 fileMenu = new JMenu("File");
		 menuBar.add(fileMenu);
		 JMenuItem loadItem = new JMenuItem("Load Events");
		 loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		 loadItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fc.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
				FileFilter filter = new FileNameExtensionFilter("Archivos .ini", "ini");
				fc.setFileFilter(filter);
				fc.showOpenDialog(MainWindow.this);
				File file = fc.getSelectedFile();
				if(file != null) {
					currentFile = file;
					eventsEditor.setText(readFile(currentFile));
					canRun = false;
					canCheckIn = true;
					canClearEvents = false;
					panelEditor.setBorder(new TitledBorder("Events: "+ correctFile(fc.getSelectedFile())));
					statusBarText.setText("File has been load succesfully.");
				}
				else {
					statusBarText.setText("File load interrupted.");
				}
			}
		 });
		 JMenuItem saveEItem = new JMenuItem("Save Events");
		 saveEItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(canClearEvents == true) {
					fc.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
					int op = fc.showSaveDialog(MainWindow.this);
					if (op == JFileChooser.APPROVE_OPTION) {
						try {
							@SuppressWarnings("resource")
							OutputStream fw = new FileOutputStream(fc.getSelectedFile() + ".ini");
					        fw.write(eventsEditor.getText().getBytes());
					        statusBarText.setText("File saved.");
					     } catch (Exception ex) {
					        ex.printStackTrace();
					        statusBarText.setText("An unexpected error occurred.");
					     }
					}
				}
				else {
					statusBarText.setText("First load events");
				}
				
			}
			 
		 });
		 JMenuItem saveRItem = new JMenuItem("Save Report");
		 saveRItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(canSaveReports == true) {
					canGenerateReports = true;
					canClearReports = true;
					canSaveReports = true;
					
					fc.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
					int op = fc.showSaveDialog(MainWindow.this);
					if (op == JFileChooser.APPROVE_OPTION) {
						try {
							@SuppressWarnings("resource")
							OutputStream fw = new FileOutputStream(fc.getSelectedFile() + ".ini.out");
					        fw.write(eventsEditor.getText().getBytes());
					        statusBarText.setText("Reports have been saved.");
					     } catch (Exception ex) {
					        ex.printStackTrace();
					        statusBarText.setText("An unexpected error occurred.");
					     }
					}
				}
				else {
					statusBarText.setText("First generate reports.");
				}				
			}
			 
		 });
		 JMenuItem exitItem = new JMenuItem("Exit");
		 exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(confirmExit() == true) {
					ctrl.getTraffic().removeObserver(MainWindow.this);
					System.exit(0);
				}
			}
		});
		 fileMenu.add(loadItem);
		 saveEItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		 fileMenu.add(saveEItem);
		 fileMenu.addSeparator();
		 saveRItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		 fileMenu.add(saveRItem);
		 fileMenu.addSeparator();
		 exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		 fileMenu.add(exitItem);
		 simulatorMenu = new JMenu("Simulator");
		 menuBar.add(simulatorMenu);
		 JMenuItem runItem = new JMenuItem("Run");
		 runItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(canRun == true) {
						int x = (int) stepsSpinner.getValue();
						ctrl.run(x);
						canRun = true;
						canCheckIn = false;
						canClearEvents = true;
						if(redirect == true) {
							reports += map.generateReport(time).toString();
							reportsArea.setText(reports);
						}
						reportsArea.setText(reports);
					}
					else {
						statusBarText.setText("First load events if simulator is not running.");
					}
					
				} catch (IOException | SimulatorError e) {
					e.printStackTrace();
				}
			}
		 });
		 JMenuItem resetItem = new JMenuItem("Reset");
		 resetItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ctrl.reset();
				eventsEditor.setText("");
				canRun = false;
				canCheckIn = false;
				canClearEvents = false;
				statusBarText.setText("Simulator has been reset.");
				panelEditor.setBorder(new TitledBorder("Events: please load an .ini file"));
			}
		 });		 
		 JCheckBox redirItem = new JCheckBox("Redirect Output", true);
		 redirItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				changeRedirect();
				if(redirect == false) {
					JOptionPane.showMessageDialog(null, "Reports will not be shown in reports area, they will "
							+ "be saved in same place as 'src'.", "Redirect Output", JOptionPane.INFORMATION_MESSAGE);
					try {
						os = new FileOutputStream(new File(currentFile.getName() + ".out"));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				else {
					JOptionPane.showMessageDialog(null, "Reports will be shown in reports area.", "Redirect Output", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			 
		 });
		 runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		 simulatorMenu.add(runItem);
		 resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		 simulatorMenu.add(resetItem);
		 //redirItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		 simulatorMenu.add(redirItem);
		 reportsMenu = new JMenu("Reports");
		 menuBar.add(reportsMenu);
		 JMenuItem geneItem = new JMenuItem("Generate");
		 geneItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		 geneItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(canGenerateReports == true) {
					reports += map.generateReport(time).toString();
					reportsArea.setText(reports);
					canGenerateReports = true;
					canClearReports = true;
					canSaveReports = true;
					statusBarText.setText("Reports have been generated.");
					}
					else {
						statusBarText.setText("First run the simulator.");
					}
			}
		 });
		 JMenuItem clearItem = new JMenuItem("Clear");
		 clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		 clearItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(canClearReports == true) {
					reports = "";
					reportsArea.setText(reports);
					canGenerateReports = false;
					canClearReports = false;
					canSaveReports = false;
					statusBarText.setText("Reports have been cleared.");	
				}
				else {
					statusBarText.setText("First load reports.");
				}
			}
		 });
		 reportsMenu.add(geneItem);
		 reportsMenu.add(clearItem);
		 this.setJMenuBar(menuBar);
	}
	
	@SuppressWarnings("resource")
	public static String readFile(File file) {
		String s = "";
		try {
			s = new Scanner(file).useDelimiter("\\A").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return s;
	}

	private void changeRedirect() {
		if(this.redirect == true) {
			this.redirect = false;
		}
		else {
			this.redirect = true;
		}
	}

	@Override
	public void registered(int time, RoadMap map, List<Event> events) {
		System.out.println("Observer registrated succesfully.");
	}

	@Override
	public void reset(int time, RoadMap map, List<Event> events) {
		this.time = time;
		this.eventsTableModel.setEventsList(events);
		this.junctionTableModel.setJunctionsList(map.getJunctions());
		this.roadsTableModel.setRoadsList(map.getRoads());
		this.vehiclesTableModel.setVehiclesList(map.getVehicles());
		this.reports = "";
		this.reportsArea.setText(this.reports);
		this.timeViewer.setText(String.valueOf(time));
		this.rm.refresh();
	}

	@Override
	public void eventAdded(int time, RoadMap map, List<Event> events) {
		this.events = events;
		this.eventsTableModel.setEventsList(events);
	}

	@Override
	public void advanced(int time, RoadMap map, List<Event> events) {
		this.time = time;
		this.junctionTableModel.setJunctionsList(map.getJunctions());
		this.roadsTableModel.setRoadsList(map.getRoads());
		this.vehiclesTableModel.setVehiclesList(map.getVehicles());
		this.timeViewer.setText(String.valueOf(time));
		this.reports += map.generateReport(time).toString();
		this.rm.refresh();
		if(!this.kmTotal.contains(new KilometrageView(map.getVehicles(), time))&& this.kmTotal.size()>0){
			if(this.kmTotal.get(this.kmTotal.size()-1).getTime() != time){
				this.kmTotal.add(new KilometrageView(map.getVehicles(), time));
			}
		}else{
			this.kmTotal.add(new KilometrageView(map.getVehicles(), time));
		}
		this.kmTableModel.setKmList(this.kmTotal);
	}

	@Override
	public void simulatorError(int time, RoadMap map, List<Event> events, SimulatorError e) {
		JOptionPane.showMessageDialog(null, e, "CRITICAL ERROR!!", JOptionPane.WARNING_MESSAGE);
	}
}