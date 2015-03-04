
package figures.creation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JPanel;

import math.Matrix;
import math.Vector2D;
import math.Vector3D;
import utils.Methods;
import figures.FigAnimation;
import figures.FigPose;
import figures.Figure;
import framework.Eye;

public class PoseCreator extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -8293452421327152836L;

	// ************* Variables ***************
	Figure figure = Figure.createEmptyFigure(15);
	Vector3D temp = new Vector3D();
	ArrayList<FigPose> poses = new ArrayList<FigPose>();

	FigAnimation anim = new FigAnimation();

	Eye eye = new Eye(null, new Dimension(1100, 800));

	boolean running = false;
	public int[] xpoints = new int[figure.struct.NPTS], ypoints = new int[figure.struct.NPTS];

	int current = 0, moveMode = -1;
	/**
	 * moveMode: -1: Do not move vertics 0: translate 1: scale about midpoint 2: rotate about
	 * midpoint
	 */

	HashSet<Integer> selected = new HashSet<Integer>();

	// ******************** Movement and Selection vars* ******

	FigPose frozen = null;
	int beginPressX = -1, beginPressY = -1, currMouseX, currMouseY, actualizeX = -1,
			actualizeY = -1;
	Vector3D midpoint = null;

	boolean lockX = false, lockY = false, lockZ = false;

	HashSet<Integer> keys;

	// / END VARIABLES

	public PoseCreator(HashSet<Integer> k) {
		setBackground(Color.BLACK);
		eye.leash = 4;
		addMouseListener(this);
		addMouseMotionListener(this);

		eye.beta = -3 * Math.PI / 8;

		eye.focus = new Vector3D(0, 0, 0.6);
		eye.update(null, 1.0);

		figure.setAnimation(anim);
		anim.poses = poses;

		this.keys = k;

		poses.add(figure.struct.defaultPose.clone());
	}

	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);

		Graphics2D g = (Graphics2D) gr;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//******************************************* DRAW AXES *******************************
		Vector3D piece1 = eye.toScreen(new Vector3D(1, -1, 0));
		Vector3D piece2 = eye.toScreen(new Vector3D(-1, 1, 0));
		Vector3D piece3 = eye.toScreen(new Vector3D(-1, -1, 2));
		Vector3D base = eye.toScreen(new Vector3D(-1, -1, 0));
		Vector3D origin = eye.toScreen(new Vector3D(0, 0, 0));

		if (piece1 != null && base != null) {
			g.setStroke(new BasicStroke(lockX ? 10 : 4));

			g.setColor(new Color(150, 100, 100));
			g.drawLine(eye.psX(base.x), eye.psY(base.y), eye.psX(piece1.x), eye.psY(piece1.y));
			g.drawString("+x", eye.psX(piece1.x), eye.psY(piece1.y));
		}
		if (piece2 != null && base != null) {
			g.setStroke(new BasicStroke(lockY ? 10 : 4));

			g.setColor(new Color(100, 150, 100));
			g.drawLine(eye.psX(base.x), eye.psY(base.y), eye.psX(piece2.x), eye.psY(piece2.y));
			g.drawString("+y", eye.psX(piece2.x), eye.psY(piece2.y));
		}
		if (piece3 != null && base != null) {
			g.setStroke(new BasicStroke(lockZ ? 10 : 4));

			g.setColor(new Color(100, 100, 150));
			g.drawLine(eye.psX(base.x), eye.psY(base.y), eye.psX(piece3.x), eye.psY(piece3.y));
			g.drawString("+z", eye.psX(piece3.x), eye.psY(piece3.y));
		}
		if (origin != null) {
			g.fillRect(eye.psX(origin.x) - 2, eye.psY(origin.y) - 2, 4, 4);
		}

		g.setStroke(new BasicStroke(1));

		//*************************************** DRAW MOTION BLUR *******************************
		if (!running) {
			for (int h = 1; h < 5; h++) {
				FigPose prevPose = poses.get( (h * poses.size() + current - h)
						% poses.size());

				for (int i = 0; i < figure.struct.NPTS; i++) {
					Vector3D v = eye.toScreen(prevPose.pos[i]);
					if (v == null) {
						xpoints[i] = -1;
						ypoints[i] = -1;
						continue;
					}
					xpoints[i] = eye.psX(v.x);
					ypoints[i] = eye.psY(v.y);
				}

				for (int i = 0; i < figure.struct.lengths.length; i++) {
					if (xpoints[figure.struct.line1[i]] != -1) {
						if (selected.contains(figure.struct.line1[i])
								&& selected.contains(figure.struct.line2[i]))
							g.setColor(Methods.colorMeld(new Color(140, 70, 70), new Color(0, 0, 0,
									0), h / 5D));
						else
							g.setColor(Methods.colorMeld(new Color(70, 70, 140), new Color(0, 0, 0,
									0), h / 5D));
						g.drawLine(xpoints[figure.struct.line1[i]], ypoints[figure.struct.line1[i]],
								xpoints[figure.struct.line2[i]], ypoints[figure.struct.line2[i]]);
					}
				}

				int w = eye.sphereWidth(prevPose.pos[figure.struct.head], figure.struct.headSize);
				if (xpoints[figure.struct.head] != -1)
					g.drawOval(xpoints[figure.struct.head] - w, ypoints[figure.struct.head] - w, 2 * w,
							2 * w);

				for (int i = 0; i < prevPose.pos.length; i++) {
					int r = running ? 1 : 8;
					if (xpoints[i] != -1) {
						if (selected.contains(i) && selected.contains(i))
							g.setColor(Methods.colorMeld(new Color(140, 70, 70), new Color(0, 0, 0,
									0), h / 5D));
						else
							g.setColor(Methods.colorMeld(new Color(70, 70, 140), new Color(0, 0, 0,
									0), h / 5D));
						g.drawOval(xpoints[i] - r, ypoints[i] - r, 2 * r, 2 * r);
					}
				}
			}
		}

		FigPose toDraw = (running ? figure.pose : poses.get(current));

		double[] distances = new double[figure.struct.NPTS];
		double minDist = Double.MAX_VALUE, maxDist = Double.MIN_VALUE;

		for (int i = 0; i < figure.struct.NPTS; i++) {
			Vector3D v = eye.toScreen(toDraw.pos[i]);
			if (v == null) {
				xpoints[i] = -1;
				ypoints[i] = -1;
				continue;
			}
			xpoints[i] = eye.psX(v.x);
			ypoints[i] = eye.psY(v.y);
			distances[i] = eye.faceDist(toDraw.pos[i]);

			if (distances[i] > maxDist)
				maxDist = distances[i];
			if (distances[i] < minDist)
				minDist = distances[i];
		}
		g.setColor(Color.gray);

		for (int i = 0; i < figure.struct.lengths.length; i++) {
			if (xpoints[figure.struct.line1[i]] != -1) {
				if (selected.contains(figure.struct.line1[i])
						&& selected.contains(figure.struct.line2[i]))
					g.setColor(new Color(255, 150, 0));
				else
					g.setColor(Color.gray);

				g.drawLine(xpoints[figure.struct.line1[i]], ypoints[figure.struct.line1[i]],
						xpoints[figure.struct.line2[i]], ypoints[figure.struct.line2[i]]);
			}
		}

		if (selected.contains(figure.struct.head))
			g.setColor(new Color(255, 150, 0));
		else
			g.setColor(Color.gray);

		int w = eye.sphereWidth(figure.pose.pos[figure.struct.head], figure.struct.headSize);
		if (xpoints[figure.struct.head] != -1)
			g.drawOval(xpoints[figure.struct.head] - w, ypoints[figure.struct.head] - w, 2 * w, 2 * w);

		for (int i = 0; i < toDraw.pos.length; i++) {
			int r = running ? 1 : 8;
			if (xpoints[i] != -1) {
				g.setColor(selected.contains(i) ? new Color(255, 150, 0, 100) : //
						Methods.colorMeld(Color.WHITE, Color.DARK_GRAY,
								(distances[i] - minDist + .05) / (maxDist - minDist + .10), 100));
				g.fillOval(xpoints[i] - r, ypoints[i] - r, 2 * r, 2 * r);

				g.setColor(selected.contains(i) ? new Color(255, 150, 0) : Color.BLACK);
				g.drawOval(xpoints[i] - r, ypoints[i] - r, 2 * r, 2 * r);
			}
		}

		if (beginPressX > 0 || beginPressY > 0) {
			g.setColor(new Color(255, 150, 0, 30));

			g.fillRect(Math.min(beginPressX, currMouseX), Math.min(beginPressY, currMouseY),
					Math.abs(currMouseX - beginPressX), Math.abs(currMouseY - beginPressY));
			g.setColor(new Color(255, 150, 0));

			g.drawRect(Math.min(beginPressX, currMouseX), Math.min(beginPressY, currMouseY),
					Math.abs(currMouseX - beginPressX), Math.abs(currMouseY - beginPressY));
		}
	}

	public void keyPress(int key) {
		if (key == KeyEvent.VK_G) {
			actualizeX = currMouseX;
			actualizeY = currMouseY;
			frozen = poses.get(current).clone();
			moveMode = 0;
		} else if (key == KeyEvent.VK_S) {
			actualizeX = currMouseX;
			actualizeY = currMouseY;
			frozen = poses.get(current).clone();
			moveMode = 1;
		} else if (key == KeyEvent.VK_R) {
			actualizeX = currMouseX;
			actualizeY = currMouseY;
			frozen = poses.get(current).clone();
			moveMode = 2;
		} else if (key == KeyEvent.VK_A) {
			moveMode = -1;
			boolean fullSelection = true;
			for (int i = 0; i < figure.struct.NPTS; i++)
				if (!selected.contains(i)) {
					selected.add(i);
					fullSelection = false;
				}

			if (fullSelection)
				selected.clear();
			calculateMidpoint();
		} else if (key == KeyEvent.VK_X && actualizeX != -1) {
			lockX = !lockX;
		} else if (key == KeyEvent.VK_Y && actualizeX != -1) {
			lockY = !lockY;
		} else if (key == KeyEvent.VK_Z && actualizeX != -1) {
			lockZ = !lockZ;
		} else if (key == KeyEvent.VK_ESCAPE && frozen != null) {
			poses.set(current, frozen);
			frozen = null;
			moveMode = -1;
			actualizeX = -1;
			actualizeY = -1;
			lockX = lockY = lockZ = false;
		}
	}

	public void mouseMoved(MouseEvent evt) {
		if (!selected.isEmpty() && frozen != null) {
			boolean locked = ! (lockX || lockY || lockZ);
			if (moveMode == 0) {
				for (int select : selected) {
					double d = eye.faceDist(frozen.pos[select]);
					Vector3D v = eye.pickDist(evt.getX(), evt.getY(), d)
							.sub(eye.pickDist(actualizeX, actualizeY, d))
							.add(frozen.pos[select]);
					poses.get(current).pos[select].set(v, locked || lockX, locked || lockY,
							locked || lockZ);
					calculateMidpoint();
				}
			} else if (moveMode == 1) {
				for (int select : selected) {
					double d = eye.faceDist(frozen.pos[select]);
					double factor = eye.pickDist(evt.getX(), evt.getY(), d).sub(midpoint)
							.magnitude()
							/ eye.pickDist(actualizeX, actualizeY, d).sub(midpoint).magnitude();
					Vector3D v = (frozen.pos[select].clone().sub(midpoint)).scale(factor)
							.add(midpoint);
					poses.get(current).pos[select].set(v, locked || lockX, locked || lockY,
							locked || lockZ);
				}
			} else if (moveMode == 2) {
				for (int select : selected) {
					// double d = eye.faceDist(frozen.pos[select]);
					Vector3D screenmp = eye.toScreen(midpoint);
					double angle = Math.atan2(evt.getY() - eye.psY(screenmp.y),
							evt.getX() - eye.psX(screenmp.x))
							- Math.atan2(actualizeY - eye.psY(screenmp.y),
									actualizeX - eye.psX(screenmp.x));
					Vector3D v = Matrix
							.createArbitraryRotationMatrix(angle, eye.calculateDirection())
							.applyTo(frozen.pos[select].clone().sub(midpoint)).add(midpoint);
					poses.get(current).pos[select].set(v, locked || lockX, locked || lockY,
							locked || lockZ);
				}
			}
		}

		currMouseX = evt.getX();
		currMouseY = evt.getY();

	}

	public void mouseClicked(MouseEvent evt) {}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent evt) {
		if (evt.isMetaDown()) {
			if (frozen != null)
				poses.set(current, frozen);
		} else {
			int mx = evt.getX(), my = evt.getY();
			for (int i = 0; i < xpoints.length; i++) {
				if ( (xpoints[i] - mx) * (xpoints[i] - mx) + (ypoints[i] - my) * (ypoints[i] - my) < 8 * 8) {
					if (! (keys.contains(KeyEvent.VK_CONTROL) || keys.contains(KeyEvent.VK_SHIFT)))
						selected.clear();
					if (keys.contains(KeyEvent.VK_SHIFT))
						selected.remove(i);
					else
						selected.add(i);

					calculateMidpoint();
					break;
				}
			}
		}

		moveMode = -1;
		frozen = null;
		actualizeX = -1;
		actualizeY = -1;
		beginPressX = currMouseX = evt.getX();
		beginPressY = currMouseY = evt.getY();
		lockX = lockY = lockZ = false;
	}

	public void mouseReleased(MouseEvent evt) {
		int minx = Math.min(beginPressX, currMouseX), maxx = Math.max(beginPressX, currMouseX), miny = Math
				.min(beginPressY, currMouseY), maxy = Math.max(beginPressY, currMouseY);

		if ( (minx - maxx) * (minx - maxx) + (miny - maxy) * (miny - maxy) > 200) {
			if (! (keys.contains(KeyEvent.VK_CONTROL) || keys.contains(KeyEvent.VK_SHIFT)))
				selected.clear();
			for (int i = 0; i < xpoints.length; i++) {
				if (xpoints[i] > minx && xpoints[i] < maxx && ypoints[i] > miny
						&& ypoints[i] < maxy)
					if (keys.contains(KeyEvent.VK_SHIFT))
						selected.remove(i);
					else
						selected.add(i);
			}
		}

		calculateMidpoint();

		beginPressX = -1;
		beginPressY = -1;
	}

	@Override
	public void mouseDragged(MouseEvent evt) {
		currMouseX = evt.getX();
		currMouseY = evt.getY();
		if (evt.isMetaDown()) {
			int dx = evt.getX() - beginPressX, dy = evt.getY() - beginPressY;
			eye.alpha -= dx / 100d;
			eye.beta += dy / 100d;
			eye.beta = Math.min(Math.max(-Math.PI, eye.beta), 0);

			beginPressX = evt.getX();
			beginPressY = evt.getY();
		} else {

		}
	}

	public void calculateMidpoint() {
		if (selected.size() == 0) {
			midpoint = null;
			return;
		}

		Vector3D tot = new Vector3D();

		for (int i : selected)
			tot.add(poses.get(current).pos[i]);

		midpoint = tot.scale(1D / selected.size());
	}
}
