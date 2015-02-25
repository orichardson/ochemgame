package figures;

import java.util.List;

public class Figure {
	String fileName;
	
	public FigPose pose;
	public FigStructure struct;
	List<FigAnimation> animations;

	FigAnimation current_anim;

	public Figure(FigStructure fs) {}

	public void update(double s) {
		current_anim.update(s);
		pose = FigPose.linearMeld(pose, current_anim.getPose(), 0.8);
	}

	public void setAnimation(FigAnimation a) {
		this.current_anim = a;
	}

	public Figure setFile(String name) {
		this.fileName = name;
		return this;
	}

	public void saveToFile() {
		
	}

	public static Figure fromFile(String filename) {
		
	}
}
