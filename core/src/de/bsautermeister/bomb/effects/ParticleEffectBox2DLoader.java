package de.bsautermeister.bomb.effects;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class ParticleEffectBox2DLoader extends SynchronousAssetLoader<ParticleEffectBox2D, ParticleEffectBox2DLoader.ParticleEffectBox2DParameter> {
    public ParticleEffectBox2DLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public ParticleEffectBox2D load (AssetManager am, String fileName, FileHandle file, ParticleEffectBox2DParameter param) {
        ParticleEffectBox2D effect = new ParticleEffectBox2D(param.world);
        if (param != null && param.atlasFile != null)
            effect.load(file, am.get(param.atlasFile, TextureAtlas.class), param.atlasPrefix);
        else if (param != null && param.imagesDir != null)
            effect.load(file, param.imagesDir);
        else
            effect.load(file, file.parent());
        return effect;
    }

    @Override
    public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, ParticleEffectBox2DParameter param) {
        Array<AssetDescriptor> deps = null;
        if (param != null && param.atlasFile != null) {
            deps = new Array();
            deps.add(new AssetDescriptor<>(param.atlasFile, TextureAtlas.class));
        }
        return deps;
    }

    /** Parameter to be passed to {@link AssetManager#load(String, Class, AssetLoaderParameters)} if additional configuration is
     * necessary for the {@link ParticleEffect}. */
    public static class ParticleEffectBox2DParameter extends AssetLoaderParameters<ParticleEffectBox2D> {
        public final World world;
        /** Atlas file name. */
        public final String atlasFile;
        /** Optional prefix to image names **/
        public String atlasPrefix;
        /** Image directory. */
        public FileHandle imagesDir;

        public ParticleEffectBox2DParameter(String atlasFile, World world) {
            this.atlasFile = atlasFile;
            this.world = world;
        }
    }
}
