package de.bsautermeister.bomb.screens.game.tutorial;

import de.bsautermeister.bomb.objects.Controllable;

public class TutorialController implements Controllable {

    private final TutorialStep[] steps = new TutorialStep[] {
            new TutorialStep("To move...", "OTHER", 2f, 2f, true),
            new TutorialStep("LEFT", "LEFT", 0.5f, 0.25f, false),
            new TutorialStep("RIGHT", "RIGHT", 0.5f, 0.25f, false),
            new TutorialStep("JUMP", "JUMP", 0.5f, 0.25f, false),
            new TutorialStep("Fall as deep as possible!", "GOAL", 0.5f, 5f, true),
            new TutorialStep("And stay alive!", "OTHER", 0.5f, 3f, true),
    };

    private int currentStep;

    private float delayTimer;
    private float completedTimer;
    private boolean currentCompleted;
    private boolean justCompletedFlag;

    public TutorialController() {
        reset();
    }

    private void reset() {
        delayTimer = getCurrentStep().getDelay();
        completedTimer = getCurrentStep().getCompletedDelay();
        currentCompleted = getCurrentStep().isAutoComplete();
    }

    public void skip() {
        currentStep = steps.length;
    }

    @Override
    public void control(boolean up, boolean left, boolean right) {
        if (!isVisible() || isFinished()) {
            return;
        }

        TutorialStep step = getCurrentStep();
        if (!step.isAutoComplete()) {
            if (left && "LEFT".equals(step.getTag())
            || right && "RIGHT".equals(step.getTag())
            || up && "JUMP".equals(step.getTag())) {
                currentCompleted = true;
            }
        }
    }

    public void update(float delta) {
        delayTimer -= delta;
        if (currentCompleted && isVisible()) {
            completedTimer -= delta;
        }

        if (completedTimer <= 0 && currentCompleted) {
            currentStep++;
            if (!isFinished()) {
                reset();
            } else {
                currentCompleted = false;
                justCompletedFlag = true;
            }
        }
    }

    public TutorialStep getCurrentStep() {
        return steps[currentStep];
    }

    public boolean isFinished() {
        return currentStep >= steps.length;
    }

    public boolean justCompleted() {
        if (justCompletedFlag) {
            justCompletedFlag = false;
            return true;
        }
        return false;
    }

    public boolean isVisible() {
        return delayTimer <= 0f;
    }
}
