package org.pikaju.anicavity.sound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

	public static float VOLUME = 0.0f;
	
	private String path;
	
	public Sound(String path) {
		try {
			this.path = path;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void play(final Sound sound, final float volume) {
		if(VOLUME == 0.0f) return;
		new Thread(new Runnable() {
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(getClass().getResource(sound.path)));
					clip.start();
					FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					float dB = (float) (Math.log(VOLUME * volume) / Math.log(10.0) * 20.0);
					if(dB < -80) dB = -80;
					control.setValue(dB);
					while (!clip.isRunning())
					    Thread.sleep(10);
					while (clip.isRunning())
					    Thread.sleep(10);
					clip.drain();
					clip.flush();
					clip.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "Sound").start();
	}
	
	public static void play(final Sound[] sounds, final float volume) {
		play(sounds[(int) (Math.random() * sounds.length)], volume);
	}

	public static Sound[] load(String path, int amount) {
		Sound[] sounds = new Sound[amount];
		for(int i = 0; i < sounds.length; i++) {
			sounds[i] = new Sound(path + i + ".wav");
		}
		return sounds;
	}
}
