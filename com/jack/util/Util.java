package com.jack.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;

import com.jack.entities.Boundary;

public class Util {

	public static boolean intersects(Shape a, Shape b) {
		Area areaA = new Area(a);
		areaA.intersect(new Area(b));
		return !areaA.isEmpty();
	}

	public static boolean inArea(Shape a, Point p) {
		return a.getBounds().contains(p);
	}

	public static boolean isBoundary(ArrayList<Boundary> boundaries, Point p) {

		for (Boundary boundary : boundaries) {
			if (inArea(boundary, p)) {
				return true;
			}
		}
		return false;
	}

	public static boolean inBoundaries(ArrayList<Boundary> boundaries, Rectangle pBounds) {

		for (Boundary boundary : boundaries) {
			if (intersects(boundary, pBounds)) {
				return true;
			}
		}
		return false;
	}

	public static int ordinalIndexOf(String str, String substr, int n) {
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
			pos = str.indexOf(substr, pos + 1);
		return pos;
	}

}
