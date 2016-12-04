package org.pikaju.anicavity.world.entity.item;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.connection.packet.Packet7PickupItem;
import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.world.entity.PhysicsMob;
import org.pikaju.anicavity.world.entity.Player;
import org.pikaju.anicavity.world.tile.Tile;

public class ItemDrop extends PhysicsMob {

	private ItemStack itemStack;
	private int timer = 0;
	
	public ItemDrop() {
		width = 0.5f;
		height = 0.5f;
	}
	
	public void update() {
		super.update();
		if(isLocal()) {
			tick();
			if(onGround) {
				setDX(0);
			}
			timer++;
			if(timer > 0) handlePickup();
			if(timer > 60 * 60) {
				remove();
				sendUpdatePacket();
			}
			if(moved) sendUpdatePacket();
		} else {
			handleUpdatePackets();
		}
	}
	
	public void render(Bitmap screen, int scrollX, int scrollY) {
		float[] light = world.getLightAt((int) x, (int) y);
		itemStack.getItem().render(screen, x * Tile.SIZE + scrollX, y * Tile.SIZE + scrollY, width * Tile.SIZE, height * Tile.SIZE, light[0], light[1], light[2]);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	
	public void writeData(DataOutput out) {
		super.writeData(out);
		itemStack.writeData(out);
		try {
			out.writeInt(timer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		super.readData(in);
		itemStack = new ItemStack();
		itemStack.readData(in);
		try {
			timer = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handlePickup() {
		for(Player p : world.getPlayers()) {
			if(isRemoved()) return;
			if(intersects(p)) {
				Packet7PickupItem pickupItem = new Packet7PickupItem();
				pickupItem.setItemStack(itemStack);
				world.registerPacket(pickupItem);
				if(pickupItem.isServer()) {
					pickupItem.server.send(pickupItem, p.connection);
				}
				remove();
				sendUpdatePacket();
			}
		}
	}
	
	public void setTimer(int timer) {
		this.timer = timer;
	}
}
