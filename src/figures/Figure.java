
package figures;

import java.util.List;

public class Figure {
	public FigPose pose;
	public FigStructure struct;
	List<FigAnimation> animations;

	FigAnimation current_anim;
	
	public Figure(FigStructure fs) {
		asdf.
	}

	public void update(double s) {
		current_anim.update(s);
		pose = FigPose.linearMeld(pose, current_anim.getPose(), 0.8);
	}

	public void setAnimation(FigAnimation a) {
		this.current_anim = a;
	}

	public String() {

	}
	
	public static Figure fromFile(String filename) {

	}
}
