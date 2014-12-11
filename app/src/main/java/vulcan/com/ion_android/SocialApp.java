package vulcan.com.ion_android;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by jayl on 12/4/14.
 */
public class SocialApp extends Application {
    private static SocialApp theSingleton;


    public static SocialApp getInstance(){
        return theSingleton;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        theSingleton = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
