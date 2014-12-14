package nl.frankkie.convention;

import android.app.Activity;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fbouwens on 10-12-14.
 */
public class Util {
    public static final int navigationDrawerIntentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP;

    public static final String DATE_FORMAT = "E, HH:mm"; //example: Sunday, 16:30
    public static SimpleDateFormat displayDataFormat = new SimpleDateFormat(DATE_FORMAT);

    public static String getDataTimeString(long timestamp) {
        //*1000, because
        // http://www.onlineconversion.com/unix_time.htm
        // uses SECONDS from 1970, but Date uses MILLISECONDS from 1970
        return displayDataFormat.format(new Date(timestamp * 1000));
    }

    public static void navigateFromNavDrawer(Activity from, Intent to){
        //Inspired by NavUtils.navigateToUp
        //see: https://android.googlesource.com/platform/frameworks/support/+/refs/heads/master/v4/java/android/support/v4/app/NavUtils.java
        to.addFlags(navigationDrawerIntentFlags);
        from.startActivity(to);
        from.finish();
    }

    public static void navigateFromNavDrawer(Activity thisAct, int position) {
        switch (position) {
            case 0: {
                if (!(thisAct instanceof ScheduleActivity))
                navigateFromNavDrawer(thisAct,new Intent(thisAct,ScheduleActivity.class));
                break;
            }
            case 1: {
                //Don't restart current activity
                if (!(thisAct instanceof EventListActivity))
                //Not instanceof, see: http://stackoverflow.com/questions/9068150/best-way-to-negate-an-instanceof
                navigateFromNavDrawer(thisAct,new Intent(thisAct, EventListActivity.class));
                break;
            }
            case 2: {
                if (!(thisAct instanceof MapActivity))
                    navigateFromNavDrawer(thisAct,new Intent(thisAct, MapActivity.class));
                break;
            }
            case 3: {
                if (!(thisAct instanceof AboutActivity))
                    navigateFromNavDrawer(thisAct,new Intent(thisAct, AboutActivity.class));
                break;
            }
        }
    }
}
