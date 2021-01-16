package de.bsautermeister.bomb.screens.game.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.bsautermeister.bomb.assets.Styles;

public class PauseOverlay extends Overlay {

    public interface Callback {
        void quit();
        void resume();
    }

    private Callback callback;

    private Actor titleLabel;
    private Table buttonTable;

    public PauseOverlay(Skin skin, Callback callback) {
        super(skin);
        this.callback = callback;
        initialize();
    }

    private void initialize() {
        titleLabel = new Label("Paused", getSkin(), Styles.Label.TITLE);
        titleLabel.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(0f),
                Actions.alpha(1f, 0.5f)
        ));
        add(titleLabel)
                .pad(8f)
                .row();

        buttonTable = new Table(getSkin());
        buttonTable.defaults()
                .pad(54f);
        buttonTable.center();
        buttonTable.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(0.25f),
                Actions.alpha(1f, 0.5f)
        ));

        Button resumeButton = new TextButton("Resume", getSkin(), Styles.TextButton.LARGE);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.resume();
            }
        });
        buttonTable.add(resumeButton);

        Button quitButton = new TextButton("Quit", getSkin(), Styles.TextButton.LARGE);
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
    public void show() {
        titleLabel.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(0f),
                Actions.alpha(1f, 0.5f)
        ));
        buttonTable.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(0.25f),
                Actions.alpha(1f, 0.5f)
        ));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            callback.resume();
        }
    }
}
