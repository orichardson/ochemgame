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

/*
 * NOTES: 
 * (1) Atoms have references to their parent groups through the scene graph. 
 */
public class Atom extends ParticleGraphEntity {
	public static final Data[] PROPERTIES;

	public int nprotons;
	ElectronCloud[] bonds; // of size four unless a hydrogen atom, or sp3d hybridized

	static {
		String[] split =
				Methods.getFileContents(new java.io.File("info/elements.txt"))
						.split("\n");
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

	public Atom(SceneNode n, int q) {
		super(n);
		this.nprotons = q;
		bonds = new ElectronCloud[4];

		this.form =
				new Form.Duality(this, DotCloud.createFromText(null,
						Matrix.createID(4),
						getSymb(), new Font("SANS_SERIF", Font.BOLD, 40),
						Methods.randomColor(255),
						Methods.randomColor(255)), DotCloud.createCircle(null,
						100, new Vector3D(),
						new Vector3D(), 0.1));

		this.particles = new Particle[400];
		for (int i = 0; i < particles.length; i++) {
			particles[i] = new Particle(form, Vector3D.random(1));
		}

	}

	public String getSymb() {
		return PROPERTIES[nprotons - 1].symbol;
	}

	public String getName() {
		return PROPERTIES[nprotons - 1].name;
	}

	public int getN() {
		return PROPERTIES[nprotons - 1].neutrons;
	}

	public double getE() {
		return PROPERTIES[nprotons - 1].electro;
	}

	public static class Data {
		String name, symbol;
		int neutrons;
		double electro;
	}

}
