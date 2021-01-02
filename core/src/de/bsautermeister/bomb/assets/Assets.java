package de.bsautermeister.bomb.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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

    abstract class ShaderPrograms {
        public static AssetDescriptor<ShaderProgram> BLAST =
                new AssetDescriptor<>("shader/multi-blast.frag", ShaderProgram.class,
                        usingDefaultVertexShader());
        public static AssetDescriptor<ShaderProgram> BLUR =
                new AssetDescriptor<>("shader/blur.frag", ShaderProgram.class,
                        usingDefaultVertexShader());

        private static ShaderProgramLoader.ShaderProgramParameter usingDefaultVertexShader() {
            ShaderProgramLoader.ShaderProgramParameter params = new ShaderProgramLoader.ShaderProgramParameter();
            params.vertexFile = "shader/default.vert";
            return params;
        }
    }

    abstract class Effects {
        public static AssetDescriptor<ParticleEffect> EXPLOSION =
                new AssetDescriptor<>("pfx/explosion.pfx", ParticleEffect.class, usingAtlas(Atlas.GAME.fileName));

        private static ParticleEffectLoader.ParticleEffectParameter usingAtlas(String atlasFile) {
            ParticleEffectLoader.ParticleEffectParameter params = new ParticleEffectLoader.ParticleEffectParameter();
            params.atlasFile = atlasFile;
            return params;
        }
    }

    interface Sounds {
        AssetDescriptor<Sound> EXPLOSION =
                new AssetDescriptor<>("sounds/explosion.wav", Sound.class);
        AssetDescriptor<Sound> HEARTBEAT =
                new AssetDescriptor<>("sounds/heartbeat.wav", Sound.class);
        AssetDescriptor<Sound> HIT =
                new AssetDescriptor<>("sounds/hit.wav", Sound.class);
    }

    interface Music {
        String MENU_SONG = "sounds/menu-song.mp3";
        String GAME_SONG = "sounds/game-song.mp3";
    }

    AssetDescriptor[] ALL = {
            Atlas.LOADING, Atlas.GAME, Atlas.UI,
            Skins.UI,
            ShaderPrograms.BLAST, ShaderPrograms.BLUR,
            Effects.EXPLOSION,
            Sounds.EXPLOSION, Sounds.HEARTBEAT, Sounds.HIT
    };
}
