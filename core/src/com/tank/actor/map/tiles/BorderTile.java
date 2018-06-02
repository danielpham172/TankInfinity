package com.tank.actor.map.tiles;

import com.tank.actor.map.Map;
import com.tank.utils.Assets;

public class BorderTile extends WallTile{
	public BorderTile(int row, int col, Map map) {
		super(row, col, map);
	}
	
	@Override
	public void build() {
		super.addTexture(Assets.manager.get(Assets.grass0));
		double rand = Math.random();
		if (rand < 0.25) {
			super.addTexture(Assets.manager.get(Assets.border1));
		} else if (rand < 0.5) {
			super.addTexture(Assets.manager.get(Assets.border2));
		} else if (rand < 0.75) {
			super.addTexture(Assets.manager.get(Assets.border3));
		} else {
			super.addTexture(Assets.manager.get(Assets.border4));
		}
	}
}
