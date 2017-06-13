package com.jack.entities;

import java.awt.Color;
import java.awt.Rectangle;

public class Boundary extends Rectangle {
	
	public Color color;

	public Boundary(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.color = Color.GRAY;
	}

}
