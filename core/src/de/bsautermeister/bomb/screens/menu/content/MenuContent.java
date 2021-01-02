package de.bsautermeister.bomb.screens.menu.content;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
                .pad(8f);

        final Label title = new Label("The Downfall", skin, Styles.Label.TITLE);
        add(title)
                .pad(8f)
                .row();

        float delay = 1.0f;
        final Button playButton = new TextButton("Play", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.playClicked();
            }
        });
        playButton.addAction(Actions.sequence(
                Actions.hide(),
                Actions.delay(delay),
                Actions.show()
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
                    Actions.hide(),
                    Actions.delay(delay),
                    Actions.show()
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
                    Actions.hide(),
                    Actions.delay(delay),
                    Actions.show()
            ));
            delay += DELAY_OFFSET;
            add(aboutButton)
                    .row();
        }

        String vibrationText = getVibrationText(gameSettings.getVibration());
        final TextButton toggleVibrateButton = new TextButton(vibrationText, skin);
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
        toggleVibrateButton.addAction(Actions.sequence(
                Actions.hide(),
                Actions.delay(delay),
                Actions.show()
        ));
        add(toggleVibrateButton)
                .row();

        String musicVolumeText = getMusicVolumeText(gameSettings.getMusicVolumeLevel());
        final TextButton musicVolumeButton = new TextButton(musicVolumeText, skin);
        musicVolumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int musicVolumeLevel = gameSettings.toggleMusicVolumeLevel();
                musicVolumeButton.setText(getMusicVolumeText(musicVolumeLevel));

            }
        });
        musicVolumeButton.addAction(Actions.sequence(
                Actions.hide(),
                Actions.delay(delay),
                Actions.show()
        ));
        add(musicVolumeButton)
                .row();

        final Button aboutButton = new TextButton("About", skin);
        aboutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.aboutClicked();
            }
        });
        aboutButton.addAction(Actions.sequence(
                Actions.hide(),
                Actions.delay(delay),
                Actions.show()
        ));
        add(aboutButton)
                .row();

        pack();
    }

    private String getVibrationText(boolean enabled) {
        return enabled ? "Vibration: ON" : "Vibration: OFF";
    }

    private String getMusicVolumeText(int level) {
        switch (level) {
            case 1:
                return "Music Volume: LOW";
            case 2:
                return "Music Volume: MEDIUM";
            case 3:
                return "Music Volume: HIGH";
            default:
                return "Music Volume: OFF";
        }
    }
}
