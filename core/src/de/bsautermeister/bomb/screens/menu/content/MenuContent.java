package de.bsautermeister.bomb.screens.menu.content;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.bsautermeister.bomb.BombGame;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.Styles;

public class MenuContent extends Table {

    public static final String TYPE = MenuContent.class.getSimpleName();

    private static float DELAY_OFFSET = 0.25f;

    private final Callbacks callbacks;

    public MenuContent(AssetManager assetManager, Callbacks callbacks) {
        this.callbacks = callbacks;
        initialize(assetManager);
    }

    private void initialize(AssetManager assetManager) {
        Skin skin = assetManager.get(Assets.Skins.UI);

        center();
        setFillParent(true);

        defaults()
                .pad(8f);

        Label title = new Label("Bombageddon", skin, Styles.Label.TITLE);
        add(title)
                .pad(8f)
                .row();

        float delay = 1.0f;
        Button playButton = new TextButton("Play", skin);
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

        if (BombGame.hasSavedData()) {
            Button continueButton = new TextButton("Resume", skin);
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

        /*if (BombGame.getGameServiceManager().isSupported()) {
            Button aboutButton = new TextButton("Achievements", skin);
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
        }*/

        Button aboutButton = new TextButton("About", skin);
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

    public interface Callbacks {
        void playClicked();
        void continueClicked();
        void achievementsClicked();
        void aboutClicked();
    }
}
