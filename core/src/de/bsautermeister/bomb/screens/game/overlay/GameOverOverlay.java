package de.bsautermeister.bomb.screens.game.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.bsautermeister.bomb.assets.Styles;

public class GameOverOverlay extends Table {
    public interface Callback {
        void quit();
        void restart();
    }

    private Callback callback;

    public GameOverOverlay(Skin skin, Callback callback) {
        super(skin);
        this.callback = callback;
        initialize();
    }

    private void initialize() {
        Label titleLabel = new Label("Game Over", getSkin(), Styles.Label.TITLE);
        add(titleLabel)
                .pad(8f)
                .row();

        Table buttonTable = new Table(getSkin());
        buttonTable.defaults()
                .pad(8f);
        buttonTable.center();
        buttonTable.addAction(Actions.sequence(
                Actions.hide(),
                Actions.delay(1f),
                Actions.show()
        ));

        Button restartButton = new TextButton("Restart", getSkin());
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.restart();
            }
        });
        buttonTable.add(restartButton).row();

        Button quitButton = new TextButton("Quit", getSkin());
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.quit();
            }
        });
        buttonTable.add(quitButton).row();

        add(buttonTable);

        center();
        setFillParent(true);
        pack();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            callback.quit();
        }
    }
}
