package de.bsautermeister.bomb.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.BombGame;
import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.core.GameApp;
import de.bsautermeister.bomb.core.ScreenBase;
import de.bsautermeister.bomb.screens.game.GameScreen;
import de.bsautermeister.bomb.screens.menu.content.AboutContent;
import de.bsautermeister.bomb.screens.menu.content.MenuContent;
import de.bsautermeister.bomb.utils.GdxUtils;

public class MenuScreen extends ScreenBase {
    private final Viewport uiViewport;
    private Stage stage;

    private Table content;

    private final String initialContentType;

    public MenuScreen(GameApp game) {
        this(game, MenuContent.TYPE);
    }

    public MenuScreen(GameApp game, String contentType) {
        super(game);
        this.uiViewport = new StretchViewport(Cfg.UI_WIDTH, Cfg.UI_HEIGHT);
        TextureAtlas atlas = getAssetManager().get(Assets.Atlas.GAME);
        this.initialContentType = contentType;
    }

    @Override
    public void show() {
        stage = new Stage(uiViewport, getGame().getBatch());
        stage.setDebugAll(Cfg.DEBUG_MODE);

        setContent(createContent(initialContentType));

        Gdx.input.setCatchKey(Input.Keys.BACK, true);
    }

    private void setContent(Table newContent) {
        if (content != null) {
            content.addAction(Actions.sequence(
                    Actions.removeActor()
            ));
        }
        content = newContent;
        stage.addActor(newContent);
    }

    private final Table createContent(String contentType) {
        if (MenuContent.TYPE.equals(contentType)) {
            return createMainContent();
        } else if (AboutContent.TYPE.equals(contentType)) {
            return createAboutContent();
        }

        throw new IllegalArgumentException("Unknown content type");
    }

    private Table createMainContent() {
        return new MenuContent((BombGame) getGame(), getAssetManager(), new MenuContent.Callbacks() {
            @Override
            public void playClicked() {
                setScreen(new GameScreen(getGame(), false));
            }

            @Override
            public void continueClicked() {
                setScreen(new GameScreen(getGame(), true));
            }

            @Override
            public void achievementsClicked() {
                // BombGame.getGameServiceManager().showAchievements();
            }

            @Override
            public void aboutClicked() {
                setContent(createAboutContent());
            }
        });
    }

    private Table createAboutContent() {
        return new AboutContent(getAssetManager());
    }

    @Override
    public void render(float delta) {
        GdxUtils.clearScreen(Color.BLACK);

        // ensure background tint color is not affected by actor actions
        getBatch().setColor(Color.WHITE);

        stage.act();
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                || (content instanceof AboutContent && Gdx.input.justTouched())) {
            if (content instanceof MenuContent) {
                Gdx.app.exit();
            } else {
                setContent(createMainContent());
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
