package de.bsautermeister.bomb.service;

public class NoopAdService implements AdService {
    @Override
    public boolean isSupported() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void initialize() { }

    @Override
    public void load() { }

    @Override
    public boolean show(RewardCallback callback) {
        return false;
    }

    @Override
    public boolean isPrivacyOptionsRequired() {
        return false;
    }

    @Override
    public void showPrivacyConsentForm() {

    }

    @Override
    public void resetConsentForTesting() {

    }
}
