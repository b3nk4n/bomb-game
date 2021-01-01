package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Locale;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.RegionNames;
import de.bsautermeister.bomb.assets.Styles;

public class GameHud implements Disposable {

    private final AssetManager assetManager;
    private final Stage stage;

    private Image lifeRatioImage;
    private Label scoreLabel;

    private int lifeRatio = Integer.MAX_VALUE;
    private int score = Integer.MAX_VALUE;

    private final Array<TextureRegionDrawable> lifeBarRegions = new Array<>(8);

    public GameHud(AssetManager assetManager, Viewport uiViewport, Batch batch) {
        this.assetManager = assetManager;

        TextureAtlas atlas = assetManager.get(Assets.Atlas.UI);
        Array<TextureAtlas.AtlasRegion> lifeBarRegions = atlas.findRegions(RegionNames.Ui.LIFE_BAR);
        for (TextureAtlas.AtlasRegion lifeBarRegion : lifeBarRegions) {
            this.lifeBarRegions.add(new TextureRegionDrawable(lifeBarRegion));
        }

        stage = new Stage(uiViewport, batch);
        stage.setDebugAll(Cfg.DEBUG_MODE);
        initialize();
        updateLifeRatio(1f);
        updateScore(0);
    }

    private void initialize() {
        Skin skin = assetManager.get(Assets.Skins.UI);
        scoreLabel = new Label("", skin, Styles.Label.DEFAULT);
        lifeRatioImage = new Image(lifeBarRegions.get(lifeBarRegions.size - 1));

        Table table = new Table()
                .top();
        table.setFillParent(true);

        table.add(scoreLabel).pad(12f).expandX().left();
        table.add(lifeRatioImage).pad(12f).expandX().right();
        table.pack();
        stage.addActor(table);
    }

    public void updateLifeRatio(float value) {
        int lifeRatio = (int) Math.ceil(value * 100);
        if (this.lifeRatio != lifeRatio) {
            this.lifeRatio = lifeRatio;
            int idx = MathUtils.ceil(value * (lifeBarRegions.size - 1));
            lifeRatioImage.setDrawable(lifeBarRegions.get(idx));
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
