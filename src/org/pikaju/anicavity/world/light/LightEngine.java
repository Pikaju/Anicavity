package org.pikaju.anicavity.world.light;

import java.util.ArrayList;

import org.pikaju.anicavity.world.Chunk;
import org.pikaju.anicavity.world.World;
import org.pikaju.anicavity.world.entity.Entity;

public class LightEngine {

	private ArrayList<Light> lights;
	private World world;
	
	public LightEngine(World world) {
		this.world = world;
		lights = new ArrayList<Light>();
	}
	
	public void refresh() {
		for(int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			if(light.entity == null) removeLight(light);
			if(light.entity.isRemoved()) removeLight(light);
			if(light.entity != world.getEntityByID(light.entity.getId())) removeLight(light);
			Chunk chunk = null;
			for(int x = (int) (light.x / (float) Chunk.SIZE - 1); x < light.x / (float) Chunk.SIZE + 2; x++) {
				for(int y = (int) (light.y / (float) Chunk.SIZE - 1); y < light.y / (float) Chunk.SIZE + 2; y++) {
					chunk = world.getChunk(x, y); if(chunk != null) chunk.removeLight();
				}
			}
		}
		for(int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			light.spread(world);
		}
	}
	
	public void addLight(Light light, Entity e) {
		lights.add(light);
		light.entity = e;
	}
	
	public void removeLight(Light light) {
		if(light == null) return;
		Chunk chunk = null;
		for(int x = (int) (light.x / (float) Chunk.SIZE - 1); x < light.x / (float) Chunk.SIZE + 2; x++) {
			for(int y = (int) (light.y / (float) Chunk.SIZE - 1); y < light.y / (float) Chunk.SIZE + 2; y++) {
				chunk = world.getChunk(x, y); if(chunk != null) chunk.removeLight();
			}
		}
		lights.remove(light);
	}
}
