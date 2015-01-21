
package figures;

import java.awt.Color;
import java.util.LinkedList;

public class FigStructure {
	public int NPTS, NLINES, NTRIS;

	// point-indexed sets:
	public double[] ptsizes;
	public int[][] neighbors; // (computed)

	// line-indexed sets
	public double[] lengths;
	public int[] line1, line2;

	// triangle-indexed sets
	public int[] tri1, tri2, tri3;
	public Color[] colors;

	public FigPose defaultPose;

	// empty private constructor (want to create via static factory methods)
	private FigStructure() {}

	public String save() {
		StringBuilder strb = new StringBuilder();

		strb.append("Autogenerated Structure file. Comments are default; coding regions are lines of the form PARAM|VALUE\n\n");
		strb.append("NPTS\t|" + NPTS + "\n");
		strb.append("PTSIZES\t|" + save(ptsizes) + "\n");
		strb.append("LENGTHS\t|" + save(lengths) + "\n");
		strb.append("LINE1\t|" + save(line1) + "\n");
		strb.append("LINE2\t|" + save(line2) + "\n");
		strb.append("\n");
		strb.append("TRI1\t|" + save(tri1) + "\n");
		strb.append("TRI2\t|" + save(tri2) + "\n");
		strb.append("TRI3\t|" + save(tri3) + "\n");
		strb.append("COLORS\t|" + save(colors));

		return strb.toString();
	}
	public void computeNeighbors() {
		neighbors = new int[NPTS][];

		for (int i = 0; i < NPTS; i++) {
			LinkedList<Integer> found = new LinkedList<Integer>();
			for (int l = 0; l < NLINES; l++) {
				if (line1[l] == i)
					found.add(line2[l]);
				if (line2[l] == i)
					found.add(line1[l]);
			}

			neighbors[i] = new int[found.size()];

			int count = 0;
			for (Integer f : found)
				neighbors[i][count++] = f;
		}
	}
	public static FigStructure unpack(String str) {
		String[] lines = str.split("\n");

		FigStructure struct = new FigStructure();

		for (int i = 0; i < lines.length; i++) {
			if (!lines[i].contains("|"))
				continue;

			String data = lines[i].substring(lines[i].indexOf("|") + 1);

			if (lines[i].startsWith("NPTS\t|"))
				struct.NPTS = Integer.parseInt(data);

			else if (lines[i].startsWith("PTSIZES\t|"))
				struct.ptsizes = parseDoubles(data);

			else if (lines[i].startsWith("LENGTHS\t|"))
				struct.lengths = parseDoubles(data);

			else if (lines[i].startsWith("LINE1\t|"))
				struct.line1 = parseInts(data);

			else if (lines[i].startsWith("LINE2\t|"))
				struct.line2 = parseInts(data);

			else if (lines[i].startsWith("TRI1\t|"))
				struct.tri1 = parseInts(data);

			else if (lines[i].startsWith("TRI2\t|"))
				struct.tri2 = parseInts(data);

			else if (lines[i].startsWith("TRI3\t|"))
				struct.tri3 = parseInts(data);

			else if (lines[i].startsWith("COLORS\t|")) {
				String[] split = data.split("\\s");
				struct.colors = new Color[split.length];

				for (int q = 0; q < split.length; q++)
					struct.colors[q] = new Color(Integer.parseInt(split[q]));
			}

			else if (lines[i].startsWith("DEFAULT\t|"))
				struct.defaultPose = FigPose.unpack(data);
		}

		if (struct.tri1 == null) {
			struct.NTRIS = 0;
			struct.tri1 = new int[0];
			struct.tri2 = new int[0];
			struct.tri3 = new int[0];
		}

		struct.NLINES = struct.line1.length;
		struct.NTRIS = struct.tri1.length;

		if (struct.line2.length != struct.NLINES || struct.lengths.length != struct.NLINES)
			throw new IllegalArgumentException(
					"Malformed Struct File: ragged length numbers !( |end1| == |end2| == |length| )");

		if (struct.colors.length != struct.NTRIS || struct.tri2.length != struct.NTRIS
				|| struct.tri3.length != struct.NTRIS)
			throw new IllegalArgumentException(
					"Malformed Struct File: ragged length numbers !( |tri1| == |tri2| == |tri3| == |color| )");

		struct.computeNeighbors();

		return struct;
	}
	// ***** helper methods...
	public static int[] parseInts(String line) {
		String[] parts = line.split("\\s");
		int[] ints = new int[parts.length];

		for (int i = 0; i < ints.length; i++)
			ints[i] = Integer.parseInt(parts[i]);

		return ints;
	}
	public static double[] parseDoubles(String line) {
		String[] parts = line.split("\\s");
		double[] ints = new double[parts.length];

		for (int i = 0; i < ints.length; i++)
			ints[i] = Double.parseDouble(parts[i]);

		return ints;
	}
	public static String save(Color[] things) {
		StringBuilder sb = new StringBuilder();
		for (Color i : things)
			sb.append(i.getRGB() + "\t");
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	public static String save(int[] things) {
		StringBuilder sb = new StringBuilder();
		for (int i : things)
			sb.append(i + "\t");
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	public static String save(double[] things) {
		StringBuilder sb = new StringBuilder();
		for (double i : things)
			sb.append(i + "\t");
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
