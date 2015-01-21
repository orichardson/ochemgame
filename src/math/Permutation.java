package math;

public class Permutation {
	int[] foreward;
	int[] backward;

	public Permutation(int... n) {
		this.foreward = n;
		createInverse();
	}

	public static Permutation createID(int s) {
		int[] p = new int[s];
		for (int i = 0; i < s; i++) {
			p[i] = i;
		}
		return new Permutation(p);
	}

	public Permutation clone() {
		int[] newO = new int[foreward.length];
		for (int i = 0; i < foreward.length; i++)
			newO[i] = foreward[i];
		return new Permutation(newO);
	}

	public Permutation cyclize() {
		int[] used = new int[foreward.length];
		for (int i = 0; i < foreward.length; i++)
			used[i] = 0; // false

		int last = 0;

		for (int i = 0; i < foreward.length - 1; i++) {
			used[last] = 1;
			int raw = (int) ((foreward.length - i - 1) * Math.random());

			int index = -1, count = 0;

			do {
				index++;
				if (used[index] == 0)
					count++;
			} while (count <= raw);

			foreward[last] = index;
			last = index;
		}

		foreward[last] = 0;
		createInverse();
		return this;
	}

	public void createInverse() {
		int[] in = new int[foreward.length];

		for (int i = 0; i < foreward.length; i++)
			in[foreward[i]] = i;

		this.backward = in;
	}

	public Permutation invert() {
		int[] temp = this.foreward;
		this.foreward = this.backward;
		this.backward = temp;
		return this;
	}

	public Permutation randomize() {
		int[] used = new int[foreward.length];
		for (int i = 0; i < foreward.length; i++)
			used[i] = 0;

		for (int i = 0; i < foreward.length; i++) {
			int raw = (int) ((foreward.length - i) * Math.random());

			int index = -1, count = 0;
			do {
				index++;
				if (used[index] == 0)
					count++;
			} while (count <= raw);

			foreward[i] = index;
			used[index] = 1;
		}
		createInverse();
		return this;
	}

	public int of(int x) {
		return foreward[x];
	}

	public int fo(int y) {
		return backward[y];
	}
}
