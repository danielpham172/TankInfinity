package com.ttr.stage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ttr.screen.GameScreen;
import com.ttr.screen.MainMenuScreen;

public class MainMenu extends Stage implements InputProcessor{
	public MainMenu() {
		super(new ScreenViewport());
		super.addActor(buildTable());
	}
	
	private Table buildTable() {
		Table uiTable = new Table();
		uiTable.setFillParent(true);
		uiTable.setDebug(true); // This is optional, but enables debug lines for tables.

		// Add widgets to the table here.
		Skin skin = new Skin(Gdx.files.internal("menu/uiskin.json"));

		TextButton startButton = new TextButton("New Game", skin);
		TextButton quitButton = new TextButton("Quit Game", skin);
		
		startButton.addListener(new ClickListener() {
	         @Override
	         public void clicked(InputEvent event, float x, float y) {
	        	 ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
	            event.stop();
	         }
	      });

		uiTable.padTop(30);
		uiTable.add(startButton).padBottom(30);
		uiTable.row();
		uiTable.add(quitButton); 
		
		return uiTable;
	}
}