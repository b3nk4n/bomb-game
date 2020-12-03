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

    private Label lifeRatioLabel;
    private Label scoreLabel;

    private int lifeRatio = Integer.MAX_VALUE;
    private int score = Integer.MAX_VALUE;

    public GameHud(AssetManager assetManager, Viewport uiViewport, Batch batch) {
        this.assetManager = assetManager;
        stage = new Stage(uiViewport, batch);
        stage.setDebugAll(Cfg.DEBUG_MODE);
        initialize();
        updateLifeRatio(1f);
        updateScore(0);
    }

    private void initialize() {
        Skin skin = assetManager.get(Assets.Skins.UI);
        lifeRatioLabel = new Label("", skin, Styles.Label.DEFAULT);
        scoreLabel = new Label("", skin, Styles.Label.DEFAULT);

        Table table = new Table()
                .top();
        table.setFillParent(true);

        table.add(scoreLabel).pad(8f).expandX().left();
        table.add(lifeRatioLabel).pad(8f).expandX().right();
        table.pack();
        stage.addActor(table);
    }

    public void updateLifeRatio(float value) {
        int lifeRatio = (int) Math.ceil(value * 100);
        if (this.lifeRatio != lifeRatio) {
            this.lifeRatio = lifeRatio;
            lifeRatioLabel.setText(String.format(Locale.US, "%d %%", lifeRatio));
        }
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
