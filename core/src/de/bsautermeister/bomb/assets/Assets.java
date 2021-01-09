package de.bsautermeister.bomb.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.bsautermeister.bomb.effects.ParticleEffectBox2D;
import de.bsautermeister.bomb.effects.ParticleEffectBox2DLoader;

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

        public static AssetDescriptor<ShaderProgram> VIGNETTING =
                new AssetDescriptor<>("shader/vignetting.frag", ShaderProgram.class,
                        usingDefaultVertexShader());

        private static ShaderProgramLoader.ShaderProgramParameter usingDefaultVertexShader() {
            ShaderProgramLoader.ShaderProgramParameter params = new ShaderProgramLoader.ShaderProgramParameter();
            params.vertexFile = "shader/default.vert";
            return params;
        }
    }

    abstract class Effects {
        public enum LazyEffect {
            EXPLOSION_PARTICLES("pfx/explosion-particles.pfx"),
            PLAYER_PARTICLES("pfx/player-particles.pfx");

            String value;

            LazyEffect(String value) {
                this.value = value;
            }
        }

        /**
         * Get the asset descriptor for the box-2d explosion effect for lazy loading, which needs
         * to be lazy loaded because the {@link World} instance is created after the loading screen,
         * and has to be recreated for every game session.
         * @param world The Box2D world instance
         */
        public static AssetDescriptor<ParticleEffectBox2D> lazyEffect(World world, LazyEffect effect) {
            return new AssetDescriptor<>(effect.value, ParticleEffectBox2D.class,
                    new ParticleEffectBox2DLoader.ParticleEffectBox2DParameter(Atlas.GAME.fileName, world));
        }

        public static AssetDescriptor<ParticleEffect> EXPLOSION_GLOW =
                new AssetDescriptor<>("pfx/explosion-glow.pfx", ParticleEffect.class,
                        usingAtlas(Atlas.GAME.fileName));

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

    AssetDescriptor[] PRELOAD = {
            Atlas.LOADING, Atlas.GAME, Atlas.UI,
            Skins.UI,
            ShaderPrograms.BLAST, ShaderPrograms.BLUR, ShaderPrograms.VIGNETTING,
            Effects.EXPLOSION_GLOW,
            Sounds.EXPLOSION, Sounds.HEARTBEAT, Sounds.HIT
    };
}
