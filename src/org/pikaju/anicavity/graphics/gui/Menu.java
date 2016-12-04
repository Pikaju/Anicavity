package org.pikaju.anicavity.graphics.gui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;

import org.pikaju.anicavity.Anicavity;
import org.pikaju.anicavity.Game;
import org.pikaju.anicavity.connection.Server;
import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Font;
import org.pikaju.anicavity.graphics.Screen;
import org.pikaju.anicavity.graphics.Sprite;
import org.pikaju.anicavity.graphics.gui.components.TextButton;
import org.pikaju.anicavity.graphics.gui.components.TextField;
import org.pikaju.anicavity.graphics.gui.components.TextView;
import org.pikaju.anicavity.world.entity.enemy.Slime;

public class Menu extends GUIContainer {
	private enum MenuState {
		MAIN, PLAY, OPTIONS, CREDITS
	}
	
	private MenuState state;
	private Game game;
	
	private static String[] updateTexts = new String[] {
		"Now with a menu!",
		"Now with hooks!",
		"Now with fancy lighting!",
		"Now with slimes!",
		"Now with glowing crystals!",
		"Now with laser guns!",
		"Now with boss spawners!",
		"Now with rubies!",
	};
	
	//MAIN
	private TextButton play;
	private TextButton settings;
	private TextButton credits;
	private TextButton quit;
	
	private TextButton back;
	
	private Sprite decorationSprite;
	private float decorationSpriteTimer = 0;
	private int decorationSpriteMax = 4;
	private int decorationSpriteMul = 1;
	
	
	private int updateText = -1;
	
	//PLAY
	private TextField ipAddress;
	private TextField port;
	private TextButton connect;

	private TextField hostPort;
	private TextButton startServer;
	
	public TextField username;
	
	public Menu(Game game) {
		this.game = game;
		
		state = MenuState.MAIN;
		generateDecoration(0);
	
		play = new TextButton("Play", Screen.WIDTH / 2 - 260, 160, 200, 18);
		settings = new TextButton("Options", Screen.WIDTH / 2 - 260, 200, 200, 18);
		credits = new TextButton("Credits", Screen.WIDTH / 2 - 260, 240, 200, 18);
		quit = new TextButton("Quit", Screen.WIDTH / 2 - 260, 280, 200, 18);
		back = new TextButton("Back", 5, Screen.HEIGHT - 23, 200, 18);
		
		
		ipAddress = new TextField("localhost", 100, 200, 160, 18);
		port = new TextField("25565", 100, 240, 160, 18);
		connect = new TextButton("Connect", 100, 280, 160, 18);
		hostPort = new TextField("25565", 440, 200, 160, 18);
		startServer = new TextButton("Start Server", 440, 240, 160, 18);
		
		username = new TextField("Unnamed", 440, Screen.HEIGHT - 23, 160, 18);
	}

	public void update() {
		if(state == MenuState.MAIN) {
			play.update(); if(play.wasClicked()) state = MenuState.PLAY;
			settings.update();
			credits.update();
			quit.update(); if(quit.wasClicked()) Anicavity.i.stop();
			
			decorationSpriteTimer += 0.1f;
			if(decorationSpriteTimer >= decorationSpriteMax) decorationSpriteTimer = 0;
		} else {
			back.update(); if(back.wasClicked()) state = MenuState.MAIN;
		}
		if(state == MenuState.PLAY) {
			ipAddress.update();
			port.update();
			connect.update();
			if(connect.wasClicked()) {
				game.connect(ipAddress.getText(), Integer.parseInt(port.getText()));
				game.state = 1;
			}
			
			hostPort.update();
			startServer.update();
			if(startServer.wasClicked()) {
				startServer.highlight = !startServer.highlight;
				if(startServer.highlight) Server.open(Integer.parseInt(hostPort.getText()));
				else Server.close();
			}
			
			username.update();
		}
	}
	
	public void render(Bitmap screen) {
		if(state == MenuState.MAIN) {
			TextView.draw(screen, Anicavity.NAME, Screen.WIDTH / 2 - Font.getStringWidth(Anicavity.NAME) / 2 * 5, 50, 45);
			play.render(screen);
			settings.render(screen);
			credits.render(screen);
			quit.render(screen);
			
			screen.draw(decorationSprite.getBitmap((int) (decorationSpriteTimer * decorationSpriteMul)), 400, 220, 64, 64, 0, 1, 1, 1);
			screen.draw(0xffffff, 400 + 32, 200, 1, 16);
			String updateText = getRandomUpdateText();
			screen.draw(updateText, 400 + 32 - Font.getStringWidth(updateText) / 2, 200 - updateText.split("\n").length * 10, 9, 1, 1, 1);
		} else {
			back.render(screen);
		}
		if(state == MenuState.PLAY) {
			TextView.draw(screen, "Play", Screen.WIDTH / 2 - Font.getStringWidth("Play") / 2 * 5, 50, 45);
			
			TextView.draw(screen, "Join", 150, 140, 27);
			
			TextView.draw(screen, "IP:", 30, 200, 18);
			ipAddress.render(screen);
		
			TextView.draw(screen, "Port:", 30, 240, 18);
			port.render(screen);
			
			connect.render(screen);
			
			TextView.draw(screen, "Host", 450, 140, 27);
			
			TextView.draw(screen, "Port:", 370, 200, 18);
			hostPort.render(screen);
			
			startServer.render(screen);
			
			TextView.draw(screen, "Name:", 370, Screen.HEIGHT - 23, 18);
			username.render(screen);
		}
	}
	
	private String getRandomUpdateText() {
		if(updateText == -1) {
			updateText = new Random().nextInt(updateTexts.length);
		}
		return updateTexts[updateText];
	}

	private void generateDecoration(int decoration) {
		if(decoration == 0) {
			decorationSprite = Slime.sprite;
		}
	}

	public void readData(DataInput in) {
		try {
			username.setText(in.readUTF());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(DataOutput out) {
		try {
			out.writeUTF(username.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}