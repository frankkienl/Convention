package nl.frankkie.convention.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by FrankkieNL on 22-11-2014.
 */
public class EventProvider extends ContentProvider {

    /*
     * Event
     * content://nl.frankkie.convention/
     * event
     * event/speakers
     * speakers
     * location
     */
    public static final int EVENT = 100;
    public static final int EVENT_WITH_SPEAKERS = 101;
    public static final int SPEAKER = 200;
    public static final int SPEAKER_ID = 201;
    public static final int LOCATION = 300;
    public static final int LOCATION_ID = 301;


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    SQLiteOpenHelper mOpenHelper;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_EVENT, EVENT);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_EVENT + "/#", EVENT_WITH_SPEAKERS);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_SPEAKER, SPEAKER);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_SPEAKER + "/#", SPEAKER_ID);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_LOCATION, LOCATION);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_LOCATION + "/#",LOCATION_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new EventDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        //TODO: finish this!
//        retCursor = mOpenHelper.getReadableDatabase().query()
        switch (sUriMatcher.match(uri)) {
            case EVENT: {
                //List of Events
                break;
            }
            case EVENT_WITH_SPEAKERS: {
                //1 Event, with speakers
                break;
            }
            case SPEAKER: {
                //list
                break;
            }
            case SPEAKER_ID: {
                //1 speaker
                break;
            }
            case LOCATION: {
                //List of location
                break;
            }
            case LOCATION_ID: {
                //1 location
                break;
            }
        }
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case EVENT: {
                return EventContract.EventEntry.CONTENT_TYPE; //list
            }
            case EVENT_WITH_SPEAKERS: {
                return EventContract.EventEntry.CONTENT_ITEM_TYPE; //1 event with speakers
            }
            case SPEAKER: {
                return EventContract.SpeakerEntry.CONTENT_TYPE; //list
            }
            case SPEAKER_ID: {
                return EventContract.SpeakerEntry.CONTENT_ITEM_TYPE; //list
            }
            case LOCATION: {
                return EventContract.LocationEntry.CONTENT_TYPE; // list
            }
            case LOCATION_ID: {
                return EventContract.LocationEntry.CONTENT_ITEM_TYPE; //1 location
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
