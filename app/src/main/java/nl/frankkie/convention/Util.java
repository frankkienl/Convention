package nl.frankkie.convention;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

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

    public static void navigateFromNavDrawer(Activity from, Intent to) {
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
                    navigateFromNavDrawer(thisAct, new Intent(thisAct, ScheduleActivity.class));
                break;
            }
            case 1: {
                //Don't restart current activity
                if (!(thisAct instanceof EventListActivity))
                    //Not instanceof, see: http://stackoverflow.com/questions/9068150/best-way-to-negate-an-instanceof
                    navigateFromNavDrawer(thisAct, new Intent(thisAct, EventListActivity.class));
                break;
            }
            case 2: {
                if (!(thisAct instanceof MapActivity))
                    navigateFromNavDrawer(thisAct, new Intent(thisAct, MapActivity.class));
                break;
            }
            case 3: {
                if (!(thisAct instanceof AboutActivity))
                    navigateFromNavDrawer(thisAct, new Intent(thisAct, AboutActivity.class));
                break;
            }
        }
    }

    public static Account createDummyAccount(Context context) {
        //TODO: Change domain when using for a different convention
        Account account = new Account("dummyaccount", "nl.frankkie.convention");
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        boolean success = accountManager.addAccountExplicitly(account, null, null);
        if (!success) {
            Log.e(context.getString(R.string.app_name), "Cannot create account for Sync.");
        }
        return account;
    }

    public static void syncData(Context context){
        //Create Account needed for SyncAdapter
        Account acc = createDummyAccount(context);
        //Sync
        Bundle syncBundle = new Bundle();
        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); //as in: run NOW.
        ContentResolver.requestSync(acc, "nl.frankkie.convention", syncBundle);
    }

    public static void showNotification(Context context, String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(message);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        builder.setVibrate(new long[]{50, 250, 50, 250}); //delay,vibrate,delay,etc.
        //http://stackoverflow.com/questions/8801122/set-notification-sound-from-assets-folder
        //The docs are not clear about how to add sound, StackOverflow to the rescue!
        builder.setSound(Uri.parse("android.resource://nl.frankkie.convention/raw/yay"));
        builder.setSmallIcon(R.drawable.ic_stat_amber_notification);
        Intent i = new Intent(context,EventListActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,i,0);
        builder.setContentIntent(pi);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1,builder.build());
    }
}
