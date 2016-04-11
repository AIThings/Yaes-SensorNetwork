package yaes.sensornetwork.agents.directeddiffusion;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yaes.sensornetwork.model.SensorNode;

/**
 * This is a class which has a collection of factory functions which create
 * specific sink and sensor agents of the type directed diffusion
 * 
 * @author Administrator
 * 
 */
public class DirectedDiffusionAgentFactory {

	/**
	 * Create a set of rectangles which are covering the border of an area
	 * 
	 * @param initX
	 * @param initY
	 * @param finX
	 * @param finY
	 * @param xDivisions
	 *            the number of steps to divide the x side
	 * @param yDivisions
	 *            the number of steps to divide the y side
	 * @return
	 */
	public static List<Rectangle2D.Double> createBorderAreas(
			Rectangle2D.Double rect, int xDivisions, int yDivisions) {
		List<Rectangle2D.Double> interestRectangles = new ArrayList<Rectangle2D.Double>();
		double width = rect.getWidth() / xDivisions;
		double height = rect.getHeight() / yDivisions;
		// make the rectanges on the x borders
		double xLower = rect.getX();
		double yLower = rect.getY();
		double yUpper = (double) (yDivisions - 1) / (double) yDivisions
				* rect.getHeight() + rect.getY();
		for (int i = 0; i != xDivisions; i++) {
			Rectangle2D.Double rectlower = new Rectangle2D.Double(xLower,
					yLower, width, height);
			interestRectangles.add(rectlower);
			Rectangle2D.Double rectupper = new Rectangle2D.Double(xLower,
					yUpper, width, height);
			interestRectangles.add(rectupper);
			xLower = xLower + width;
		}
		xLower = rect.getX();
		yLower = rect.getY() + height;
		double xUpper = (double) (xDivisions - 1) / (double) xDivisions
				* rect.getWidth() + rect.getX();
		for (int i = 1; i != xDivisions - 1; i++) {
			Rectangle2D.Double rectlower = new Rectangle2D.Double(xLower,
					yLower, width, height);
			interestRectangles.add(rectlower);
			Rectangle2D.Double rectupper = new Rectangle2D.Double(xUpper,
					yLower, width, height);
			interestRectangles.add(rectupper);
			yLower = yLower + height;
		}
		return interestRectangles;
	}

	/**
	 * Creates an interest rectangle of width size around every node
	 * 
	 * @param nodes
	 * @param size
	 * @return
	 */
	public static List<Rectangle2D.Double> createInterestRectanglesAroundNodes(
			List<SensorNode> nodes, double size) {
		List<Rectangle2D.Double> interestRectangles = new ArrayList<Rectangle2D.Double>();
		for (SensorNode node : nodes) {
			Rectangle2D.Double rect = new Rectangle2D.Double(node.getLocation()
					.getX()
					- size / 2, node.getLocation().getY() - size / 2, size,
					size);
			interestRectangles.add(rect);
		}
		return interestRectangles;
	}

	/**
	 * 
	 * Create a number of randomly placed interest areas of the specified size
	 * in a rectangle. Try to assure that the rectangles do not intersect each
	 * other.
	 * 
	 * @param rect
	 * @param numOfRect
	 * @param xstep
	 * @param ystep
	 * @return
	 */
	public static List<Rectangle2D.Double> createRandomAreas(
			Rectangle2D.Double rect, int numOfRect, double xstep, double ystep) {
		// Generate arbitrary number of rectangles in the grid placed
		// randomly
		// Assume size of xstep, ystep
		Random generator = new Random();
		List<Rectangle2D.Double> interestRectangles = new ArrayList<Rectangle2D.Double>();
		int count = 0;
		int maxCount = 2000;
		while (true) {
			if (interestRectangles.size() == numOfRect) {
				return interestRectangles;
			}
			if (count > maxCount) {
				throw new Error("Even after " + maxCount
						+ " tries, I could not generate " + numOfRect
						+ " non-intersecting rectangles. I am giving up.");
			}
			boolean isOk = true;
			double x = rect.getMinX() + generator.nextDouble()
					* rect.getWidth();
			double y = rect.getMinY() + generator.nextDouble()
					* rect.getHeight();
			Rectangle2D.Double newRect = new Rectangle2D.Double(x, y, xstep,
					ystep);
			for (Rectangle2D.Double testRect : interestRectangles) {
				if (testRect.intersects(newRect)) {
					isOk = false;
					break;
				}
			}
			if (isOk) {
				interestRectangles.add(newRect);
			}
		}
	}

	/**
	 * Creates a directed diffusion agent with only one interest rectangle. ---
	 * which we assume to be at 100, 100, 150, 150
	 * 
	 * @return
	 */
	public static List<Rectangle2D.Double> createSingleInterestRectangle() {
		List<Rectangle2D.Double> interestRectangles = new ArrayList<Rectangle2D.Double>();
		for (int i = 0; i != 8; i++) {
			for (int j = 0; j != 8; j++) {
				Rectangle2D.Double rect = new Rectangle2D.Double(200 + i * 50,
						200 + j * 50, 50, 50);
				interestRectangles.add(rect);
			}
		}
		return interestRectangles;
	}

}
