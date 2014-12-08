package nl.frankkie.convention.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by FrankkieNL on 6-12-2014.
 */
public class ConventionSyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver mContentResolver;
    public ConventionSyncAdapter(Context c, boolean autoInit){
        super(c,autoInit);
        mContentResolver = c.getContentResolver();
    }

    public ConventionSyncAdapter(Context c, boolean autoInit, boolean allowParallel){
        super(c,autoInit,allowParallel);
        mContentResolver = c.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //TODO: sync data from server to database
        Log.d("Convention","SyncAdapter: onPerformSync");
        //<editor-fold desc="boring http downloading code">
        String json = null;
        HttpURLConnection urlConnection = null;
        BufferedReader br = null;

        try{
            //CHANGE THIS URL WHEN USING FOR OTHER CONVENTION
            URL url = new URL("http://frankkie.nl/pony/hwcon/convention_data.json");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //<rant>
            //Read it with streams, using much boilerplate,
            //because apparently that is more awesome compared to just using the Apache HttpClient Libs.
            //Srsly, this is 2014, we have libs to do this for us now.
            //https://github.com/udacity/Sunshine/blob/6.10-update-map-intent/app/src/main/java/com/example/android/sunshine/app/sync/SunshineSyncAdapter.java#L118
            //</rant>
            InputStream is = urlConnection.getInputStream();
            if (is == null){
                //Apparently there is no inputstream.
                //We're done here
                return;
            }

            //Why does Sunshine use a StringBuffer instead of a StringBuilder?
            //A StringBuffer is ThreadSafe (Synchronised) but has worse performance.
            //There is no need to use a ThreadSafe StringBuffer here, this sync-option will never be called multiple times from other threads.
            //Because of 'android:allowParallelSyncs="false"' in R.xml.syncadapter
            //See:
            //https://github.com/udacity/Sunshine/blob/6.10-update-map-intent/app/src/main/java/com/example/android/sunshine/app/sync/SunshineSyncAdapter.java#L120
            //http://stackoverflow.com/questions/355089/stringbuilder-and-stringbuffer
            StringBuilder sb = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while (true){
                line = br.readLine();
                if (line == null){break;}
                //Chained append is better than concat
                //https://github.com/udacity/Sunshine/blob/6.10-update-map-intent/app/src/main/java/com/example/android/sunshine/app/sync/SunshineSyncAdapter.java#L132
                sb.append(line).append("\n");
            }
            if (sb.length() == 0){
                //empty
                return;
            }

            json = sb.toString();
        } catch (IOException e) {
            Log.e("Convention","Error while downloading convention data ", e);
            return;
        } finally {
            if (urlConnection!=null){
                urlConnection.disconnect();
            }
            if (br != null){
                //*cough* boilerplate *cough*
                try {br.close();}catch(IOException e) {Log.e("Convention","Error closing BufferedReader",e);}
            }
        }
        //</editor-fold>

        //Time for JSON Parsing
        

    }
}
