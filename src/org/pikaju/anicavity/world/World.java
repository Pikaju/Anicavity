package org.pikaju.anicavity.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.pikaju.anicavity.connection.Client;
import org.pikaju.anicavity.connection.Server;
import org.pikaju.anicavity.connection.packet.Packet;
import org.pikaju.anicavity.connection.packet.Packet3NewEntity;
import org.pikaju.anicavity.connection.packet.Packet5RemoveEntity;
import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Screen;
import org.pikaju.anicavity.io.Input;
import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.Entity;
import org.pikaju.anicavity.world.entity.Player;
import org.pikaju.anicavity.world.entity.enemy.Enemy;
import org.pikaju.anicavity.world.entity.item.Item;
import org.pikaju.anicavity.world.entity.item.ItemDrop;
import org.pikaju.anicavity.world.entity.item.ItemStack;
import org.pikaju.anicavity.world.light.LightEngine;
import org.pikaju.anicavity.world.tile.Tile;
import org.pikaju.anicavity.world.tile.TileData;
import org.pikaju.anicavity.world.worldgen.EnemyGenerator;
import org.pikaju.anicavity.world.worldgen.WorldGenerator;

public class World {

	private Random random;
	private long seed;
	
	private float scrollX;
	private float scrollY;
	
	public Chunk[][] chunks;
	private HashMap<Long, Entity> entities;
	private ArrayList<Entity> privateEntities;
	
	public String username;
	private Input input;
	private LightEngine lightEngine;
	
	private Client client;
	private Server server;
	
	private SpawnPoint[] spawnPoints;
	
	public boolean renderTiles = true;
	
	public World(long seed) {
		spawnPoints = new SpawnPoint[10];
		entities = new HashMap<Long, Entity>();
		privateEntities = new ArrayList<Entity>();
		lightEngine = new LightEngine(this);
		setSeed(seed);
	}
	
	public void update() {
		Entity[] entities = getEntities();
		for(int i = 0; i < entities.length; i++) {
			entities[i].update();
			if(entities[i].isRemoved()) {
				removeEntity(entities[i].getId());
			}
		}
		if(isClient()) {
			for(int i = 0; i < privateEntities.size(); i++) {
				privateEntities.get(i).update();
				if(privateEntities.get(i).isRemoved()) {
					removePrivateEntity(privateEntities.get(i).getId());
				}
			}
		}
		
		Player localPlayer = getLocalPlayer();
		if(localPlayer != null) {
			scrollX = localPlayer.x * Tile.SIZE - Screen.WIDTH / 2 + localPlayer.width * Tile.SIZE / 2;
			scrollY = (int) (localPlayer.y * Tile.SIZE) - Screen.HEIGHT / 2 + localPlayer.height * Tile.SIZE / 2;
			if(scrollX < 0) scrollX = 0;
			if(scrollX > getWidth() * Tile.SIZE - Screen.WIDTH) scrollX = getWidth() * Tile.SIZE - Screen.WIDTH;
			lightEngine.refresh();
		}
		
		if(isServer()) {
			EnemyGenerator.generateEnemies(this);
		}
	}

	public void render(Bitmap screen) {
		screen.draw(0x00b0ff, 0, 0, screen.width, screen.height);

		int x0 = (int) ((scrollX) / Tile.SIZE - 1);
		int y0 = (int) ((scrollY) / Tile.SIZE - 1);
		int x1 = (int) Math.ceil((scrollX + screen.width) / Tile.SIZE);
		int y1 = (int) Math.ceil((scrollY + screen.height) / Tile.SIZE);
		
		renderTiles(screen, x0, y0, x1, y1, 1);
		
		Entity[] entities = getEntities();
		for(int i = 0; i < entities.length; i++) {
			entities[i].render(screen, (int) -scrollX, (int) -scrollY);
		}
		for(int i = 0; i < privateEntities.size(); i++) {
			privateEntities.get(i).render(screen, (int) -scrollX, (int) -scrollY);
		}
		
		renderTiles(screen, x0, y0, x1, y1, 0);
		
		if(isClient()) {
			Player p = getLocalPlayer();
			if(p != null) {
				p.renderOtherPlayers(screen);
				p.getInventory().render(screen);
			}
		}
	}
	
	public void renderTiles(Bitmap screen, int x0, int y0, int x1, int y1, int layer) {
		if(!renderTiles) return;
		for(int x = x0; x < x1; x++) {
			for(int y = y0; y < y1; y++) {
				TileData data = getTileData(x, y);
				data.render(screen, x, y, (int) -scrollX, (int) -scrollY, getLightMapData(x, y), layer);
			}
		}
	}
	
	public void generate() {
		WorldGenerator generator = new WorldGenerator(this);
		generator.generateWorld(random);
	}
	
	public synchronized void addEntity(Entity e, long id) {
		e.setId(id);
		e.setLevel(this);
		synchronized(entities) {
			if(entities.containsKey(id)) entities.remove(id);
			entities.put(e.getId(), e);
		}
	}
	
	public void addEntity(Entity e) {
		addEntity(e, getFreeID());
	}
	
	public void addNewEntity(Entity e) {
		if(isServer()) {
			addEntity(e);
		}
		Packet3NewEntity packet = new Packet3NewEntity();
		packet.setEntity(e);
		registerPacket(packet);
		packet.send();
	}
	
	public synchronized void removeEntity(long id) {
		if(getEntityMap().containsKey(id)) {
			getEntityMap().remove(id);
		}
	}
	
	public long getFreeID() {
		long id = 0;
		while(getEntityMap().containsKey(id)) id++;
		return id;
	}
	
	public synchronized HashMap<Long, Entity> getEntityMap() {
		return entities;
	}
	
	public synchronized Entity[] getEntities() {
		Entity[] e;
		synchronized(entities) {
			synchronized(entities.values()) {
				e = entities.values().toArray(new Entity[0]);
			}
		}
		return e;
	}
	
	private void removePrivateEntity(long id) {
		synchronized(privateEntities) {
			privateEntities.remove(id);
		}
	}
	
	public void addPrivateEntity(Entity e) {
		synchronized(privateEntities) {
			privateEntities.add(e);
		}
	}
	
	public Chunk getChunk(int x, int y) {
		if(chunks == null) return null;
		if(x < 0 || y < 0 || x >= chunks.length || y >= chunks[0].length) {
			return null;
		}
		return chunks[x][y];
	}

	public int getWidth() {
		if(chunks == null) return 0;
		return chunks.length * Chunk.SIZE;
	}

	public int getHeight() {
		if(chunks == null) return 0;
		return chunks[0].length * Chunk.SIZE;
	}
	
	public Player getLocalPlayer() {
		if(username == null) return null;
		Player[] players = getPlayers();
		for(int i = 0; i < players.length; i++) {
			if(players[i].username.equals(username)) {
				return players[i];
			}
		}
		return null;
	}
	
	public void setSize(int width, int height) {
		chunks = new Chunk[width][height];
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				chunks[x][y] = new Chunk();
			}
		}
	}
	
	public void setSeed(long seed) {
		this.seed = seed;
		random = new Random(this.seed);
	}
	
	public long getSeed() {
		return seed;
	}
	
	public String toString() {
		return "World[seed = " + seed + ", width = " + getWidth() + ", height = " + getHeight() + "]";
	}

	public Player[] getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		Entity[] entities = getEntities();
		for(int i = 0; i < entities.length; i++) {
			if(entities[i] instanceof Player) {
				players.add((Player) entities[i]);
			}
		}
		return players.toArray(new Player[0]);
	}
	
	public void spreadLight(int x, int y, float[] data, float range, float sub) {
		range -= sub;
		if(getTileData(x, y).isSolid()) range -= sub * 1;
		if(range <= 0) return;
		float[] newData = new float[] { data[0] * range, data[1] * range, data[2] * range };
		float[] oldData = getLightMapData(x, y);
		boolean change = false;
		for(int i = 0; i < oldData.length; i++) {
			if(oldData[i] < newData[i]) {
				getLightMapData(x, y)[i] = newData[i];
				change = true;
			}
		}
		if(!change) return;
		spreadLight(x + 1, y, newData, range, sub);
		spreadLight(x - 1, y, newData, range, sub);
		spreadLight(x, y + 1, newData, range, sub);
		spreadLight(x, y - 1, newData, range, sub);
	}
	
	public void spreadLightCircle(float x, float y, float[] data, float range, float sub, boolean func) {
		for(int xx = (int) (x - range); xx < x + range; xx++) {
			for(int yy = (int) (y - range); yy < y + range; yy++) {
				float xd = xx - x;
				float yd = yy - y;
				float dist = MathHelper.sqrt(xd * xd + yd * yd);
				float absdist = (float) ((range - dist) / range);
				
				float[] newData = new float[] { data[0] * absdist, data[1] * absdist, data[2] * absdist };
				float[] oldData = getLightMapData(xx, yy);
				for(int i = 0; i < oldData.length; i++) {
					if(newData[i] < 0) continue;
					if(func && newData[i] < oldData[i]) continue;
					if(!func) getLightMapData(xx, yy)[i] += newData[i];
					else getLightMapData(xx, yy)[i] = newData[i];
				}
			}
		}
	}
	
	public void setTileData(int x, int y, TileData data) {
		if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
			return;
		}
		int chunkX = x / Chunk.SIZE;
		int chunkY = y / Chunk.SIZE;
		Chunk chunk = getChunk(chunkX, chunkY);
		if(chunk == null) {
			return;
		}
		int tileX = x % Chunk.SIZE;
		int tileY = y % Chunk.SIZE;
		chunk.setTileData(tileX, tileY, data);
	}
	
	public TileData getTileData(int x, int y) {
		if(x < 0 || x >= getWidth() || y >= getHeight()) {
			return new TileData(Tile.STONE, Tile.STONE);
		}
		if(y < 0) return new TileData(Tile.AIR, Tile.AIR);
		int chunkX = x / Chunk.SIZE;
		int chunkY = y / Chunk.SIZE;
		Chunk chunk = getChunk(chunkX, chunkY);
		if(chunk == null) {
			return new TileData(Tile.AIR, Tile.AIR);
		}
		int tileX = x % Chunk.SIZE;
		int tileY = y % Chunk.SIZE;
		return chunk.getTileData(tileX, tileY);
	}
	
	public float[] getLightMapData(int x, int y) {
		if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
			return new float[] { 0, 0, 0 };
		}
		int chunkX = x / Chunk.LIGHT_MAP_DETAIL / Chunk.SIZE;
		int chunkY = y / Chunk.LIGHT_MAP_DETAIL / Chunk.SIZE;
		Chunk chunk = getChunk(chunkX, chunkY);
		if(chunk == null) {
			return new float[] { 0, 0, 0 };
		}
		int tileX = x % (Chunk.SIZE * Chunk.LIGHT_MAP_DETAIL);
		int tileY = y % (Chunk.SIZE * Chunk.LIGHT_MAP_DETAIL);
		return chunk.getLightMapData(tileX, tileY);
	}
	
	public void setLightMapData(int x, int y, float[] data) {
		if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
			return;
		}
		int chunkX = x / Chunk.LIGHT_MAP_DETAIL / Chunk.SIZE;
		int chunkY = y / Chunk.LIGHT_MAP_DETAIL / Chunk.SIZE;
		Chunk chunk = getChunk(chunkX, chunkY);
		if(chunk == null) {
			return;
		}
		int tileX = x % (Chunk.SIZE * Chunk.LIGHT_MAP_DETAIL);
		int tileY = y % (Chunk.SIZE * Chunk.LIGHT_MAP_DETAIL);
		chunk.setLightMapData(tileX, tileY, data);
	}
	
	public void addLightMapData(int x, int y, float[] data) {
		if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
			return;
		}
		int chunkX = x / Chunk.LIGHT_MAP_DETAIL / Chunk.SIZE;
		int chunkY = y / Chunk.LIGHT_MAP_DETAIL / Chunk.SIZE;
		Chunk chunk = getChunk(chunkX, chunkY);
		if(chunk == null) {
			return;
		}
		int tileX = x % (Chunk.SIZE * Chunk.LIGHT_MAP_DETAIL);
		int tileY = y % (Chunk.SIZE * Chunk.LIGHT_MAP_DETAIL);
		chunk.addLightMapData(tileX, tileY, data);
	}
	
	public void setInput(Input input) {
		this.input = input;
	}
	
	public Input getInput() {
		return input;
	}

	public Player getPlayer(String username) {
		Player[] players = getPlayers();
		for(int i = 0; i < players.length; i++) if(players[i].username.equals(username)) return players[i];
		return null;
	}
	
	public Enemy[] getEnemies() {
		ArrayList<Enemy> enemies = new ArrayList<Enemy>();
		for(Entity e : getEntities()) {
			if(e instanceof Enemy) enemies.add((Enemy) e);
		}
		return enemies.toArray(new Enemy[0]);
	}

	public Chunk getChunkAt(int x, int y) {
		return getChunk(x / Chunk.SIZE, y / Chunk.SIZE);
	}
	
	public LightEngine getLightEngine() {
		return lightEngine;
	}

	public void spreadStaticLight(int x, int y, float intensity, float range, float sub) {
		range -= sub;
		if(getTileData(x, y).isSolid()) range -= sub * 1;
		if(range <= 0) return;
		boolean change = false;
		if(getTileData(x, y).getStaticLight() < intensity * range) change = true;
		if(!change) return;
		getTileData(x, y).setStaticLight(intensity * range);
		spreadStaticLight(x + 1, y, intensity, range, sub);
		spreadStaticLight(x - 1, y, intensity, range, sub);
		spreadStaticLight(x, y + 1, intensity, range, sub);
		spreadStaticLight(x, y - 1, intensity, range, sub);
	}

	public float[] getLightAt(int x, int y) {
		float[] lightMapData = getLightMapData(x, y);
		float staticLightData = getTileData(x, y).getStaticLight();
		return new float[] { MathHelper.max(lightMapData[0], staticLightData), MathHelper.max(lightMapData[1], staticLightData), MathHelper.max(lightMapData[2], staticLightData) };
	}
	
	public Entity getEntityByID(long id) {
		if(!getEntityMap().containsKey(id)) return null;
		return getEntityMap().get(id);
	}
	
	public void registerPacket(Packet packet) {
		packet.client = client;
		packet.server = server;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
	public void setServer(Server server) {
		this.server = server;
	}

	public boolean isClient() {
		return client != null;
	}

	public boolean isServer() {
		return server != null;
	}

	public float getScrollX() {
		return scrollX;
	}

	public void setScrollX(float scrollX) {
		this.scrollX = scrollX;
	}

	public float getScrollY() {
		return scrollY;
	}

	public void setScrollY(float scrollY) {
		this.scrollY = scrollY;
	}

	public SpawnPoint getSpawnPoint() {
		return spawnPoints[(int) (Math.random() * spawnPoints.length)];
	}

	public void setSpawnPoints(SpawnPoint[] spawnPoints) {
		this.spawnPoints = spawnPoints;
	}
	
	public TileData getHighestTile(int x) {
		for(int y = 0; y < getHeight(); y++) {
			TileData data = getTileData(x, y);
			if(data.getTile(0).getID() != Tile.AIR.getID() || data.getTile(1).getID() != Tile.AIR.getID()) {
				return data;
			}
		}
		return null;
	}

	public float getHighestTileHeight(int x) {
		for(int y = 0; y < getHeight(); y++) {
			TileData data = getTileData(x, y);
			if(data.getTile(0).getID() != Tile.AIR.getID() || data.getTile(1).getID() != Tile.AIR.getID()) {
				return y - 1;
			}
		}
		return 0;
	}

	public Player getRandomPlayer() {
		Player[] players = getPlayers();
		if(players.length == 0) return null;
		return players[(int) (MathHelper.getRandom().nextInt(players.length))];
	}

	public float getAvarageLightAt(int x, int y) {
		float v = 0;
		float[] light = getLightAt(x, y);
		for(int i = 0; i < light.length; i++) v += light[i];
		return v / light.length;
	}

	public void removeEntity(Entity e) {
		e.remove();
		Packet5RemoveEntity removeEntity = new Packet5RemoveEntity();
		removeEntity.setEntity(e);
		registerPacket(removeEntity);
		removeEntity.send();
	}

	public SpawnPoint[] getSpawnPoints() {
		return spawnPoints;
	}

	public void addSpawnPoint(SpawnPoint spawnPoint) {
		for(int i = 0; i < spawnPoints.length; i++) {
			if(spawnPoints[i] == null) {
				spawnPoints[i] = spawnPoint;
				return;
			}
		}
	}

	public int getSpawnPointAmount() {
		for(int i = 0; i < spawnPoints.length; i++) {
			if(spawnPoints[i] == null) return i;
		}
		return spawnPoints.length;
	}
	
	public void dropItem(Item item, int amount, float x, float y) {
		dropItem(item, amount, x, y, 30);
	}
	
	public void dropItem(Item item, int amount, float x, float y, int pickupTime) {
		for(int i = 0; i < amount; i++) {
			ItemDrop itemDrop = new ItemDrop();
			itemDrop.x = x;
			itemDrop.y = y;
			itemDrop.setDX((MathHelper.getRandom().nextFloat() - 0.5f) * 0.1f);
			itemDrop.setDY((-MathHelper.getRandom().nextFloat()) * 0.1f);
			itemDrop.setItemStack(new ItemStack(item, 1));
			itemDrop.setTimer(-pickupTime);
			addNewEntity(itemDrop);
		}
	}
}
