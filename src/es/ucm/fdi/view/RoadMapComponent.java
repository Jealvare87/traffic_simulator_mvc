package es.ucm.fdi.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import es.ucm.fdi.model.Junction;
import es.ucm.fdi.model.Junction.IncomingRoad;
import es.ucm.fdi.model.Road;
import es.ucm.fdi.model.RoadMap;
import es.ucm.fdi.model.Vehicle;

public class RoadMapComponent extends JComponent {

	private static final long serialVersionUID = 1L;

	private static final int _junctionRadius = 20;
	private static final int _vehicleRadius = 5;

	private class Point {
		int cX;
		int cY;
		int tX;
		int tY;

		public Point(int cX, int cY, int tX, int tY) {
			this.cX = cX;
			this.cY = cY;
			this.tX = tX;
			this.tY = tY;
		}
	}

	private RoadMap _map;
	private Map<String, Point> _nodesPisitions;
	private int _lastWidth;
	private int _lastHeight;

	RoadMapComponent() {
		_nodesPisitions = new HashMap<>();
		setMinimumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(500, 500));
		_lastWidth = -1;
		_lastHeight = -1;
	}

	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		if (_map == null || _map.getJunctions().size() == 0) {
			g.setColor(Color.red);
			g.drawString("No map yet!", getWidth() / 2 - 50, getHeight() / 2);
		} else {
			drawMap(g);
		}
	}

	private void drawMap(Graphics g) {

		// if the size of the component has changed since the last time we
		// calculated the positions of the nodes, then we recalculate again.
		// This way the map get scaled down/up.
		if (_lastHeight != getHeight() || _lastWidth != getWidth()) {
			_lastHeight = getHeight();
			_lastWidth = getWidth();
			calculateJunCoordinates();
		}

		// draw nodes
		for (Junction j : _map.getJunctions()) {
			Point p = _nodesPisitions.get(j.getId());
			g.setColor(Color.blue);
			g.fillOval(p.cX - _junctionRadius / 2, p.cY - _junctionRadius / 2, _junctionRadius, _junctionRadius);
			g.setColor(Color.black);
			g.drawString(j.getId(), p.tX, p.tY);
		}

		// draw edges
		for (Road e : _map.getRoads()) {
			Point p1 = _nodesPisitions.get(e.getSource().getId());
			Point p2 = _nodesPisitions.get(e.getDestination().getId());

			// draw the edge
			Color arrowColor = hasGreen(e) ? Color.GREEN : Color.RED;
			drawArrowLine(g, p1.cX, p1.cY, p2.cX, p2.cY, 15, 5, Color.BLACK, arrowColor);

			// draw dots as circles. Dots at the same location are drawn with circles of
			// different diameter.
			int lastLocation = -1;
			int diam = _vehicleRadius;
			for (Vehicle d : e.getVehicles()) {
				if (lastLocation != d.getLocation()) {
					lastLocation = d.getLocation();
					diam = _vehicleRadius;
				} else {
					diam += _vehicleRadius;
				}
				Color dotColor = d.getFaulyTime() > 0.5 ? Color.RED : Color.MAGENTA;
				drawCircleOnALine(g, p1.cX, p1.cY, p2.cX, p2.cY, e.getLength(), d.getLocation(), diam, dotColor,
						d.getId());
			}
		}
	}

	private boolean hasGreen(Road e) {
		for (IncomingRoad r : e.getDestination().getRoadsInfo()) {
			if (r.getRoad().equals(e)) {
				if (r.hasGreenLight())
					return true;
				else
					return false;
			}
		}
		// never reached in principle
		return false;
	}

	/**
	 * put the objects in a circle, for each one store the center coordinate and a
	 * coordinate for a corresponding text.
	 */
	private void calculateJunCoordinates() {

		int r = Math.min(_lastHeight, _lastWidth) / 2 - _junctionRadius - 50; // 50 for
																			// text
		int tr = (r + _junctionRadius + 10);

		int xc = _lastWidth / 2 - 10;
		int yc = _lastHeight / 2 - 10;

		double slice = 2 * Math.PI / _map.getJunctions().size();
		int i = 0;
		for (Junction n : _map.getJunctions()) {

			double angle = slice * i;
			int cX = (int) (xc + r * Math.cos(angle));
			int cY = (int) (yc + r * Math.sin(angle));
			int tX = (int) (xc + tr * Math.cos(angle));
			int tY = (int) (yc + tr * Math.sin(angle));

			_nodesPisitions.put(n.getId(), new Point(cX, cY, tX, tY));
			i++;
		}

	}

	/**
	 * Draws a circle on the line from (x1,y1) to (x2,y2). Assuming the (virtual)
	 * length of the line is virtualLength, the circles is drawn at location
	 * virtualLocation (0..virtualLength). The diameter is 'diam'
	 */
	private void drawCircleOnALine(Graphics g, int x1, int y1, int x2, int y2, int virtualLength, int virtualLocation,
			int diam, Color c, String txt) {

		// The actual length of the line
		double lineActualLength = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) - 45;

		// the angle of the line with the horizontal axis
		double alpha = Math.atan(((double) Math.abs(x1 - x2)) / ((double) Math.abs(y1 - y2)));

		// the actual location on the line (0..lineActualLength)
		double actualLocation = lineActualLength * ((double) virtualLocation) / ((double) virtualLength) + 15;

		// the coordinates of the location
		double x = Math.sin(alpha) * actualLocation;
		double y = Math.cos(alpha) * actualLocation;

		// signs repressing the direction of the line (left/right, up/down)
		int xDir = x1 < x2 ? 1 : -1;
		int yDir = y1 < y2 ? 1 : -1;

		// draw the point
		g.setColor(c);
		g.drawOval(x1 + xDir * ((int) x) - diam / 2, y1 + yDir * ((int) y) - diam / 2, diam, diam);

		// draw the text
		g.setColor(Color.darkGray);
		g.drawString(txt, x1 + xDir * ((int) x) - diam / 2, y1 + yDir * ((int) y) - diam / 2);
	}

	/**
	 * Draws a line from (x1,y1) to (x2,y2) with an arrow of width d and height h.
	 * The color of the line is lineColor and that of the arrow is arrowColor.
	 */
	private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h, Color lineColor,
			Color arrowColor) {
		int dx = x2 - x1, dy = y2 - y1;
		double D = Math.sqrt(dx * dx + dy * dy);
		double xm = D - d, xn = xm, ym = h, yn = -h, x;
		double sin = dy / D, cos = dx / D;

		x = xm * cos - ym * sin + x1;
		ym = xm * sin + ym * cos + y1;
		xm = x;

		x = xn * cos - yn * sin + x1;
		yn = xn * sin + yn * cos + y1;
		xn = x;

		int[] xpoints = { x2, (int) xm, (int) xn };
		int[] ypoints = { y2, (int) ym, (int) yn };

		g.setColor(lineColor);
		g.drawLine(x1, y1, x2, y2);
		g.setColor(arrowColor);
		g.fillPolygon(xpoints, ypoints, 3);
	}

	public void setMap(RoadMap map) {
		_map = map;
		calculateJunCoordinates();
		refresh();
	}

	public void refresh() {
		repaint();
	}

}