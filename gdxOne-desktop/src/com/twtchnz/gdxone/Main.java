package com.twtchnz.gdxone;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "gdxOne";
		cfg.useGL20 = true;
		cfg.width = 1024;
		cfg.height = 768;
        cfg.fullscreen = false;
		
		new LwjglApplication(new MyGdxGame(), cfg);
	}
}
