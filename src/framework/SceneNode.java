
package framework;

import game.Game;

import java.awt.Graphics2D;
import java.util.ArrayList;

import math.Matrix;

public abstract class SceneNode {
	private Matrix transform;
	private Matrix precalculated;

	// properties below
	public boolean updateChildren = true;
	public boolean visible = true;

	protected SceneNode parent;
	ArrayList<SceneNode> children = new ArrayList<SceneNode>();

	public SceneNode(SceneNode p) {
		this.transform = Matrix.createID(4);
		this.precalculated= Matrix.createID(4);

		setParent(p);
	}

	public void setParent(SceneNode p) {
		this.parent = p;
		if (p != null)
			p.children.add(this);
	}

	public void setTransform(Matrix m) {
		this.transform = m;
		calculateTransformToLeaves();
	}

	public Matrix getTransform() {
		return precalculated;
	}

	public Matrix getLocalTransform() {
		return transform;
	}

	public void calculateTransformToRoot() {
		if (parent == null) {
			precalculated = transform;
			return;
		}

		parent.calculateTransformToRoot();
		precalculated = parent.getTransform().mult(transform);
	}

	public void calculateTransformToLeaves() {
		if (parent == null) {
			precalculated = transform;
			return;
		}

		precalculated = parent.getTransform().mult(transform);

		for (SceneNode n : children)
			n.calculateTransformToLeaves();
	}

	// ************** DEBUGGING METHODS **********

	protected void chainPrintParent() {
		if (parent == null) {
			System.out.println("(end)\n");
			return;
		}
		System.out.println(parent.getClass().getName());
		parent.chainPrintParent();
	}

	protected void chainPrintTransform() {
		if (parent == null) {
			System.out.println("(end)\n");
			return;
		}
		System.out.println(precalculated);
		parent.chainPrintTransform();
	}

	protected void chainPrintMatrix() {
		if (parent == null) {
			System.out.println("(end)\n");
			return;
		}
		System.out.println(transform);
		parent.chainPrintMatrix();
	}

	// ************** Override methods ***************

	public void update(Game g, double speed) {		
		if (updateChildren)
			for (SceneNode n : children)
				n.update(g, speed);
	}

	public void draw(Graphics2D g, Eye e) {
		if (!visible)
			return;

		for (SceneNode n : children)
			n.draw(g, e);
	}
}
