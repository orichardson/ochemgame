
package framework;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import framework.forms.Particle;
import game.Game;
import math.Vector3D;
import utils.Methods;

/*
 * Each represents a pair of triangles
 */
public abstract class TerrainShard extends SceneNode {

	protected TerrainShard(SceneNode p) {
		super(p);
	}

	public abstract double findZ(double x, double y);
	public abstract Color findC(double x, double y);

	public static class Tile extends TerrainShard {
		GridPoint v1, v2, v3, v4;

		// arranged as follows:
		// (1) (2)
		// (3) (4)

		protected Tile(SceneNode p, GridPoint v1, GridPoint v2, GridPoint v3, GridPoint v4) {
			super(p);

			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
			this.v4 = v4;
		}

		@Override
		public double findZ(double x, double y) {
			return Vector3D.meldBiLin(v1, v2, v3, v4, (x - v1.x) / (v2.x - v1.x), (y - v1.y)
					/ (v3.y - v1.y)).z;
			// Vector3D[][] v = new Vector3D[][]{{v4,v1,
			// v2,v3},{v4,v1,v2,v3},{v1,v3,v4,v2},{v1,v3,v4,v2}};
			// return Vector3D.meldBiCub(v, (x - v1.x)
			// / (v2.x - v1.x), (y - v1.y) / (v3.y - v1.y)).z;
		}

		@Override
		public Color findC(double x, double y) {
			return Methods.colorMeldBiLin(v1.color, v2.color, v3.color, v4.color, (x - v1.x)
					/ (v2.x - v1.x), (y - v1.y) / (v3.y - v1.y));
		}
	}

	public static class Grid extends TerrainShard {
		TerrainShard[][] tiles;
		GridPoint[][] points;
		private int N;
		Vector3D center;
		private boolean smooth = true;

		int type;

		double length;

		public Grid(SceneNode sn, int n, Vector3D center, double side) {
			super(sn);
			this.N = n;
			this.length = side;
			this.center = center;

			tiles = new TerrainShard[n][n];
			points = new GridPoint[n + 1][n + 1];

			for (int i = 0; i <= n; i++)
				for (int j = 0; j <= n; j++) {
					points[i][j] = new GridPoint(center).add(side * (i - n / 2) / n, side
							* (j - n / 2) / n, -Math.random() * 2);
					points[i][j].color = Methods.randomColor();
				}

			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++) {
					tiles[i][j] = new Tile(this, points[i][j], points[i + 1][j], points[i][j + 1],
							points[i + 1][j + 1]);
				}
		}

		public Grid(SceneNode n, Collection<GridPoint> list) {
			super(n);

			// list.
		}

		@Override
		public double findZ(double x, double y) {
			int xind = (int) Methods.bound(N * (x - points[0][0].x) / (length), 0, N - 1);
			int yind = (int) Methods.bound(N * (y - points[0][0].y) / (length), 0, N - 1);

			if (!smooth || tiles[xind][yind] instanceof Grid)
				return tiles[xind][yind].findZ(x, y);

			// bicubic intepolation?
			Vector3D[][] v = new Vector3D[4][4];
			for (int i = -1; i <= 2; i++)
				for (int j = -1; j <= 2; j++)
					v[i + 1][j + 1] = points[ (xind + i + N) % N][ (yind + j + N) % N];

			return Vector3D.meldBiCub(v, (x - v[1][1].x) / (v[2][1].x - v[1][1].x), (y - v[1][1].y)
					/ (v[1][2].y - v[1][1].y)).z;

		}

		@Override
		public Color findC(double x, double y) {
			int xind = (int) Methods.bound(N * (x - points[0][0].x) / (length), 0, N - 1);
			int yind = (int) Methods.bound(N * (y - points[0][0].y) / (length), 0, N - 1);

			return tiles[xind][yind].findC(x, y);
		}

		@Override
		public void draw(Graphics2D g, Eye e) {
			super.draw(g, e);

			double NPTS = 20 * length;
			for (double i = -1; i < 1; i += 2d / NPTS) {

				for (double j = -1; j < 1; j += 2d / NPTS) {
					Vector3D v = new Vector3D(center.x + i * length / 2, center.y + j * length / 2,
							0);
					v.z = findZ(v.x, v.y);

					Vector3D scr = e.toScreenDepthBufferUpdate(v, 5);
					if (scr != null) {
						int r = (int) scr.z;
						g.setColor(findC(v.x, v.y));
						g.fillRect((int) scr.x - r, (int) scr.y - r, (r * 2 + 1), (r * 2 + 1));
					}
				}

			}
		}

	}

	public static class GridPoint extends Vector3D {
		public Color color;
		public Vector3D norm;

		public GridPoint(double a, double b, double c) {
			super(a, b, c);
		}

		public GridPoint(Vector3D v) {
			set(v);
			this.norm = new Vector3D(0, 0, 1);
			this.color = Color.ORANGE;
		}

		@Override
		public GridPoint add(Vector3D v) {
			super.add(v);
			return this;
		}

		@Override
		public GridPoint add(double a, double b, double c) {
			super.add(a, b, c);
			return this;
		}

		@Override
		public GridPoint sub(Vector3D v) {
			super.sub(v);
			return this;
		}

	}

	public class TerrainParticle extends Particle {
		double fade = 0f, fadeTo = 1f;

		public TerrainParticle(SceneNode p) {
			super(p);
		}

		@Override
		public void update(Game g, double factor) {
			fade += Math.signum(fadeTo - fade) * factor * 0.005;
		}
	}

}
