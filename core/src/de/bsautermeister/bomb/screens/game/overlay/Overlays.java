package de.bsautermeister.bomb.screens.game.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.utils.TextureUtils;

public class Overlays<T extends Enum> {

    private final ObjectMap<T, Overlay> overlays;
    private final Stage overlayStage;

    // workaround: do not act during the first frame, otherwise button event which triggered
    // this overlay to show are processed in the overlay, which could immediately close it again
    private boolean skipNextOverlayAct = false;

    private TextureRegion backgroundRegion;

    public Overlays(Viewport uiViewport, Batch batch, int backgroundRgba8888) {
        overlays = new ObjectMap<>();

        overlayStage = new Stage(uiViewport, batch);
        overlayStage.setDebugAll(Cfg.DEBUG_MODE);

        backgroundRegion = TextureUtils.singleColorTexture(backgroundRgba8888);

        Gdx.input.setInputProcessor(overlayStage);
    }

    public void register(T state, Overlay overlay) {
        overlays.put(state, overlay);
    }

    public void update(T state) {
        if (!isVisible()) {
            Overlay overlay = overlays.get(state);
            if (overlay != null) {
                overlayStage.addActor(overlay);
                overlay.show();
                skipNextOverlayAct = true;
            }
        } else if (!overlays.containsKey(state)) {
            overlayStage.clear();
        }
    }

    public void render(SpriteBatch batch) {
        if (isVisible()) {
            if (skipNextOverlayAct) {
                skipNextOverlayAct = false;
                return;
            }
            overlayStage.act();
            batch.begin();
            batch.draw(backgroundRegion, 0f, 0f, Cfg.Ui.WIDTH, Cfg.Ui.HEIGHT);
            batch.end();
            overlayStage.draw();
        }
    }

    public boolean isVisible() {
        return !overlayStage.getActors().isEmpty();
    }

    public InputProcessor getInputProcessor() {
        return overlayStage;
    }
}
