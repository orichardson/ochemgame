
package figures;

import math.Vector3D;

public class FigPose {
	int FIG_PTS;

	public Vector3D[] pos;

	private FigPose(int n) {
		FIG_PTS = n;
		pos = new Vector3D[n];
	}

	public FigPose clone() {
		FigPose p = new FigPose(FIG_PTS);
		for (int i = 0; i < FIG_PTS; i++)
			p.pos[i] = pos[i].clone();

		return p;
	}

	public String pack() {
		return pack(pos);
	}

	public static FigPose linearMeld(FigPose p1, FigPose p2, double prc) {
		// assume p1 and p2 have same npoints, for speed.
		FigPose meld = new FigPose(p1.FIG_PTS);

		for (int i = 0; i < p1.FIG_PTS; i++)
			meld.pos[i] = Vector3D.meld(p1.pos[i], p2.pos[i], prc);

		return meld;
	}

	public static FigPose cubicMeld(FigPose p1, FigPose p2, FigPose p0, FigPose p3, double t1,
			double t2, double prc) {
		// assume p1 and p2 have same npoints, for speed.
		FigPose meld = new FigPose(p1.FIG_PTS);

		for (int i = 0; i < p1.FIG_PTS; i++)
			meld.pos[i] = Vector3D.meldCubic(p1.pos[i], p2.pos[i], p0.pos[i], p3.pos[i], t1, t2,
					prc);

		return meld;
	}

	public static FigPose unpack(String str) {
		String[] split = str.split("\\|");
		FigPose pose = new FigPose(split.length);

		for (int i = 0; i < split.length; i++) {
			pose.pos[i] = Vector3D.unpack(split[i]);
		}

		return pose;
	}

	public static String pack(Vector3D[] things) {
		StringBuilder sb = new StringBuilder();
		for (Vector3D i : things)
			sb.append(i.pack() + " | ");
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public Vector3D get(int i) {
		return pos[i];
	}

}
