package ganainy.dev.gymmasters;

import android.app.Application;

import ganainy.dev.gymmasters.utils.AuthUtils;

public class MyApp extends Application {

    private static final String TAG = "MyAppTag";

    @Override
    public void onCreate() {
        super.onCreate();
        AuthUtils.setLoggedUser(getApplicationContext());

    }

}
