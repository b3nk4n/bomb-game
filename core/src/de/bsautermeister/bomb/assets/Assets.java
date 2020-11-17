package de.bsautermeister.bomb.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public interface Assets {
    interface Atlas {
        AssetDescriptor<TextureAtlas> LOADING =
                new AssetDescriptor<>("loading/loading.atlas", TextureAtlas.class);
        AssetDescriptor<TextureAtlas> GAME =
                new AssetDescriptor<>("game/game.atlas", TextureAtlas.class);
        AssetDescriptor<TextureAtlas> UI =
                new AssetDescriptor<>("ui/ui.atlas", TextureAtlas.class);
    }

    AssetDescriptor[] ALL = {
            Atlas.LOADING, Atlas. GAME
    };
}
