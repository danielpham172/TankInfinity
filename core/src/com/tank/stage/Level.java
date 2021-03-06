package com.tank.stage;

import java.util.ArrayList;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.tank.actor.items.AbstractItem;
import com.tank.actor.items.RepairBoxItem;
import com.tank.actor.items.SubWeaponItem;
import com.tank.actor.map.Map;
import com.tank.actor.map.tiles.AbstractMapTile;
import com.tank.actor.map.tiles.FloorTile;
import com.tank.actor.projectiles.AbstractProjectile;
import com.tank.actor.projectiles.Caltrop;
import com.tank.actor.projectiles.LandMine;
import com.tank.actor.ui.AbstractUI;
import com.tank.actor.vehicles.AbstractVehicle;
import com.tank.actor.vehicles.ArtilleryEnemy;
import com.tank.actor.vehicles.PeashooterEnemy;
import com.tank.actor.vehicles.BigShotEnemy;
import com.tank.actor.vehicles.FreePeashooterEnemy;
import com.tank.actor.vehicles.LandminerEnemy;
import com.tank.animations.DeathExplosion;
import com.tank.game.Player;
import com.tank.game.TankInfinity;

public class Level extends Stage {
	protected TankInfinity game;
	protected Actor groundLevel;
	protected Actor subSkyLevel;
	protected Actor skyLevel;
	protected int mapWidth;
	protected int mapHeight;
	protected Map map;
	protected LevelCamera camera;
	protected int enemyCount;

	/**
	 * Creates a new level depending on the level number
	 */
	public Level(TankInfinity game, int levelNum, float timePlayed) {
		// world is first scaled to fit within the viewport, then the shorter dimension
		// is lengthened to fill the viewport
		super(new ExtendViewport(15 * AbstractMapTile.SIZE, 9 * AbstractMapTile.SIZE));
		this.game = game;
		mapWidth = 20 + (int) (Math.pow(levelNum - 1, 1.1) / 2 + Math.pow(levelNum - 1, 1.5) / 5);
		mapHeight = 20 + (int) (Math.pow(levelNum - 1, 0.9) / 2 + Math.pow(levelNum - 1, 1.4) / 6);

		map = new Map(mapWidth, mapHeight, this);
		super.addActor(map);
		
		ArrayList<FloorTile> emptySpaces = map.getEmptyNonSpawnFloorTiles();
		int minItems = (int) (2.5 * Math.pow(levelNum, 0.25) + Math.pow(levelNum, 0.5));
		int maxItems = (int) (5.0 * Math.pow(levelNum, 0.25) + Math.pow(levelNum, 0.5));
		int itemCount = (int) (Math.random() * (maxItems - minItems)) + minItems - (int)(Math.max(0, (timePlayed / 60) - levelNum));
		for (int i = 0; i < itemCount + 1; i++) {
			if (!emptySpaces.isEmpty()) {
				AbstractMapTile randomFloor = emptySpaces.remove((int) (Math.random() * emptySpaces.size()));
				if (Math.random() < 0.9) {
					super.addActor(new SubWeaponItem(randomFloor.getRow(), randomFloor.getCol()));
				}
				else {
					super.addActor(new RepairBoxItem(randomFloor.getRow(), randomFloor.getCol(), new String()));
				}
			}
		}
		
		super.addActor((groundLevel = new Actor()));

		int minEnemies = (int) (2.0 * Math.pow(levelNum, 0.25) + Math.pow(levelNum, 1.1) / 2) + 1;
		int maxEnemies = (int) (3.5 * Math.pow(levelNum, 0.25) + Math.pow(levelNum, 1.1) / 2);
		int enemyCount = (int) (Math.random() * (maxEnemies - minEnemies)) + minEnemies;
		for (int i = 0; i < enemyCount; i++) {
			if (!emptySpaces.isEmpty()) {
				AbstractMapTile randomFloor = emptySpaces.remove((int) (Math.random() * emptySpaces.size()));
				int[] pos = new int[] { randomFloor.getCol() * AbstractMapTile.SIZE + AbstractMapTile.SIZE / 2,
						randomFloor.getRow() * AbstractMapTile.SIZE + AbstractMapTile.SIZE / 2 };
				switch ((int)(Math.random() * 9)) {
					case 0: case 2: case 7:
						super.addActor(new PeashooterEnemy(pos[0], pos[1], levelNum));
						break;
					case 1: case 3:
						super.addActor(new FreePeashooterEnemy(pos[0], pos[1], levelNum));
						break;
					case 4: case 8:
						super.addActor(new BigShotEnemy(pos[0], pos[1], levelNum));
						break;
					case 5:
						super.addActor(new ArtilleryEnemy(pos[0], pos[1], levelNum));
						break;
					case 6:
						super.addActor(new LandminerEnemy(pos[0], pos[1], levelNum));
						break;
				}
			}
		}
		this.enemyCount = enemyCount;
		
		if (levelNum == 1)
			spawnInPlayers(true);
		else
			spawnInPlayers(false);
		
		super.addActor((subSkyLevel = new Actor()));
		super.addActor((skyLevel = new Actor()));

		// replace default stage OrthographicCamera with LevelCamera
		camera = new LevelCamera(mapWidth, mapHeight, this.game.players);
		super.getViewport().setCamera(camera);
	}

	private void spawnInPlayers(boolean first) {
		ArrayList<Player> players = new ArrayList<Player>();
		for (Player p : game.players) {
			if (p.isEnabled()) {
				players.add(p);
				// p.initializeTank();
				// addActor(p.tank);
			}
		}

		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (players.size() == 1) {
				p.initializeTank(map.getSpawnPoint()[0], map.getSpawnPoint()[1], 90, first);
			} else if (players.size() == 2) {
				if (i == 0) {
					p.initializeTank(map.getSpawnPoint()[0], map.getSpawnPoint()[1] - 1, 180, first);
				} else if (i == 1) {
					p.initializeTank(map.getSpawnPoint()[0], map.getSpawnPoint()[1] + 1, 0, first);
				}
			} else if (players.size() == 3) {
				if (i == 0) {
					p.initializeTank(map.getSpawnPoint()[0] + 1, map.getSpawnPoint()[1], 90, first);
				} else if (i == 1) {
					p.initializeTank(map.getSpawnPoint()[0] - 1, map.getSpawnPoint()[1] - 1, 210, first);
				} else if (i == 2) {
					p.initializeTank(map.getSpawnPoint()[0] - 1, map.getSpawnPoint()[1] + 1, 330, first);
				}
			} else if (players.size() == 4) {
				if (i == 0) {
					p.initializeTank(map.getSpawnPoint()[0] + 1, map.getSpawnPoint()[1] - 1, 90, first);
				} else if (i == 1) {
					p.initializeTank(map.getSpawnPoint()[0] + 1, map.getSpawnPoint()[1] + 1, 0, first);
				} else if (i == 2) {
					p.initializeTank(map.getSpawnPoint()[0] - 1, map.getSpawnPoint()[1] - 1, 180, first);
				} else if (i == 3) {
					p.initializeTank(map.getSpawnPoint()[0] - 1, map.getSpawnPoint()[1] + 1, 270, first);
				}
			}
			super.addActor(p.tank);
		}
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public LevelCamera getCamera() {
		return camera;
	}

	public Map getMap() {
		return map;
	}

	public TankInfinity getGame() {
		return game;
	}
	
	public int getEnemyCount() {
		return enemyCount;
	}
	
	public void changeEnemyCount(int change) {
		enemyCount += change;
	}
	
	@Override
	public void addActor(Actor a) {
		if (a instanceof AbstractProjectile || a instanceof DeathExplosion) {
			if (!(a instanceof LandMine) && !(a instanceof Caltrop)) {
				getRoot().addActorBefore(subSkyLevel, a);
			}
			else {
				getRoot().addActorBefore(groundLevel, a);
			}
		}
		else if (a instanceof AbstractVehicle) {
			getRoot().addActorAfter(groundLevel, a);
		}
		else if (a instanceof AbstractItem){
			getRoot().addActorBefore(groundLevel, a);
		}
		else if (a instanceof AbstractUI || a instanceof Label) {
			getRoot().addActorBefore(skyLevel, a);
		}
		else {
			super.addActor(a);
		}
	}

	@Override
	public void dispose() {
		for (AbstractVehicle vehicle : AbstractVehicle.vehicleList) {
			vehicle.remove();
		}
		AbstractVehicle.vehicleList.clear();
		for (AbstractProjectile projectile : AbstractProjectile.projectileList) {
			projectile.remove();
		}
		AbstractProjectile.projectileList.clear();
		for (AbstractItem item : AbstractItem.items) {
			item.remove();
		}
		AbstractItem.items.clear();
		for (Actor actor : getActors()) {
			actor.remove();
		}
		super.dispose();
	}
}