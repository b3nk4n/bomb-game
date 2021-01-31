package de.bsautermeister.bomb.screens.game.tutorial;

public class TutorialStep {
    private final String text;
    private final String tag;
    private final float delay;
    private final float completedDelay;
    private final boolean autoComplete;

    public TutorialStep(String text, String tag, float delay, float completedDelay, boolean autoComplete) {
        this.text = text;
        this.tag = tag;
        this.delay = delay;
        this.completedDelay = completedDelay;
        this.autoComplete = autoComplete;
    }

    public String getText() {
        return text;
    }

    public String getTag() {
        return tag;
    }

    public float getDelay() {
        return delay;
    }

    public float getCompletedDelay() {
        return completedDelay;
    }

    public boolean isAutoComplete() {
        return autoComplete;
    }
}
