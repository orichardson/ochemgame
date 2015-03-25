
package figures;

import math.Vector3D;

public class FigPose {
	int FIG_PTS;

	public Vector3D[] pos;
	public double[] sizes;

	private FigPose(int n) {
		FIG_PTS = n;
		pos = new Vector3D[n];
		sizes = new double[n];
	}

	public FigPose clone() {
		FigPose p = new FigPose(FIG_PTS);
		for (int i = 0; i < FIG_PTS; i++) {
			p.pos[i] = pos[i].clone();
			p.sizes[i] = sizes[i];
		}

		return p;
	}

	public String pack() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < FIG_PTS; i++) {
			sb.append(pos[i].pack() + ", " + sizes[i] + " | ");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static FigPose linearMeld(FigPose p1, FigPose p2, double prc) {
		// assume p1 and p2 have same npoints, for speed.
		FigPose meld = new FigPose(p1.FIG_PTS);

		for (int i = 0; i < p1.FIG_PTS; i++) {
			meld.pos[i] = Vector3D.meld(p1.pos[i], p2.pos[i], prc);
			meld.sizes[i] = p1.sizes[i] + prc * (p2.sizes[i] - p1.sizes[i]);
		}

		return meld;
	}

	public static FigPose cubicMeld(FigPose p1, FigPose p2, FigPose p0, FigPose p3, double t1,
			double t2, double prc) {
		// assume p1 and p2 have same npoints, for speed.
		FigPose meld = new FigPose(p1.FIG_PTS);

		for (int i = 0; i < p1.FIG_PTS; i++) {
			meld.pos[i] = Vector3D.meldCubic(p1.pos[i], p2.pos[i], p0.pos[i], p3.pos[i], t1, t2,
					prc);
			meld.sizes[i] = Vector3D.cub_int(p1.sizes[i], p2.sizes[i], p0.sizes[i], p3.sizes[i],
					t1, t2, prc);
		}

		return meld;
	}

	public static FigPose unpack(String str) {
		String[] split = str.split("\\|");
		FigPose pose = new FigPose(split.length);

		for (int i = 0; i < split.length; i++) {
			String[] parts = split[i].split(",");

			pose.pos[i] = new Vector3D(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
					Double.parseDouble(parts[2]));
			pose.sizes[i] = parts.length > 3 ? Double.parseDouble(parts[3]) : 0;
		}

		return pose;
	}

	public Vector3D get(int i) {
		return pos[i];
	}

}
