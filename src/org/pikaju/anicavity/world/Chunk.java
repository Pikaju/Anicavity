package org.pikaju.anicavity.world;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.world.tile.TileData;

public class Chunk {
	
	public static final int LIGHT_MAP_DETAIL = 1;
	public static final int SIZE = 8;
	
	private TileData[] tileData;
	private float[][][] lightMap;
	
	public Chunk() {
		tileData = new TileData[SIZE * SIZE];
		lightMap = new float[SIZE * LIGHT_MAP_DETAIL][SIZE * LIGHT_MAP_DETAIL][3];
		for(int i = 0; i < tileData.length; i++) {
			tileData[i] = new TileData();
		}
		removeLight();
	}
	
	public TileData getTileData(int x, int y) {
		return tileData[x + y * SIZE];
	}
	
	public void setTileData(int x, int y, TileData data) {
		tileData[x + y * SIZE] = data;
	}
	
	public float[] getLightMapData(int x, int y) {
		return lightMap[x][y];
	}
	
	public void setLightMapData(int x, int y, float[] data) {
		lightMap[x][y] = data;
	}
	
	public void addLightMapData(int x, int y, float[] data) {
		for(int i = 0; i < lightMap[0][0].length; i++) lightMap[x][y][i] += data[i];
	}
	
	public void removeLight() {
		for(int x = 0; x < lightMap.length; x++) {
			for(int y = 0; y < lightMap[0].length; y++) {
				setLightMapData(x, y, new float[] { 0.0f, 0.0f, 0.0f });
			}
		}
	}
	
	public void writeData(DataOutput out) {
		try {
			out.writeInt(TileData.LAYERS);
			for(int i = 0; i < tileData.length; i++) {
				tileData[i].writeData(out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		try {
			TileData.LAYERS = in.readInt();
			for(int i = 0; i < tileData.length; i++) {
				tileData[i].readData(in);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
