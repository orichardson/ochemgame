
package framework;

import game.Game;

import java.awt.Color;
import java.awt.Dimension;

import math.Matrix;
import math.Vector2D;
import math.Vector3D;
import utils.Methods;

public class Eye extends SceneNode {
	public static double AMBIENT_LIGHT = 0.1;
	public double followK = 20;

	public Vector3D sunDir = new Vector3D(0, 1, 4);

	public Dimension screen;

	public double scale = 1000;

	// **** VARS THAT REQUIRE SYNCHRONIZATION
	private Matrix rotator, invrotator;
	private double ortho_synced, leash_synced;
	private Vector3D pos_synced;

	// **** Non-synchronized variables
	public double ortho = 0; // between 0 and 1;

	public Vector3D focus = new Vector3D();
	private Vector3D pos;

	public double alpha = -Math.PI / 2, beta = 0, spin = Math.PI / 2;
	public double da, db, ds;

	public double leash = 2, saturation = 1;
	private Vector3D color = new Vector3D(1, 0, 1);

	// **************** DEPTH MAP VARS ************

	int grain = 2; // negative grain means no depth map use.
	private double[] dmap;

	public Eye(SceneNode parent, Dimension screen) {
		super(parent);

		this.visible = false;
		this.updateChildren = false;

		pos = new Vector3D(0, 0, leash);
		this.screen = screen;
		updatePosition();
		synchronize();
	}

	public double getPercent(double angle) {
		// return angle >= Math.PI / 2 ? AMBIENT_LIGHT : Math.cos(angle) * (1 -
		// AMBIENT_LIGHT) + AMBIENT_LIGHT;
		return Math.abs(Math.cos(angle)) * (1 - AMBIENT_LIGHT) + AMBIENT_LIGHT;
	}

	public Color transform(Color c, Vector3D norm, int mode) {
		if (c == null)
			return null;

		Vector3D vk = new Vector3D(c.getRed(), c.getGreen(), c.getBlue());
		Vector3D colorProj = vk.projOnto(color);
		Vector3D last = Vector3D.meld(colorProj, vk, saturation);

		double prc = 1;

		if (norm != null) {
			if (mode == 0)
				prc = getPercent(norm.angleBetween(sunDir));
			else if (mode == 1) {
				prc = getPercent(Math.abs(Math.PI / 2 - norm.angleBetween(sunDir)));
			}
		}

		Color clr = new Color((int) (prc * Methods.bound(last.x, 0, 255)),
				(int) (prc * Methods.bound(last.y, 0, 255)), (int) (prc * Methods.bound(last.z, 0,
						255)), c.getAlpha());
		return clr;
	}

	public void updatePosition() {
		pos = focus.clone().add(
				Matrix.create3DEulerRotMatrix(spin, beta, alpha).calculateInverse()
						.applyTo(new Vector3D(0, 0, leash)));
	}

	public void synchronize() {
		rotator = Matrix.create3DEulerRotMatrix(spin, beta, alpha);
		invrotator = rotator.calculateInverse();
		pos_synced = pos.clone();
		ortho_synced = ortho;
		leash_synced = leash;

		if (grain > 0)
			dmap = new double[ (screen.width / grain) * (screen.height / grain)];
	}
	public void focusApproach(Vector3D v, double factor) {
		focus.x += (v.x - focus.x) / (factor * followK);
		focus.y += (v.y - focus.y) / (factor * followK);
		focus.z += (v.z - focus.z) / (factor * followK);
	}

	@Override
	public void update(Game g, double speed) {
		alpha += da / speed;
		beta = Methods.bound(beta + db / speed, -Math.PI, 0);
		spin += ds / speed;

		double f = Math.pow(0.97, 1 / speed);

		da *= f;
		db *= f;
		ds *= f;

		sunDir.incrementAngle(0.01 / speed);
	}

	public double faceDist(Vector3D v) {
		Vector3D raw = rotator.applyTo(v.clone().sub(pos_synced));
		return -raw.z;
	}

	public boolean cull(Vector3D v) {
		return false;
		// return v.clone()
		// .sub(this)
		// .angleBetween(
		// Matrix.create3DEulerRotMatrix(spin, beta, alpha).calculateInverse()
		// .applyTo(new Vector3D(0, 0, leash))) > Math.PI / 4;
	}

	@Deprecated
	public Eye clone() {
		Eye e2 = new Eye(null, this.screen);
		e2.alpha = alpha;
		e2.beta = beta;
		e2.spin = spin;
		e2.da = da;
		e2.db = db;
		e2.ds = ds;

		e2.pos = pos.clone();
		e2.pos_synced = pos_synced.clone();
		e2.focus = focus.clone();
		e2.rotator = rotator.clone();
		e2.invrotator = invrotator.clone();

		e2.ortho = ortho;
		e2.ortho_synced = ortho_synced;
		e2.leash = leash;

		return e2;
	}

	/**
	 * Note that this does minimal culling! Off-screen vectors will still be returned, because it
	 * doesn't deal with pixels at all
	 * 
	 * @param v
	 *            the world position in question
	 * @return the rotated screen position
	 */
	public Vector3D toScreen(Vector3D v) {
		if (v == null)
			return null;
		// always use protection
		Vector3D raw = rotator.applyTo(v.clone().sub(pos_synced));
		if (raw.z > 0)
			return null;

		double a = raw.x / leash_synced, b = raw.y / leash_synced, c = -raw.x / raw.z, d = -raw.y
				/ raw.z;

		return new Vector3D(c + (a - c) * ortho, d + (b - d) * ortho, -raw.z);
	}

	/*
	 * IMPORTANT: The z component of the return value of this method is the radius of the sphere in
	 * pixel coordinates (as it was computed in order to do depth map testing anyway)
	 */
	public Vector3D toScreenDepthBufferUpdate(Vector3D v, double size) {
		if (v == null)
			return null;

		// always use protection.
		Vector3D raw = rotator.applyTo(v.clone().sub(pos_synced));
		raw.z = -raw.z;

		if (raw.z <= 0)
			return null;

		double a = raw.x / leash_synced, b = raw.y / leash_synced, c = raw.x / raw.z, d = raw.y
				/ raw.z;

		Vector3D nice = new Vector3D(psX(c + (a - c) * ortho), psY(d + (b - d) * ortho), size
				/ raw.z);

		if (nice.x < 0 || nice.x >= screen.width || nice.y < 0 || nice.y >= screen.height)
			return null;

		if (grain > 0 && size > 0) { // only do this if there IS a depth buffer...
			int buffer_width = screen.width / grain;

			double centerDist = dmap[(int) (nice.x / grain) + (int) (nice.y / grain) * buffer_width];

			if (centerDist != 0 && raw.z > centerDist)
				return null;

			for (int i = Math.max((int) (nice.x - nice.z) / grain, 0); i < Math.min(
					(int) (nice.x + nice.z) / grain, buffer_width); i++)
				for (int j = Math.max((int) (nice.y - nice.z) / grain, 0); j < Math.min(
						(int) (nice.y + nice.z) / grain, screen.height / grain); j++) {
					double dist = dmap[i + buffer_width * j];
					dmap[i + buffer_width * j] = dist == 0 ? raw.z : Math.min(dist, raw.z);
				}
		}

		return nice;
	}

	public Vector3D pickZProj(Vector2D v) {
		return invrotator.applyTo(new Vector3D(v.x, v.y, 0));
	}

	public Vector3D pickBasic(int x, int y) { // for now assume return z = 0
		return invrotator.applyTo(
				new Vector3D(ssX(x) * leash_synced, ssY(y) * leash_synced, -leash_synced)).add(
				pos_synced);
	}

	public Vector3D pickDist(int x, int y, double dist) {
		return Vector3D.meld(invrotator.applyTo(new Vector3D(ssX(x) * dist, ssY(y) * dist, -dist))
				.add(pos_synced), pickBasic(x, y), ortho_synced);
	}

	/**
	 * Stands for "Pixel Space X/Y"
	 */
	public int psX(double d) {
		return screen.width / 2 + (int) (scale * d);
	}

	public int psY(double d) {
		return screen.height / 2 + (int) (scale * d);
	}

	/**
	 * Stands for "Screen Space X/Y"
	 */
	public double ssX(int q) {
		return (q - screen.width / 2) / scale;
	}

	public double ssY(int q) {
		return (q - screen.height / 2) / scale;
	}

	public int sphereWidth(Vector3D v, double w) {
		double v1 = (-scale * w / rotator.applyTo(v.clone().sub(pos_synced)).z);
		double v2 = (scale * w / leash_synced);
		return (int) (v1 + (v2 - v1) * ortho);
	}

	public Vector3D calcFwd() {
		return invrotator.applyTo(0, 0, 1);
	}
	public Vector3D calcRight() {
		return invrotator.applyTo(1, 0, 0);
	}
	public Vector3D calcUp() {
		return invrotator.applyTo(0, 1, 0);
	}

	public void setScreen(int width, int height) {
		screen.width = width;
		screen.height = height;
	}
}
