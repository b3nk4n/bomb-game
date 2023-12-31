package de.bsautermeister.bomb;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.bsautermeister.bomb.service.AdMobService;
import de.bsautermeister.bomb.service.AdService;
import de.bsautermeister.bomb.service.Ads;
import de.bsautermeister.bomb.service.GpgsAchievementMapper;
import de.bsautermeister.bomb.service.GpgsLeaderboardMapper;
import de.bsautermeister.bomb.service.PlayStoreRateService;
import de.golfgl.gdxgamesvcs.GpgsClient;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;

		GpgsClient gpgsClient = new GpgsClient()
				.setGpgsLeaderboardIdMapper(new GpgsLeaderboardMapper())
				.setGpgsAchievementIdMapper(new GpgsAchievementMapper())
				.initialize(this, false);

		String adUnitId = Cfg.DEBUG_ADS
				? Ads.TEST_REWARDED_VIDEO_AD_UNIT_ID
				: Ads.REWARDED_EXTRA_LIFE_AD_UNIT_ID;
		AdService adService = new AdMobService(this, adUnitId);

		initialize(new BombGame(
				new AndroidGameEnv(),
				gpgsClient,
				new PlayStoreRateService(this),
				adService
		), config);
	}
}
