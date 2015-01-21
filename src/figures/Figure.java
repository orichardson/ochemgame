package figures;

import java.util.List;

public class Figure {
	public FigPose pose;
	public FigStructure struct;
	List<FigAnimation> animations;
	String filename;

	FigAnimation current_anim;

	public Figure(FigStructure fs) {
		
	}

	public void update(double s) {
		current_anim.update(s);
		pose = FigPose.linearMeld(pose, current_anim.getPose(), 0.8);
	}

	public void setAnimation(FigAnimation a) {
		this.current_anim = a;
	}

	public static Figure fromFile(String filename) {
		String
	}
}
