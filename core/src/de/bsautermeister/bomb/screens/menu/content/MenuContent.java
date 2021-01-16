package de.bsautermeister.bomb.screens.menu.content;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
import de.bsautermeister.bomb.GameSettings;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.Styles;

public class MenuContent extends Table {

    public static final String TYPE = MenuContent.class.getSimpleName();

    private static final float DELAY_OFFSET = 0.25f;

    public interface Callbacks {
        void playClicked();
        void continueClicked();
        void achievementsClicked();
        void aboutClicked();
    }

    private final boolean canShowAchievements;
    private final boolean canResume;
    private final GameSettings gameSettings;
    private final Callbacks callbacks;

    public MenuContent(AssetManager assetManager, GameSettings gameSettings, Callbacks callbacks,
                       boolean canResume, boolean canShowAchievements) {
        this.canResume = canResume;
        this.canShowAchievements = canShowAchievements;
        this.gameSettings = gameSettings;
        this.callbacks = callbacks;
        initialize(assetManager);
    }

    private void initialize(AssetManager assetManager) {
        Skin skin = assetManager.get(Assets.Skins.UI);

        center();
        setFillParent(true);

        defaults()
                .pad(16f);

        float delay = 0f;
        final Label title = new Label("The Downfall", skin, Styles.Label.TITLE);
        title.setColor(Cfg.Colors.DARK_RED);
        title.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(delay),
                Actions.alpha(1f, 0.5f)
        ));
        delay += DELAY_OFFSET;
        add(title)
                .row();

        final Button playButton = new TextButton("Play", skin, Styles.TextButton.LARGE);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.playClicked();
            }
        });
        playButton.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(delay),
                Actions.alpha(1f, 0.5f)
        ));
        delay += DELAY_OFFSET;
        add(playButton)
                .row();

        if (canResume) {
            final Button continueButton = new TextButton("Resume", skin);
            continueButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.continueClicked();
                }
            });
            continueButton.addAction(Actions.sequence(
                    Actions.alpha(0f),
                    Actions.delay(delay),
                    Actions.alpha(1f, 0.5f)
            ));
            delay += DELAY_OFFSET;
            add(continueButton)
                    .row();
        }

        if (canShowAchievements) {
            final Button aboutButton = new TextButton("Achievements", skin);
            aboutButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.achievementsClicked();
                }
            });
            aboutButton.addAction(Actions.sequence(
                    Actions.alpha(0f),
                    Actions.delay(delay),
                    Actions.alpha(1f, 0.5f)
            ));
            delay += DELAY_OFFSET;
            add(aboutButton)
                    .row();
        }

        final Button aboutButton = new TextButton("About", skin);
        aboutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.aboutClicked();
            }
        });
        aboutButton.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(delay),
                Actions.alpha(1f, 0.5f)
        ));
        delay += DELAY_OFFSET;
        add(aboutButton)
                .row();

        final int score = 123; // TODO show actual player's highscore
        boolean hasScore = score > 0;

        Table footerTable = new Table();
        footerTable.padTop(32f);
        footerTable.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(delay),
                Actions.alpha(1f, 0.5f)
        ));

        Label vibrationLabel = new Label("Vibration", skin, Styles.Label.XSMALL);
        vibrationLabel.setColor(Cfg.Colors.DARK_RED);
        footerTable.add(vibrationLabel);

        if (hasScore) {
            Label highscoreLabel = new Label("Highscore", skin, Styles.Label.XSMALL);
            highscoreLabel.setColor(Cfg.Colors.DARK_RED);
            footerTable.add(highscoreLabel);
        } else {
            footerTable.add(new Actor());
        }

        Label musicVolumeLabel = new Label("Music Volume", skin, Styles.Label.XSMALL);
        musicVolumeLabel.setColor(Cfg.Colors.DARK_RED);
        footerTable.add(musicVolumeLabel).row();

        String vibrationValueText = getVibrationText(gameSettings.getVibration());
        final TextButton toggleVibrateButton = new TextButton(vibrationValueText, skin, Styles.TextButton.SMALL);
        toggleVibrateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean vibrationEnabled = gameSettings.toggleVibration();
                toggleVibrateButton.setText(getVibrationText(vibrationEnabled));

                if (vibrationEnabled) {
                    Gdx.input.vibrate(250);
                }
            }
        });
        footerTable.add(toggleVibrateButton).width(256f);

        if (hasScore) {
            Table scoreTable = new Table();
            Label scoreLabel = new Label("1234", skin, Styles.Label.DEFAULT);
            Label feetLabel = new Label("ft", skin, Styles.Label.XXSMALL);
            scoreTable.add(scoreLabel);
            scoreTable.add(feetLabel).padLeft(4f).padTop(16f);
            footerTable.add(scoreTable).width(512f);
        } else {
            footerTable.add(new Actor()).width(512f);
        }

        String musicVolumeValueText = getMusicVolumeText(gameSettings.getMusicVolumeLevel());
        final TextButton toggleMusicVolumeButton = new TextButton(musicVolumeValueText, skin, Styles.TextButton.SMALL);
        toggleMusicVolumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int musicVolumeLevel = gameSettings.toggleMusicVolumeLevel();
                toggleMusicVolumeButton.setText(getMusicVolumeText(musicVolumeLevel));

            }
        });
        footerTable.add(toggleMusicVolumeButton).width(256f).row();
        add(footerTable);

        pack();
    }

    private String getVibrationText(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }

    private String getMusicVolumeText(int level) {
        switch (level) {
            case 1:
                return "LOW";
            case 2:
                return "MEDIUM";
            case 3:
                return "HIGH";
            default:
                return "OFF";
        }
    }
}
