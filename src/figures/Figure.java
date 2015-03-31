package figures;

import java.io.File;
import java.util.List;

import utils.Methods;

public class Figure {
	String fileName;

	public FigPose pose;
	public FigStructure struct;
	List<FigAnimation> animations;
	String filename;

	FigAnimation current_anim;

	public Figure(FigStructure fs) {
		this.struct = fs;
		this.pose = fs.defaultPose.clone();
	}

	public double update(double s) {
		double q = current_anim.update(s);
		pose = FigPose.linearMeld(pose, current_anim.getPose(), 0.4);
		return q;
	}

	public void setAnimation(FigAnimation a) {
		this.current_anim = a;
	}

	public static Figure createEmptyFigure(int npts) {
		String structstr =
				Methods.getFileContents(new File("Resources/Figure/Structures/" + npts
						+ "PTFIG.struct"));
		return new Figure(FigStructure.unpack(structstr));
	}

	public Figure setFile(String name) {
		this.fileName = name;
		return this;
	}

	public void saveToFile() {

	}

	public static Figure fromFile(String filename) {
		String[] lines =
				Methods.getFileContents(new File(filename)).split("\n");

		FigStructure struct = FigStructure.unpack(lines[0]);
		return new Figure(struct);
	}

	public String getFileName() {
		return fileName;
	}
}
