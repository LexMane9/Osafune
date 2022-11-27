package com.alexnae.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import game.Parametros;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("OsafuneCG");
        config.setWindowedMode(Parametros.getAnchoPantalla(), Parametros.getAltoPantalla());
        config.setResizable(false);
        OCG ocg = new OCG();
        new Lwjgl3Application(new OCG(), config);
    }
}
