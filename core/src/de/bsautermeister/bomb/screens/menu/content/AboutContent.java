package de.bsautermeister.bomb.screens.menu.content;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.Styles;

public class AboutContent extends Table {

    public static final String TYPE = AboutContent.class.getSimpleName();

    private static final float TEXT_ANIMATION_DURATION = 7.5f;

    private final Skin skin;
    private final Table creditContainer = new Table();

    private final CreditEntry[] credits;

    private int currentCreditIndex = 0;


    public AboutContent(AssetManager assetManager) {
        skin = assetManager.get(Assets.Skins.UI);

        credits = new CreditEntry[] {
                new CreditEntry("Developer", "Benjamin Sautermeister"),
                new CreditEntry("SFX", "Benjamin Sautermeister", "Denis Chardonnet"),
                new CreditEntry("Effects", "Benjamin Sautermeister", "Existical"),
                new CreditEntry("Music", "TBD"),
        };

        initialize();
    }

    private void initialize() {
        center();
        setFillParent(true);

        addActor(creditContainer);
        creditContainer
                .center()
                .padBottom(32f)
                .setFillParent(true);
        updateLabels(credits[currentCreditIndex]);
    }

    private void updateLabels(CreditEntry entry) {
        creditContainer.clearChildren();
        Label titleLabel = new Label(entry.title, skin, Styles.Label.LARGE);
        titleLabel.setColor(Cfg.Colors.DARK_RED);
        creditContainer.add(titleLabel)
                .pad(16f)
                .row();
        for (String line : entry.lines) {
            creditContainer.add(new Label(line, skin, Styles.Label.DEFAULT))
                    .row();
        }
        creditContainer.pack();
        creditContainer.addAction(
                Actions.sequence(
                        Actions.alpha(0f),
                        Actions.alpha(1f, 0.5f),
                        Actions.delay(TEXT_ANIMATION_DURATION),
                        Actions.alpha(0f, 0.5f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                currentCreditIndex = ++currentCreditIndex % credits.length;
                                updateLabels(credits[currentCreditIndex]);
                            }
                        })
                )
        );
        pack();
    }

    private static class CreditEntry {
        final String title;
        final String[] lines;

        CreditEntry(String title, String... lines){
            this.title = title;
            this.lines = lines;
        }
    }
}