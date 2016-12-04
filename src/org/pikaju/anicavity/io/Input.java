package org.pikaju.anicavity.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.Anicavity;
import org.pikaju.anicavity.graphics.Screen;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	public static Input i;

	public static int KEY_EXIT = KeyEvent.VK_ESCAPE;
	public static int KEY_UP = KeyEvent.VK_W;
	public static int KEY_DOWN = KeyEvent.VK_S;
	public static int KEY_LEFT = KeyEvent.VK_A;
	public static int KEY_RIGHT = KeyEvent.VK_D;
	public static int KEY_HOOK = KeyEvent.VK_Q;
	
	//public static int KEY_UP = KeyEvent.VK_UP;
	//public static int KEY_DOWN = KeyEvent.VK_DOWN;
	//public static int KEY_LEFT = KeyEvent.VK_LEFT;
	//public static int KEY_RIGHT = KeyEvent.VK_RIGHT;
	//public static int KEY_HOOK = KeyEvent.VK_NUMPAD0;
	
	public static int BUTTON_LEFT = MouseEvent.BUTTON1;
	public static int BUTTON_MIDDLE = MouseEvent.BUTTON2;
	public static int BUTTON_RIGHT = MouseEvent.BUTTON3;
	
	private boolean[] keys;
	private boolean[] buttons;
	
	private int x;
	private int y;
	
	private char keyChar = 0;
	private boolean ctrlDown = false;
	private boolean altDown = false;
	
	private int dwheel;
	
	public Input() {
		i = this;
		keys = new boolean[65536];
		buttons = new boolean[12];
	}
	
	public void refesh() {
		keyChar = 0;
		ctrlDown = false;
		altDown = false;
		dwheel = 0;
	}

	public boolean isKeyDown(int key) {
		return keys[key];
	}
	
	public boolean isButtonDown(int button) {
		return buttons[button];
	}
	
	public int getX() {
		return (int) (x / (Anicavity.i.getWidth() / Screen.WIDTH));
	}
	
	public int getY() {
		return (int) (y / (Anicavity.i.getHeight() / Screen.HEIGHT));
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		dwheel += e.getWheelRotation();
	}

	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		buttons[e.getButton()] = true;
	}

	public void mouseReleased(MouseEvent e) {
		buttons[e.getButton()] = false;
	}

	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
		ctrlDown = e.isControlDown();
		altDown = e.isAltDown();
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) return;
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) return;
		if(e.getKeyCode() == KeyEvent.VK_ALT) return;
		keyChar = e.getKeyChar();
		if(KeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase("v")) keyChar = 'v';
	}

	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	public void keyTyped(KeyEvent e) {
	}

	public void clear() {
		for(int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}
	
	public boolean inRegion(int x, int y, int width, int height) {
		return x <= getX() && y <= getY() && x + width > getX() && y + height > getY();
	}

	public char getKeyChar() {
		return keyChar;
	}
	
	public boolean isControlDown() {
		return ctrlDown;
	}

	public boolean isAltDown() {
		return altDown;
	}
	
	public int getDWheel() {
		return dwheel;
	}

	public void readData(DataInput in) {
		try {
			KEY_EXIT = in.readInt();
			KEY_UP = in.readInt();
			KEY_DOWN = in.readInt();
			KEY_LEFT = in.readInt();
			KEY_RIGHT = in.readInt();
			KEY_HOOK = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeData(DataOutput out) {
		try {
			out.writeInt(KEY_EXIT);
			out.writeInt(KEY_UP);
			out.writeInt(KEY_DOWN);
			out.writeInt(KEY_LEFT);
			out.writeInt(KEY_RIGHT);
			out.writeInt(KEY_HOOK);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
