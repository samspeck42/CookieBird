package com.samspeck.cookiebird.engine;

public class Vector2D {
	
	public float x, y;
	
	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static Vector2D zero() {
		return new Vector2D(0,0);
	}
}
