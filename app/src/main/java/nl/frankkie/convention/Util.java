package nl.frankkie.convention;

import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fbouwens on 10-12-14.
 */
public class Util {
    public static final int navigationDrawerIntentFlags = Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK;

    public static final String DATE_FORMAT = "E, HH:mm"; //example: Sunday, 16:30
    public static SimpleDateFormat displayDataFormat = new SimpleDateFormat(DATE_FORMAT);

    public static String getDataTimeString(long timestamp) {
        //*1000, because
        // http://www.onlineconversion.com/unix_time.htm
        // uses SECONDS from 1970, but Date uses MILLISECONDS from 1970
        return displayDataFormat.format(new Date(timestamp * 1000));
    }
}
