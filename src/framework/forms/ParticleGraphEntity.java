
package framework.forms;

import framework.SceneNode;
import math.Vector3D;

public abstract class ParticleGraphEntity extends SceneNode {
	protected Form form;
	protected Particle particles[];
	public Vector3D pos, velo;
	
	public ParticleGraphEntity(SceneNode p) {
		super(p);
	}
}
