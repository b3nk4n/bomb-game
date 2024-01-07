package de.bsautermeister.bomb;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIApplication;

import de.bsautermeister.bomb.service.NoopAdService;
import de.bsautermeister.bomb.service.NoopRateService;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = true;
        config.orientationPortrait = false;
        return new IOSApplication(new BombGame(
                new GameEnv() {
                    @Override
                    public String getVersion() {
                        NSDictionary<NSString, ?> infoDictionary = NSBundle.getMainBundle().getInfoDictionary();
                        return infoDictionary.get(new NSString("CFBundleShortVersionString")).toString();
                    }
                },
                new NoGameServiceClient(), // TODO https://github.com/MrStahlfelge/gdx-gamesvcs/wiki/Apple-Game-Center
                new NoopRateService(),
                new NoopAdService()
        ), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}
