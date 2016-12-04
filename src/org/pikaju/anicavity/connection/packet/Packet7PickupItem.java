package org.pikaju.anicavity.connection.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.world.entity.Player;
import org.pikaju.anicavity.world.entity.item.ItemStack;

public class Packet7PickupItem extends Packet {

	private ItemStack itemStack;

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	
	public void readData(DataInput in) {
		itemStack = new ItemStack();
		itemStack.readData(in);
	}

	public void writeData(DataOutput out) {
		try {
			out.writeInt(7);
			itemStack.writeData(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processPacket() {
		if(isClient()) {
			Player p = world.getLocalPlayer();
			if(p != null) {
				p.getInventory().addItem(itemStack);
			}
		}
	}
}
