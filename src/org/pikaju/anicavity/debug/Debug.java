package org.pikaju.anicavity.debug;

import org.pikaju.anicavity.Anicavity;
import org.pikaju.anicavity.connection.packet.Packet6Ping;
import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Font;

public class Debug {

	public int ping = -1;
	private long pingSend = -1;
	private long lastPing = 0;
	
	public void update() {
		if(pingSend == -1 || System.currentTimeMillis() - lastPing >= 5000) {
			if(System.currentTimeMillis() - lastPing >= 1000) {
				sendPing();
			}
		}
	}

	public void render(Bitmap screen) {
		String fpsText = "Performance: " + Anicavity.i.ups + "ups, " + Anicavity.i.fps + "fps";
		screen.draw(fpsText, screen.width - 3 - Font.getStringWidth(fpsText), 3, 9, 1, 1, 1);
		String memoryUsage = "Memory Usage: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "MB / " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB (" + ((int) (Runtime.getRuntime().totalMemory() / (float) Runtime.getRuntime().maxMemory() * 100.0f)) + "%)";
		screen.draw(memoryUsage, screen.width - 3 - Font.getStringWidth(memoryUsage), 13, 9, 1, 1, 1);
		if(ping != -1) {
			String ping = "Ping: " + this.ping + "ms";
			screen.draw(ping, screen.width - 3 - Font.getStringWidth(ping), 23, 9, 1, 1, 1);
		}
	}

	public void sendPing() {
		if(Anicavity.i.game.client != null) {
			pingSend = System.currentTimeMillis();
			lastPing = System.currentTimeMillis();
			Packet6Ping ping = new Packet6Ping();
			ping.client = Anicavity.i.game.client;
			ping.send();
		}
	}
	
	public void ping() {
		ping = (int) (System.currentTimeMillis() - pingSend);
		pingSend = -1;
	}
}
