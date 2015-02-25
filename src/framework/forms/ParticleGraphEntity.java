package framework.forms;

import framework.SceneNode;
import game.Game;
import math.Vector3D;

public abstract class ParticleGraphEntity extends SceneNode {
	protected Form form;
	protected Particle particles[];
	public Vector3D pos, velo;

	public ParticleGraphEntity(SceneNode p) {
		super(p);
	}

	@Override
	public void update(Game g, double speed) {
		super.update(g, speed);

		for (int i = 0; i < particles.length; i++)
			particles[i].update();
	}
}
