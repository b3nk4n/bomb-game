package de.bsautermeister.bomb.service;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.badlogic.gdx.utils.Logger;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import de.bsautermeister.bomb.Cfg;

/**
 * For further information and FAQ, please refer to
 * <a href="https://developers.google.com/admob/android/rewarded-fullscreen?hl=en-US">AdMob</a>.
 */
public class AdMobService implements AdService {

    private static final Logger LOG = new Logger(AdMobService.class.getSimpleName(), Cfg.LOG_LEVEL);

    private RewardedAd rewardedAd;

    private final Activity activity;
    private final String adUnitId;
    private final AtomicBoolean wasRewarded = new AtomicBoolean(false);

    public AdMobService(Activity activity, final String adUnitId) {
        this.activity = activity;
        this.adUnitId = adUnitId;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public boolean isReady() {
        return rewardedAd != null;
    }

    @Override
    public void load() {
        wasRewarded.set(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RewardedAd.load(
                        activity,
                        adUnitId,
                        new AdRequest.Builder().build(),
                        new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                rewardedAd = null;

                                LOG.error("Failed to load ad: " + loadAdError.getMessage());
                            }

                            @Override
                            public void onAdLoaded(RewardedAd ad) {
                                rewardedAd = ad;
                            }
                        });
            }
        });
    }

    @Override
    public boolean show(final RewardCallback rewardCallback) {
        if (isReady()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {
                            LOG.debug("Full screen ad showed");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            LOG.debug("Full screen ad dismissed");

                            // unset so that same ad is not shown a second time
                            rewardedAd = null;

                            if (!wasRewarded.get()) {
                                rewardCallback.canceled();
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            LOG.error("Failed to show fullscreen content: " + adError.getMessage());
                            rewardCallback.canceled();
                        }
                    });
                    rewardedAd.show(
                            activity,
                            new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    LOG.info(String.format(
                                            Locale.ROOT, "Rewarded with %d %s",
                                            rewardItem.getAmount(), rewardItem.getType()));

                                    wasRewarded.set(true);
                                    rewardCallback.rewarded(rewardItem.getType(), rewardItem.getAmount());
                                }
                            });
                }
            });
            return true;
        }

        LOG.info("The rewarded ad is not ready yet");
        return false;
    }
}
