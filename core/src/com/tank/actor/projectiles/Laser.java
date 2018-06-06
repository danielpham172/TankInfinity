package com.tank.actor.projectiles;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.tank.actor.map.tiles.AbstractMapTile;
import com.tank.actor.map.tiles.WallTile;
import com.tank.actor.vehicles.AbstractVehicle;
import com.tank.interfaces.Collidable;
import com.tank.stage.Level;
import com.tank.stats.Stats;
import com.tank.utils.Assets;

public class Laser extends AbstractProjectile {
	
	private static Texture laserTexture = Assets.manager.get(Assets.laser);
	private static float angle;	//angle between diagonal of rectangle and its base
    private int bounceCount = 0;
    private float lifeTime;
    private float trailTime;
    private float timeBetweenTrails;
    private ArrayList<AbstractVehicle> vehiclesHit;

	public Laser(AbstractVehicle src, Stats stats, float x, float y, float direction) {
		super(laserTexture, src, stats, x, y);
		Vector2 v = new Vector2(stats.getStatValue("Projectile Speed"), 0);
		timeBetweenTrails = 110f / stats.getStatValue("Projectile Speed");
		lifeTime = 0f;
		velocity = v.setAngle(direction);
		setRotation(direction);
		setOrigin(laserTexture.getWidth() / 2, laserTexture.getHeight() / 2);
		setWidth(60);
		setHeight(7);
		vehiclesHit = new ArrayList<AbstractVehicle>();
		angle = (float)Math.toDegrees(Math.atan((double)getHeight()/getWidth()));
	}
	
	public void act(float delta) {
		lifeTime += delta;
		trailTime += delta;
		if (lifeTime >= stats.getStatValue("Lifetime") / 10.0f) {
			destroy();
			return;
		}
		boolean createTrail = false;
		while (trailTime >= timeBetweenTrails) {
			trailTime -= timeBetweenTrails;
			createTrail = true;
		}
		if (createTrail) getStage().getRoot().addActorBefore(this, new LaserTrail(getX(), getY(), getRotation()));
		damageNeighbors();
		super.act(delta);
	}

	protected void initializeHitbox() {
		hitbox = getHitboxAt(getX(), getY(), getRotation());
	}
	
	@Override
	public void bounce(Vector2 wall) {
		bounceCount += 1;
		if (bounceCount <= stats.getStatValue("Max Bounce")) {
			vehiclesHit.clear();
			super.bounce(wall);
		}
		else {
			destroy();
		}
	}
	
	public ArrayList<Collidable> getNeighbors() {
		// get neighboring bricks. instances of WallTile get added to neighbors
		// add all vehicles not on team to neighbors
		// add all bullets not on team to neighbors, then remove this instance
		ArrayList<Collidable> neighbors = new ArrayList<Collidable>();
		int[] gridCoords = ((Level) getStage()).getMap().getTileAt(getX(), getY());
		ArrayList<WallTile> a = ((Level) getStage()).getMap().getWallNeighbors(gridCoords[0], gridCoords[1]);
		neighbors.addAll(a);
		for (AbstractProjectile p : AbstractProjectile.projectileList) {
			boolean canCollide = !(p.getTeam() != null && getTeam() != null && getTeam().equals(p.getTeam()));
			if (canCollide)
				neighbors.add(p);
		}
		neighbors.remove(this);
		return neighbors;
	}
	
	public void damageNeighbors() {
		ArrayList<AbstractVehicle> nearbyVehicles = new ArrayList<AbstractVehicle>();
		float[] testVertices = hitbox.getVertices(); // vertices of this instance's hitbox
		for (AbstractVehicle v : AbstractVehicle.vehicleList) {
			boolean canCollide = !(v.getTeam() != null && getTeam() != null && getTeam().equals(v.getTeam()));
			if (canCollide) {
				float[] cTestVertices = v.getHitbox().getVertices();
				boolean collided = false;
				for (int i = 0; i < testVertices.length / 2; i++) {
					if (v.getHitbox().contains(testVertices[i * 2], testVertices[i * 2 + 1])) {
						nearbyVehicles.add(v);
						collided = true;
						break;
					}
				}
				if (!collided) {
					for (int i = 0; i < cTestVertices.length / 2; i++) {
						if (hitbox.contains(cTestVertices[i * 2], cTestVertices[i * 2 + 1])) {
							nearbyVehicles.add(v);
							break;
						}
					}
				}
			}
		}
		
		
		for(AbstractVehicle e: nearbyVehicles) {
			if (!vehiclesHit.contains(e)) {
				e.damage(this, stats.getStatValue("Damage"));
				vehiclesHit.add(e);
			}
		}
	}

	@Override
	public Polygon getHitboxAt(float x, float y, float direction) {
		float[] f = new float[8];
		Vector2 v = new Vector2(getWidth(), getHeight());
		v.setAngle(direction);
		v.rotate(angle);
		f[0] = x+ v.x;
		f[1] = y +v.y;
		v.rotate(180-2*angle);
		f[2] = x + v.x;
		f[3] = y + v.y;
		v.rotate(2*angle);
		f[4] = x + v.x;
		f[5] = y + v.y;
		v.rotate(180-2*angle);
		f[6] = x + v.x;
		f[7] = y + v.y;
		return new Polygon(f);
	}
}
