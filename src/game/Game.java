
package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JPanel;

import utils.Methods;
import chem.Atom;
import framework.Eye;
import framework.forms.Player;

public class Game extends JPanel implements KeyListener, Runnable {
	// public static Dimension SCREEN = new Dimension(700,400);
	public static Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();

	private static final long serialVersionUID = 4092070700879219267L;

	public World world;
	public Player player;
	public Eye view;

	public HashSet<Integer> keys = new HashSet<Integer>();

	private float fps, minFPS, updateFPS;
	private long lastTime = System.currentTimeMillis(), ltUpdate = System.currentTimeMillis();

	private ReentrantLock lock = new ReentrantLock();

	public Game() {
		setDoubleBuffered(false);
		world = new World();

		view = new Eye(world.current, SCREEN);
		player = new Player(world.current);

		new Atom(world.current, 20);
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
			updateFPS = (1000f / (t - ltUpdate));
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

			lock.lock();
			world.update(this, factor);
			view.focusApproach(player.pos, factor);
			view.updatePosition();
			lock.unlock();

			repaint();

			try {
				Thread.sleep(6);
			} catch (Exception e) {}
		}
	}

	public void paintComponent(Graphics grr) {
		long t = System.currentTimeMillis();

		fps = (1000f / (t - lastTime));
		if (fps < minFPS)
			minFPS = fps;
		lastTime = t;

		Graphics2D g = (Graphics2D) grr;

		int trans = (int) (255 * 60 / (60 + fps));
		g.setColor(Methods.getColor(world.background, trans));
		g.fill(g.getClip());

		lock.lock();
		view.synchronize();
		lock.unlock();

		world.current.draw(g, view);

		// draw fps and other debug information
		// if (keys.contains(KeyEvent.VK_F1)) {
		g.setColor(Color.WHITE);
		g.drawString("[paint fps] " + fps, 20, 40);
		g.drawString("[min paint] " + minFPS, 20, 60);
		g.drawString("[update fps] " + updateFPS, 20, 80);
		// }
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
