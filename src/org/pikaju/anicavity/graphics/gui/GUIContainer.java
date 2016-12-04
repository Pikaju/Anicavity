package org.pikaju.anicavity.graphics.gui;

import java.util.ArrayList;

import org.pikaju.anicavity.graphics.Bitmap;

public class GUIContainer {

	private ArrayList<GUIComponent> components;
	private boolean enabled;
	
	public GUIContainer() {
		components = new ArrayList<GUIComponent>();
	}
	
	public void update() {
		if(!enabled) return;
		for(int i = 0; i < components.size(); i++) {
			components.get(i).update();
		}
	}
	
	public void render(Bitmap screen) {
		if(!enabled) return;
		for(int i = 0; i < components.size(); i++) {
			components.get(i).render(screen);
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void addComponent(GUIComponent component) {
		components.add(component);
	}
}
