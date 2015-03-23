
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

	// private ReentrantLock lock = new ReentrantLock();

	public PoseCreator(HashSet<Integer> k) {
		setBackground(Color.BLACK);
		eye.leash = 4;
		addMouseListener(this);
		addMouseMotionListener(this);

		eye.beta = -3 * Math.PI / 8;

		eye.focus = new Vector3D(0, 0, 1);
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

		// lock.lock();
		eye.synchronize();
		// lock.unlock();

		// ******************************************* DRAW AXES *******************************
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

		// ************************ DRAW MOTION BLUR ************************
		// if (!running) {
		// for (int h = 1; h < 5; h++) {
		// FigPose prevPose = poses.get( (h * poses.size() + current - h) % poses.size());
		// paintFigure(g, prevPose, ColorScheme.createFaded(h / 5D));
		// }
		// }

		// ************* NOW draw current pose *****************************
		FigPose toDraw = (running ? figure.pose : poses.get(current));
		paintFigure(g, toDraw, ColorScheme.createActive());

		// ********************* draw the selection ************************
		if (beginPressX > 0 || beginPressY > 0) {
			g.setColor(new Color(255, 150, 0, 30));

			g.fillRect(Math.min(beginPressX, currMouseX), Math.min(beginPressY, currMouseY),
					Math.abs(currMouseX - beginPressX), Math.abs(currMouseY - beginPressY));
			g.setColor(new Color(255, 150, 0));

			g.drawRect(Math.min(beginPressX, currMouseX), Math.min(beginPressY, currMouseY),
					Math.abs(currMouseX - beginPressX), Math.abs(currMouseY - beginPressY));
		}
	}

	private void paintFigure(Graphics g, FigPose toDraw, ColorScheme cs) {
		double[] distances = new double[figure.struct.NPTS];
		double minDist = Double.MAX_VALUE, maxDist = Double.MIN_VALUE;

		// ... compute positions,
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

		// ... draw the lines,
		for (int i = 0; i < figure.struct.NLINES; i++) {
			if (xpoints[figure.struct.line1[i]] != -1) {
				if (selected.contains(figure.struct.line1[i])
						&& selected.contains(figure.struct.line2[i]))
					g.setColor(cs.get("SELECTED"));
				else
					g.setColor(cs.get("UNSELECTED"));

				g.drawLine(xpoints[figure.struct.line1[i]], ypoints[figure.struct.line1[i]],
						xpoints[figure.struct.line2[i]], ypoints[figure.struct.line2[i]]);
			}
		}

		// ... draw circles
		for (int i = 0; i < toDraw.pos.length; i++) {
			int r = running ? 1 : 8;
			if (xpoints[i] != -1) {
				// draw interior of circle
				g.setColor(selected.contains(i) ? cs.get("SELECTED_TRANSLUCENT") : //
						Methods.colorMeld(cs.get("CLOSE_POINT"), cs.get("FAR_POINT"), (distances[i]
								- minDist + .05)
								/ (maxDist - minDist + .10), 100));
				g.fillOval(xpoints[i] - r, ypoints[i] - r, 2 * r, 2 * r);

				// draw exterior of circle
				g.setColor(selected.contains(i) ? cs.get("SELECTED") : cs.get("BACKGROUND"));
				g.drawOval(xpoints[i] - r, ypoints[i] - r, 2 * r, 2 * r);

				// for all positive radius points, draw concentric circle
				if (figure.struct.ptsizes[i] > 0) {
					int w = eye.sphereWidth(toDraw.pos[i], figure.struct.ptsizes[i]);
					if (xpoints[i] != -1) {
						g.setColor(cs.get(selected.contains(i)?"SELECTED":"UNSELECTED"));
						g.drawOval(xpoints[i] - w, ypoints[i] - w, 2 * w, 2 * w);
					}
				}

			}
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

	public void update(boolean orthoS, boolean followS, int value) {
		if (keys.contains(KeyEvent.VK_RIGHT))
			eye.alpha -= 0.03;
		if (keys.contains(KeyEvent.VK_LEFT))
			eye.alpha += 0.03;
		if (keys.contains(KeyEvent.VK_DOWN))
			if (eye.beta < 0)
				eye.beta += 0.02;
			else
				eye.beta = 0;
		if (keys.contains(KeyEvent.VK_UP))
			if (eye.beta > -Math.PI)
				eye.beta -= 0.02;
			else
				eye.beta = -Math.PI;

		if (keys.contains(KeyEvent.VK_NUMPAD7)) {
			eye.alpha += (0 - eye.alpha) / 3;
			eye.beta += (0 - eye.beta) / 3;
		} else if (keys.contains(KeyEvent.VK_NUMPAD1)) {
			eye.alpha += (0 - eye.alpha) / 3;
			eye.beta += (-Math.PI / 2 - eye.beta) / 3;
		} else if (keys.contains(KeyEvent.VK_NUMPAD3)) {
			eye.alpha += (-Math.PI / 2 - eye.alpha) / 3;
			eye.beta += (-Math.PI / 2 - eye.beta) / 3;
		}

		eye.setScreen(getWidth(), getHeight());

		if (orthoS)
			eye.ortho += (1 - eye.ortho) / 10;
		else
			eye.ortho += -eye.ortho / 10;

		eye.leash += (value / 10D - eye.leash) / 5;

		if (moveMode == -1) {
			if (followS && midpoint != null)
				eye.focusApproach(midpoint, 1);
			eye.updatePosition();
			eye.update(null, 1.0);
		}
	}

	public void mouseMoved(MouseEvent evt) {
		if (!selected.isEmpty() && frozen != null) {
			boolean locked = ! (lockX || lockY || lockZ);
			if (moveMode == 0) {
				for (int select : selected) {
					double d = eye.faceDist(frozen.pos[select]);
					Vector3D v = eye.pickDist(evt.getX(), evt.getY(), d)
							.sub(eye.pickDist(actualizeX, actualizeY, d)).add(frozen.pos[select]);
					poses.get(current).pos[select].set(v, locked || lockX, locked || lockY, locked
							|| lockZ);
					calculateMidpoint();
				}
			} else if (moveMode == 1) {
				for (int select : selected) {
					double d = eye.faceDist(frozen.pos[select]);
					double factor = eye.pickDist(evt.getX(), evt.getY(), d).sub(midpoint)
							.magnitude()
							/ eye.pickDist(actualizeX, actualizeY, d).sub(midpoint).magnitude();
					Vector3D v = (frozen.pos[select].clone().sub(midpoint)).scale(factor).add(
							midpoint);
					poses.get(current).pos[select].set(v, locked || lockX, locked || lockY, locked
							|| lockZ);
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
					poses.get(current).pos[select].set(v, locked || lockX, locked || lockY, locked
							|| lockZ);
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
