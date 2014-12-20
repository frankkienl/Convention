package nl.frankkie.convention;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

    //GCM
    public static final String GCM_PROJECT_ID = "445536900909";

    /**
     * Gives GCM Reg ID or "" on error (or not registered yet)
     *
     * @param context
     * @return GCM Reg ID or "" on error (or not registered yet)
     */
    public static String gcmGetRegId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            if (prefs.getInt("gcm_app_version", Integer.MIN_VALUE) !=
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode) {
                return "";
            }
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(context.getString(R.string.app_name), "This app is apparently not installed. Weird.\n" + nnfe);
            return "";
        }
        return prefs.getString("gcm_reg_id", "");
    }

    public static void gcmRegister(Context context) {
        GcmRegisterTask task = new GcmRegisterTask(context);
        task.execute();
    }

    /**
     * This is called from the AsyncTask started in gcmRegister.
     *
     * @param context
     * @param regId
     */
    public static void gcmSetRegId(Context context, String regId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("gmc_reg_id", regId);
        try {
            editor.putInt("gcm_app_version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(context.getString(R.string.app_name), "This app is apparently not installed. Weird.\n" + nnfe);
        }
        editor.apply();
    }

    /**
     * This is called from the AsyncTask started in gcmRegister
     *
     * @param context
     * @param regId
     * @throws java.io.IOException will be handled in AsyncTask :P
     */
    public static void gcmSendRegIdToServer(Context context, String regId) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        String postData = "regId=" + regId + "&deviceName=" + URLEncoder.encode(getDeviceName(),"UTF-8");
        try {
            //For rant, see nl.frankkie.convention.sync.ConventionSyncAdapter
            URL url = new URL("http://frankkie.nl/pony/hwcon/gcmregister.php");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            //http://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true); //output, because post-data
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //urlConnection.setRequestProperty("charset","utf-8");
            urlConnection.setRequestProperty("Content-Length", "" + postData.getBytes().length); //simple int to String casting.
            urlConnection.setUseCaches(false);
            urlConnection.connect();
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            pw = new PrintWriter(os);
            pw.print(postData);
            pw.flush();
            pw.close();
            InputStream is = urlConnection.getInputStream();
            StringBuilder sb = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line).append("\n");
            }
            if (sb.length() == 0) {
                Log.e(context.getString(R.string.app_name), "gcmSendRegId: Empty Response");
            } else {
                Log.e(context.getString(R.string.app_name), "gcmSendRegId response:\n" + sb.toString());
                if (!"ok".equals(sb.toString())){
                    //Server should return 'ok' onSucces, something else otherwise
                    //So some error has occured
                    throw new IOException("gcmSendRegId: Server did not send 'ok', something must be wrong.");
                }
            }
        } catch (IOException ioe) {
            Log.e(context.getString(R.string.app_name), "gcmSendRegId: IOException");
            ioe.printStackTrace();
            throw new IOException(ioe); //throw to method that called this.
        } finally {
            //*cough* boilerplate *cough*
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(context.getString(R.string.app_name), "Error closing BufferedReader", e);
                    e.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
    }

    public static String getDeviceName() {
        String ans = Build.BRAND + " " + Build.MODEL + " ( Android " + Build.VERSION.RELEASE + ", SDK " + Build.VERSION.SDK_INT + " )";
        return ans;
    }

    public static class GcmRegisterTask extends AsyncTask<Void, Void, Void> {

        Context context;

        public GcmRegisterTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String regId;
            try {
                GoogleCloudMessaging gcm;
                gcm = GoogleCloudMessaging.getInstance(context);
                regId = gcm.register(GCM_PROJECT_ID);
                gcmSendRegIdToServer(context, regId);
                gcmSetRegId(context, regId);
            } catch (IOException ioe) {
                Log.e(context.getString(R.string.app_name), "Error, cannot register for GCM\n" + ioe);
                ioe.printStackTrace();
            }
            return null;
        }
    }
}
