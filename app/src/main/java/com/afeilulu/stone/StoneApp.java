package com.afeilulu.stone;

import android.app.Application;
import android.content.Context;

/**
 * Created by chen on 7/16/14.
 */
public class StoneApp extends Application {

    private static StoneApp instance;

    public StoneApp() {
        super();
        instance = this;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
