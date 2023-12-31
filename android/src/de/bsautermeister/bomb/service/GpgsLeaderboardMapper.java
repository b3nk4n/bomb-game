package de.bsautermeister.bomb.service;

import de.golfgl.gdxgamesvcs.IGameServiceIdMapper;

public class GpgsLeaderboardMapper implements IGameServiceIdMapper<String> {
    @Override
    public String mapToGsId(String key) {
        if (ServiceKeys.Scores.MAX_DEPTH.equals(key)) {
            return "CgkIqLCqoc8VEAIQAg";
        }

        return null;
    }
}
