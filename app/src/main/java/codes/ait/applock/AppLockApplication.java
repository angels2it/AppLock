package codes.ait.applock;

import android.app.Application;

import java.util.HashMap;

import codes.ait.applock.R;

/**
 * Created by amitshekhar on 01/05/15.
 */
public class AppLockApplication extends Application {

    public static final String PROPERTY_ID = "UA-118046125-3";
    private static AppLockApplication appInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER // Tracker used by all the apps from a company. eg:
        // roll-up tracking.
    }

    /**
     * Get an instance of application
     *
     * @return
     */
    public static synchronized AppLockApplication getInstance() {
        return appInstance;
    }
}
