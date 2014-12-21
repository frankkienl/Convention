package nl.frankkie.convention;

import android.app.Application;

/**
 * Created by FrankkieNL on 21-12-2014.
 */
@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        formUri = "", //SECRET to be set later
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin = "", //SECRET to be set later
        formUriBasicAuthPassword = "" //SECRET to be set later
)
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate(); //To change body of generated methods, choose Tools | Templates.

        //Crash reports
        ACRA.init(this);
        //Set the Secret Password
        SecretAcraKey.providePassword(this);

        //DEBUG
        //Debug.startMethodTracing("bronylivewallpaper.debugtrace");
    }
}