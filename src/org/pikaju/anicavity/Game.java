package org.pikaju.anicavity;

import org.pikaju.anicavity.connection.Client;
import org.pikaju.anicavity.connection.Connection;
import org.pikaju.anicavity.debug.Debug;
import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.gui.Menu;
import org.pikaju.anicavity.io.Input;
import org.pikaju.anicavity.world.World;

public class Game {

	public int state = -1;
	
	public World world;
	public Menu menu;
	public Client client;
	public Debug debug;
	
	public Game() {
		state = 0;
		world = new World(-1);
		world.setInput(new Input());
		menu = new Menu(this);
		debug = new Debug();
	}

	public void update() {
		if(world.getInput().isKeyDown(Input.KEY_EXIT)) {
			disconnect();
			debug.ping = -1;
			state = 0;
		}
		if(state == 0) {
			menu.update();
		}
		if(state == 1) {
			world.update();
		}
		debug.update();
		world.getInput().refesh();
	}
	
	public void render(Bitmap screen) {
		if(state == 0) {
			menu.render(screen);
		}
		if(state == 1) {
			world.render(screen);
		}
		debug.render(screen);
	}
	
	public void connect(String ip, int port) {
		world.username = menu.username.getText();
		client = new Client(world);
		client.connect(new Connection(ip, port));
		client.start();
		world.setClient(client);
	}

	public void disconnect() {
		if(client != null) {
			client.disconnect();
			client = null;
			if(world.getLocalPlayer() != null) world.removeEntity(world.getLocalPlayer().getId());
		}
	}
}
