
package math;

import java.util.ArrayList;

import utils.Methods;

public class FiniteMapping {
	public int IN, OUT;
	int[][] foreward;
	int[][] backward;

	public FiniteMapping(int[]... n) {
		this.foreward = n;
		IN = n.length;
		createInverse();
	}

	public static FiniteMapping createID(int in, int out) {
		int[][] p = new int[in][];
		for (int i = 0; i < Math.max(in, out); i++) {
			p[i % in] = Methods.add(p[i % in], i % out);
		}
		FiniteMapping fm = new FiniteMapping(p);
		return fm;
	}

	public static FiniteMapping createRandom(int in, int out) {
		int[][] p = new int[in][];
		for (int i = 0; i < in; i++) {
			int[] next = new int[out];
			for (int j = 0; j < out; j++)
				next[j] = j;
			p[i] = next;
		}
		FiniteMapping fm = new FiniteMapping(p);
		return fm;
	}

	public FiniteMapping clone() {
		int[][] newO = new int[foreward.length][];
		for (int i = 0; i < foreward.length; i++)
			for (int j = 0; j < foreward[i].length; j++)
				newO[i][j] = foreward[i][j];
		return new FiniteMapping(newO);
	}

	public void createInverse() {
		OUT = Methods.max(foreward) + 1;
		int[][] f2 = new int[OUT][];

		for (int q = 0; q < OUT; q++) {
			ArrayList<Integer> found = new ArrayList<Integer>();
			for (int i = 0; i < foreward.length; i++)
				for (int j = 0; j < foreward[i].length; j++)
					if (foreward[i][j] == q)
						found.add(i);
			f2[q] = Methods.createArray(found);
		}

		this.backward = f2;
	}

	public FiniteMapping invert() {
		int[][] temp = this.foreward;
		this.foreward = this.backward;
		this.backward = temp;

		int te = OUT;
		this.OUT = this.IN;
		this.IN = te;

		return this;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < foreward.length; i++) {
			sb.append(i + " -> ");

			for (int j = 0; j < foreward[i].length; j++)
				sb.append(foreward[i][j] + ",");

			if (foreward[i].length > 0)
				sb.deleteCharAt(sb.length() - 1);

			sb.append("\n");
		}

		// for (int i = 0; i < backward.length; i++) {
		// sb.append("( " + i + " <- ");
		//
		// for (int j = 0; j < backward[i].length; j++)
		// sb.append(backward[i][j] + ",");
		//
		// if (backward[i].length > 0)
		// sb.deleteCharAt(sb.length() - 1);
		//
		// sb.append(" )\n");
		// }

		return sb.toString();
	}

	public FiniteMapping randomizeUniform() {
		boolean inv = false;
		if (IN > OUT) {
			invert();
			inv = true;
		}

		int[] used = new int[OUT];
		for (int i = 0; i < OUT; i++)
			used[i] = 0;

		int nMade = 0;

		for (int i = 0; i < IN; i++) {
			double inF = (Math.max(OUT, IN) - nMade) / (double) (IN - i), outF = 1;

			int toMake = Methods.weightedRandomRound(inF);

			foreward[i] = new int[toMake];

			for (int q = 0; q < toMake; q++) {
				int raw = (int) ( (Math.max(OUT, IN) - nMade) * Math.random());

				int index = -1, count = 0;
				do {
					index++;
					if (used[index % OUT] < outF)
						count++;
				} while (count <= raw);

				foreward[i][q] = index % OUT;
				used[index % OUT]++;

				nMade++;
			}
		}

		createInverse();

		if (inv)
			invert();

		return this;
	}

	// ofRandom
	public int ofR(int x) {
		return foreward[x][(int) (Math.random() * foreward[x].length)];
	}

	// referse of Random
	public int foR(int x) {
		return backward[x][(int) (Math.random() * backward[x].length)];
	}

	public int[] of(int x) {
		return foreward[x];
	}

	public int[] fo(int y) {
		return backward[y];
	}
}
