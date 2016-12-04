package org.pikaju.anicavity;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import org.pikaju.anicavity.graphics.Screen;
import org.pikaju.anicavity.io.ImageLoader;
import org.pikaju.anicavity.io.Save;

public class Anicavity extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public static boolean programMode = false;
	
	public static final String NAME = "Anicavity";
	public static final String VERSION = "Pre-Alpha";
	
	public static Anicavity i;
	
	private int width;
	private int height;
	public float scale;
	public static boolean vsync = false;
	
	private Thread thread;
	private boolean running;
	private JFrame frame;
	
	public int ups = 0;
	public int fps = 0;
	
	private Screen screen;
	public Game game;

	public Anicavity(int width, int height, float scale) {
		this.width = width;
		this.height = height;
		this.scale = scale;
		if(scale == -1) setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		else setPreferredSize(new Dimension((int) (this.width * scale), (int) (this.height * scale)));
	}
	
	public static void main(String[] args) {
		if(args.length > 0 && args[0].equals("eclipse")) programMode = true;
		i = new Anicavity(640, 360, Toolkit.getDefaultToolkit().getScreenSize().getWidth() == 1920 ? 2 : 1);
		//i = new Anicavity(640, 360, -1);
		i.start();
	}
	
	private void init() {
		frame = new JFrame(NAME + "  |  " + VERSION);
		if(scale == -1) frame.setUndecorated(true);
		frame.add(this);
		frame.pack();
		setBackground(Color.BLACK);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(ImageLoader.loadImage("/logo.png"));
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stop();
			}
			public void windowLostFocus(WindowEvent e) {
				game.world.getInput().clear();
			}
		});
		
		screen = new Screen(width, height);

		game = new Game();
		addMouseListener(game.world.getInput());
		addMouseMotionListener(game.world.getInput());
		addMouseWheelListener(game.world.getInput());
		addKeyListener(game.world.getInput());

		Save save = new Save();
		save.readFile("/Anicavity/save.ani");
		if(save.active()) {
			game.menu.readData(save.getDataInput());
			game.world.getInput().readData(save.getDataInput());
		}
		save.close();
		
		frame.setVisible(true);
	}
	
	private void cleanup() {
		game.disconnect();
		Save save = new Save();
		save.writeFile("/Anicavity/save.ani");
		if(save.active()) {
			game.menu.writeData(save.getDataOutput());
			game.world.getInput().writeData(save.getDataOutput());
		}
		save.close();
	}
	
	public synchronized void start() {
		if(running) {
			return;
		}
		init();
		running = true;
		thread = new Thread(this, NAME);
		thread.start();
	}
	
	public synchronized void stop() {
		if(!running) {
			return;
		}
		cleanup();
		running = false;
		System.exit(0);
	}

	public void run() {
		long currentTime = 0;
		long timer = 0;
		long lastTime = System.nanoTime();
		
		double ns = 1000000000.0 / 60.0;
		double delta = 0;
		
		int updates = 0;
		int frames = 0;
		
		while(running) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / ns;
			timer += currentTime - lastTime;
			lastTime = currentTime;
			
			boolean render = !vsync;
			
			while(delta >= 1) {
				update();
				updates++;
				delta--;
				render = true;
			}
			
			if(render) {
				render();
				frames++;
			}
			
			if(timer >= 1000000000) {
				timer -= 1000000000;
				ups = updates;
				fps = frames;
				updates = 0;
				frames = 0;
			}
			
			try {
				if(fps >= 60) Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void update() {
		game.update();
	}
	
	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			requestFocus();
			return;
		}
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();

		game.render(screen);
		screen.render(g, getWidth(), getHeight());
		
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
		bs.show();
	}
}
