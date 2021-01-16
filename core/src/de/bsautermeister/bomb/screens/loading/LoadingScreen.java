package de.bsautermeister.bomb.screens.loading;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.RegionNames;
import de.bsautermeister.bomb.core.GameApp;
import de.bsautermeister.bomb.core.ScreenBase;
import de.bsautermeister.bomb.screens.menu.MenuScreen;
import de.bsautermeister.bomb.utils.GdxUtils;

public class LoadingScreen extends ScreenBase {
    private static final Logger LOG = new Logger(LoadingScreen.class.getSimpleName(), Cfg.LOG_LEVEL);

    private Stage stage;

    private Label loadingLabel;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image loadingBg;

    private float startX, endX;

    private Image loadingBar;

    private float remainingTimeWhenFullyLoaded = 0.25f;

    private boolean isLoadingFinished = false;

    public LoadingScreen(GameApp game) {
        super(game);
    }

    @Override
    public void show() {
        Viewport viewport = new FitViewport(Cfg.Ui.WIDTH, Cfg.Ui.HEIGHT);

        // Tell the manager to load assets for the loading screen
        getAssetManager().load(Assets.Atlas.LOADING);
        getAssetManager().load(Assets.Skins.LOADING);
        // Wait until they are finished loading
        getAssetManager().finishLoading();

        // Initialize the stage where we will place everything
        stage = new Stage(viewport, getBatch());

        // Get our texture atlas from the manager
        TextureAtlas atlas = getAssetManager().get(Assets.Atlas.LOADING);

        // Grab the regions from the atlas and create some images
        Skin skin = getAssetManager().get(Assets.Skins.LOADING);
        loadingLabel = new Label("Loading...", skin);
        loadingFrame = new Image(atlas.findRegion(RegionNames.Loading.FRAME));
        loadingBarHidden = new Image(atlas.findRegion(RegionNames.Loading.BAR_HIDDEN));
        loadingBg = new Image(atlas.findRegion(RegionNames.Loading.FRAME_BACKGROUND));
        loadingBar = new Image(atlas.findRegion(RegionNames.Loading.LOADING_ANIMATION));
        loadingBar.setColor(Cfg.Colors.DARK_RED);

        // Add all the actors to the stage
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        stage.addActor(loadingLabel);

        // Add everything to be loaded, for instance:
        loadAssets();
    }

    private void loadAssets() {
        for (AssetDescriptor assetDescriptor : Assets.PRELOAD) {
            getAssetManager().load(assetDescriptor);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width , height, true);

        // Place the logo in the middle of the screen on top of the loading bar
        loadingLabel.setX((stage.getWidth() - loadingLabel.getWidth()) / 2);
        loadingLabel.setY((stage.getHeight() - loadingLabel.getHeight()) / 2 + 80);
        loadingLabel.setOrigin(Align.center);

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

        // Place the loading bar at the same spot as the frame
        loadingBar.setX(loadingFrame.getX());
        loadingBar.setY(loadingFrame.getY());

        // Place the image that will hide the bar on top of the bar
        loadingBarHidden.setX(loadingBar.getX());
        loadingBarHidden.setY(loadingBar.getY());
        // The start position and how far to move the hidden loading bar
        startX = loadingFrame.getX();
        endX = loadingFrame.getWidth() - loadingBarHidden.getWidth();

        // The rest of the hidden bar
        loadingBg.setSize(loadingFrame.getWidth(), loadingFrame.getHeight());
        loadingBg.setX(loadingBarHidden.getX());
        loadingBg.setY(loadingBarHidden.getY());
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        GdxUtils.clearScreen(Color.BLACK);

        float percent = MathUtils.round(getAssetManager().getProgress() * 100) / 100f;

        // Update positions (and size) to match the percentage
        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + loadingBarHidden.getWidth());
        loadingBg.setWidth((loadingFrame.getWidth() - loadingBarHidden.getWidth()) * (1 - percent));
        loadingBg.invalidate();

        // Show the loading screen
        stage.act();
        stage.draw();

        if (getAssetManager().isFinished()) {
            remainingTimeWhenFullyLoaded -= delta;
        }

        if (getAssetManager().update() && remainingTimeWhenFullyLoaded <= 0 && !isLoadingFinished) {
            isLoadingFinished = true;

            // loading game service content, as soon as the asses have been loaded, and the user is
            // logged in
            //BombGame.getGameServiceManager().refresh();

            setScreen(new MenuScreen(getGame()));
        }
    }

    @Override
    public void dispose() {
        // Dispose the loading assets as we no longer need them
        getAssetManager().unload(Assets.Atlas.LOADING.fileName);

        stage.dispose();
    }

    @Override
    public void pause() {
        super.pause();

        LOG.debug("PAUSE");
    }

    @Override
    public void resume() {
        super.resume();

        LOG.debug("RESUME");
    }
}
