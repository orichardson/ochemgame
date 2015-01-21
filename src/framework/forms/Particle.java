
package framework.forms;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import math.Matrix;
import math.Vector3D;
import utils.Methods;
import framework.Eye;
import framework.SceneNode;

public class Particle extends SceneNode {
	public Vector3D pos;
	Vector3D velo;
	public Color color;

	double fuzz = 0;
	double phase = 0;
	double speed = 1;
	double order = 1;

	public Form form;
	double size = 6;
	int target;

	public Particle(Form f, Vector3D p) {
		super(f);
		phase = Math.random();
		this.pos = p;
		this.velo = new Vector3D();
		this.fuzz = 0;
		this.form = f;
		this.target = (int) (Math.random() * f.getSize());

		fuzz = 0.1;

		this.color = Color.white;
	}

	public void update() {
		phase += 0.01;
		fuzz = (Math.sin(phase) + 1) * 0.5;

		Dot d = form.getDot(target);

		Vector3D f = d.pos.clone().sub(pos);
		double d2 = f.magnitude();
		f.normalize();

		double threshold = getThresh();

		if (d2 < threshold) {
			// color = target.c;
			// pos = d.pos.clone().add(Vector3D.random(threshold));
			target = d.pullNext(form);
			// velo = new Vector3D();
			velo = Vector3D.meld(
					form.getDot(target).pos.clone().sub(pos).normalize()
							.scale(velo.magnitude() * 0.9), velo, fuzz);
		} else {
			velo.scale(0.99);

			velo = Vector3D.meld(f.clone().scale(veloTransform(d2)), velo, Math.pow(fuzz, 0.2)); // velo
																									// only
			velo.add(f.clone().scale(veloTransform(d2)).scale( (fuzz) * 0.01)); // acceleration only

			velo.add(Vector3D.random(0.001 * fuzz));

			color = Methods.colorMeld(color, d.c, 0.9);

			// velo.scale(1 - Methods.bound(.01 / (1+ pos.dist2(d.pos)), 0, 1));

			pos.add(velo);
		}

	}

	private double getThresh() {
		return 0.01 * (1 + fuzz * 5);
	}

	private double veloTransform(double d) {
		// return speed * 0.003 / (1 + Math.exp(d * 0.01 - 1));
		return 0.01 * speed * (10 * d * d + 1);
	}

	private Vector3D last;

	@Override
	public void draw(Graphics2D g, Eye e) {
		Matrix m = getTransform();
		Vector3D v = e.toScreenDepthBufferUpdate(m.applyTo(pos), size);

		if (v != null) {
			int r = (int) v.z;
			g.setColor(e.transform(color, velo.cross(m.applyTo(pos)), 0));
			g.fillRect((int) v.x - r, (int) v.y - r, (r * 2 + 1), (r * 2 + 1));

			if (last != null) {
				Vector3D v2 = e.toScreenDepthBufferUpdate(m.applyTo(last), size);
				if (v2 != null) {
					g.setStroke(new BasicStroke(r * 2));
					g.drawLine((int) v.x, (int) v.y, (int) v2.x, (int) v2.y);
				}
			}
		}

		last = pos.clone();
	}
}
