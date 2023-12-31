package de.bsautermeister.bomb.service;

import de.golfgl.gdxgamesvcs.IGameServiceIdMapper;

public class GpgsAchievementMapper implements IGameServiceIdMapper<String> {
    @Override
    public String mapToGsId(String key) {
        if (ServiceKeys.Achievements.EXPLORER_DEPTH_1000.equals(key)) {
            return "CgkIqLCqoc8VEAIQBQ";
        }
        if (ServiceKeys.Achievements.HERO_DEPTH_2500.equals(key)) {
            return "CgkIqLCqoc8VEAIQBg";
        }
        if (ServiceKeys.Achievements.Incremental.SURVIVOR_25_250.equals(key)) {
            return "CgkIqLCqoc8VEAIQAw";
        }
        if (ServiceKeys.Achievements.Incremental.TRUE_SURVIVOR_50_500.equals(key)) {
            return "CgkIqLCqoc8VEAIQBA";
        }

        return null;
    }
}
