
package math;

import java.text.DecimalFormat;

public class Matrix {
	double[][] entries;// row, column

	public Matrix(int a, int b) {
		entries = new double[a][b];// num rows, num columns
		for (int i = 0; i < a; i++)
			for (int j = 0; j < b; j++)
				entries[i][j] = 0;
	}

	public Matrix(double[]... data) {
		this.entries = data;
	}

	public double det() {
		if (getRows() != getColumns())
			throw new IllegalStateException();

		if (getRows() == 0)
			return 1;
		if (getRows() == 1)
			return entries[0][0];
		else if (getRows() == 2)
			return get(0, 0) * get(1, 1) - get(0, 1) * get(1, 0);

		// go along top
		double total = 0;
		for (int j = 0; j < getColumns(); j++)
			total += getEliminationReduction(0, j).det() * entries[0][j] * sign(0, j);

		return total;
	}

	public Matrix getCofactorMatrix() {
		Matrix m = getMinorMatrix();
		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++)
				m.entries[i][j] = m.entries[i][j] * sign(i, j);
		return m;
	}

	public Matrix calculateInverse() {
		double det = det();
		if (det == 0)
			return null;
		return getCofactorMatrix().transpose().scale(1 / det);
	}

	public int sign(int i, int j) {
		return (i + j) % 2 == 0 ? 1 : -1;
	}

	public Matrix getMinorMatrix() {
		Matrix m = new Matrix(getRows(), getColumns());

		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++) {
				m.entries[i][j] = getEliminationReduction(i, j).det();
			}

		return m;
	}

	public Matrix getEliminationReduction(int row, int col) {
		Matrix m = new Matrix(getRows() - 1, getColumns() - 1);

		for (int i = 0; i < getRows() - 1; i++)
			for (int j = 0; j < getColumns() - 1; j++)
				m.entries[i][j] = entries[i < row ? i : i + 1][j < col ? j : j + 1];

		return m;
	}

	public static Matrix createOrthogonalProjection(GenVector... basis) {
		Matrix a = createFromColumns(basis);
		Matrix at = a.getTranspose();
		return a.clone().mult( (at.clone().mult(a)).calculateInverse()).mult(at);
	}

	public static Matrix createFromColumns(GenVector... v) {
		Matrix m = new Matrix(v[0].dimension, v.length);

		for (int i = 0; i < v.length; i++)
			for (int j = 0; j < v[i].dimension; j++)
				m.entries[j][i] = v[i].data[j];

		return m;
	}

	public static Matrix createFromRows(GenVector... v) {
		Matrix m = new Matrix(v.length, v[0].dimension);

		for (int i = 0; i < v.length; i++)
			for (int j = 0; j < v[0].dimension; j++)
				m.entries[i][j] = v[i].data[j];

		return m;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		DecimalFormat df = new DecimalFormat(".##");

		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getColumns(); j++) {
				str.append(df.format(entries[i][j]));
				if (j != getColumns() - 1)
					str.append(",\t");
			}
			if (i != getRows() - 1)
				str.append("},\n{");
		}

		return "\n{{" + str.toString() + "}}";
	}

	public static Matrix createID(int dim) {
		Matrix m = new Matrix(dim, dim);
		for (int i = 0; i < dim; i++)
			m.entries[i][i] = 1;
		return m;
	}

	public int getRows() {
		return entries.length;
	}

	public int getColumns() {
		if (entries.length == 0)
			return 0;
		return entries[0].length;
	}

	public GenVector applyTo(GenVector v) {
		GenVector rslt = GenVector.create(getRows());
		for (int i = 0; i < getRows(); i++) {
			rslt.data[i] = 0;
			for (int j = 0; j < getColumns(); j++)
				rslt.data[i] += (j >= v.dimension ? 0 : v.data[j]) * entries[i][j];
		}
		return rslt;
	}

	public Vector3D applyTo(Vector3D v) {
		Vector3D rslt = new Vector3D();
		for (int i = 0; i < getRows(); i++) {
			double t = 0;
			for (int j = 0; j < getColumns(); j++) {
				t += (j == 3 ? 1 : v.get(j)) * entries[i][j];
			}
			rslt.set(i, t);
		}
		return rslt;
	}

	public void set(int r, int c, double val) {
		entries[r][c] = val;
	}

	public double get(int r, int c) {
		return entries[r][c];
	}

	public Matrix add(Matrix m2) {
		if (getRows() != m2.getRows() || getColumns() != m2.getColumns())
			throw new IllegalArgumentException("Matricies are not of the same dimension");

		Matrix result = createZeroMatrix(getRows(), getColumns());

		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < m2.getColumns(); j++)
				result.set(i, j, get(i, j) + m2.get(i, j));

		return result;
	}

	public Matrix sub(Matrix m2) {
		if (getRows() != m2.getRows() || getColumns() != m2.getColumns())
			throw new IllegalArgumentException("Matricies are not of the same dimension");

		Matrix result = createZeroMatrix(getRows(), getColumns());

		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < m2.getColumns(); j++)
				result.set(i, j, get(i, j) - m2.get(i, j));

		return result;
	}

	public Matrix scale(double s) {
		Matrix result = createZeroMatrix(getRows(), getColumns());

		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++)
				result.set(i, j, get(i, j) * s);

		return result;
	}

	public Matrix mult(Matrix m2) {
		if (getColumns() != m2.getRows())
			throw new IllegalArgumentException(
					"Matricies are not of the correct dimension for multiplication: [" + getRows()
							+ "," + getColumns() + "] x [" + m2.getRows() + "," + m2.getColumns()
							+ "]");

		Matrix result = createZeroMatrix(getRows(), m2.getColumns());

		for (int r = 0; r < getRows(); r++)
			for (int c = 0; c < m2.getColumns(); c++) {
				double total = 0;
				for (int i = 0; i < getColumns(); i++)
					total += get(r, i) * m2.get(i, c);

				result.set(r, c, total);
			}

		return result;
	}

	public boolean equals(Matrix m) {
		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++)
				if (m.get(i, j) != get(i, j))
					return false;
		return true;
	}

	public Matrix clone() {
		double[][] dataclone = new double[getRows()][getColumns()];
		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++)
				dataclone[i][j] = entries[i][j];

		return new Matrix(dataclone);
	}

	public static Matrix createZeroMatrix(int r, int c) {
		double[][] data = new double[r][c];

		for (int i = 0; i < r; i++)
			for (int j = 0; j < c; j++)
				data[i][j] = 0;

		return new Matrix(data);
	}

	public Matrix expandTo(int dim) {
		double[][] data = new double[dim][dim];

		for (int i = 0; i < dim; i++)
			for (int j = 0; j < dim; j++) {
				if (i < entries.length && j < entries[i].length)
					data[i][j] = entries[i][j];
				else if (i == j)
					data[i][j] = 1;
				else
					data[i][j] = 0;
			}

		this.entries = data;
		return this;
	}

	public static Matrix create2DRot(double t) {
		return new Matrix(new double[][]{ { Math.cos(t), -Math.sin(t) },
				{ Math.sin(t), Math.cos(t) } });
	}

	public Matrix transpose() {
		Matrix m = Matrix.createZeroMatrix(getColumns(), getRows());
		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++) {
				m.set(j, i, get(i, j));
			}

		this.entries = m.entries;
		return this;
	}

	public Matrix getTranspose() {
		Matrix m = Matrix.createZeroMatrix(getColumns(), getRows());
		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++) {
				m.set(j, i, get(i, j));
			}

		return m;
	}

	public static Matrix createIdentityMatrix(int n) {
		Matrix m = createZeroMatrix(n, n);

		for (int i = 0; i < n; i++)
			m.set(i, i, 1);

		return m;
	}

	public static Matrix createRandomMatrix(int r, int c, double d) {
		Matrix m = createZeroMatrix(r, c);

		for (int i = 0; i < r; i++)
			for (int j = 0; j < c; j++)
				m.set(i, j, (i == j ? 1 : 0) + d * (Math.random() - 0.5));

		return m;
	}

	public static Matrix create3DAffineTranslateMatrix(Vector3D offset) {
		return new Matrix(new double[][]{ { 1, 0, 0, offset.x }, { 0, 1, 0, offset.y },
				{ 0, 0, 1, offset.z }, { 0, 0, 0, 1 } });
	}

	public static Matrix createRotateX(double theta) {
		double c = Math.cos(theta), s = Math.sin(theta);
		return new Matrix(new double[][]{ { 1, 0, 0 }, { 0, c, -s }, { 0, s, c } });
	}

	public static Matrix createRotateY(double theta) {
		double c = Math.cos(theta), s = Math.sin(theta);
		return new Matrix(new double[][]{ { c, 0, s }, { 0, 1, 0 }, { -s, 0, c } });
	}

	public static Matrix createRotateZ(double theta) {
		double c = Math.cos(theta), s = Math.sin(theta);
		return new Matrix(new double[][]{ { c, -s, 0 }, { s, c, 0 }, { 0, 0, 1 } });
	}

	public static Matrix create3DRotationMatrix(double yaw, double pitch, double roll) {
		double ca = Math.cos(yaw), sa = Math.sin(yaw), cb = Math.cos(pitch), sb = Math.sin(pitch), cg = Math
				.cos(roll), sg = Math.sin(roll);

		return new Matrix(new double[][]{ // keeping lines stright is so
											// hard...
						{ (ca * cb), (ca * sb * sg - sa * cg), (ca * sb * cg + sa * sg) },//
						{ (sa * cb), (sa * sb * sg + ca * cg), (sa * sb * cg - ca * sg) }, //
						{ (-sb), (cb * sg), (cb * cg) } });//
	}

	public static Matrix createArbitraryRotationMatrix(double theta, Vector3D n) {
		double c = Math.cos(theta), s = Math.sin(theta);
		n.normalize();
		return new Matrix(new double[][]{//
						{ n.x * n.x + (n.y * n.y + n.z * n.z) * c, n.x * n.y * (1 - c) - n.z * s,
								n.x * n.z * (1 - c) + n.y * s },//
						{ n.x * n.y * (1 - c) + n.z * s, n.y * n.y + (n.x * n.x + n.z * n.z) * c,
								n.y * n.z * (1 - c) - n.x * s },//
						{ n.x * n.z * (1 - c) - n.y * s, n.z * n.y * (1 - c) + n.x * s,
								n.z * n.z + (n.x * n.x + n.y * n.y) * c } //
				});
	}

	public static Matrix create3DEulerRotMatrix(double alpha, double beta, double gamma) {
		double ca = Math.cos(alpha), cb = Math.cos(beta), cg = Math.cos(gamma), sa = Math
				.sin(alpha), sg = Math.sin(gamma), sb = Math.sin(beta);
		return new Matrix(new double[][]{//
				{ ca * cb * cg - sa * sg, -ca * cb * sg - sa * cg, ca * sb },//
						{ sa * cb * cg + ca * sg, -sa * cb * sg + ca * cg, sa * sb },//
						{ -sb * cg, sb * sg, cb } //
				});
	}

	public static Matrix createCrossProductMatrix(Vector3D v) {
		v.normalize();
		return new Matrix(new double[][]{ { 0, -v.z, v.y }, { v.z, 0, -v.x }, { -v.y, v.x, 0 } });
	}

	public static Matrix createRotationMatrixAbout(Vector3D v, double theta) {
		return createRotationMatrixOnto(new Vector3D(0, 0, 1), v).mult(createRotateZ(theta));
	}

	public static Matrix createRotationMatrixOnto(Vector3D a, Vector3D b) {
		double magA = a.magnitude(), magB = b.magnitude();

		Vector3D v = a.cross(b);

		if (v.mag2() == 0)
			return Matrix.createID(3);

		v.scale(1 / (magA * magB));

		Matrix vx = v.getSkewCrossMatrix();
		double c = a.dot(b) / (magA * magB);

		return createID(3).add(vx).add(vx.mult(vx).scale( (1 - c) / v.mag2()));
	}

	public Matrix getSquareChop() {
		int size = Math.min(getRows(), getColumns());

		Matrix m = new Matrix(size, size);

		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				m.entries[i][j] = entries[i][j];

		return m;
	}

	public Matrix getSquareAugment() {
		int size = Math.max(getRows(), getColumns());

		Matrix m = new Matrix(size, size);

		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++)
				m.entries[i][j] = entries[i][j];

		return m;
	}

	public Matrix getResize(int a, int b) {
		Matrix m = new Matrix(a, b);

		for (int i = 0; i < Math.min(getRows(), a); i++)
			for (int j = 0; j < Math.min(getColumns(), b); j++)
				m.entries[i][j] = entries[i][j];

		return m;
	}

	public static Matrix create3DProjectionMatrix(Vector3D v) {
		v = v.clone().normalize();
		return new Matrix(new double[][]{ { v.x * v.x, v.y * v.x, v.z * v.x },
				{ v.x * v.y, v.y * v.y, v.z * v.y }, { v.x * v.z, v.y * v.z, v.z * v.z } });
	}

	public static Matrix create3DProjectionMatrix(Vector3D a, Vector3D b) {
		a.normalize();
		b.sub(a.clone().scale(b.dot(a)));
		b.normalize();

		return create3DProjectionMatrix(a).add(create3DProjectionMatrix(b));
	}

	public static Matrix getSum(Matrix x, Matrix y) {
		Matrix m = x.clone();
		m.add(y);
		return m;
	}

	public static Matrix getDiff(Matrix x, Matrix y) {
		Matrix m = x.clone();
		m.sub(y);
		return m;
	}

	public static Matrix getProduct(Matrix x, Matrix y) {
		Matrix m = x.clone();
		m.mult(y);
		return m;
	}
}
