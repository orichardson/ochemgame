
package framework.forms;

import java.awt.Graphics2D;
import java.util.HashMap;

import math.FiniteMapping;
import math.Vector3D;
import utils.Methods;
import framework.Eye;
import framework.SceneNode;
import game.Game;

public abstract class Form extends SceneNode {
	public Form(SceneNode p) {
		super(p);
	}

	public abstract int getSize();
//
	public abstract int maxSize();

	public abstract Dot getDot(int index);

	public abstract int transformChildIndex(int index, Form child);

	public final int findIndexRecursive(int q, Form child, Form target) {
		int index = transformChildIndex(q, child);

		if (parent instanceof Form && target != this)
			return ((Form) parent).findIndexRecursive(index, this, target);
		return index;
	}

	public static class Duality extends DotCloud {
		public static final double K = 13;

		protected int size;
		FiniteMapping transA, transB;
		Form a, b;
		double prc, prcTo;

		public Duality(SceneNode p, Form q, Form r) {
			super(p, new Dot[Math.max(q.getSize(), r.getSize())]);

			size = q.getSize();

			this.a = q;
			this.b = r;
			this.prc = 0;
			this.prcTo = 0;

			q.setParent(this);
			r.setParent(this);

			transA = FiniteMapping.createID(dot.length, q.getSize()).randomizeUniform();
			transB = FiniteMapping.createID(dot.length, r.getSize()).randomizeUniform();

			for (int i = 0; i < dot.length; i++) {
				dot[i] = new Dot(i, new Vector3D(), Methods.randomColor(), this);
				dot[i].setNext();
			}
		}

		@Override
		public int transformChildIndex(int index, Form child) {
			if (child == a)
				return transA.foR(index);
			else if (child == b)
				return transB.foR(index);
			return index;
		}

		@Override
		public Dot getDot(int i) {
			return dot[i];
		}

		public int maxSize() {
			return dot.length;
		}

		public void to(double d) {
			this.prcTo = d;
		}
		
		public double get() {
			return prc;
		}

		@Override
		public void update(Game g, double speed) {
			super.update(g, speed);

			prc += (prcTo - prc) / K;

			if (Math.abs(prc - prcTo) < .001)
				prc = prcTo;

			// Note: This is for the Dot pattern, not the particles. Those are
			// acceleration-based.
			a.update(g, speed);
			b.update(g, speed);

			// size = (int) (a.getSize() + prc * (b.getSize() - a.getSize()));
			size = dot.length;

			for (int i = 0; i < dot.length; i++) {
				Dot ad = a.getDot(transA.ofR(i)), bd = b.getDot(transB.ofR(i));
				try {
					dot[i].c = Methods.colorMeld(ad.c, bd.c, prc);
				} catch (Exception e) {}
				dot[i].pos = Vector3D.meld(ad.pos, bd.pos, prc);
				dot[i].referencedUpdate(ad, bd, prc);
			}
		}

		@Override
		public int getSize() {
			return size;
		}
	}

	public static class Composite extends Form {
		private int size;
		Form[] forms;
		HashMap<Form, Integer> formOffsets;

		public Composite(SceneNode parent, Form... forms) {
			super(parent);

			formOffsets = new HashMap<Form, Integer>();

			size = 0;
			this.forms = forms;

			for (Form q : forms) {
				q.setParent(this);
				formOffsets.put(q, size);

				size += q.getSize();
			}
		}

		public int maxSize() {
			return size;
		}

		@Override
		public Dot getDot(int index) {
			for (int i = 0; i < forms.length; i++) {
				int n = forms[i].getSize();

				if (index >= n)
					index -= n;
				else
					return forms[i].getDot(index);
			}
			return null;
		}

		@Override
		public int transformChildIndex(int index, Form child) {
			return formOffsets.get(child) + index;
		}

		@Override
		public void update(Game g, double s) {
			super.update(g, s);
			for (int i = 0; i < forms.length; i++)
				forms[i].update(g, s);
		}

		@Override
		public int getSize() {
			return size;
		}
	}

	@Override
	public void draw(Graphics2D g, Eye e) {

		// if (! (parent instanceof Form)) {
		// Matrix m = getTransform();
		//
		// for (int i = 0; i < getSize(); i++) {
		// Dot d = getDot(i);
		//
		// Vector3D v = e.toScreenDepthBufferUpdate(m.applyTo(d.pos), -20);
		//
		// if (v != null) {
		// int r = (int) -v.z;
		// g.setColor(Methods.getColor(d.c, 100));
		// g.fillOval((int) v.x - r, (int) v.y - r, (r * 2 + 1), (r * 2 + 1));
		//
		// for (int j = 0; j < d.next.length; j++) {
		// Dot d2 = getDot(d.findNthNext(this, j));
		//
		// Vector3D v2 = e.toScreen(m.applyTo(d2.pos));
		//
		// if (v2 != null) {
		// double pdiff = Methods.bound(d.getProb(j), 0, 1);
		// g.setStroke(new BasicStroke((int) (pdiff * 6)));
		// g.setColor(Methods.colorMeld(new Color(70, 70, 70, 100), d2.c, pdiff));
		// g.drawLine((int) v.x, (int) v.y, e.psX(v2.x), e.psY(v2.y));
		// }
		// }
		// }
		// }
		// }

		super.draw(g, e);

	}
}
