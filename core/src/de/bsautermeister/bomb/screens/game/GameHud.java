package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.Styles;

public class GameHud implements Disposable {

    private final AssetManager assetManager;
    private final Stage stage;

    private Group group = new Group();
    private Label scoreLabel;
    private int score = Integer.MAX_VALUE;

    private static final float SMALL_SCALE = 0.5f;

    private static final String SCORE_LABEL_STYLE = Styles.Label.DEFAULT;
    private static final String FEET_LABEL_STYLE = Styles.Label.XSMALL;

    public GameHud(AssetManager assetManager, Viewport uiViewport, Batch batch) {
        this.assetManager = assetManager;

        Skin skin = assetManager.get(Assets.Skins.UI);
        enableSmoothScaling(skin.getFont(SCORE_LABEL_STYLE));
        enableSmoothScaling(skin.getFont(FEET_LABEL_STYLE));

        stage = new Stage(uiViewport, batch);
        stage.setDebugAll(Cfg.DEBUG_MODE);
        initialize();
        updateScore(0);
    }

    private static void enableSmoothScaling(BitmapFont font) {
        font.getRegion().getTexture()
                .setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private void initialize() {
        Skin skin = assetManager.get(Assets.Skins.UI);
        Table scoreWithUnitTable = new Table();
        scoreLabel = new Label("", skin, SCORE_LABEL_STYLE);
        Label feetLabel = new Label(" ft", skin, FEET_LABEL_STYLE);
        scoreWithUnitTable.add(scoreLabel);
        scoreWithUnitTable.add(feetLabel).padTop(8f);
        scoreWithUnitTable.align(Align.center | Align.top);
        group.addActor(scoreWithUnitTable);
        group.setTransform(true);
        group.setScale(SMALL_SCALE);
        group.setColor(Color.CLEAR);

        Table table = new Table()
                .top();
        table.setFillParent(true);

        table.add(group).center();
        table.pack();
        stage.addActor(table);
    }

    public void updateScore(int value) {
        if (this.score != value) {
            this.score = value;
            scoreLabel.setText(String.valueOf(value));
            if (score > 0) {
                group.clearActions();
                group.addAction(
                        Actions.parallel(
                                Actions.sequence(
                                        Actions.scaleTo(1f, 1f, 0.25f, Interpolation.smooth),
                                        Actions.delay(1f),
                                        Actions.scaleTo(SMALL_SCALE, SMALL_SCALE, 0.25f, Interpolation.smooth)),
                                Actions.alpha(1, 1f)));
            }
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
