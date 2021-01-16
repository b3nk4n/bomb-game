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

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Styles;

public class GameOverOverlay extends Overlay {
    public interface Callback {
        void quit();
        void restart();
    }

    private Callback callback;

    private Actor titleLabel;
    private Table scoresTable;
    private Table buttonTable;

    public GameOverOverlay(Skin skin, Callback callback) {
        super(skin);
        this.callback = callback;
        initialize();
    }

    private void initialize() {
        titleLabel = new Label("Game Over", getSkin(), Styles.Label.TITLE);
        titleLabel.setColor(Cfg.Colors.DARK_RED);
        add(titleLabel)
                .pad(8f)
                .row();

        scoresTable = new Table();
        Label scoreTitleLabel = new Label("Score", getSkin(), Styles.Label.XSMALL);
        scoresTable.add(scoreTitleLabel).padTop(32f).row();

        Table scoreTable = new Table();
        scoreTable.padTop(-24f);
        Label scoreLabel = new Label("1234", getSkin(), Styles.Label.TITLE);
        Label feetLabel = new Label("ft", getSkin(), Styles.Label.SMALL);
        scoreTable.add(scoreLabel);
        scoreTable.add(feetLabel).padLeft(4f).padTop(18f);
        scoresTable.add(scoreTable).row();

        final boolean newHighschore = false;
        if (newHighschore) {
            Label newHighscoreLabel = new Label("New Highscore!", getSkin(), Styles.Label.XSMALL);
            newHighscoreLabel.setColor(Cfg.Colors.DARK_RED);
            scoresTable.add(newHighscoreLabel).row();
        } else {
            Table highscoreTable = new Table();
            Label highscoreLabel = new Label("Highscore", getSkin(), Styles.Label.XSMALL);
            highscoreLabel.setColor(Cfg.Colors.DARK_RED);
            Label highscoreValueLabel = new Label("1234", getSkin(), Styles.Label.XSMALL);
            highscoreValueLabel.setColor(Cfg.Colors.DARK_RED);
            Label highscoreFeetLabel = new Label("ft", getSkin(), Styles.Label.XXXSMALL);
            highscoreFeetLabel.setColor(Cfg.Colors.DARK_RED);
            highscoreTable.add(highscoreLabel).padRight(16f);
            highscoreTable.add(highscoreValueLabel);
            highscoreTable.add(highscoreFeetLabel).padLeft(2f).padTop(6f);
            scoresTable.add(highscoreTable).row();
        }
        add(scoresTable).row();


        buttonTable = new Table(getSkin());
        buttonTable.defaults()
                .pad(54);
        buttonTable.center();

        Button restartButton = new TextButton("Restart", getSkin(), Styles.TextButton.LARGE);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.restart();
            }
        });
        buttonTable.add(restartButton);

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
        scoresTable.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(0.25f),
                Actions.alpha(1f, 0.5f)
        ));
        buttonTable.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(0.5f),
                Actions.alpha(1f, 0.5f)
        ));
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
