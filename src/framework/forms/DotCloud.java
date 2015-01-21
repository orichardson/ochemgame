
package framework.forms;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

import math.Matrix;
import math.Vector3D;
import utils.Methods;
import framework.SceneNode;

public class DotCloud extends Form {
	public Dot[] dot;

	public DotCloud(SceneNode p, Dot[] d) {
		super(p);
		this.dot = d;
	}
	protected DotCloud(SceneNode p, int n) {
		super(p);
		this.dot = new Dot[n];
	}

	@Override
	public int getSize() {
		return dot.length;
	}

	public int maxSize() {
		return dot.length;
	}

	@Override
	public Dot getDot(int index) {
		return dot[index];
	}

	@Override
	public int transformChildIndex(int index, Form child) {
		return index;
	}

	public static DotCloud createCircle(SceneNode p, int n, Vector3D center, Vector3D norm, double r) {
		Color c1 = Methods.randomColor(), c2 = Methods.randomColor();
		DotCloud dots = new DotCloud(p, n);
		for (int i = 0; i < dots.dot.length; i++) {
			double t = 2 * Math.PI * i / n;
			Matrix rot = Matrix.createRotationMatrixAbout(norm, t);

			dots.dot[i] = new Dot(i, center.clone()
					.add(rot.applyTo(new Vector3D(1, 0, 0)).scale(
							r * (0.5 * (Math.random() - 0.5) + 1))), Methods.colorMeld(c1, c2,
					1 - Math.abs(1 - 2D * i / (n))), dots);
			dots.dot[i].setNext(new int[]{ (i + 1) % n, (n - 1 + i) % n }, new double[]{ 1, 1 });
		}

		return dots;
	}

	public static DotCloud createFromText(SceneNode p, Matrix m, String str, Font f, Color c1,
			Color c2) {
		TextLayout tl = new TextLayout(str, f, new FontRenderContext(new AffineTransform(), true,
				true));
		Shape sh = tl.getOutline(null);
		return createFromShape(p, m, sh, c1, c2);
	}

	public static DotCloud createFromShape(SceneNode p, Matrix m, Shape sh, Color c1, Color c2) {
		ArrayList<Vector3D> al = new ArrayList<Vector3D>();

		PathIterator pi = sh.getPathIterator(null);
		Rectangle bound = sh.getBounds();

		double[] cc = new double[6];

		while (!pi.isDone()) {
			int type = pi.currentSegment(cc);
			al.add(new Vector3D( (cc[0] - bound.width / 2) / 80D, (cc[1] - bound.height / 2) / 80D,
					type));
			pi.next();
		}

		DotCloud dc = new DotCloud(p, al.size());

		int last = 0, n = al.size();
		for (int i = 0; i < n; i++) {
			dc.dot[i] = new Dot(i, m.applyTo(al.get(i).to2DV().to3DV()), Methods.colorMeld(c1, c2,
					i / (double) n), dc);

			if (al.get(i).z == PathIterator.SEG_MOVETO) {
				dc.dot[i].setNext( (i - 1 + n) % n);
				last = i;
			} else {
				if (al.get(i).z == PathIterator.SEG_CLOSE) {
					dc.dot[i].setNext(last, (i - 1 + n) % n);
					dc.dot[last].setNext(i, (last + 1 + n) % n);
				} else
					dc.dot[i].setNext( (i + 1) % n, (i - 1 + n) % n);

			}
		}

		return dc;
	}

	// public static DotCloud createSphere(Vector3D origin, double r, int n) {
	// for(double az = Math.PI/2; az > -Math.PI/2; az -= Math.PI/n) {
	//
	// }
	// }
}
