package nl.frankkie.convention.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import nl.frankkie.convention.data.EventContract;

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
            //URL url = new URL("http://frankkie.nl/pony/hwcon/convention_data.json");
            URL url = new URL("https://raw.githubusercontent.com/frankkienl/Convention/master/convention_data.json");
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

        Log.v("Covention",json);

        //Time for JSON Parsing
        try {
            JSONObject data = new JSONObject(json).getJSONObject("data");

            //<editor-fold desc="events">
            JSONArray events = data.getJSONArray("events");
            //Yes thats a word now
            ContentValues[] eventCVs = new ContentValues[events.length()];
            for (int i = 0; i < events.length(); i++){
                JSONObject event = events.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(EventContract.EventEntry._ID,event.getInt("_id"));
                values.put(EventContract.EventEntry.COLUMN_NAME_TITLE,event.getString("title"));
                values.put(EventContract.EventEntry.COLUMN_NAME_DESCRIPTION,event.getString("description"));
                values.put(EventContract.EventEntry.COLUMN_NAME_KEYWORDS,event.getString("keywords"));
                values.put(EventContract.EventEntry.COLUMN_NAME_IMAGE,event.getString("image"));
                values.put(EventContract.EventEntry.COLUMN_NAME_COLOR,event.getString("color"));
                values.put(EventContract.EventEntry.COLUMN_NAME_START_TIME,event.getString("start_time"));
                values.put(EventContract.EventEntry.COLUMN_NAME_END_TIME,event.getString("end_time"));
                values.put(EventContract.EventEntry.COLUMN_NAME_LOCATION_ID,event.getInt("location_id"));
                values.put(EventContract.EventEntry.COLUMN_NAME_SORT_ORDER,event.getInt("sort_order"));
                eventCVs[i] = values;
            }

            //Delete old values
            getContext().getContentResolver().delete(EventContract.EventEntry.CONTENT_URI,null,null); //null deletes all rows
            //Insert new ones
            getContext().getContentResolver().bulkInsert(EventContract.EventEntry.CONTENT_URI,eventCVs);
            //Notify observers
            getContext().getContentResolver().notifyChange(EventContract.EventEntry.CONTENT_URI,null);
            //</editor-fold>

            //<editor-fold desc="speakers">
            JSONArray speakers = data.getJSONArray("speakers");
            ContentValues[] speakerCVs = new ContentValues[speakers.length()];
            for (int i = 0; i < speakers.length(); i++){
                JSONObject speaker = speakers.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(EventContract.SpeakerEntry._ID,speaker.getInt("_id"));
                values.put(EventContract.SpeakerEntry.COLUMN_NAME_NAME,speaker.getString("name"));
                values.put(EventContract.SpeakerEntry.COLUMN_NAME_DESCRIPTION,speaker.getString("description"));
                values.put(EventContract.SpeakerEntry.COLUMN_NAME_IMAGE,speaker.getString("image"));
                values.put(EventContract.SpeakerEntry.COLUMN_NAME_COLOR,speaker.getString("color"));
                speakerCVs[i] = values;
            }
            getContext().getContentResolver().delete(EventContract.SpeakerEntry.CONTENT_URI,null,null);
            getContext().getContentResolver().bulkInsert(EventContract.SpeakerEntry.CONTENT_URI,speakerCVs);
            getContext().getContentResolver().notifyChange(EventContract.SpeakerEntry.CONTENT_URI,null);
            //</editor-fold>

            //<editor-fold desc="locations">
            JSONArray locations = data.getJSONArray("locations");
            ContentValues[] locationCVs = new ContentValues[locations.length()];
            for (int i = 0; i < locations.length(); i++){
                JSONObject location = locations.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(EventContract.LocationEntry._ID,location.getInt("_id"));
                values.put(EventContract.LocationEntry.COLUMN_NAME_NAME,location.getString("name"));
                values.put(EventContract.LocationEntry.COLUMN_NAME_DESCRIPTION,location.getString("description"));
                values.put(EventContract.LocationEntry.COLUMN_NAME_MAP_LOCATION,location.getString("map_location"));
                values.put(EventContract.LocationEntry.COLUMN_NAME_FLOOR,location.getInt("floor"));
                locationCVs[i] = values;
            }
            getContext().getContentResolver().delete(EventContract.LocationEntry.CONTENT_URI,null,null);
            getContext().getContentResolver().bulkInsert(EventContract.LocationEntry.CONTENT_URI,locationCVs);
            getContext().getContentResolver().notifyChange(EventContract.LocationEntry.CONTENT_URI,null);
            //</editor-fold>

            //<editor-fold desc="speakers in events">
            JSONArray speakersInEvents = data.getJSONArray("speakers_in_events");
            ContentValues[] sieCVs = new ContentValues[speakersInEvents.length()];
            for (int i = 0; i < speakersInEvents.length(); i++){
                JSONObject sie = speakersInEvents.getJSONObject(i);
                ContentValues sieCV = new ContentValues();
                sieCV.put(EventContract.SpeakersInEventsEntry._ID,sie.getInt("_id"));
                sieCV.put(EventContract.SpeakersInEventsEntry.COLUMN_NAME_EVENT_ID,sie.getInt("event_id"));
                sieCV.put(EventContract.SpeakersInEventsEntry.COLUMN_NAME_SPEAKER_ID,sie.getInt("speaker_id"));
                sieCVs[i] = sieCV;
            }
            getContext().getContentResolver().delete(EventContract.SpeakersInEventsEntry.CONTENT_URI,null,null);
            getContext().getContentResolver().bulkInsert(EventContract.SpeakersInEventsEntry.CONTENT_URI,sieCVs);
            getContext().getContentResolver().notifyChange(EventContract.SpeakersInEventsEntry.CONTENT_URI,null);
            //</editor-fold>

        } catch (JSONException e){
            Log.e("Convention", "Error in SyncAdapter.onPerformSync, JSON ", e);
            return;
        }

    }
}
