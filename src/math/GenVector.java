package math;

import java.awt.Point;

public class GenVector {
	public static final GenVector ZERO = new GenVector();
	public double[] data;
	public int dimension;

	public GenVector(double... dat) {
		this.data = dat;
		this.dimension = dat.length;
	}

	public static GenVector create(int dim) {
		double[] dat = new double[dim];
		for (int i = 0; i < dim; i++)
			dat[i] = 0;

		return new GenVector(dat);
	}

	public Matrix toMat() {
		return Matrix.createFromColumns(this);
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < dimension; i++)
			str.append(data[i] + ", ");
		if (dimension > 0) {
			str.deleteCharAt(str.length() - 1);
			str.deleteCharAt(str.length() - 1);
		}
		return "[ " + str.toString() + " ]";
	}

	public static GenVector getIDMask(int num, int dim) {
		GenVector v = create(dim);
		v.data[num] = 1;
		return v;
	}

	public double dot(GenVector other) {
		double total = 0;
		for (int i = 0; i < Math.min(dimension, other.dimension); i++)
			total += other.data[i] * data[i];

		return total;
	}

	public double magnitude() {
		return Math.sqrt(dot(this));
	}

	public double get(int i) {
		return data[i];
	}

	public GenVector normalize() {
		double m = magnitude();
		for (int i = 0; i < dimension; i++)
			data[i] = data[i] / m;
		return this;// for chaining
	}

	public GenVector scale(double d) {
		for (int i = 0; i < dimension; i++)
			data[i] *= d;
		return this;// for chaining
	}

	public GenVector add(GenVector other) {
		for (int i = 0; i < Math.min(dimension, other.dimension); i++)
			data[i] += other.data[i];
		return this;// for chaining
	}

	public GenVector sub(GenVector other) {
		for (int i = 0; i < Math.min(dimension, other.dimension); i++)
			data[i] -= other.data[i];
		return this;// for chaining
	}

	public GenVector clone() {
		return new GenVector(data);
	}

	public static GenVector getSum(GenVector a, GenVector b) {
		return a.clone().add(b);
	}

	public static GenVector getDif(GenVector a, GenVector b) {
		return a.clone().sub(b);
	}

	public boolean isZero() {
		for (int i = 0; i < dimension; i++)
			if (data[i] != 0)
				return false;
		return true;
	}

	public GenVector perp2D() {
		return new GenVector(-get(1), get(0));
	}

	public void sub(Point p) {
		data[0] -= p.x;
		data[1] -= p.y;
	}

	public void add(Point p) {
		data[0] += p.x;
		data[1] += p.y;
	}
}
