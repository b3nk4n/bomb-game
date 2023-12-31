package de.bsautermeister.bomb.service;

public interface ServiceKeys {
    interface Achievements {
        String EXPLORER_DEPTH_1000 = "EXPLORER_DEPTH_1000";
        String HERO_DEPTH_2500 = "HERO_DEPTH_2500";

        interface Incremental {
            String SURVIVOR_25_250 = "SURVIVOR_25_250";
            String TRUE_SURVIVOR_50_500 = "TRUE_SURVIVOR_50_500";
        }
    }

    interface Scores {
        String MAX_DEPTH = "MAX_DEPTH";
    }
}
