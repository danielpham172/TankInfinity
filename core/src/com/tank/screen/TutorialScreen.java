
/**
 * @author The Church of Daniel Pham
 * Description:
 * Screens are one of the highest levels of code we
 * implement using Libgdx, the highest being Game/DesktopLauncher.
 * The Libgdx framework calls the render(...) method,
 * and we write the Stages that get called in this method.
 * Screens essentially implement their non-screen,
 * Stage counterparts, calling their act(...) and draw()
 * methods.
 * 
 * The Tutorial Screen class is used to invoke the logic
 * and draw the textures of the tutorial, which include
 * tutorial images and navigation buttons
 */
package com.tank.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.tank.game.TankInfinity;
import com.tank.stage.Tutorial;
import com.tank.utils.Constants;

public class TutorialScreen implements Screen {
	public Tutorial tutorial;
	protected TankInfinity game;
	
	public TutorialScreen (TankInfinity game) {
		this.game = game;
		tutorial = new Tutorial(this.game);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void show() {
		tutorial.goToFirstSlide();
		game.addInput(tutorial);
	}
	
	@Override
	public void hide() {
		game.removeInput(tutorial);
	}

	@Override
	public void resize (int width, int height) {
		tutorial.getViewport().update(width, height, true);
	}
	
	@Override
	public void render (float delta) {
		//Clear the screen
		Gdx.gl.glClearColor(Constants.CLEAR_COLOR, Constants.CLEAR_COLOR, Constants.CLEAR_COLOR, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Update the stage
		tutorial.act(delta);
		tutorial.draw();
	}

	public void dispose() {
		tutorial.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}