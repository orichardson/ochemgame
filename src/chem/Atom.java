
package chem;

import java.awt.Font;

import math.Matrix;
import math.Vector3D;
import utils.Methods;
import framework.SceneNode;
import framework.forms.DotCloud;
import framework.forms.Form;
import framework.forms.Particle;
import framework.forms.ParticleGraphEntity;
import game.Game;

public class Atom extends ParticleGraphEntity {
	public static final Data[] PROPERTIES;

	public int nprotons;
	Bond[] bonds; // of size four unless a hydrogen atom, or sp3d hybridized

	static {
		String[] split = Methods.getFileContents(new java.io.File("info/elements.txt")).split("\n");
		PROPERTIES = new Data[split.length];

		for (int i = 0; i < split.length; i++) {
			String[] sub = split[i].split("\t");
			PROPERTIES[i] = new Data();
			PROPERTIES[i].symbol = sub[0];
			PROPERTIES[i].name = sub[1];
			PROPERTIES[i].neutrons = Integer.parseInt(sub[2]);
			PROPERTIES[i].electro = Double.parseDouble(sub[3]);
		}
	}

	public Atom(SceneNode n, int protons) {
		super(n);
		this.nprotons = protons;

		/*
		 * Form: Each Neutron / Proton is a render shard of 1 particle
		 */
		this.form = new Form.Duality(this, DotCloud.createFromText(null, Matrix.createID(4),
				getSymbol(), new Font("SANS_SERIF", Font.BOLD, 40), Methods.randomColor(255),
				Methods.randomColor(255)), DotCloud.createCircle(null, 100, new Vector3D(),
				new Vector3D(), 0.1));

		this.particles = new Particle[400];
		for (int i = 0; i < particles.length; i++) {
			particles[i] = new Particle(form, Vector3D.random(1));
		}
	}

	@Override
	public void update(Game g, double speed) {
		super.update(g, speed);

		// setTransform(getLocalTransform().mult(Matrix.createRandomMatrix(4, 4,
		// 0.01).expandTo(4)));

		Form.Duality f = ((Form.Duality) form);
		if (f.get() < 0.001)
			f.to(1);
		else if(f.get() > 0.999)
			f.to(0);

		for (int i = 0; i < particles.length; i++)
			particles[i].update();
	}

	public String getSymbol() {
		return PROPERTIES[nprotons - 1].symbol;
	}

	public String getName() {
		return PROPERTIES[nprotons - 1].name;
	}

	public int getNeutrons() {
		return PROPERTIES[nprotons - 1].neutrons;
	}

	public double getElectronegativity() {
		return PROPERTIES[nprotons - 1].electro;
	}

	public static class Data {
		String name, symbol;
		int neutrons;
		double electro;
	}
}
