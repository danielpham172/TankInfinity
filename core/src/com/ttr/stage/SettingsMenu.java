package com.ttr.stage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ttr.TankTankRevolution;
import com.ttr.utils.Assets;
import com.ttr.utils.Constants;

public class SettingsMenu extends Stage implements InputProcessor{
	protected Game game;
	private Skin skin = Assets.manager.get(Assets.skin);
	
	public SettingsMenu(Game game) {
		super(new ScreenViewport());
		this.game = game;
		super.addActor(buildTable());
	}
	
	private Table buildTable() {
		Table uiTable = new Table();
		uiTable.setFillParent(true);
		uiTable.setDebug(true); // This is optional, but enables debug lines for tables.

		// Add widgets to the table here.
		Label windowLabel = new Label("Window Mode ", skin);
		windowLabel.setAlignment(Align.right);
		final TextButton windowModeButton = new TextButton(Constants.WINDOW_MODES[Constants.WINDOW_MODE_INDEX], skin);
		Label resolutionLabel = new Label("Resolution ", skin);
		resolutionLabel.setAlignment(Align.right);
		final Label resolutionValueLabel = new Label(Constants.WINDOW_WIDTH + " x " + Constants.WINDOW_HEIGHT, skin);
		resolutionValueLabel.setAlignment(Align.center);
		TextButton plusButton = new TextButton("+", skin);
		TextButton minusButton = new TextButton("-", skin);
		TextButton applyButton = new TextButton("Apply", skin);
		TextButton returnButton = new TextButton("Return to Main Menu", skin);
		
		windowModeButton.addListener(new ClickListener() {
	         @Override
	         public void clicked(InputEvent event, float x, float y) {
	        	 Constants.toggleWindowMode();
	        	 windowModeButton.setText(Constants.WINDOW_MODES[Constants.WINDOW_MODE_INDEX]);
	        	 event.stop();
	         }
	      });
		
		plusButton.addListener(new ClickListener() {
	         @Override
	         public void clicked(InputEvent event, float x, float y) {
	        	 Constants.toggleResolution(true);
	        	 resolutionValueLabel.setText(Constants.WINDOW_WIDTH + " x " + Constants.WINDOW_HEIGHT);
	        	 event.stop();
	         }
	      });
		
		minusButton.addListener(new ClickListener() {
	         @Override
	         public void clicked(InputEvent event, float x, float y) {
	        	 Constants.toggleResolution(false);
	        	 resolutionValueLabel.setText(Constants.WINDOW_WIDTH + " x " + Constants.WINDOW_HEIGHT);
	        	 event.stop();
	         }
	      });
		
		applyButton.addListener(new ClickListener() {
	         @Override
	         public void clicked(InputEvent event, float x, float y) {
	        	 Constants.updateWindowMode();
	        	 Constants.updateResolution();
	        	 event.stop();
	         }
	      });
		
		returnButton.addListener(new ClickListener() {
	         @Override
	         public void clicked(InputEvent event, float x, float y) {
	        	 game.setScreen(((TankTankRevolution)game).mainMenuScreen);
	        	 event.stop();
	         }
	      });
		
		uiTable.defaults().width(200).height(75).space(25).center();
		uiTable.add(windowLabel).right();
		uiTable.add(windowModeButton).width(300).colspan(3);
		uiTable.row();
		uiTable.add(resolutionLabel).right();
		uiTable.add(minusButton).width(50).spaceRight(0);
		uiTable.add(resolutionValueLabel).width(200).spaceLeft(0).spaceRight(0);
		uiTable.add(plusButton).width(50).spaceLeft(0);
		uiTable.row();
		uiTable.add(applyButton).width(150).colspan(4);
		uiTable.row();
		uiTable.add(returnButton).width(300).colspan(4);
		
		return uiTable;
	}
}
