
package framework.forms;

import java.awt.event.KeyEvent;

import math.Matrix;
import math.Vector2D;
import math.Vector3D;
import framework.SceneNode;
import game.Game;

public class Player extends ParticleGraphEntity {
	public static final int UP = 0, LEFT = 1, DOWN = 2, RIGHT = 3, JUMP = 4, SWITCH = 5;
	int[] keyBindings = { KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D,
			KeyEvent.VK_SPACE, KeyEvent.VK_Q };

	public double walkAngle = 0;
	Vector3D velo = new Vector3D();

	public Player(SceneNode p) {
		super(p);
		this.pos = new Vector3D(0, 0, 0);
		this.particles = new TargetParticle[3000];

		this.form = new Form.Duality(this, DotCloud.createCircle(null, 40, new Vector3D(),
				new Vector3D(0, 0, 1), 1), new Form.Composite(null, DotCloud.createCircle(null, 10,
				new Vector3D(.75, 0, 0), new Vector3D(1, 0, 0), .45), DotCloud.createCircle(null,
				10, new Vector3D(-.75, 0, 0), new Vector3D(1, 0, 0), .45)));

		// this.form = new Form.Duality(this, DotCloud.createCircle(null, 20, new Vector3D(.75, 0,
		// 0),
		// new Vector3D(0, 0, 1), .45), DotCloud.createCircle(null, 41, new Vector3D(-.75, 0,
		// 0), new Vector3D(0, 0, 1), .45));

		// this.form = new Form.Composite(this, DotCloud.createCircle(null, 30,
		// new Vector3D(.75, 0, 0), new Vector3D(0, 0, 1), .45), DotCloud.createCircle(null,
		// 30, new Vector3D(-.75, 0, 0), new Vector3D(0, 0, 1), .45));

		for (int i = 0; i < particles.length; i++) {
			particles[i] = new TargetParticle(form, Vector3D.random(1));
			particles[i].fuzz = 0.2;
		}

	}
	
	@Override
	public void update(Game g, double speed) {
		super.update(g, speed);
		
		for (int i = 0; i < particles.length; i++)
			particles[i].update(speed);

		if (form instanceof Form.Duality) {
			Form.Duality fd = ((Form.Duality) form);
			if (g.keys.contains(KeyEvent.VK_0))
				fd.prcTo = fd.prc < 0.5 ? fd.prc + 0.05 : fd.prc - 0.05;
			if (g.keys.contains(KeyEvent.VK_1))
				fd.prcTo = 0;
			if (g.keys.contains(KeyEvent.VK_2))
				fd.prcTo = 1;
		}

		Vector2D m = new Vector2D(0, 0);
		Vector2D up = g.view.pickZProj(new Vector2D(0, 1)).to2DV().unitize(), right = g.view
				.pickZProj(new Vector2D(-1, 0)).to2DV().unitize();

		if (g.keys.contains(keyBindings[LEFT]))
			m.add(right);
		if (g.keys.contains(keyBindings[RIGHT]))
			m.minus(right);
		if (g.keys.contains(keyBindings[UP]))
			m.minus(up);
		if (g.keys.contains(keyBindings[DOWN]))
			m.add(up);

		m.unitize();
		m.scale(0.05);

		double aTo = walkAngle;

		// this is to normalize the walkToAngle to its [0, 2pi) range
		if (m.mag2() > 0) {
			aTo = Math.atan2(m.y, m.x) - Math.PI;

			if (Math.abs(walkAngle - aTo) > Math.PI)
				if (walkAngle < aTo)
					aTo -= Math.PI * 2;
				else
					aTo += Math.PI * 2;

			walkAngle %= 2 * Math.PI;
		}

		walkAngle += (aTo - walkAngle) / 10;

		velo.add(new Vector3D(m.x, m.y, velo.z).sub(velo).scale(0.15));
		pos.add(velo);

		this.setTransform(Matrix.create3DAffineTranslateMatrix(pos));
	}
}
