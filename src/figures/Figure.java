
package figures;

import java.io.File;
import java.util.List;

import utils.Methods;

public class Figure {
	public FigPose pose;
	public FigStructure struct;
	List<FigAnimation> animations;
	String filename;

	FigAnimation current_anim;

	public Figure(FigStructure fs) {
		this.struct = fs;
	}

	public void update(double s) {
		current_anim.update(s);
		pose = FigPose.linearMeld(pose, current_anim.getPose(), 0.8);
	}

	public void setAnimation(FigAnimation a) {
		this.current_anim = a;
	}

	public static Figure fromFile(String filename) {
		String[] lines = Methods.getFileContents(new File(filename)).split("\n");

		FigStructure struct = FigStructure.unpack(lines[0]);
		return new Figure(struct);
	}
}
