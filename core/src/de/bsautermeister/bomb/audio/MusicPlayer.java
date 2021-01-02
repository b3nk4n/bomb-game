package de.bsautermeister.bomb.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.serializers.KryoExternalSerializer;

public class MusicPlayer implements KryoExternalSerializer, Disposable {
    private static final Logger LOG = new Logger(MusicPlayer.class.getSimpleName(), Cfg.LOG_LEVEL);

    private final static float VOLUME_CHANGE_IN_SECONDS = 2.0f;
    public final static float MAX_VOLUME = 0.25f;

    private float masterVolume = 1.0f;
    private float currentVolume = 0.0f;
    private float targetVolume = MAX_VOLUME;

    private Music music;
    private String selectedFilePath;

    private float smoothLoopPosition = Float.MAX_VALUE;

    private Array<Music> fadeOutAndDisposeQueue = new Array<>();

    public void selectSmoothLoopedMusic(String filePath, float smoothLoopPosition) {
        LOG.debug("Select: " + filePath);
        if (music != null) {
            fadeOutAndDisposeQueue.add(music);
        }

        FileHandle fileHandle = Gdx.files.internal(filePath);
        music = Gdx.audio.newMusic(fileHandle);
        this.smoothLoopPosition = smoothLoopPosition;
        selectedFilePath = filePath;
    }

    public void update(float delta) {
        if (music != null) {
            if (targetVolume != currentVolume) {
                float diff = targetVolume - currentVolume;

                if (diff > 0) {
                    currentVolume += delta / VOLUME_CHANGE_IN_SECONDS;
                    currentVolume = Math.min(targetVolume, currentVolume);
                } else {
                    currentVolume -= delta / VOLUME_CHANGE_IN_SECONDS;
                    currentVolume = Math.max(targetVolume, currentVolume);
                }
            }

            music.setVolume(currentVolume * masterVolume);

            if (smoothLoopPosition <= music.getPosition()) {
                selectSmoothLoopedMusic(selectedFilePath, smoothLoopPosition);
                playFromBeginning();
            }
        }

        if (fadeOutAndDisposeQueue.size > 0) {
            for (Music fadeOutMusic : fadeOutAndDisposeQueue) {
                float newVolume = fadeOutMusic.getVolume() - delta;
                if (newVolume > 0) {
                    fadeOutMusic.setVolume(newVolume * masterVolume);
                } else {
                    fadeOutMusic.dispose();
                    fadeOutAndDisposeQueue.removeValue(fadeOutMusic, true);
                }
            }
        }
    }

    public void playFromBeginning() {
        if (music == null) {
            return;
        }

        LOG.debug("Play music from beginning");
        music.stop();
        music.play();
    }

    public void resumeOrPlay() {
        if (music == null) {
            return;
        }

        music.pause();
        music.play();
    }

    public boolean isPlaying() {
        if (music == null) {
            return false;
        }

        return music.isPlaying();
    }

    public void pause() {
        if (music == null) {
            return;
        }

        LOG.debug("Pause music");
        music.pause();
    }

    public void stop() {
        if (music == null) {
            return;
        }

        LOG.debug("Stop music");
        music.stop();
    }

    public void fadeOutStop() {
        if (music == null) {
            return;
        }

        fadeOutAndDisposeQueue.add(music);
        music = null;
    }

    public void setMasterVolume(float masterVolume) {
        this.masterVolume = masterVolume;
    }

    public void setVolume(float volume, boolean immediate) {
        targetVolume = volume;

        if (immediate) {
            currentVolume = volume;
        }
    }

    public float getVolume() {
        return currentVolume;
    }

    public boolean isSelected(String filePath) {
        return selectedFilePath != null && selectedFilePath.equals(filePath);
    }

    @Override
    public void dispose() {
        if (music != null) {
            music.dispose();
        }
    }

    @Override
    public void write(Kryo kryo, Output output) {
        LOG.debug("Write music state");
        if (music == null) {
            output.writeFloat(-1f);
            return;
        }
        output.writeFloat(music.getPosition());
        output.writeString(selectedFilePath);
        output.writeFloat(smoothLoopPosition);
        output.writeFloat(currentVolume);
        output.writeFloat(targetVolume);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        LOG.debug("Read music state");
        float pos = input.readFloat();
        if (pos == -1f) {
            return;
        }
        String musicPath = input.readString();
        float smoothLoopPosition = input.readFloat();
        currentVolume = input.readFloat();
        targetVolume = input.readFloat();
        if (pos > 0) {
            // at least on Desktop it is required to call play first
            // before seeking the audio position
            selectSmoothLoopedMusic(musicPath, smoothLoopPosition);
            music.setVolume(currentVolume);
            music.play();
            music.setPosition(pos);
        }
    }
}
