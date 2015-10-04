
package utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import math.Vector2D;

public class Methods {
	public static String getFileContents(File f) {
		// Make a string builder.
		StringBuilder sb = new StringBuilder();
		try {
			// Make a reader
			BufferedReader fr = new BufferedReader(new FileReader(f));
			// Go through the reader line by line and form it into a string.
			while (true) {
				String temp = fr.readLine();
				if (temp == null)
					break;
				sb.append(temp + "\n");
			}
			// Close the readers.
			fr.close();
			// Return the string
			return sb.toString();
		} catch (Exception e) {
			// Catch an exception.
			e.printStackTrace();
		}
		return null;
	}

	public static void centerFrame(int frameWidth, int frameHeight, Component c) {
		Toolkit tools = Toolkit.getDefaultToolkit();
		Dimension screen = tools.getScreenSize();
		int xUpperLeftCorner = (screen.width - frameWidth) / 2;
		int yUpperLeftCorner = (screen.height - frameHeight) / 2;

		c.setBounds(xUpperLeftCorner, yUpperLeftCorner, frameWidth, frameHeight);
	}

	public static void replaceColor(BufferedImage img, Color a, Color b, int tollerence) {
		int w = img.getWidth(), h = img.getHeight();
		int[] pixels = img.getRGB(0, 0, w, h, null, 0, w);

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (colorsAreClose(pixels[x + w * y], a.getRGB(), tollerence))
					img.setRGB(x, y, b.getRGB());
			}
		}
	}

	public static void drawArrow(Graphics g, int x, int y, int x2, int y2) {
		g.drawLine(x, y, x2, y2);
		g.drawLine(x2, y2, x2 + (x - x2) / 8 + (y2 - y) / 4, y2 + (y - y2) / 8 - (x2 - x) / 4);
		g.drawLine(x2, y2, x2 + (x - x2) / 8 - (y2 - y) / 4, y2 + (y - y2) / 8 + (x2 - x) / 4);
	}

	public static void drawCenteredText(Graphics g, String s, int x, int y, int w, int h) {
		// Find the size of string s in font f in the current Graphics context
		// g.
		FontMetrics fm = g.getFontMetrics(g.getFont());

		java.awt.geom.Rectangle2D rect = fm.getStringBounds(s, g);

		int textHeight = (int) (rect.getHeight());
		int textWidth = (int) (rect.getWidth());

		// Center text horizontally and vertically
		int p = (w - textWidth) / 2 + x;
		int q = (h - textHeight) / 2 + fm.getAscent() + y;

		g.drawString(s, p, q); // Draw the string.
	}

	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ( (len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static int[] ensureContains(int[] array, int... values) {
		int newLength = array.length + values.length;
		for (int i = 0; i < values.length; i++)
			if (Methods.contains(array, values[i]))
				newLength--;

		int[] newArray = new int[newLength];
		int count = 0;
		for (int v : array)
			newArray[count++] = v;

		for (int v : values)
			if (!Methods.contains(array, v))
				newArray[count++] = v;

		return newArray;
	}

	public static int[] removeAll(int[] array, int... values) {
		int newLength = array.length - values.length;
		for (int i = 0; i < values.length; i++)
			if (!Methods.contains(array, values[i]))
				newLength++;

		int[] newArray = new int[newLength];
		int count = 0;
		for (int v : array) {
			if (Methods.contains(values, v))
				continue;
			newArray[count++] = v;
		}

		return newArray;
	}

	public static Vector2D lineIntersect(Vector2D p1, Vector2D p2, Vector2D p3, Vector2D p4) {
		Vector2D q2 = p2.clone().sub(p1);
		Vector2D q4 = p4.clone().sub(p1);

		double bot = (q2.cross2D(q4));

		if (bot == 0)
			return null;

		double param = (p3.clone().sub(p1).cross2D(q4)) / bot;
		return p1.add(q2.scale(param));
	}

	public static Vector2D segmentIntersect(Vector2D p1, Vector2D p2, Vector2D p3, Vector2D p4) {
		// line p1-->p2, p3-->p4
		Vector2D q2 = p2.clone().sub(p1);
		Vector2D q4 = p4.clone().sub(p3);

		double bot = (q2.cross2D(q4));

		if (bot == 0)
			return null;

		double slide1 = (p3.clone().sub(p1).cross2D(q4)) / bot;
		double slide2 = (p1.clone().sub(p3).cross2D(q2)) / bot;

		if (slide1 > 0 && slide1 < 1 && slide2 > 0 && slide2 < 1) {
			return p1.clone().add(q2.scale(slide1));
		}
		return null;
	}

	public static Vector2D toLine(Vector2D p1, Vector2D p2, Vector2D point) {
		Vector2D dist = p2.clone().sub(p1), end = point.clone().sub(p1);
		return end.sub(end.projOnto(dist));
	}

	public static int getComposite(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = (rgb >> 0) & 0xff;
		return (r + g + b) / 3;
	}

	public static <T> int indexOf(T object, T[] array) {
		for (int i = 0; i < array.length; i++)
			if (object.equals(array[i]))
				return i;
		return -1;
	}

	public static int[] ensureContainsOnly(int[] array, int... values) {
		int newLength = array.length;
		for (int i = 0; i < array.length; i++)
			if (!Methods.contains(values, array[i]))
				newLength--;

		int[] newArray = new int[newLength];
		int count = 0;
		for (int v : array) {
			if (Methods.contains(values, v))
				newArray[count++] = v;
		}

		return newArray;
	}

	public static boolean colorsAreClose(int a, int b, int t) {
		int a1 = (a >> 24) & 0xff;
		int r1 = (a >> 16) & 0xff;
		int g1 = (a >> 8) & 0xff;
		int b1 = (a >> 0) & 0xff;

		int a2 = (b >> 24) & 0xff;
		int r2 = (b >> 16) & 0xff;
		int g2 = (b >> 8) & 0xff;
		int b2 = (b >> 0) & 0xff;

		return (Math.abs(r1 - r2) <= t) && (Math.abs(g1 - g2) <= t) && (Math.abs(b1 - b2) <= t)
				&& (Math.abs(a1 - a2) <= t);
	}

	public static Color getOppositeColor(Color c) {
		return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
	}

	public static <T> void shuffle(T[] stuff) {
		for (int i = 0; i < stuff.length * 10; i++)
			swap(stuff, (int) (Math.random() * stuff.length), (int) (Math.random() * stuff.length));
	}

	public static void shuffle(int[] stuff) {
		for (int i = 0; i < stuff.length * 10; i++)
			swap(stuff, (int) (Math.random() * stuff.length), (int) (Math.random() * stuff.length));
	}

	public static <T> void swap(T[] stuff, int posA, int posB) {
		T inA = stuff[posA];
		stuff[posA] = stuff[posB];
		stuff[posB] = inA;
	}

	public static Color colorMeld(Color a, Color b, double ratio) {
		return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * ratio),
				(int) (a.getGreen() + (b.getGreen() - a.getGreen()) * ratio),
				(int) (a.getBlue() + (b.getBlue() - a.getBlue()) * ratio),
				(int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * ratio));
	}

	public static Color colorMeld(Color a, Color b, double ratio, int q) {
		return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * ratio),
				(int) (a.getGreen() + (b.getGreen() - a.getGreen()) * ratio),
				(int) (a.getBlue() + (b.getBlue() - a.getBlue()) * ratio), q);
	}

	public static Point mouse() {
		try {
			return MouseInfo.getPointerInfo().getLocation();
		} catch (Exception e) {
			return new Point(0, 0);
		}
	}

	public static void swap(int[] stuff, int posA, int posB) {
		int inA = stuff[posA];
		stuff[posA] = stuff[posB];
		stuff[posB] = inA;
	}

	public static double pseudoRandom(int x) {
		x = (x << 13) ^ x;
		return (1.0f - ( (x * (x * x * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0d);
	}

	public static Color randomColor() {
		return new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
				(int) (Math.random() * 255));
	}

	public static Color randomColor(int alpha) {
		return new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
				(int) (Math.random() * 255), alpha);
	}

	public static Color randomColor(int seed, int alpha) {
		return new Color((int) Math.abs(pseudoRandom(seed) * 255),
				(int) Math.abs(pseudoRandom(seed + 52) * 255),
				(int) Math.abs(pseudoRandom(seed * 5) * 255),
				alpha < 0 ? (int) Math.abs(pseudoRandom(seed * 29 + 17) * 255) : alpha);
	}

	public static Color randomColorRange(int seed, int alpha, int min, int max) {
		return new Color((int) Math.abs(pseudoRandom(seed) * (max - min) + min),
				(int) Math.abs(pseudoRandom(seed + 52) * (max - min) + min),
				(int) Math.abs(pseudoRandom(seed * 5) * (max - min) + min),
				alpha < 0 ? (int) Math.abs(pseudoRandom(seed * 29 + 17) * 255) : alpha);
	}

	public static int[] createArray(ArrayList<Integer> found) {
		int[] js = new int[found.size()];
		for (int i = 0; i < found.size(); i++)
			js[i] = found.get(i);
		return js;
	}

	public static Object[] createObjectArray(ArrayList<? extends Object> ls, Object[] ar) {
		for (int i = 0; i < ls.size(); i++)
			ar[i] = ls.get(i);
		return ar;
	}

	public static <T> ArrayList<T> getList(T[] from) {
		ArrayList<T> list = new ArrayList<T>();

		for (T t : from)
			list.add(t);

		return list;
	}

	public static double max(double[][] array) {
		double max = array[0][0];
		for (double[] d : array)
			for (double number : d)
				if (number > max)
					max = number;

		return max;
	}

	public static int max(int[][] array) {
		int max = array[0][0];
		for (int[] d : array)
			for (int number : d)
				if (number > max)
					max = number;

		return max;
	}

	public static double getMax(Collection<? extends Number> nums) {
		double max = nums.iterator().next().doubleValue();
		for (Number n : nums)
			if (n.doubleValue() > max)
				max = n.doubleValue();
		return max;

	}

	public static double sum(double[] array) {
		double sum = 0;
		for (double d : array)
			sum += d;
		return sum;
	}

	public static int sum(List<Integer> array) {
		int sum = 0;
		for (int d : array)
			sum += d;
		return sum;
	}

	/*
	 * start at i and don't quite get to the end; thus createSubArray(0,length,Array[]) will coppy
	 * it.
	 */
	public static <T> T[] createSubArray(T[] original, int start, int end, T[] toFill) {
		if (toFill.length != end - start) {
			System.err.println("Big problem in Methods.createSubArray(T[] a, int b, int c, T[] d)");
			return null;
		}

		for (int i = start; i < end; i++) {
			toFill[i - start] = original[i];
		}
		return toFill;
	}

	public static boolean contains(int[] array, int value) {
		for (int o : array)
			if (o == value)
				return true;
		return false;
	}

	public static boolean contains(double[] array, double value) {
		for (double o : array)
			if (o == value)
				return true;
		return false;
	}

	public static <T> boolean contains(T[] array, T value) {
		for (T o : array)
			if (o.equals(value))
				return true;
		return false;
	}

	public static <T> T[] createSubArray(List<T> original, int start, int end, T[] toFill) {
		if (toFill.length != end - start) {
			System.out.println("Big problem in Methods.createSubArray(T[] a, int b, int c, T[] d)");
			return null;
		}

		for (int i = start; i < end; i++)
			toFill[i - start] = original.get(i);

		return toFill;
	}

	public static int[] unbox(Integer[] createArray) {
		int[] arr = new int[createArray.length];
		for (int i = 0; i < createArray.length; i++)
			arr[i] = createArray[i];

		return arr;
	}

	public static double[] unbox(Double[] createArray) {
		double[] arr = new double[createArray.length];
		for (int i = 0; i < createArray.length; i++)
			arr[i] = createArray[i];

		return arr;
	}

	/*
	 * http://www.dreamincode.net/code/snippet516.htm
	 */
	public static void insertionSort(double[] list) {
		int firstOutOfOrder, location;
		double temp;

		for (firstOutOfOrder = 1; firstOutOfOrder < list.length; firstOutOfOrder++) {
			// Starts at second term, goes until the end of the array.
			if (list[firstOutOfOrder] < list[firstOutOfOrder - 1]) {
				// If the two are out of order, we move the element to its
				// rightful place.
				temp = list[firstOutOfOrder];
				location = firstOutOfOrder;

				do { // Keep moving down the array until we find exactly where
						// it's supposed to go.
					list[location] = list[location - 1];
					location--;
				} while (location > 0 && list[location - 1] > temp);

				list[location] = temp;
			}
		}
	}

	public static double mean(double[] vals) {
		if (vals.length == 0)
			return 0;

		double mean = 0;

		for (double d : vals)
			mean += d;

		return mean / vals.length;
	}

	public static int mean(int[] vals) {
		if (vals.length == 0)
			return 0;

		int mean = 0;

		for (double d : vals)
			mean += d;

		return mean / vals.length;
	}

	public static double median(double[] vals) {
		if (vals.length == 0)
			return 0;
		if (vals.length == 1)
			return vals[0];

		double[] d = new double[vals.length];
		for (int i = 0; i < vals.length; i++)
			d[i] = vals[i];

		insertionSort(d);
		return d.length % 2 == 1 ? d[d.length / 2 - 1]
				: (d[d.length / 2 - 1] + d[d.length / 2]) / 2;
	}

	public static double mode(double[] vals) {
		HashMap<Double, Integer> occ = new HashMap<Double, Integer>();

		for (double d : vals)
			if (occ.containsKey(d))
				occ.put(d, occ.get(d) + 1);
			else
				occ.put(d, 1);

		double mode = 0;

		for (Double d : occ.keySet())
			if (occ.get(d) > (occ.get(mode) == null ? -1 : occ.get(mode)))
				mode = d;

		return mode;
	}

	public static double stdev(double[] vals) {
		if (vals.length < 2)
			return 0;

		double sum = 0;
		double mean = mean(vals);

		for (double d : vals)
			sum += (mean - d) * (mean - d);

		return Math.sqrt(sum / (vals.length - 1));
	}

	public static int[] intersection(int[] classes, int[] classes2) {
		int count = 0;
		for (int c : classes)
			if (contains(classes2, c))
				count++;

		int[] ints = new int[count];

		for (int i = 0; i < classes.length; i++)
			if (contains(classes2, classes[i]))
				ints[--count] = classes[i];

		return ints;
	}

	public static int min(int... arr) {
		int min = arr[0];
		for (int number : arr)
			if (number < min)
				min = number;
		return min;
	}

	public static int count(String text, char c) {
		int count = 0;
		for (int i = 0; i < text.length(); i++)
			if (text.charAt(i) == c)
				count++;
		return count;
	}

	public static Color getColor(Color color, int i) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), i);
	}

	public static double cosInterpolate(double a, double b, double x) {
		return a * (1 - (1 - Math.cos(Math.PI * x) / 2)) + b * (1 - Math.cos(Math.PI * x)) / 2;
	}

	public static double linInterpolate(double a, double b, double x) {
		return a + (b - a) * x;
	}

	public static double cubInterpolate(double y0, double y1, double y2, double y3, double mu) {
		double a0, a1, a2, a3, mu2;

		mu2 = mu * mu;
		a0 = y3 - y2 - y0 + y1;
		a1 = y0 - y1 - a0;
		a2 = y2 - y0;
		a3 = y1;

		return (a0 * mu * mu2 + a1 * mu2 + a2 * mu + a3);
	}

	public static double getRotateX(double x, double y, double angle) {
		return x * Math.cos(angle) - y * Math.sin(angle);
	}

	public static double getRotateY(double x, double y, double angle) {
		return y * Math.cos(angle) + x * Math.sin(angle);
	}

	public static double bound(double x, double i, double j) {
		return x < i ? i : x > j ? j : x;
	}

	public static double[] arrayOf(double v, int length) {
		double[] x = new double[length];
		for (int i = 0; i < length; i++)
			x[i] = v;
		return x;
	}

	public static int weightedRandomRound(double factor) {
		if (Math.random() > (factor % 1))
			return (int) factor;
		return (int) factor + 1;
	}

	public static int[] add(int[] is, int q) {
		if (is == null)
			return new int[]{ q };

		int[] newA = new int[is.length + 1];
		for (int i = 0; i < is.length; i++)
			newA[i] = is[i];
		newA[is.length] = q;
		return newA;
	}

	public static Color colorMeldBiLin(Color c1, Color c2, Color c3, Color c4, double x, double y) {
		return colorMeld(colorMeld(c1, c2, x), colorMeld(c3, c4, x), y);
	}

}