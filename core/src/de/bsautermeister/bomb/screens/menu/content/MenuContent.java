package de.bsautermeister.bomb.screens.menu.content;

import static de.bsautermeister.bomb.assets.Styles.ImageButton.ABOUT;
import static de.bsautermeister.bomb.assets.Styles.ImageButton.AUDIO0;
import static de.bsautermeister.bomb.assets.Styles.ImageButton.AUDIO1;
import static de.bsautermeister.bomb.assets.Styles.ImageButton.AUDIO2;
import static de.bsautermeister.bomb.assets.Styles.ImageButton.AUDIO3;
import static de.bsautermeister.bomb.assets.Styles.ImageButton.NO_VIBRATE;
import static de.bsautermeister.bomb.assets.Styles.ImageButton.PRIVACY;
import static de.bsautermeister.bomb.assets.Styles.ImageButton.STAR;
import static de.bsautermeister.bomb.assets.Styles.ImageButton.VIBRATE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.GameSettings;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.Styles;
import de.bsautermeister.bomb.screens.game.score.GameScores;

public class MenuContent extends Table {

    public static final String TYPE = MenuContent.class.getSimpleName();

    private static final float DELAY_OFFSET = 0.25f;

    public interface Callbacks {
        void playClicked();
        void continueClicked();
        void leaderboardsClicked();
        void achievementsClicked();
        void aboutClicked();
        void privacyClicked();
        void rateClicked();
        void signOut();
    }

    private int hiddenSignOutClickCounter;

    private final boolean canShowAchievements;
    private final boolean canShowLeaderboards;
    private final boolean canResume;
    private final GameSettings gameSettings;
    private final GameScores gameScores;
    private final Callbacks callbacks;

    private final boolean privacyOptionRequired;

    public MenuContent(AssetManager assetManager, GameSettings gameSettings, GameScores gameScores,
                       Callbacks callbacks, boolean canResume, boolean canShowLeaderboards,
                       boolean canShowAchievements, boolean privacyOptionRequired) {
        this.canResume = canResume;
        this.canShowLeaderboards = canShowLeaderboards;
        this.canShowAchievements = canShowAchievements;
        this.gameSettings = gameSettings;
        this.gameScores = gameScores;
        this.callbacks = callbacks;
        this.privacyOptionRequired = privacyOptionRequired;
        initialize(assetManager);
    }

    private void initialize(AssetManager assetManager) {
        final Skin skin = assetManager.get(Assets.Skins.UI);

        center();
        setFillParent(true);

        defaults()
                .pad(8f);

        Table contentTable = new Table();
        contentTable.defaults().pad(8f);
        add(contentTable).colspan(3).expand().row();

        Table leftFooterTable = new Table();
        leftFooterTable.defaults().pad(16f);
        add(leftFooterTable).left();

        Table centerFooterTable = new Table();
        centerFooterTable.defaults().pad(16f);
        add(centerFooterTable).expandX();

        Table rightFooterTable = new Table();
        rightFooterTable.defaults().pad(16f);
        add(rightFooterTable).right();


        float delay = 0f;
        final Label title = new Label("The Downfall", skin, Styles.Label.TITLE);
        title.setColor(Cfg.Colors.DARK_RED);
        title.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(delay),
                Actions.alpha(1f, 0.5f)
        ));
        title.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hiddenSignOutClickCounter++;
                if (hiddenSignOutClickCounter == 10) {
                    hiddenSignOutClickCounter = 0;
                    callbacks.signOut();
                }
            }
        });
        delay += DELAY_OFFSET;
        contentTable.add(title)
                .padTop(64f)
                .padBottom(0f)
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
        contentTable.add(playButton)
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
            contentTable.add(continueButton)
                    .row();
        }

        if (canShowLeaderboards) {
            final Button leaderboardsButton = new TextButton("Leaderboards", skin);
            leaderboardsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.leaderboardsClicked();
                }
            });
            leaderboardsButton.addAction(Actions.sequence(
                    Actions.alpha(0f),
                    Actions.delay(delay),
                    Actions.alpha(1f, 0.5f)
            ));
            delay += DELAY_OFFSET;
            contentTable.add(leaderboardsButton)
                    .row();
        }

        if (canShowAchievements) {
            final Button achievementsButton = new TextButton("Achievements", skin);
            achievementsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.achievementsClicked();
                }
            });
            achievementsButton.addAction(Actions.sequence(
                    Actions.alpha(0f),
                    Actions.delay(delay),
                    Actions.alpha(1f, 0.5f)
            ));
            delay += DELAY_OFFSET;
            contentTable.add(achievementsButton)
                    .row();
        }

        leftFooterTable.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(delay),
                Actions.alpha(1f, 0.5f)
        ));
        centerFooterTable.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(delay),
                Actions.alpha(1f, 0.5f)
        ));
        rightFooterTable.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(delay),
                Actions.alpha(1f, 0.5f)
        ));

        final int personalHighscore = gameScores.getPersonalBestScore();

        if (personalHighscore > 0) {
            Table scoreTable = new Table();
            Label bestLabel = new Label("Personal  Best:", skin, Styles.Label.SMALL);
            bestLabel.setColor(Cfg.Colors.DARK_RED);
            scoreTable.add(bestLabel);
            Label bestValueLabel = new Label(String.valueOf(personalHighscore), skin, Styles.Label.SMALL);
            bestValueLabel.setColor(Cfg.Colors.DARK_RED);
            scoreTable.add(bestValueLabel)
                    .padLeft(24f);
            Label feetLabel = new Label("ft", skin, Styles.Label.XXSMALL);
            feetLabel.setColor(Cfg.Colors.DARK_RED);
            scoreTable.add(feetLabel)
                    .padLeft(4f)
                    .padTop(8f);
            centerFooterTable.add(scoreTable).expandX();
        }

        final ImageButton vibrateButton = new ImageButton(getVibrateStyle(skin, gameSettings.getVibration()));
        vibrateButton.setColor(Cfg.Colors.DARK_RED);
        vibrateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean vibrationEnabled = gameSettings.toggleVibration();
                vibrateButton.setStyle(getVibrateStyle(skin, vibrationEnabled));
                if (vibrationEnabled) {
                    Gdx.input.vibrate(250);
                }
            }
        });
        leftFooterTable.add(vibrateButton);

        final ImageButton audioButton = new ImageButton(getAudioStyle(skin, gameSettings.getMusicVolumeLevel()));
        audioButton.setColor(Cfg.Colors.DARK_RED);
        audioButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int audioLevel = gameSettings.toggleMusicVolumeLevel();
                audioButton.setStyle(getAudioStyle(skin, audioLevel));
            }
        });
        leftFooterTable.add(audioButton);

        final ImageButton rateButton = new ImageButton(skin, STAR);
        rateButton.setColor(Cfg.Colors.DARK_RED);
        rateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.rateClicked();
            }
        });
        rightFooterTable.add(rateButton);

        if (privacyOptionRequired) {
            ImageButton privacyOptionButton = new ImageButton(skin, PRIVACY);
            privacyOptionButton.setColor(Cfg.Colors.DARK_RED);
            privacyOptionButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.privacyClicked();
                }
            });
            rightFooterTable.add(privacyOptionButton);
        }

        ImageButton aboutButton = new ImageButton(skin, ABOUT);
        aboutButton.setColor(Cfg.Colors.DARK_RED);
        aboutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.aboutClicked();
            }
        });
        rightFooterTable.add(aboutButton);

        pack();
    }

    private static ImageButton.ImageButtonStyle getVibrateStyle(Skin skin, boolean enabled) {
        return getImgBtnStyle(skin, enabled ? VIBRATE : NO_VIBRATE);
    }

    private static ImageButton.ImageButtonStyle getAudioStyle(Skin skin, int audioLevel) {
        switch (audioLevel) {
            case 1:
                return getImgBtnStyle(skin, AUDIO1);
            case 2:
                return getImgBtnStyle(skin, AUDIO2);
            case 3:
                return getImgBtnStyle(skin, AUDIO3);
            case 0:
            default:
                return getImgBtnStyle(skin, AUDIO0);
        }
    }

    private static ImageButton.ImageButtonStyle getImgBtnStyle(Skin skin, String vibrate) {
        return skin.get(vibrate, ImageButton.ImageButtonStyle.class);
    }
}
