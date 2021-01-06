package de.bsautermeister.bomb.audio;

import com.badlogic.gdx.audio.Sound;

/**
 * A small wrapper around {@link com.badlogic.gdx.audio.Sound} to interact with a sound effect
 * while it is running, without having to keep track of the reference ID.
 */
public class LoopSound {
    private final Sound sound;
    private long id = -1;

    public LoopSound(Sound sound) {
        this.sound = sound;
    }

    public void loop(float volume) {
        if (isPlaying()) {
            sound.setVolume(id, volume);
        } else {
            id = sound.loop(volume);
        }
    }

    public  void stop() {
        if (isPlaying()) {
            sound.stop(id);
            id = -1;
        }
    }

    public boolean isPlaying() {
        return id != -1;
    }
}
