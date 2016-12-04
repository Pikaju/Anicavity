package org.pikaju.anicavity.connection.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.Anicavity;

public class Packet6Ping extends Packet {
	
	public void readData(DataInput in) {
		
	}

	public void writeData(DataOutput out) {
		try {
			out.writeInt(6);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processPacket() {
		if(isServer()) {
			server.send(this, connection);
		}
		if(isClient()) {
			Anicavity.i.game.debug.ping();
		}
	}
}
