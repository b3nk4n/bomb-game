package de.bsautermeister.bomb.service;

import static com.google.android.ump.ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA;
import static com.google.android.ump.ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.badlogic.gdx.utils.Logger;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

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
    private final boolean debug;
    private final String testDeviceHashedId;
    private final String adUnitId;
    private final AtomicBoolean wasRewarded = new AtomicBoolean(false);

    /**
     * User consent for IAB Europe TCF framework.
     * For more information, see:
     * <a href="https://support.google.com/admob/answer/10207733">European regulations messages</a>.
     * Or for developer documentation, see:
     * <a href="https://developers.google.com/admob/android/privacy">AdMob UMP</a>.
     */
    private ConsentInformation consentInformation;

    /**
     * Indicates whether the ad initialization has been called already.
     * <p>
     * Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
     */

    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    public AdMobService(Activity activity, final String adUnitId, boolean debug, String testDeviceHashedId) {
        this.activity = activity;
        this.debug = debug;
        this.testDeviceHashedId = testDeviceHashedId;

        this.adUnitId = debug
                ? Ads.TEST_REWARDED_VIDEO_AD_UNIT_ID
                : adUnitId;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public boolean isReady() {
        return rewardedAd != null && consentInformation.canRequestAds();
    }

    /**
     * Initializes the consent information and the mobile ads SDK.
     * <p>
     * This init method bust be called within {@code onCreate}, otherwise it might (currently) end
     * up with: java.lang.IllegalStateException: Method must be call on main thread.
     */
    @Override
    public void initialize() {
        ConsentRequestParameters params = buildConsentRequestParams(
                activity, debug, testDeviceHashedId);

        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> {
                    // Consent update required: load and show consent form
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            activity,
                            loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    LOG.error(String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    initializeMobileAdsSdk();
                                }
                            }
                    );
                },
                requestConsentError -> {
                    // Consent gathering failed
                    LOG.error(String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk();
        }
    }

    private static ConsentRequestParameters buildConsentRequestParams(Context context,
                                                                      boolean debug,
                                                                      String testDeviceHashedId) {
        if (!debug) {
            return new ConsentRequestParameters.Builder().build();
        }

        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(context)
                .addTestDeviceHashedId(testDeviceHashedId)
                .setDebugGeography(DEBUG_GEOGRAPHY_EEA)
                .build();

        return new ConsentRequestParameters.Builder()
                .setConsentDebugSettings(debugSettings)
                .build();
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(activity, initializationStatus -> LOG.info("Init mobile ads completed"));
    }

    @Override
    public void load() {
        if (!consentInformation.canRequestAds()) {
            return;
        }

        wasRewarded.set(false);
        activity.runOnUiThread(() -> RewardedAd.load(
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
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }
                }));
    }

    @Override
    public boolean show(final RewardCallback rewardCallback) {
        if (isReady()) {
            activity.runOnUiThread(() -> {
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
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        LOG.error("Failed to show fullscreen content: " + adError.getMessage());
                        rewardedAd = null;
                        rewardCallback.canceled();
                    }

                    @Override
                    public void onAdClicked() {
                        LOG.debug("Ad was clicked");
                    }

                    @Override
                    public void onAdImpression() {
                        LOG.debug("Ad recorded an impression");
                    }
                });
                rewardedAd.show(
                        activity,
                        rewardItem -> {
                            LOG.info(String.format(
                                    Locale.ROOT, "Rewarded with %d %s",
                                    rewardItem.getAmount(), rewardItem.getType()));

                            wasRewarded.set(true);
                            rewardCallback.rewarded(rewardItem.getType(), rewardItem.getAmount());
                        });
            });
            return true;
        }

        LOG.info("The rewarded ad is not ready yet");
        return false;
    }

    @Override
    public boolean isPrivacyOptionsRequired() {
        return consentInformation.getPrivacyOptionsRequirementStatus() == REQUIRED;
    }

    @Override
    public void showPrivacyConsentForm() {
        activity.runOnUiThread(() ->
                UserMessagingPlatform.showPrivacyOptionsForm(
                        activity,
                        formError -> {
                            if (formError != null) {
                                LOG.error(String.format("%s: %s",
                                        formError.getErrorCode(),
                                        formError.getMessage()));
                            }
                        }
                ));
    }

    @Override
    public void resetConsentForTesting() {
        // Caution: This method is intended to be used for testing purposes only.
        // You shouldn't call reset() in production code.
        // See: https://developers.google.com/admob/android/privacy#reset_consent_state
        consentInformation.reset();
    }
}
