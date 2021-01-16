package de.bsautermeister.bomb.screens.game.overlay;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class Overlay extends Table {

    public Overlay() { }

    public Overlay(Skin skin) {
        super(skin);
    }

    public abstract void show();
}
