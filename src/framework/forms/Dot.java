
package framework.forms;

import java.awt.Color;

import math.Vector3D;
import utils.Methods;

public class Dot {
	// Struct Variables
	int index;
	public Vector3D pos;
	public Color c;

	// GRAPH STRUCTURE
	int[] next;
	private double[] probability;
	private double totalP;
	// reference to dotcloud
	Form home;

	public Dot(int index, Vector3D p, Color c, Form place) {
		this.index = index;
		this.pos = p;
		this.c = c;
		this.home = place;
	}

	public void setNext(int... d) {
		this.next = d;
		probability = Methods.arrayOf(1, d.length);
		totalP = d.length;
	}

	public void setNext(int[] d, double[] p) {
		this.next = d;
		this.probability = p;

		totalP = 0;
		for (double q : p)
			totalP += q;
	}

	public double getProb(int i) {
		return probability[i] / totalP;
	}

	public int findNthNext(Form target, int n) {
		return home.findIndexRecursive(next[n], null, target);
	}

	public int pullNext(Form target) {
		if (next.length == 0)
			return home.findIndexRecursive(index, null, target);

		double q = Math.random() * totalP;
		for (int i = 0; i < next.length; i++) {
			if (q > probability[i])
				q -= probability[i];
			else {
				return home.findIndexRecursive(next[i], null, target);
			}
		}

		return -1;
	}

	public void referencedUpdate(Dot ad, Dot bd, double prc) {
		if (! (home instanceof Form.Duality)) {
			System.err
					.println("!!!!!!!!!!!!!!!!!! Non-duality Trying to meld connections !!!!!!!!!!!!!!!!!!!!!");
			return;
		}

		int la = ad.next.length, lb = bd.next.length;
		if (next == null || probability == null || next.length != la + lb) {
			next = new int[la + lb];
			probability = new double[la + lb];
		}

		double totalP = 0;

		for (int i = 0; i < la; i++) {
			probability[i] = (1 - prc) * ad.probability[i];
			next[i] = ad.home.findIndexRecursive(ad.next[i], null, home);
			totalP += (1 - prc) * ad.probability[i];
		}

		for (int i = 0; i < lb; i++) {
			probability[i + la] = prc * bd.probability[i];
			next[i + la] = bd.home.findIndexRecursive(bd.next[i], null, home);
			totalP += prc * bd.probability[i];
		}
		this.totalP = totalP;
	}
}
