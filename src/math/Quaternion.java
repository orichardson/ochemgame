package math;

public class Quaternion {
	double w, x, y, z;

	public Quaternion(double a, double b, double c, double d) {
		w = a;
		x = b;
		y = c;
		z = d;
	}

	public Quaternion conjugate() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public double norm() {
		return Math.sqrt(w * w + x * x + y * y + z * z);
	}

	public Quaternion clone() {
		return new Quaternion(w, x, y, z);
	}

	public Quaternion add(Quaternion other) {
		w += other.w;
		x += other.x;
		y += other.y;
		z += other.z;
		return this;
	}

	public Quaternion mult(Quaternion other) {
		double m = w * other.w - x * other.x - y * other.y - z * other.z;
		double n = w * other.x + x * other.w + y * other.z - z * other.y;
		double o = w * other.y - x * other.z + y * other.w + z * other.x;
		double p = w * other.z + x * other.y - y * other.x + z * other.w;
		w = m;
		x = n;
		y = o;
		z = p;
		return this;
	}
	
	public Matrix toMatrix() {
		double n = w * w + x * x + y * y + z * z;
		double s = (n == 0 ? 0 : 2 / n);
		double wx = s * w * x, wy = s * w * y, wz = s * w * z;
		double xx = s * x * x, xy = s * x * y, xz = s * x * z;
		double yy = s * y * y, yz = s * y * z, zz = s * z * z;
		
		return new Matrix(new double[] { 1 - (yy + zz), xy - wz, xz + wy }, new double[] { xy + wz, 1 - (xx + zz),
				yz - wx }, new double[] { xz - wy, yz + wx, 1 - (xx + yy) });
	}
}
