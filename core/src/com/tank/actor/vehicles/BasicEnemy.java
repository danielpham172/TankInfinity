package com.tank.actor.vehicles;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tank.actor.map.tiles.AbstractMapTile;
import com.tank.actor.projectiles.Bullet;
import com.tank.game.Player;
import com.tank.media.MediaSound;
import com.tank.utils.Assets;
import com.tank.utils.lineofsight.LineOfSight;
import com.tank.utils.pathfinding.PathfindingUtil;
import com.tank.stage.Level;

public class BasicEnemy extends FixedTank {
	
	protected Vector2 targetPos = new Vector2(1500, 1500);
	protected LinkedList<Vector2> path;
	protected Thread pathfindingThread;
	protected int[] endTargetTile = new int[] {37, 37};
	protected boolean forwarding;
	protected boolean reversing;
	protected boolean randomTurnReverse;
	protected float reverseTime;
	protected float reverseTimeThreshold;
	
	protected boolean patrolling;
	protected float timeSinceLastPathfind;
	
	protected boolean honeInMode;
	protected boolean attackMode;
	protected PlayerTank target;
	
	protected float cooldownLastShot;
	
	protected MediaSound shoot_sound = new MediaSound(Assets.manager.get(Assets.bullet_fire), 0.5f);
	
	public BasicEnemy(float x, float y) {
		super(x, y, Assets.manager.get(Assets.tread_default));
		initializeStats();
		initializePathfinding();
		forwarding = false;
		reversing = false;
		randomTurnReverse = false;
		patrolling = true;
		reverseTime = 0;
		
		reverseTimeThreshold = 0.5f;
		timeSinceLastPathfind = 0f;
		cooldownLastShot = 0.5f;
	}
	
	protected void initializeStats() {
		stats.addStat("Damage", 35);
		stats.addStat("Spread", 40);
		stats.addStat("Accuracy", 50);
		stats.addStat("Stability", 50);
		stats.addStat("Max Bounce", 1);
		stats.addStat("Projectile Speed", 75);
		stats.addStat("Lifetime", 80);
		stats.addStat("Fire Rate", 30);
		stats.addStat("Max Projectile", 6);
		
		maxHealth = health = 100;
		stats.addStat("Armor", 15);
		
		stats.addStat("Traction", 100); // (fraction out of 100)^delta to scale velocity by
		stats.addStat("Acceleration", 120);
		stats.addStat("Angular Acceleration", 120);
		
		stats.addStat("Projectile Durability", 1);
	}
	
	public void initializePathfinding() {
		path = new LinkedList<Vector2>();
		pathfindingThread = new Thread() {
			@Override
			public void run() {
				int[][] map = ((Level)getStage()).getMap().getLayout();
				int[] startPos = ((Level)getStage()).getMap().getTileAt(getX(), getY());
				synchronized (path) {
					path = PathfindingUtil.pathfinding(map, startPos[0], startPos[1], endTargetTile[0], endTargetTile[1]);
				}
			}
		};
	}
	
	@Override
	public void act(float delta) {
		if(isDestroyed()) return;
		if (patrolling) {
			//Request pathfinding after a certain amount of time not pathfinding
			timeSinceLastPathfind += delta;
			if (timeSinceLastPathfind >= 20f) requestPathfinding();
			//Check if it's on the path (may happen if enemy overshoots tiles)
			if (isOnPath()) setNextTarget(path.removeFirst());
			//Finished patrolling to certain tile? Find something new
			if (path.isEmpty() || onTile(endTargetTile) || targetPos == null) {
				selectNewEndTargetTile();
			}
			//Any player nearby?
			float shortestDistance = -1f;
			for (Player player : getPlayers()) {
				PlayerTank playerTank = player.tank;
				if (playerTank != null && !playerTank.isDestroyed()) {
					float distance = getDistanceTo(playerTank);
					if (shortestDistance == -1f || distance < shortestDistance) {
						shortestDistance = distance;
						if (shortestDistance <= AbstractMapTile.SIZE * 12) {
							target = playerTank;
							patrolling = false;
							honeInMode = true;
						}
					}
				}
			}
			
			//Moving based things
			moveByTargetTile(delta);
		}
		else if (honeInMode && target != null) {
			if (isOnPath()) setNextTarget(path.removeFirst());
			float distanceToTarget = getDistanceTo(target);
			if (distanceToTarget <= AbstractMapTile.SIZE * 15) {
				if (distanceToTarget >= AbstractMapTile.SIZE * 8 || !hasLineOfSight(target.getX(), target.getY())) {
					timeSinceLastPathfind += delta;
					if (timeSinceLastPathfind >= 20f) {
						endTargetTile = getTileAt(target.getX(), target.getY());
						requestPathfinding();
					}
					else if (path.isEmpty() || onTile(endTargetTile) || targetPos == null) {
						endTargetTile = getTileAt(target.getX(), target.getY());
						requestPathfinding();
					}
					moveByTargetTile(delta);
				}
				else {
					timeSinceLastPathfind += delta;
					if (timeSinceLastPathfind >= 20f) {
						endTargetTile = getTileAt(target.getX(), target.getY());
						requestPathfinding();
					}
					if (reversing) {
						backingUp(delta);
					}
					else if (forwarding) {
						super.applyForce(delta * stats.getStatValue("Acceleration") * 10f, getRotation());
						reverseTime += delta;
						if (reverseTime >= 0.5f) {
							forwarding = false;
							reverseTime = -delta;
						}
						
					}
					else if (!rotateTowardsTarget(delta, target.getX(), target.getY())) {
						if (cooldownLastShot <= 0f) {
							shoot();
							int fireRate = stats.getStatValue("Fire Rate");
							cooldownLastShot = 2.0f * (1.0f - ((float)(fireRate) / (fireRate + 60)));
						}
					}
				}
			}
			else {
				target = null;
				honeInMode = false;
			}
		}
		else {
			patrolling = true;
			selectNewEndTargetTile();
		}
		
		super.applyFriction(delta);
		if (!super.move(delta)){
			if (honeInMode && cooldownLastShot > 0f) {
				
			}
			else if (!reversing && !forwarding && reverseTime >= 1.0f) {
				reversing = true;
				if (reverseTime < 2.5f) {
					reverseTimeThreshold += 0.5f;
					if (reverseTimeThreshold >= 1.5f) {
						randomTurnReverse = (Math.random() < 0.5);
					}
				}
				else reverseTimeThreshold = 0.5f;
				reverseTime = 0f;
			}
			else if (!reversing && !forwarding) {
				reverseTime += delta;
			}
		}
		else{
			reverseTime += delta;
		}
		if (cooldownLastShot > 0f) cooldownLastShot -= delta;
	}
	
	public void moveByTargetTile(float delta) {
		if (targetPos != null && !reversing && !forwarding) {
			moveToTarget(delta);
		}
		else {
			if (reversing) {
				backingUp(delta);
			}
			else if (forwarding) {
				super.applyForce(delta * stats.getStatValue("Acceleration") * 10f, getRotation());
				reverseTime += delta;
				if (reverseTime >= 0.5f) {
					forwarding = false;
					reverseTime = -delta;
				}
				
			}
			else if (targetPos == null) {
				if (path != null && !path.isEmpty()) {
					Vector2 nextTile;
					do {
						nextTile = path.removeFirst();
					} while (!path.isEmpty() && (onTile((int)nextTile.x, (int)nextTile.y)));
					synchronized (path) { setNextTarget(nextTile); }
				}
			}
		}
	}
	
	public void moveToTarget(float delta) {
		float targetRotation = (float) Math.toDegrees(Math.atan2((targetPos.y - getY()), (targetPos.x - getX())));
		float rotationDifference = targetRotation - getRotation();
		while (rotationDifference < -180f) {
			rotationDifference += 360f;
		}
		while (rotationDifference > 180f) {
			rotationDifference -= 360f;
		}
		int direction = 0;
		if (rotationDifference > 10) direction = 1;
		else if (rotationDifference < -10) direction = -1;
		
		int moveForward = 0;
		if (!(onTile(getTileAt(targetPos.x, targetPos.y)))) {
			if (Math.abs(rotationDifference) < 25f) moveForward = 1;
		}
		else {
			if (!(onTile(endTargetTile))) {
				if (path != null && !path.isEmpty()) {
					Vector2 nextTile;
					do {
						nextTile = path.removeFirst();
					} while (!path.isEmpty() && (onTile((int)nextTile.x, (int)nextTile.y)));
					synchronized (path) { setNextTarget(nextTile); }
				}
			}
			else {
				targetPos = null;
				direction = 0;
				moveForward = 0;
			}
		}
		//requestPathfinding();
		super.applyAngularForce(delta * stats.getStatValue("Angular Acceleration") * 2.5f * direction);
		super.applyForce(delta * stats.getStatValue("Acceleration")  * 10f * moveForward, getRotation());
	}
	
	public boolean rotateTowardsTarget(float delta, float x, float y) {
		float targetRotation = (float) Math.toDegrees(Math.atan2((y - getY()), (x - getX())));
		float rotationDifference = targetRotation - getRotation();
		while (rotationDifference < -180f) {
			rotationDifference += 360f;
		}
		while (rotationDifference > 180f) {
			rotationDifference -= 360f;
		}
		int direction = 0;
		if (rotationDifference > 10) direction = 1;
		else if (rotationDifference < -10) direction = -1;
		
		super.applyAngularForce(delta * stats.getStatValue("Angular Acceleration") * 2.5f * direction);
		return (direction != 0);
	}
	
	public void shoot() {
		Vector2 v = new Vector2(160, 0);
		float randomAngle = randomShootAngle();
		v.setAngle(getRotation());
		getStage().addActor(new Bullet(this, createBulletStats(), getX() + v.x, getY() + v.y, getRotation() + randomAngle));
		shoot_sound.play();
	}
	
	public void backingUp(float delta) {
		super.applyForce(delta * stats.getStatValue("Acceleration") * 10f, 180 + getRotation());
		if (reverseTimeThreshold >= 1.5f) {
			if (randomTurnReverse) 
				super.applyAngularForce(delta * stats.getStatValue("Angular Acceleration") * 2.5f);
			else
				super.applyAngularForce(-delta * stats.getStatValue("Angular Acceleration") * 2.5f);
		}
		reverseTime += delta;
		if (reverseTime >= reverseTimeThreshold) {
			reversing = false;
			if (reverseTime >= 2.5f) {
				if (patrolling) requestPathfinding();
				forwarding = true;
			}
			reverseTime = -delta;
		}
	}
	
	public void selectNewEndTargetTile() {
		AbstractMapTile randomEmpty = ((Level)getStage()).getMap().getRandomFloorTile();
		endTargetTile[0] = randomEmpty.getRow();
		endTargetTile[1] = randomEmpty.getCol();
		requestPathfinding();
	}
	
	public boolean isOnPath() {
		if (path != null) {
			ListIterator<Vector2> iter = path.listIterator();
			while (iter.hasNext()) {
				Vector2 tile = iter.next();
				if (onTile((int)tile.x, (int)tile.y)) {
					//setNextTarget(tile);
					iter.previous();
					while (iter.hasPrevious()) {
						iter.previous();
						iter.remove();
					}
					return true;
				}
			}
		}
		return false;
	}
	
	public void requestPathfinding() {
		timeSinceLastPathfind = 0;
		if (!pathfindingThread.isAlive() && endTargetTile != null) {
			pathfindingThread = new Thread() {
				@Override
				public void run() {
					int[][] map = ((Level)getStage()).getMap().getLayout();
					int[] startPos = ((Level)getStage()).getMap().getTileAt(getX(), getY());
					path = PathfindingUtil.pathfinding(map, startPos[0], startPos[1], endTargetTile[0], endTargetTile[1]);
				}
			};
			pathfindingThread.start();
		}
	}
	
	public boolean hasLineOfSight(float x, float y) {
		int[] currentTile = getTileAt(getX(), getY());
		int[] targetTile = getTileAt(x, y);
		int[][] map = ((Level)getStage()).getMap().getLayout();
		return LineOfSight.hasSight(map, currentTile[0], currentTile[1], targetTile[0], targetTile[1]);
	}
	
	public void setNextTarget(Vector2 rowCol) {
		float x = rowCol.y * AbstractMapTile.SIZE + AbstractMapTile.SIZE / 2;	//center of tile
		float y = rowCol.x * AbstractMapTile.SIZE + AbstractMapTile.SIZE / 2;
		targetPos = new Vector2(x, y);
	}
	
	public int[] getTileAt(float x, float y) {
		return ((Level)getStage()).getMap().getTileAt(x, y);
	}
	
	public ArrayList<Player> getPlayers(){
		return ((Level)getStage()).getGame().players;
	}
	
	public boolean onTile(int row, int col) {
		int[] currentTile = getTileAt(getX(), getY());
		return (currentTile[0] == row && currentTile[1] == col);
	}
	
	public boolean onTile(int[] rowCol) {
		int[] currentTile = getTileAt(getX(), getY());
		return (currentTile[0] == rowCol[0] && currentTile[1] == rowCol[1]);
	}
	
	public float getDistanceTo(Actor other) {
		return (float)Math.sqrt(Math.pow(getX() - other.getX(), 2) + Math.pow(getY() - other.getY(), 2));
	}

	@Override
	protected void initializeHitbox() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Polygon getHitboxAt(float x, float y, float direction) {
		float[] f = new float[8];
		Vector2 v = new Vector2((float) (super.tankTexture.getWidth()) / 2, 0);
		v.setAngle(direction);
		v.rotate(45);

		for (int i = 0; i < 4; i++) {
			f[i * 2] = x + v.x;
			f[i * 2 + 1] = y + v.y;
			v.rotate(90);
		}
		return new Polygon(f);
	}
	
	@Override
	public String getTeam() {
		return "ENEMY";
	}
}
