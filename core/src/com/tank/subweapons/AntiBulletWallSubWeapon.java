/**
 * @author Daniel Pham
 * The subweapon class for the projectile- anti bullet wall
 * Description: Implements the given subweapon projectile class and spawns it 
 * correctly onto the map, giving the 'projectile' its necessary information 
 * to spawn, as well as stats, playing the necessary projectile sound, setting 
 * the player's fire rate, and giving recoil to the player upon firing. If 
 * necessary, gives the spawned projectile a degree of randomness and/or 
 * spawns multiple projectiles of the given type.
 */
package com.tank.subweapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.tank.actor.projectiles.AntiBulletWall;
import com.tank.actor.vehicles.PlayerTank;
import com.tank.media.MediaSound;
import com.tank.stats.Stats;
import com.tank.utils.Assets;

public class AntiBulletWallSubWeapon extends SubWeapon{
	/**
	 * the texture of the subweapon
	 */
	private static Texture antiBulletWallTexture = Assets.manager.get(Assets.bulletWall_icon);
	/**
	 * the volume of the shoot sound, out of 1.0
	 */
	private static final float SHOOT_VOLUME = 0.6f;
	/**
	 * the sound used upon shooting
	 */
	private static MediaSound shootSound = new MediaSound(Assets.manager.get(Assets.bulletWall_fire), SHOOT_VOLUME);
	
	public AntiBulletWallSubWeapon(int ammo) {
		super("Anti-Bullet Wall", antiBulletWallTexture, ammo);
	}

	@Override
	public void shoot(PlayerTank source) {
		Vector2 v = new Vector2(PlayerTank.TANK_GUN_LENGTH, 0);
		v.setAngle(source.getGunRotation());
		source.getStage().addActor(new AntiBulletWall(source, createStats(source), source.getX() + v.x, source.getY() + v.y, source.getGunRotation()));
		source.applySecondaryForce(30.0f * (float) Math.sqrt(source.getStatValue("Projectile Speed")), source.getGunRotation() + 180);
		int fireRate = source.getStatValue("Fire Rate");
		source.setReloadTime(5.0f * (1.0f - ((float) (fireRate) / (fireRate + 60))));
		shootSound.play();
	}

	@Override
	public Stats createStats(PlayerTank source) {
		Stats stats = new Stats();
		stats.addStat("Projectile Speed", (int)(70 * Math.sqrt(source.getStatValue("Projectile Speed"))));
		stats.addStat("Projectile Durability", source.getStatValue("Projectile Durability") * 15 + 10);
		stats.addStat("Lifetime", source.getStatValue("Lifetime") * 2 + 30);
		return stats;
	}
}
