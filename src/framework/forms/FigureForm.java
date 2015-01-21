
package framework.forms;

import figures.Figure;
import framework.SceneNode;
import game.Game;

public class FigureForm extends DotCloud {
	Figure fig;

	public FigureForm(SceneNode par, Figure s) {
		super(par, s.struct.NPTS);
		this.fig = s;
	}
	
	@Override
	public void update(Game g, double speed) {
		super.update(g, speed);
		for(int i = 0; i < dot.length; i++)
			dot[i].pos = this.fig.pose.get(i);
	}
}
