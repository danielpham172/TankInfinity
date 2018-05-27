package com.tank.interfaces;

import java.util.ArrayList;

import com.badlogic.gdx.math.Polygon;

public interface Collidable {
	public void checkCollisions(ArrayList<Collidable> other);
	public Polygon getHitboxAt(float x, float y, float direction);
	public Polygon getHitbox();
	public ArrayList<Collidable> getNeighbors();
}
