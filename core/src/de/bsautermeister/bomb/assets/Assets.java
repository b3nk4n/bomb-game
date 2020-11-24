package de.bsautermeister.bomb.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public interface Assets {
    interface Atlas {
        AssetDescriptor<TextureAtlas> LOADING =
                new AssetDescriptor<>("loading/loading.atlas", TextureAtlas.class);
        AssetDescriptor<TextureAtlas> GAME =
                new AssetDescriptor<>("game/game.atlas", TextureAtlas.class);
        AssetDescriptor<TextureAtlas> UI =
                new AssetDescriptor<>("ui/ui.atlas", TextureAtlas.class);
    }

    interface Skins {
        AssetDescriptor<Skin> UI =
                new AssetDescriptor<>("ui/ui.skin", Skin.class);
    }

    AssetDescriptor[] ALL = {
            Atlas.LOADING, Atlas. GAME, Atlas.UI,
            Skins.UI
    };
}
