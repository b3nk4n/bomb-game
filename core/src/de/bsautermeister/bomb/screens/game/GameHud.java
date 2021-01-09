package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Locale;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.Styles;

public class GameHud implements Disposable {

    private final AssetManager assetManager;
    private final Stage stage;

    private Label scoreLabel;
    private int score = Integer.MAX_VALUE;

    public GameHud(AssetManager assetManager, Viewport uiViewport, Batch batch) {
        this.assetManager = assetManager;

        stage = new Stage(uiViewport, batch);
        stage.setDebugAll(Cfg.DEBUG_MODE);
        initialize();
        updateScore(0);
    }

    private void initialize() {
        Skin skin = assetManager.get(Assets.Skins.UI);
        scoreLabel = new Label("", skin, Styles.Label.DEFAULT);

        Table table = new Table()
                .top();
        table.setFillParent(true);

        table.add(scoreLabel).pad(12f).expandX().left();
        table.pack();
        stage.addActor(table);
    }

    public void updateScore(int value) {
        if (this.score != value) {
            this.score = value;
            scoreLabel.setText(String.format(Locale.US, "%d ft", value));
        }
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Camera getCamera() {
        return stage.getCamera();
    }
}
