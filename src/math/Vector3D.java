
package math;

import java.text.DecimalFormat;

import utils.Methods;

public class Vector3D {
	public static final Vector3D ZERO = new Vector3D(0, 0, 0);
	public double x, y, z;

	public Vector3D(double x, double y, double z) {
		set(x, y, z);
	}

	public Vector3D() {
		this(0, 0, 0);
	}

	public double magnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public double getHorizontalAngle() {
		return Math.atan2(y, x);
	}

	public double getVerticalAngle() {
		return Math.acos(z / magnitude());
	}

	public double dot(Vector3D other) {
		return x * other.x + y * other.y + z * other.z;
	}

	public Vector3D cross(Vector3D v) {
		return new Vector3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
	}

	public double angleBetween(Vector3D other) {
		return Math.acos(dot(other) / (magnitude() * other.magnitude()));
	}

	public Vector3D clone() {
		return new Vector3D(x, y, z);
	}

	public static Vector3D cubicInterpolate(Vector3D v1, Vector3D v2, Vector3D v3, Vector3D v4,
			double d) {
		return new Vector3D(// asdf
				Methods.cubInterpolate(v1.x, v2.x, v3.x, v4.x, d),//
				Methods.cubInterpolate(v1.y, v2.y, v3.y, v4.y, d), //
				Methods.cubInterpolate(v1.z, v2.z, v3.z, v4.z, d));//
	}

	public Vector3D add(Vector3D v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	public Vector3D projOnto(Vector3D other) {
		if (other.mag2() == 0)
			return clone();
		return other.clone().scale(this.dot(other) / other.mag2());
	}

	public Vector2D to2DV() {
		return new Vector2D(x, y);
	}

	public Vector3D negate() {
		x *= -1;
		y *= -1;
		z *= -1;
		return this;
	}

	public Vector3D normalize() {
		double m = magnitude();
		z /= m;
		x /= m;
		y /= m;
		return this;
	}

	public Vector3D set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat(".##");
		return "Vector3D(" + df.format(x) + ", " + df.format(y) + ", " + df.format(z) + ")";
	}

	public Vector3D incrementAngle(double alpha) {
		double a = Methods.getRotateX(x, y, alpha);
		double b = Methods.getRotateY(x, y, alpha);
		x = a;
		y = b;
		return this;
	}

	public Vector3D scale(double m) {
		x *= m;
		y *= m;
		z *= m;
		return this;
	}

	public Vector3D sub(Vector3D v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	public double get(int j) {
		switch (j) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			return 0;
		}
	}

	public void set(int j, double v) {
		switch (j) {
		case 0:
			x = v;
		case 1:
			y = v;
		case 2:
			z = v;
		}
	}

	public Vector3D add(Vector2D v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public void approach(Vector3D v, int n) {
		x += (v.x - x) / n;
		y += (v.y - y) / n;
		z += (v.z - z) / n;
	}

	public void set(Vector2D v) {
		x = v.x;
		y = v.y;
	}

	public Vector3D add(double a, double b, double c) {
		x += a;
		y += b;
		z += c;
		return this;
	}

	public double mag2() {
		return x * x + y * y + z * z;
	}

	public Vector3D set(Vector3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
		return this;
	}

	public static Vector3D meld(Vector3D a, Vector3D b, double d) {
		return new Vector3D(a.x + (b.x - a.x) * d, a.y + (b.y - a.y) * d, a.z + (b.z - a.z) * d);
	}

	public void set(Vector3D v, boolean a, boolean b, boolean c) {
		if (a)
			x = v.x;
		if (b)
			y = v.y;
		if (c)
			z = v.z;
	}

	public String pack() {
		return x + "," + y + "," + z;
	}

	public static Vector3D unpack(String string) {
		String[] parts = string.split(",");
		return new Vector3D(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
				Double.parseDouble(parts[2]));
	}

	public Vector3D sub(Vector2D v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public Vector3D perp() {
		double old = x;
		x = -y;
		y = old;
		return this;
	}

	public Vector3D reflectAcrossPoint(Vector3D v) {
		return v.clone().scale(2).sub(this);
	}

	public Vector3D abs() {
		if (x < 0)
			x = -x;
		if (y < 0)
			y = -y;
		if (z < 0)
			z = -z;
		return this;
	}

	public double dist2(Vector3D c) {
		return (c.z - z) * (c.z - z) + (c.x - x) * (c.x - x) + (c.y - y) * (c.y - y);
	}

	public Vector3D projOnto(Vector2D q) {
		return projOnto(new Vector3D(q.x, q.y, 0));
	}

	public static Vector3D random(double q) {
		q = Math.cbrt(Math.random()) * q;
		double phi = Math.random() * 2 * Math.PI;
		double theta = Math.acos(Math.random() * 2 - 1);

		return new Vector3D(q * Math.cos(phi) * Math.sin(theta), q * Math.sin(phi)
				* Math.sin(theta), q * Math.cos(theta));
	}

	public Matrix getSkewCrossMatrix() {
		return new Matrix(new double[][]{ { 0, -z, y }, { z, 0, -x }, { -y, x, 0 } });
	}

	public static Vector3D meldCubic(Vector3D v1, Vector3D v2, Vector3D v0, Vector3D v3, double prc) {
		return new Vector3D(cub_int(v1.x, v2.x, v0.x, v3.x, prc), cub_int(v1.y, v2.y, v0.y, v3.y,
				prc), cub_int(v1.z, v2.z, v0.z, v3.z, prc));
	}

	public static Vector3D meldCubic(Vector3D v1, Vector3D v2, Vector3D v0, Vector3D v3, double d1,
			double d2, double prc) {
		return new Vector3D(cub_int(v1.x, v2.x, v0.x, v3.x, d1, d2, prc), cub_int(v1.y, v2.y, v0.y,
				v3.y, d1, d2, prc), cub_int(v1.z, v2.z, v0.z, v3.z, d1, d2, prc));
	}

	private static double cub_int(double p1, double p2, double p0, double p3, double prc) {
		double a0, a1, x2;
		x2 = prc * prc;
		a0 = p3 - p2 - p0 + p1;
		a1 = p0 - p1 - a0;
		return (a0 * x2 * prc) + a1 * x2 + (p2 - p0) * prc + p1;
	}

	private static double cub_int(double p1, double p2, double p0, double p3, double d1, double d2,
			double prc) {
		double a0, a1, a2, x2, den1, den2, den3, den4, r, s;

		r = -d1;
		s = 1 + d2;

		den1 = (r - 1) * r * (r - s);
		den2 = r * s;
		den3 = (r - 1) * (s - 1);
		den4 = (s - 1) * s * (r - s);

		x2 = prc * prc;
		a0 = p0 / den1 - p1 / den2 + p2 / den3 - p3 / den4;
		a1 = -p0 * (s + 1) / den1 + (r + s + 1) * p1 / den2 - (r + s) * p2 / den3 + p3 * (r + 1)
				/ den4;
		a2 = - (a0 + a1) - p1 + p2;
		// a2 = s * p0 / den1 - (s*r + r + s)*p1/den2 + r*s*p2/den3 - r*p3/den4;
		// System.out.println(w+"\t"+a2+"\t"+(w-a2));
		return (a0 * x2 * prc) + a1 * x2 + a2 * prc + p1;
	}
}
