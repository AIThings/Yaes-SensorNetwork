package yaes.sensornetwork.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.Serializable;

import yaes.sensornetwork.scenarios.bridgeprotection.CatastrophicEvent;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.PaintSpec;
import yaes.ui.visualization.painters.PainterHelper;

/**
 * Paints a catastrophic event
 * 
 * @author Lotzi Boloni
 * 
 */
public class paintCatastrophicEvent implements IPainter, Serializable {


	private static final long serialVersionUID = -1413325070027223356L;
	private PaintSpec activeSpec = null;
	//private PaintSpec notactiveSpec = null;

	public paintCatastrophicEvent() {
		//activeSpec = PaintSpec.createDraw(Color.WHITE);
		activeSpec = PaintSpec.createFill(Color.DARK_GRAY);
		// notactiveSpec = PaintSpec.createFill(Color.LIGHT_GRAY.brighter());
	}

	@Override
	public int getLayer() {
		return BACKGROUND_EVENT_LAYER;
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		CatastrophicEvent ce = (CatastrophicEvent) o;
		if (ce.isActive()) {
			for (Shape s : ce.getShapes()) {
				PainterHelper.paintShape(s, activeSpec, g, panel);
			}
		} else {
			/*
			for (Shape s : ce.getShapes()) {
				PainterHelper.paintShape(s, notactiveSpec, g, panel);
			}
			*/
		}
	}

	@Override
	public void registerParameters(
			VisualizationProperties visualizationProperties) {
		// TODO Auto-generated method stub

	}

}
