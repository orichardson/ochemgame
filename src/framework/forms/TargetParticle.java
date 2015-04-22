
package framework.forms;

import java.awt.Color;

import math.Vector3D;
import utils.Methods;

public class TargetParticle extends Particle {
	double fuzz = 0;
	double phase = 0;
	double speed = 1;
	double order = 1;

	public Form form;
	int target;

	public TargetParticle(Form f, Vector3D p) {
		super(f);

		phase = Math.random();
		this.pos = p;
		this.fuzz = 0;
		this.form = f;
		this.target = (int) (Math.random() * f.getSize());

		fuzz = 0.1;

		this.color = Color.white;
	}

	public void update(double factor) {
		phase += 0.005 / factor;
		// fuzz = (Math.sin(phase) + 1) * 0.5;

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
			velo.scale(Math.pow(0.98, factor));

			f.scale(veloTransform(d2));

			// velo only
			velo = Vector3D.meld(f, velo, Math.pow(fuzz, 0.2));
			// acceleration only
			velo.addScaled(f, fuzz * 0.01);

			velo.add(Vector3D.random(0.001 * fuzz));

			// LOOK HERE FOR TRANSPARENCY
			color = Methods.colorMeld(color, d.c, 0.9, 150);

			// velo.scale(1 - Methods.bound(.01 / (1+ pos.dist2(d.pos)), 0, 1));

			pos.addScaled(velo, 1 / factor);
		}

	}

	private double getThresh() {
		return 0.01 * (1 + fuzz * 5);
	}

	private double veloTransform(double d) {
		// return speed * 0.003 / (1 + Math.exp(d * 0.01 - 1));
		return 0.005 * speed * (7 * d * d + 1);
	}


}
