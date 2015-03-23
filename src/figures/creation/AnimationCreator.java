
package figures.creation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import figures.Figure;

public class AnimationCreator extends JPanel implements ActionListener, ChangeListener, Runnable,
		KeyListener {
	private static final long serialVersionUID = 3997801619703816644L;

	HashSet<Integer> keys = new HashSet<Integer>();

	// *** Components are here *******************
	BetterTabbedThing controls = new BetterTabbedThing();

	// FRAME ANIMATION:
	JButton load = new JButton("Load");
	JButton save = new JButton("Save");

	JButton animate = new JButton("Animate");
	JButton stop = new JButton("Stop");

	JButton start = new JButton("<");
	JButton back = new JButton("Back");
	JLabel ind = new JLabel(" Frame 0 ");
	JButton next = new JButton("Next");
	JButton end = new JButton(">");

	JButton add = new JButton("Add to End");
	JButton ins = new JButton("Insert");
	JButton del = new JButton("Delete");
	JButton clr = new JButton("Delete All");

	JButton original = new JButton("Default Pose");
	JSlider speedSlide = new JSlider(-10, 60, 0); // powers of two (over ten)

	// CAMERA / SELECTION / VIEWPORT
	JToggleButton orthographic = new JToggleButton("Perspective");
	JSlider distSlide = new JSlider(8, 150, 20); // not powers of two :(
	JCheckBox followSelection = new JCheckBox("Follow Selection");

	/**
	 * Checkbox following selection Input Focus point (XYZ)
	 * 
	 */

	JButton help = new JButton("Help");

	JButton changeStruct = new JButton("Change Structure");

	// UTILITIES:
	JCheckBox preserve = new JCheckBox("Rigid Bones");
	AnimTimePanel timeline;
	PoseCreator ps;

	public AnimationCreator() throws IllegalArgumentException, IllegalAccessException {
		ps = new PoseCreator(keys);

		controls.setFocusable(false);

		setLayout(new BorderLayout());

		controls.setBackground(Color.DARK_GRAY);

		for (Field f : getClass().getDeclaredFields()) {
			if (f.getType().equals(JButton.class)) {
				JButton b = (JButton) f.get(this);
				b.addActionListener(this);
				b.setBackground(new Color(255, 200, 200));
				b.setForeground(Color.BLACK);
				b.setFocusable(false);
				b.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), b.getBorder()));
			} else if (f.getType().equals(JCheckBox.class)) {
				JCheckBox b = (JCheckBox) f.get(this);
				b.setOpaque(false);
				b.setForeground(Color.WHITE);
				b.setFocusable(false);
			}
		}

		orthographic.setFocusable(false);
		speedSlide.setFocusable(false);
		distSlide.setFocusable(false);

		enableButtons(true);

		orthographic.setForeground(Color.BLACK);
		ind.setForeground(Color.WHITE);
		preserve.setForeground(Color.WHITE);
		preserve.setOpaque(false);
		speedSlide.setOpaque(false);
		distSlide.setOpaque(false);
		orthographic.addActionListener(this);
		speedSlide.addChangeListener(this);

		speedSlide.setPreferredSize(new Dimension(120, 25));
		distSlide.setPreferredSize(new Dimension(120, 25));

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2)), p2 = new JPanel(
				new FlowLayout(FlowLayout.CENTER, 2, 2)), p3 = new JPanel(), p4 = new JPanel();
		p1.setOpaque(false);
		p2.setOpaque(false);
		p3.setOpaque(false);

		p1.add(load);
		p1.add(save);
		p1.add(Box.createHorizontalStrut(40));
		p1.add(start);
		p1.add(back);
		p1.add(ind);
		p1.add(next);
		p1.add(end);
		p1.add(Box.createHorizontalStrut(40));
		p1.add(add);
		p1.add(ins);
		p1.add(del);
		p1.add(clr);

		JLabel l1 = new JLabel("Speed: ");
		l1.setForeground(Color.WHITE);

		p2.add(animate);
		p2.add(stop);
		p2.add(Box.createHorizontalStrut(40));
		p2.add(l1);
		p2.add(speedSlide);
		p2.add(Box.createHorizontalStrut(50));
		p2.add(original);
		// p2.add(help);
		// p2.add(changeStruct);
		// p2.add(preserve);

		JLabel l2 = new JLabel("Distance: ");
		l2.setForeground(Color.WHITE);

		p3.add(l2);
		p3.add(distSlide);
		p3.add(orthographic);
		p3.add(followSelection);

		timeline = new AnimTimePanel(ps, this);

		JPanel frametools = new JPanel(new GridLayout(2, 1));
		frametools.setOpaque(false);
		frametools.add(p1);
		frametools.add(p2);

		controls.addSTab("Animation", frametools, 1);
		controls.addSTab("Camera", p3, 1);
		controls.addSTab("Timeline", timeline, -1);
		controls.addSTab("Creation", p4, 1);
		controls.addSTab("Remove", null, -1);

		add(ps, BorderLayout.CENTER);
		add(controls, BorderLayout.SOUTH);

		new Thread(this).start();
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == animate) {
			ps.running = true;
			enableButtons(false);
		} else if (evt.getSource() == stop) {
			ps.running = false;
			enableButtons(true);
		} else if (evt.getSource() == add) {
			ps.poses.add(ps.poses.get(ps.current).clone());
			ps.current = ps.poses.size() - 1;
		} else if (evt.getSource() == ins) {
			ps.poses.add(ps.current, ps.poses.get(ps.current).clone());
			ps.current++;
		} else if (evt.getSource() == orthographic) {
			orthographic.setText(orthographic.isSelected() ? "Orthographic" : "Perspective");
		} else if (evt.getSource() == load) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir"), "Resources"));

			chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
				public boolean accept(File f) {
					return (f.getName().endsWith(".fig") || f.isDirectory());
				}

				public String getDescription() {
					return "Figure (.fig)";
				}
			});

			int r = chooser.showOpenDialog(this);
			if (r == JFileChooser.APPROVE_OPTION) {
				Figure newFig = Figure.fromFile(chooser.getSelectedFile().getAbsolutePath());
				// ps.anim = ga;
				// ps.poses = ga.poses;
				// ps.figure.current_anim = ga;

				ps.current = 0;
				ps.selected.clear();
				ps.figure = newFig;
				ps.xpoints = new int[newFig.struct.NPTS];
				ps.ypoints = new int[newFig.struct.NPTS];
				// speedSlide.setValue((int) (10 * Math.log(ga.speed) / Math.log(2)));
			}
		} else if (evt.getSource() == save) {
			JFileChooser chooser = new JFileChooser(ps.figure.getFileName());
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir"), "Resources"));
			chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
				public boolean accept(File f) {
					return (f.getName().endsWith(".fig") || f.isDirectory());
				}

				public String getDescription() {
					return "Figure (.fig)";
				}
			});
			int r = chooser.showSaveDialog(this);
			if (r == JFileChooser.APPROVE_OPTION) {
				String dir = chooser.getSelectedFile().getAbsolutePath();
				if (! (dir.endsWith(".fig")))
					dir += ".fig";
			}

			ps.figure.saveToFile();

		}

		else if (evt.getSource() == next && ps.current < ps.poses.size() - 1) {
			moveFrame(ps.current + 1);
		} else if (evt.getSource() == back && ps.current > 0) {
			moveFrame(ps.current - 1);
		} else if (evt.getSource() == start) {
			moveFrame(0);
		} else if (evt.getSource() == end) {
			moveFrame(ps.poses.size() - 1);
		} else if (evt.getSource() == original) {
			ps.poses.set(ps.current, ps.figure.struct.defaultPose.clone());
		} else if (evt.getSource() == del) {
			ps.poses.remove(ps.current);
			if (ps.current > 0)
				ps.current--;
		} else if (evt.getSource() == clr) {
			ps.poses.clear();
			ps.poses.add(ps.figure.struct.defaultPose.clone());
			ps.current = 0;
		}
	}

	@Override
	public void stateChanged(ChangeEvent evt) {
		if (evt.getSource() == speedSlide)
			ps.anim.speed = Math.pow(2, speedSlide.getValue() / 10D);
	}

	public void moveFrame(int n) {
		ps.figure.pose = ps.poses.get(n);
		ps.current = n % ps.poses.size();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// TODO: ???
			// if (ps.running)
			// ps.current = ps.figure.update(0.05, 0) % ps.poses.size();

			timeline.update();
			update();
			ps.repaint();
		}
	}

	public void update() {
		ps.update(orthographic.isSelected(), followSelection.isSelected(), distSlide.getValue());
		ind.setText(" Frame " + ps.current + " ");
	}

	public void enableButtons(boolean x) {
		if (x) {
			load.setBackground(new Color(255, 150, 110));
			save.setBackground(new Color(255, 150, 110));

			start.setBackground(new Color(185, 130, 130));
			back.setBackground(new Color(205, 110, 110));
			next.setBackground(new Color(205, 110, 110));
			end.setBackground(new Color(185, 130, 130));

			orthographic.setBackground(Color.WHITE);
			add.setBackground(new Color(200, 40, 120));
			del.setBackground(new Color(200, 40, 120));
			clr.setBackground(new Color(200, 40, 120));
			ins.setBackground(new Color(200, 40, 120));

			original.setBackground(new Color(200, 100, 200));
			help.setBackground(new Color(200, 100, 200));
			changeStruct.setBackground(new Color(200, 100, 200));
		} else {
			load.setBackground(Color.DARK_GRAY);
			save.setBackground(Color.DARK_GRAY);

			start.setBackground(Color.DARK_GRAY);
			back.setBackground(Color.DARK_GRAY);
			next.setBackground(Color.DARK_GRAY);
			end.setBackground(Color.DARK_GRAY);

			ins.setBackground(Color.DARK_GRAY);
			add.setBackground(Color.DARK_GRAY);
			del.setBackground(Color.DARK_GRAY);
			clr.setBackground(Color.DARK_GRAY);
			original.setBackground(Color.DARK_GRAY);
			help.setBackground(Color.DARK_GRAY);
			changeStruct.setBackground(Color.DARK_GRAY);
		}
		controls.enable(x);
		ins.setEnabled(x);
		changeStruct.setEnabled(x);
		help.setEnabled(x);
		original.setEnabled(x);
		add.setEnabled(x);
		clr.setEnabled(x);
		load.setEnabled(x);
		save.setEnabled(x);
		start.setEnabled(x);
		end.setEnabled(x);
		// original.setEnabled(x);
		del.setEnabled(x);
		back.setEnabled(x);
		next.setEnabled(x);
	}

	public void keyPressed(KeyEvent evt) {
		keys.add(evt.getKeyCode());

		if (evt.getKeyCode() == KeyEvent.VK_NUMPAD5) {
			orthographic.setSelected(!orthographic.isSelected());
			actionPerformed(new ActionEvent(orthographic, ActionEvent.ACTION_PERFORMED,
					"toggle ortho"));
		}
		ps.keyPress(evt.getKeyCode());
	}

	public void keyReleased(KeyEvent evt) {
		keys.remove(evt.getKeyCode());
	}

	public void keyTyped(KeyEvent evt) {}

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		JFrame frame = new JFrame();
		frame.setBounds(200, 50, 1400, 800);

		AnimationCreator ac = new AnimationCreator();

		frame.setContentPane(ac);
		frame.addKeyListener(ac);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
