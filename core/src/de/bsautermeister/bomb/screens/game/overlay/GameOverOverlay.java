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

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Styles;
import de.bsautermeister.bomb.objects.Player;
import de.bsautermeister.bomb.screens.game.GameController;
import de.bsautermeister.bomb.screens.game.score.GameScores;
import de.bsautermeister.bomb.screens.game.score.ScoreUtils;

public class GameOverOverlay extends Overlay {
    public interface Callback {
        void quit();
        void revive();
        void restart();
    }

    private final Callback callback;
    private final Player player;
    private final GameScores gameScores;
    private final GameController controller;

    public GameOverOverlay(Skin skin, GameController controller) {
        super(skin);
        this.callback = controller.getGameOverCallback();
        this.player = controller.getPlayer();
        this.gameScores = controller.getGameScores();
        this.controller = controller;
    }

    @Override
    public void show() {
        clear();
        Label titleLabel = new Label("Game Over", getSkin(), Styles.Label.TITLE);
        titleLabel.setColor(Cfg.Colors.DARK_RED);
        add(titleLabel)
                .pad(8f)
                .row();
        titleLabel.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(0f),
                Actions.alpha(1f, 0.5f)
        ));

        Table scoresTable = new Table();
        scoresTable.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(0.25f),
                Actions.alpha(1f, 0.5f)
        ));
        Label scoreTitleLabel = new Label("Score", getSkin(), Styles.Label.XSMALL);
        scoresTable.add(scoreTitleLabel).padTop(32f).row();

        int newScore = ScoreUtils.toScore(player.getMaxDepth());
        int personalBest = gameScores.getPersonalBestScore();
        Table scoreTable = new Table();
        scoreTable.padTop(-24f);
        Label scoreLabel = new Label(String.valueOf(newScore), getSkin(), Styles.Label.TITLE);
        Label feetLabel = new Label("ft", getSkin(), Styles.Label.DEFAULT);
        scoreTable.add(scoreLabel);
        scoreTable.add(feetLabel).padLeft(4f).padTop(32f);
        scoresTable.add(scoreTable).row();

        if (newScore >= personalBest) {
            Label newHighscoreLabel = new Label("Personal  Best!", getSkin(), Styles.Label.XSMALL);
            newHighscoreLabel.setColor(Cfg.Colors.DARK_RED);
            scoresTable.add(newHighscoreLabel).row();
        } else {
            Table highscoreTable = new Table();
            Label highscoreLabel = new Label("Highscore", getSkin(), Styles.Label.XSMALL);
            highscoreLabel.setColor(Cfg.Colors.DARK_RED);
            Label highscoreValueLabel = new Label(String.valueOf(personalBest), getSkin(), Styles.Label.XSMALL);
            highscoreValueLabel.setColor(Cfg.Colors.DARK_RED);
            Label highscoreFeetLabel = new Label("ft", getSkin(), Styles.Label.XXXSMALL);
            highscoreFeetLabel.setColor(Cfg.Colors.DARK_RED);
            highscoreTable.add(highscoreLabel).padRight(16f);
            highscoreTable.add(highscoreValueLabel);
            highscoreTable.add(highscoreFeetLabel).padLeft(2f).padTop(6f);
            scoresTable.add(highscoreTable).row();
        }
        add(scoresTable).row();


        Table buttonTable = new Table(getSkin());
        buttonTable.defaults()
                .pad(54);
        buttonTable.center();
        buttonTable.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(0.5f),
                Actions.alpha(1f, 0.5f)
        ));

        Button restartButton = new TextButton("Restart", getSkin(), Styles.TextButton.LARGE);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.restart();
            }
        });
        buttonTable.add(restartButton);

        /*if (controller.canRevive()) {
            Table reviveTable = new Table();
            Button reviveButton = new TextButton("Revive", getSkin(), Styles.TextButton.LARGE);
            reviveButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callback.revive();
                }
            });
            reviveTable.add(reviveButton).padTop(12f).row();
            Label watchAdsLabel = new Label("(watch ad)", getSkin(), Styles.Label.XXSMALL);
            reviveTable.add(watchAdsLabel).padTop(-16f);
            buttonTable.add(reviveTable);
        }*/

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
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            callback.quit();
        }
    }
}
