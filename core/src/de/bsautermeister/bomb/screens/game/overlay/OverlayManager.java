package de.bsautermeister.bomb.screens.game.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.Cfg;

public class OverlayManager<T extends Enum> {

    private final ObjectMap<T, Actor> overlays;
    private final Stage overlayStage;

    // workaround: do not act during the first frame, otherwise button event which triggered
    // this overlay to show are processed in the overlay, which could immediately close it again
    private boolean skipNextOverlayAct = false;

    public OverlayManager(Viewport uiViewport, Batch batch) {
        overlays = new ObjectMap<>();

        overlayStage = new Stage(uiViewport, batch);
        overlayStage.setDebugAll(Cfg.DEBUG_MODE);

        Gdx.input.setInputProcessor(overlayStage);
    }

    public void register(T state, Table overlay) {
        overlays.put(state, overlay);
    }

    public void update(T state) {
        if (overlayStage.getActors().isEmpty()) {
            Actor overlay = overlays.get(state);
            if (overlay != null) {
                overlayStage.addActor(overlay);
                skipNextOverlayAct = true;
            }
        } else {
            if (!overlays.containsKey(state)) {
                overlayStage.clear();
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (!overlayStage.getActors().isEmpty()) {
            if (skipNextOverlayAct) {
                skipNextOverlayAct = false;
                return;
            }
            overlayStage.act();
            batch.begin();
            // batch.draw(backgroundOverlayRegion, 0f, 0f, Cfg.UI_WIDTH, Cfg.UI_HEIGHT);
            batch.end();
            overlayStage.draw();
        }
    }

    public InputProcessor getInputProcessor() {
        return overlayStage;
    }
}
