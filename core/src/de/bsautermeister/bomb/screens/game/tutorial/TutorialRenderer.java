package de.bsautermeister.bomb.screens.game.tutorial;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.Styles;

public class TutorialRenderer {

    private final TutorialController controller;
    private final BitmapFont tutorialFont;
    private final Viewport uiViewport;

    public TutorialRenderer(TutorialController controller, AssetManager assetManager, Viewport uiViewport) {
        this.controller = controller;
        this.uiViewport = uiViewport;

        Skin skin = assetManager.get(Assets.Skins.UI);
        tutorialFont = skin.getFont(Styles.Fonts.LARGE);
    }

    private static final float LINE_WIDTH = 10f;
    private static final float LINE_HWIDTH = LINE_WIDTH / 2f;
    private static final float LINE_RECT_SIZE = 125f;
    private static final float LINE_EDGE_SIZE = 25f;
    private static final GlyphLayout tutGlyph = new GlyphLayout();
    public void render(ShapeRenderer shapeRenderer, Batch batch) {

        if (!controller.isVisible() || controller.isFinished()) {
            return;
        }

        TutorialStep step = controller.getCurrentStep();
        tutGlyph.setText(tutorialFont, step.getText());
        batch.begin();
        tutorialFont.draw(batch, step.getText(), (uiViewport.getWorldWidth() - tutGlyph.width) * 0.5f, 132);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        switch (step.getTag()) {
            case "LEFT":
                renderTouchArea(shapeRenderer, 32f, 32f);
                break;
            case "RIGHT":
                renderTouchArea(shapeRenderer, uiViewport.getWorldWidth() - LINE_RECT_SIZE - 32f, 32f);
                break;
            case "JUMP":
                renderTouchArea(shapeRenderer, 32f, 300f);
                renderTouchArea(shapeRenderer, uiViewport.getWorldWidth() - LINE_RECT_SIZE - 32f, 300f);
                break;
        }

        shapeRenderer.end();
    }

    private static void renderTouchArea(ShapeRenderer shapeRenderer, float offsetX, float offsetY) {
        // bottom left
        shapeRenderer.rectLine(offsetX - LINE_HWIDTH, offsetY, offsetX + LINE_EDGE_SIZE, offsetY, LINE_WIDTH);
        shapeRenderer.rectLine(offsetX, offsetY - LINE_HWIDTH, offsetX, offsetY + LINE_EDGE_SIZE, LINE_WIDTH);
        // top left
        shapeRenderer.rectLine(offsetX - LINE_HWIDTH, offsetY + LINE_RECT_SIZE, offsetX + LINE_EDGE_SIZE, offsetY + LINE_RECT_SIZE , LINE_WIDTH);
        shapeRenderer.rectLine(offsetX, offsetY + LINE_RECT_SIZE + LINE_HWIDTH, offsetX, offsetY + LINE_RECT_SIZE - LINE_EDGE_SIZE, LINE_WIDTH);
        // bottom right
        shapeRenderer.rectLine(offsetX + LINE_RECT_SIZE + LINE_HWIDTH, offsetY, offsetX + LINE_RECT_SIZE - LINE_EDGE_SIZE, offsetY, LINE_WIDTH);
        shapeRenderer.rectLine(offsetX + LINE_RECT_SIZE, offsetY - LINE_HWIDTH, offsetX + LINE_RECT_SIZE, offsetY + LINE_EDGE_SIZE, LINE_WIDTH);
        // top right
        shapeRenderer.rectLine(offsetX + LINE_RECT_SIZE + LINE_HWIDTH, offsetY + LINE_RECT_SIZE, offsetX + LINE_RECT_SIZE - LINE_EDGE_SIZE, offsetY + LINE_RECT_SIZE, LINE_WIDTH);
        shapeRenderer.rectLine(offsetX + LINE_RECT_SIZE, offsetY + LINE_RECT_SIZE + LINE_HWIDTH, offsetX + LINE_RECT_SIZE, offsetY + LINE_RECT_SIZE - LINE_EDGE_SIZE, LINE_WIDTH);
    }
}
