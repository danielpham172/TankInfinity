package com.tank.controls;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GamepadController extends TankController {
	public int index;
	private LinkedHashMap<String, KeyControl> keyMap;
	private float deadzone = 0.30f;
	private float sensitivity = 15f;
	private static ArrayList<Integer> inUse = new ArrayList<Integer>();
	private static Array<Controller> controllers = Controllers.getControllers();

	public GamepadController() throws Exception {
		keyMap = new LinkedHashMap<String, KeyControl>();
		keyMap.putAll(ControlConstants.DEFAULT_GAMEPAD_CONTROLS);
		index = -1;
		for (int i = 0; i < controllers.size; i++) {
			if (!inUse.contains(index)) {
				this.index = i;
				inUse.add(index);
				break;
			}
		}
		if (index == -1) throw new Exception("No more controllers");
	}

	public void setDeadzone(float d) {
		deadzone = d;
	}
	
	public void setSensitivity(float s) {
		sensitivity = s;
	}

	public boolean upPressed() {
		if (index >= controllers.size || controllers.get(index) == null) {
			return false;
		}
		KeyControl up = keyMap.get("UP");
		if (up.getKeyType() == 0) {
			return controllers.get(index).getButton(up.getKeyCode());
		}
		else {
			if (up.getDirection() < 0) {
				return (controllers.get(index).getAxis(up.getKeyCode()) <= -deadzone);
			}
			else if (up.getDirection() > 0) {
				return (controllers.get(index).getAxis(up.getKeyCode()) >= deadzone);
			}
				
		}
		return false;
	}

	public boolean downPressed() {
		if (index >= controllers.size || controllers.get(index) == null) {
			return false;
		}
		KeyControl down = keyMap.get("DOWN");
		if (down.getKeyType() == 0) {
			return controllers.get(index).getButton(down.getKeyCode());
		}
		else {
			if (down.getDirection() < 0) {
				return (controllers.get(index).getAxis(down.getKeyCode()) <= -deadzone);
			}
			else if (down.getDirection() > 0) {
				return (controllers.get(index).getAxis(down.getKeyCode()) >= deadzone);
			}
				
		}
		return false;
	}

	public boolean rightPressed() {
		if (index >= controllers.size || controllers.get(index) == null) {
			return false;
		}
		KeyControl right = keyMap.get("RIGHT");
		if (right.getKeyType() == 0) {
			return controllers.get(index).getButton(right.getKeyCode());
		}
		else {
			if (right.getDirection() < 0) {
				return (controllers.get(index).getAxis(right.getKeyCode()) <= -deadzone);
			}
			else if (right.getDirection() > 0) {
				return (controllers.get(index).getAxis(right.getKeyCode()) >= deadzone);
			}
				
		}
		return false;
	}

	public boolean leftPressed() {
		if (index >= controllers.size || controllers.get(index) == null) {
			return false;
		}
		KeyControl left = keyMap.get("LEFT");
		if (left.getKeyType() == 0) {
			return controllers.get(index).getButton(left.getKeyCode());
		}
		else {
			if (left.getDirection() < 0) {
				return (controllers.get(index).getAxis(left.getKeyCode()) <= -deadzone);
			}
			else if (left.getDirection() > 0) {
				return (controllers.get(index).getAxis(left.getKeyCode()) >= deadzone);
			}
				
		}
		return false;
	}

	public boolean firePressed() {
		//testButtons();
		if (index >= controllers.size || controllers.get(index) == null) {
			return false;
		}
		KeyControl shoot = keyMap.get("SHOOT");
		if (shoot.getKeyType() == 0) {
			return controllers.get(index).getButton(shoot.getKeyCode());
		}
		else {
			if (shoot.getDirection() < 0) {
				return (controllers.get(index).getAxis(shoot.getKeyCode()) <= -deadzone);
			}
			else if (shoot.getDirection() > 0) {
				return (controllers.get(index).getAxis(shoot.getKeyCode()) >= deadzone);
			}
				
		}
		return false;
	}

	public boolean subPressed() {
		if (index >= controllers.size || controllers.get(index) == null) {
			return false;
		}
		KeyControl sub = keyMap.get("SUB");
		if (sub.getKeyType() == 0) {
			return controllers.get(index).getButton(sub.getKeyCode());
		}
		else {
			if (sub.getDirection() < 0) {
				return (controllers.get(index).getAxis(sub.getKeyCode()) <= -deadzone);
			}
			else if (sub.getDirection() > 0) {
				return (controllers.get(index).getAxis(sub.getKeyCode()) >= deadzone);
			}
				
		}
		return false;
	}

	public boolean subRightPressed() {
		if (index >= controllers.size || controllers.get(index) == null) {
			return false;
		}
		KeyControl rSwitch = keyMap.get("RSHIFT");
		if (rSwitch.getKeyType() == 0) {
			return controllers.get(index).getButton(rSwitch.getKeyCode());
		}
		else {
			if (rSwitch.getDirection() < 0) {
				return (controllers.get(index).getAxis(rSwitch.getKeyCode()) <= -deadzone);
			}
			else if (rSwitch.getDirection() > 0) {
				return (controllers.get(index).getAxis(rSwitch.getKeyCode()) >= deadzone);
			}
				
		}
		return false;
	}

	public boolean subLeftPressed() {
		if (index >= controllers.size || controllers.get(index) == null) {
			return false;
		}
		KeyControl lSwitch = keyMap.get("LSHIFT");
		if (lSwitch.getKeyType() == 0) {
			return controllers.get(index).getButton(lSwitch.getKeyCode());
		}
		else {
			if (lSwitch.getDirection() < 0) {
				return (controllers.get(index).getAxis(lSwitch.getKeyCode()) <= -deadzone);
			}
			else if (lSwitch.getDirection() > 0) {
				return (controllers.get(index).getAxis(lSwitch.getKeyCode()) >= deadzone);
			}
				
		}
		return false;
	}
	
	public boolean pausePressed() {
		if (index >= controllers.size || controllers.get(index) == null) {
			return false;
		}
		KeyControl pause = keyMap.get("PAUSE");
		if (pause.getKeyType() == 0) {
			return controllers.get(index).getButton(pause.getKeyCode());
		}
		else {
			if (pause.getDirection() < 0) {
				return (controllers.get(index).getAxis(pause.getKeyCode()) <= -deadzone);
			}
			else if (pause.getDirection() > 0) {
				return (controllers.get(index).getAxis(pause.getKeyCode()) >= deadzone);
			}
				
		}
		return false;
	}

	public void testButtons() {
		for (int i = 0; i < 100; i++) {
			if (controllers.get(index).getButton(i))
				System.out.println("Pressed Button " + i);
		}
		for (int i = 0; i < 100; i++) {
			if (Math.abs(controllers.get(index).getAxis(i)) > 0.3)
				System.out.println("Axis " + i + " moved: " + controllers.get(index).getAxis(i));
		}
		for (int i = 0; i < 100; i++) {
			if (!controllers.get(index).getPov(i).equals(PovDirection.center))
				System.out.println("POV " + i + ": " + controllers.get(index).getPov(i).toString());
		}
	}

	public Vector3 getCursor(Vector3 oldCursor) {
		if (index >= controllers.size || controllers.get(index) == null) 
			return oldCursor;
		KeyControl vertical = keyMap.get("CURSOR-V");
		KeyControl horizontal = keyMap.get("CURSOR-H");
		float newX = oldCursor.x;
		float newY = oldCursor.y;
		if (Math.abs(controllers.get(index).getAxis(horizontal.getKeyCode())) > deadzone) newX += sensitivity * controllers.get(index).getAxis(horizontal.getKeyCode());
		if (Math.abs(controllers.get(index).getAxis(vertical.getKeyCode())) > deadzone) newY += sensitivity * controllers.get(index).getAxis(vertical.getKeyCode());
		return new Vector3(newX, newY, 0);
	}
	
	public void setKey(String key, KeyControl control) {
		keyMap.replace(key, control);
	}
	public int getKey(String key){return keyMap.get(key).getKeyCode();}
}
