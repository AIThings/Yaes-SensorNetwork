package yaes.sensornetwork.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.identification.IntruderNodeType;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.ui.format.HtmlFormatter;
import yaes.ui.text.TextUi;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.painters.PainterHelper;
import yaes.ui.visualization.painters.paintMobileNode;

/**
 * Implements a painter for a mobile node in the sensor network (typically used
 * for intruders). This painter uses icons
 * 
 * @author Lotzi Boloni <lboloni@eecs.ucf.edu>
 * 
 */
public class paintIntruderIcon extends paintMobileNode implements Serializable {

	protected final String vpropLightUpWhenOverheard = "vpropLightUpWhenOverheard";
	boolean graphicPaint = true;
	private Map<IntruderNodeType, Image> icons = new HashMap<IntruderNodeType, Image>();
	private String iconPostFix = "30"; // small, med

	public paintIntruderIcon(SensorNetworkWorld sensingManager) {
		super(15, Color.black, Color.red);
		if (graphicPaint) {
			addIcon(IntruderNodeType.ANIMAL, "rabbit");
//			addIcon(IntruderNodeType.ANIMAL, "raccoon");
			addIcon(IntruderNodeType.FRIENDLY_HUMAN, "hiker");
			addIcon(IntruderNodeType.INTRUDER_HUMAN, "spy");
			addIcon(IntruderNodeType.SMALL_UGV, "UGV");
			addIcon(IntruderNodeType.VEHICLE, "tank");
			// addIcon(IntruderNodeType.UNIDENTIFIED, "question");
		}
	}

	private void addIcon(IntruderNodeType type, String iconName) {
		try {
			InputStream is = getClass().getResourceAsStream(
					"images/" + iconName + "-" + iconPostFix + ".png");
			// URL url = getClass().getResource("images/" + iconName + ".png");
			Image img = ImageIO.read(is);
			if (img != null) {
				icons.put(type, img);
			} else {
				TextUi.errorPrint("Could not load" + iconName);
			}
		} catch (IOException e) {
			TextUi.errorPrint("Could not load " + iconName);
		}
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		IntruderNode node = (IntruderNode) o;
		if (!graphicPaint) {
			HtmlFormatter fmt = new HtmlFormatter("");
			StringBuffer buf = new StringBuffer();
			buf.append("padding: 0;");
			buf.append("margin: 0;");
			buf.append("background-color: white;");
			buf.append("border-style: solid;");
			buf.append("border-color: black;");
			buf.append("border-width: 2px;");
			buf.append("width: 18px;");
			buf.append("height: 18px;");
			buf.append("vertical-align: middle;");
			String style = "style='" + buf.toString() + "'";
			fmt.openHtml(style);
			buf = new StringBuffer();
			buf.append("padding: 0;");
			buf.append("margin: 0;");
			buf.append("background-color: white;");
			buf.append("text-align: center;");
			buf.append("color: red;");
			buf.append("font-family: arial;");
//			buf.append("font-size: 15pt;");
			buf.append("font-size: 12pt;");
			buf.append("font-weight: 900;");			
			style = "style='" + buf.toString() + "'";
//			fmt.addDiv("R", style);
			fmt.addDiv("IX", style);
			fmt.closeHtml();
			String htmlText = fmt.toString();
			PainterHelper.paintHtmlLabel(htmlText, node.getLocation(), true, g,
					panel);
			// super.paint(g, o, panel);
		} else {
			Image img = icons.get(node.getIntruderNodeType());
			PainterHelper.paintImage(node.getLocation(), img, g, panel);
			if (paintLabel) {
				paintLabel(o, node.getLocation(), g, panel);
			}
		}
	}

}
