
package framework;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import math.Vector3D;
import utils.Methods;
import framework.forms.Particle;
import game.Game;

/*
 * Each represents a pair of triangles
 */
public class TerrainShard extends SceneNode {

	public static int[] DX = { 0, 1, 1, 1, 0, -1, -1, -1 };
	public static int[] DY = { -1, -1, 0, 1, 1, 1, 0, -1 };

	TerrainShard[][] tiles;
	GridPoint[][] points;
	private int N;

	int type;
	GridPoint v1, v2, v3, v4;
	Vector3D normal;
	double length;

	public TerrainShard(SceneNode sn, int n, Vector3D center, double side) {
		super(sn);
		this.N = n;
		this.length = side / 2;

		this.v1 = new GridPoint(center).add(-length, -length, 0);
		this.v2 = new GridPoint(center).add(length, -length, 0);
		this.v3 = new GridPoint(center).add(length, length, 0);
		this.v4 = new GridPoint(center).add(-length, length, 0);

		Vector3D X = new Vector3D(side, 0, 0), Y = new Vector3D(0, side, 0);

		if (N > 1) {
			tiles = new TerrainShard[n][n];
			points = new GridPoint[n+1][n+1];
			
			for (int i = 0; i <= n; i++)
				for (int j = 0; j <= n; j++) {
					points[i][j] = new GridPoint();
				}
			
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++) {
					tiles[i][j] = new TerrainShard(this, );
				}
		}
	}
	public TerrainShard(SceneNode n, GridPoint v1, GridPoint v2, GridPoint v3, GridPoint v4) {
		super(n);
	}

	public TerrainShard(SceneNode n, Collection<GridPoint> list) {
		super(n);
		
		list.
	}
	public double findZ(double x, double y) {
		if (N <= 1) {
			return center.z;
		}

		int xind = (int) (N * (radius + x - center.x) / (2 * radius));
		int yind = (int) (N * (radius + y - center.y) / (2 * radius));

		xind = (int) Methods.bound(xind, 0, N - 1);
		yind = (int) Methods.bound(yind, 0, N - 1);

		double estimate = 0;
		// double init = tiles[xind][yind].findZ(x, y);
		double weight = 0;

		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				if (xind + i < 0 || xind + i >= N || yind + j < 0 || yind + j >= N)
					continue;
				TerrainShard t = tiles[xind + i][yind + j];

				double dist = t.center.dist2(x, y);
				double w = 1 / ( (dist * dist * dist));
				estimate += t.center.z * w;
				weight += w;
			}
		estimate /= weight;
		// estimate += init;

		return estimate;
		// return init;
	}

	@Override
	public void draw(Graphics2D g, Eye e) {
		// super.draw(g, e);

		double NPTS = 200 * radius;
		for (double i = -1; i < 1; i += 2d / NPTS)
			for (double j = -1; j < 1; j += 2d / NPTS) {
				Vector3D v = new Vector3D(center.x + i * radius, center.y + j * radius, 0);
				v.z = findZ(v.x, v.y);

				Vector3D scr = e.toScreenDepthBufferUpdate(v, 5);
				if (scr != null) {
					int r = (int) scr.z;
					g.setColor(Methods.randomColor((int) (radius * 109 + i * radius + j * radius),
							255));
					g.fillRect((int) scr.x - r, (int) scr.y - r, (r * 2 + 1), (r * 2 + 1));
				}
			}
	}

	public void deapen(int branchSize) {

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
