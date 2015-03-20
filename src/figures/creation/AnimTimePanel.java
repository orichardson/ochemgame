package figures.creation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JPanel;

import figures.FigPose;

public class AnimTimePanel extends JPanel implements MouseListener,
		MouseMotionListener,
		ActionListener {
	private static final long serialVersionUID = -586712284739201373L;

	HashSet<Integer> selected = new HashSet<Integer>();
	HashSet<Integer> keys = new HashSet<Integer>();
	PoseCreator pc;
	AnimationCreator ac;

	double pixCurrent = 30;
	int beginPressX = -1, beginPressY = -1, currMouseX, currMouseY;
	int frozenX;

	JButton animate = new JButton("Animate");
	JButton interp = new JButton("Frame Interpolate");

	AnimTimePanel(PoseCreator pc, AnimationCreator ac) {
		setLayout(null);
		setBackground(Color.DARK_GRAY);
		this.pc = pc;
		this.ac = ac;
		this.keys = pc.keys;

		setPreferredSize(new Dimension(600, 150));

		addMouseListener(this);
		addMouseMotionListener(this);

		animate.setBounds(10, 120, 100, 28);
		animate.setFocusable(false);
		animate.setBackground(Color.pink);
		animate.addActionListener(this);

		interp.setBounds(110, 120, 140, 28);
		interp.setFocusable(false);
		interp.setBackground(Color.pink);
		interp.addActionListener(this);

		add(animate);
		add(interp);
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == animate) {
			if (!pc.running) {
				pc.running = true;
				ac.enableButtons(false);
				animate.setText("Stop");
				animate.setBackground(Color.CYAN);

				interp.setEnabled(false);
				interp.setBackground(Color.DARK_GRAY);
			} else {
				pc.running = false;
				ac.enableButtons(true);
				animate.setText("Animate");
				animate.setBackground(Color.PINK);

				interp.setEnabled(true);
				interp.setBackground(Color.PINK);
			}
		} else if (evt.getSource() == interp) {
			ArrayList<Integer> toDo = new ArrayList<Integer>();
			for (int i : selected)
				if (selected.contains(i + 1))
					toDo.add(i);

			for (int i = 0; i < toDo.size(); i++) {
				FigPose p1 = pc.poses.get(i), p2 = pc.poses.get(i + 1);
				FigPose p = FigPose.linearMeld(p1, p2, 0.5);

				pc.poses.add(i + 1, p);
				if (pc.current > i)
					pc.current++;
			}
		}
	}

	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);

		Graphics2D g = (Graphics2D) gr;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < pc.poses.size(); i++) {
			int size = getRadius();
			g.setColor(Color.BLACK);
			g.fillOval(getX(i) - size, getY(i) - size, 2 * size, 2 * size);
			g.setColor(i == pc.current ? Color.MAGENTA
					: (selected.contains(i) ? new Color(0, 150,
							255) : Color.gray));
			g.drawOval(getX(i) - size, getY(i) - size, size * 2, size * 2);
		}

		if (beginPressY > 0) {
			g.setColor(new Color(0, 150, 255, 30));

			g.fillRect(Math.min(beginPressX, currMouseX),
					Math.min(beginPressY, currMouseY),
					Math.abs(currMouseX - beginPressX),
					Math.abs(currMouseY - beginPressY));
			g.setColor(new Color(0, 150, 255));
			g.drawRect(Math.min(beginPressX, currMouseX),
					Math.min(beginPressY, currMouseY),
					Math.abs(currMouseX - beginPressX),
					Math.abs(currMouseY - beginPressY));
		}
	}

	public void update() {
		repaint();
	}

	public int getX(int n) {
		return n * (getRadius() * 2 + 2) + (int) pixCurrent;
	}

	public int getY(int n) {
		return 20;
	}

	public int getRadius() {
		return Math.min((getWidth() - 100) / pc.poses.size(), 30) / 2;
	}

	public void mouseDragged(MouseEvent evt) {
		currMouseX = evt.getX();
		currMouseY = evt.getY();

		if (evt.isMetaDown())
			pixCurrent = frozenX + evt.getX() - beginPressX;
	}

	public void mouseMoved(MouseEvent arg0) {}

	public void mouseClicked(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent evt) {
		currMouseX = evt.getX();
		currMouseY = evt.getY();
		beginPressX = evt.getX();

		int mx = evt.getX(), my = evt.getY();
		for (int i = 0; i < pc.poses.size(); i++) {
			double xi = getX(i), yi = getY(i), r = getRadius();
			if ((xi - mx) * (xi - mx) + (yi - my) * (yi - my) < r * r) {
				pc.current = i;
			}
		}

		if (evt.isMetaDown())
			frozenX = (int) pixCurrent;
		else {
			beginPressY = evt.getY();
		}

	}

	public void mouseReleased(MouseEvent evt) {
		if (!evt.isMetaDown()) {
			int minx = Math.min(beginPressX, currMouseX), maxx =
					Math.max(beginPressX, currMouseX), miny = Math
					.min(beginPressY, currMouseY), maxy =
					Math.max(beginPressY, currMouseY);

			if ((minx - maxx) * (minx - maxx) + (miny - maxy) * (miny - maxy) > 200) {
				if (!(keys.contains(KeyEvent.VK_CONTROL) || keys
						.contains(KeyEvent.VK_SHIFT)))
					selected.clear();
				for (int i = 0; i < pc.poses.size(); i++) {
					if (getX(i) > minx && getX(i) < maxx && getY(i) > miny
							&& getY(i) < maxy)
						if (keys.contains(KeyEvent.VK_SHIFT))
							selected.remove(i);
						else
							selected.add(i);
				}
			}
		}

		beginPressX = -1;
		beginPressY = -1;
	}
}
