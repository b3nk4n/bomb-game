package de.bsautermeister.bomb.service;

public interface AdService {

    interface RewardCallback {
        /**
         * This callback is called when the reward-video timer reaches zero, but before the
         * user resumes to the game. The game is inactive during this time, so no render()
         * method is called.
         * @param type The reward type.
         * @param amount The reward amount.
         */
        void rewarded(String type, int amount);

        void canceled();
    }

    boolean isSupported();
    boolean isReady();
    void load();
    boolean show(RewardCallback callback);
}
