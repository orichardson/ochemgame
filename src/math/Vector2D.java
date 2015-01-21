package math;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

public class Vector2D {
	public static final Vector2D ZERO = new Vector2D(0, 0);
	public double x, y;

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double get(int index) {
		return index == 0 ? x : y;
	}

	public Vector2D clone() {
		return new Vector2D(x, y);
	}

	public Vector2D scale(double d) {
		x *= d;
		y *= d;
		return this;
	}

	public double dot(Vector2D other) {
		return x * other.x + y * other.y;
	}

	public double mag() {
		return Math.sqrt(x * x + y * y);
	}

	public double angleBetween(Vector2D other) {
		return Math.acos(dot(other) / (mag() * other.mag()));
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat();
		return "Vector2D(" + df.format(x) + "," + df.format(y) + ")";
	}

	public Vector2D add(Vector2D v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Vector2D negate() {
		x *= -1;
		y *= -1;
		return this;
	}

	public Vector2D unitize() {
		if (x == 0 && y == 0)
			return this;
		double mag = mag();
		x /= mag;
		y /= mag;
		return this;
	}

	public Vector2D perp() {
		double old = x;
		x = -y;
		y = old;
		return this;
	}

	public Vector2D projOnto(Vector2D other) {
		return other.clone().scale(this.dot(other) / other.mag2());
	}

	public void set(double d, double e) {
		x = d;
		y = e;
	}

	public Vector2D minus(Vector2D v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public double mag2() {
		return x * x + y * y;
	}

	public Vector2D rotate(double rotation) {
		double c = Math.cos(rotation), s = Math.sin(rotation);
		double a = x * c + -y * s, b = x * s + y * c;
		x = a;
		y = b;
		return this;
	}

	public void set(Vector2D s) {
		x = s.x;
		y = s.y;
	}

	public double cross2D(Vector2D w) {
		return x * w.y - y * w.x;
	}

	public static Vector2D randomUnit() {
		double t = Math.random() * 2 * Math.PI;
		return new Vector2D(Math.cos(t), Math.sin(t));
	}

	public double angle() {
		return Math.atan2(y, x);
	}

	public Point2D point2D() {
		return new Point2D.Double(x, y);
	}

	public Vector2D sub(Vector2D v1) {
		x -= v1.x;
		y -= v1.y;
		return this;
	}

	public Vector3D to3DV() {
		return new Vector3D(x, y, 0);
	}

	public void approach(Vector2D m, double i) {
		x += (m.x - x) / i;
		y += (m.y - y) / i;
	}

	public Vector2D sub(double x2, double y2) {
		x -= x2;
		y -= y2;
		return this;
	}

	public static Vector2D fromAngle(double dw) {
		return new Vector2D(Math.cos(dw), Math.sin(dw));
	}

	public Vector2D add(Vector3D pos) {
		x += pos.x;
		y += pos.y;
		return this;
	}

	public static double dist(double x2, double y2, double x3, double y3) {
		return Math.sqrt((x2 - x3) * (x2 - x3) + (y2 - y3) * (y2 - y3));
	}
}
