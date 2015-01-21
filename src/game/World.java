package game;

import java.awt.Color;
import java.util.ArrayList;

import framework.SceneNode;

public class World {
	public Color background = Color.BLACK;

	Tile current;

	// loaded tiles outside of the current tile; stepping on things might
	// trigger loading, and loaded files get put here.
	ArrayList<Tile> loaded = new ArrayList<Tile>();

	public World() {
		current = new Tile();
	}

	public void update(Game game, double factor) {
		current.update(game, factor);
	}

	public class Tile extends SceneNode {
		public Tile() {
			super(null);
		}
	}

}
