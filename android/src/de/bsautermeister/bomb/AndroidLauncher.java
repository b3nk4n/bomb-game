package de.bsautermeister.bomb;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.bsautermeister.bomb.service.AdMobService;
import de.bsautermeister.bomb.service.AdService;
import de.bsautermeister.bomb.service.PlayStoreRateService;
import de.bsautermeister.bomb.service.ServiceKeys;
import de.golfgl.gdxgamesvcs.GpgsClient;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;

		GpgsClient gpgsClient = new GpgsClient();
		gpgsClient.initialize(this, false);

		String adUnitId = Cfg.DEBUG_ADS
				? ServiceKeys.Ads.TEST_REWARDED_VIDEO_AD_UNIT_ID
				: ServiceKeys.Ads.REWARDED_EXTRA_LIFE_AD_UNIT_ID;
		AdService adService = new AdMobService(this, adUnitId);

		initialize(new BombGame(
				gpgsClient,
				new PlayStoreRateService(this),
				adService
		), config);
	}
}
