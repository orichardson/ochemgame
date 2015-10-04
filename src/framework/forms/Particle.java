
package framework.forms;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import math.Matrix;
import math.Vector3D;
import framework.Eye;
import framework.SceneNode;

public abstract class Particle extends SceneNode {

	public Vector3D pos;
	public Color color;
	Vector3D velo = new Vector3D();
	double size = 6;

	public Particle(SceneNode p) {
		super(p);
	}

	private Vector3D last;

	@Override
	public void draw(Graphics2D g, Eye e) {
		Matrix m = getTransform();
		Vector3D v = e.toScreenDepthBufferUpdate(m.applyTo(pos), size);

		if (v != null) {
			int r = (int) v.z;
			g.setColor(e.transform(color, velo, 1));
			g.fillRect((int) v.x - r, (int) v.y - r, (r * 2 + 1), (r * 2 + 1));

			if (last != null) {
				Vector3D v2 = e.toScreenDepthBufferUpdate(m.applyTo(last), size);
				if (v2 != null) {
					g.setStroke(new BasicStroke(r * 2));
					g.drawLine((int) v.x, (int) v.y, (int) v2.x, (int) v2.y);
					g.setStroke(new BasicStroke(1));
				}
			}
		}

		// if (last == null)
		last = pos.clone();
		// else
		// last = Vector3D.meld(last, pos, 0.5);
	}
}
