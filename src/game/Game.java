package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import utils.Methods;
import chem.Atom;
import framework.Eye;
import framework.forms.Player;

public class Game extends JPanel implements KeyListener, Runnable {
	// public static Dimension SCREEN = new Dimension(700,400);
	public static Dimension SCREEN = Toolkit.getDefaultToolkit()
			.getScreenSize();

	private static final long serialVersionUID = 4092070700879219267L;

	public World world;
	public Player player;
	public Eye view;

	public HashSet<Integer> keys = new HashSet<Integer>();

	private int fps, minFPS, updateFPS;
	private long lastTime = System.currentTimeMillis(), ltUpdate = System
			.currentTimeMillis();

	public Game() {
		world = new World();

		view = new Eye(world.current, SCREEN);
		player = new Player(world.current);

		new Atom(world.current, 44);
		// Atom b = new Atom(world.current, 32);
		// Atom c = new Atom(world.current, 31);

		// a.setTransform(Matrix.create3DAffineTranslateMatrix(new
		// Vector3D(1,0,0)).mult(Matrix.create3DEulerRotMatrix(0, Math.PI/2, 0).expandTo(4)));
		// b.setTransform(Matrix.create3DAffineTranslateMatrix(new
		// Vector3D(-.7,-.7,0)).mult(Matrix.create3DEulerRotMatrix(0, Math.PI/2, 0).expandTo(4)));
		// c.setTransform(Matrix.create3DAffineTranslateMatrix(new
		// Vector3D(-.7,.7,0)).mult(Matrix.create3DEulerRotMatrix(0, Math.PI/2, 0).expandTo(4)));

		setBackground(world.background);
	}

	public void run() {
		int counter = 0;

		while (true) {
			counter++;

			long t = System.currentTimeMillis();
			updateFPS = (int) (1000f / (t - ltUpdate));
			double factor = 10D / (t - ltUpdate);
			ltUpdate = t;

			if (counter % 100 == 0)
				minFPS = fps;

			double diff = 0.0015;

			if (keys.contains(KeyEvent.VK_E))
				view.da += diff / factor;
			if (keys.contains(KeyEvent.VK_Q))
				view.da -= diff / factor;
			if (keys.contains(KeyEvent.VK_X))
				view.db += diff / factor;
			if (keys.contains(KeyEvent.VK_Z))
				view.db -= diff / factor;

			world.update(this, factor);

			view.focusApproach(player.pos, factor);

			repaint();

			try {
				Thread.sleep(12);
			} catch (Exception e) {}
		}
	}

	public void paintComponent(Graphics grr) {
		// super.paintComponent(grr);

		long t = System.currentTimeMillis();

		fps = (int) (1000f / (t - lastTime));
		if (fps < minFPS)
			minFPS = fps;
		lastTime = t;

		Graphics2D g = (Graphics2D) grr;

		g.setColor(Methods.getColor(world.background, 100));
		g.fill(g.getClip());

		view.synchronize();
		world.current.draw(g, view);

		g.setColor(Color.WHITE);
		g.drawString(fps + ", " + minFPS + ", " + updateFPS, 20, 40);
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		keys.add(evt.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent evt) {
		keys.remove(evt.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent a) {}

	public static Game GAME;

	public static void main(String[] args) {
		GAME = new Game();

		JFrame frame = new JFrame("GAME");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setSize(SCREEN);

		frame.setContentPane(GAME);
		frame.addKeyListener(GAME);

		new Thread(GAME).start();

		frame.setVisible(true);
	}
}
