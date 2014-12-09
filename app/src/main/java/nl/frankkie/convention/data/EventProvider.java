package nl.frankkie.convention.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
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
    //content://nl.frankkie.convention/event/ (LIST)
    public static final int EVENT = 100;
    //content://nl.frankkie.convention/event/ID (ITEM)
    public static final int EVENT_ID = 101;
    //content://nl.frankkie.convention/speaker/ (LIST)
    public static final int SPEAKER = 200;
    //content://nl.frankkie.convention/speaker/ (ITEM)
    public static final int SPEAKER_ID = 201;
    //content://nl.frankkie.convention/location/ (LIST)
    public static final int LOCATION = 300;
    //content://nl.frankkie.convention/location/ID (ITEM)
    public static final int LOCATION_ID = 301;
    //content://nl.frankkie.convention/speakers_in_events/ (LIST)
    public static final int SPEAKERS_IN_EVENTS = 400;
    //content://nl.frankkie.convention/speakers_in_events/ID (ITEM)
    public static final int SPEAKERS_IN_EVENTS_ID = 401;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    SQLiteOpenHelper mOpenHelper;

    private static final SQLiteQueryBuilder sEventWithLocationQueryBuilder;

    static {
        // Sunshine combines location with weather, this app will combine event and location
        sEventWithLocationQueryBuilder = new SQLiteQueryBuilder();
        // Lets hope 'INNER JOIN' does what I think it does.
        // I should have paid better attention at Database-lessons at school... >.>
        //SELECT event.title, location.name FROM event JOIN location ON event.location_id = location._id WHERE event._id = 3
        sEventWithLocationQueryBuilder.setTables(
                EventContract.EventEntry.TABLE_NAME + " INNER JOIN " +
                        EventContract.LocationEntry.TABLE_NAME +
                        " ON " + EventContract.EventEntry.TABLE_NAME +
                        "." + EventContract.EventEntry.COLUMN_NAME_LOCATION_ID +
                        " = " + EventContract.LocationEntry.TABLE_NAME +
                        "." + EventContract.LocationEntry._ID
        );
    }


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_EVENT, EVENT);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_EVENT + "/#", EVENT_ID);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_SPEAKER, SPEAKER);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_SPEAKER + "/#", SPEAKER_ID);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_LOCATION, LOCATION);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_LOCATION + "/#", LOCATION_ID);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_SPEAKERS_IN_EVENTS, SPEAKERS_IN_EVENTS);
        matcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_SPEAKERS_IN_EVENTS + "/#", SPEAKERS_IN_EVENTS_ID);
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
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EventContract.EventEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, //having
                        null, //group by
                        sortOrder
                );
                break;
            }
            case EVENT_ID: {
                //1 Event, with speakers
                //Override selection when empty
                if (selection == null || "".equals(selection)) {
                    selection = EventContract.EventEntry._ID + " = ?";
                    //content://nl.frankkie.convention/event/0 <-- last segment is ID.
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                }
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EventContract.EventEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, //having
                        null, //group by
                        sortOrder
                );
                break;
            }
            case SPEAKER: {
                //list
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EventContract.SpeakerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, //having
                        null, //group by
                        sortOrder
                );
                break;
            }
            case SPEAKER_ID: {
                //1 speaker
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EventContract.SpeakerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, //having
                        null, //group by
                        sortOrder
                );
                break;
            }
            case LOCATION: {
                //List of location
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EventContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, //having
                        null, //group by
                        sortOrder
                );
                break;
            }
            case LOCATION_ID: {
                //1 location
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EventContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, //having
                        null, //group by
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Register a content-observer (to watch for content changes)
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case EVENT: {
                return EventContract.EventEntry.CONTENT_TYPE; //list
            }
            case EVENT_ID: {
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
        //No idea why the db-var is final in Sunshine.
        //Its only used in this method, so idk, might be performance?
        //https://github.com/udacity/Sunshine/blob/6.10-update-map-intent/app/src/main/java/com/example/android/sunshine/app/data/WeatherProvider.java#L221
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            //*cough* code duplication *cough*
            case EVENT: {
                //Database insert returns row_id of new row.
                long id = db.insert(EventContract.EventEntry.TABLE_NAME, null, values);
                if (id != -1L) { //return -1 on error
                    returnUri = EventContract.EventEntry.buildEventUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert Event row into: " + uri);
                }
                break;
            }
            case LOCATION: {
                long id = db.insert(EventContract.LocationEntry.TABLE_NAME, null, values);
                if (id != -1L) {
                    returnUri = EventContract.LocationEntry.buildLocationUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert Location row into: " + uri);
                }
                break;
            }
            case SPEAKER: {
                long id = db.insert(EventContract.SpeakerEntry.TABLE_NAME, null, values);
                if (id != -1L) {
                    returnUri = EventContract.SpeakerEntry.buildSpeakerUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert Speaker row into: " + uri);
                }
                break;
            }
            case SPEAKERS_IN_EVENTS: {
                long id = db.insert(EventContract.SpeakersInEventsEntry.TABLE_NAME, null, values);
                if (id != -1L) {
                    returnUri = EventContract.SpeakersInEventsEntry.buildSpeakersInEventsUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert SpeakersInEventsEntry row into: " + uri);
                }
                break;
            }
            default:
                throw new android.database.SQLException("Unknown uri: " + uri);
        }
        //Notify that DB has changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENT: {
                db.beginTransaction();
                int returnInt = 0; //number of added rows
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(EventContract.EventEntry.TABLE_NAME, null, value);
                        if (id != -1L) {
                            returnInt++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                return returnInt;
            }
            case LOCATION: {
                db.beginTransaction();
                int returnInt = 0; //number of added rows
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(EventContract.LocationEntry.TABLE_NAME, null, value);
                        if (id != -1L) {
                            returnInt++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                return returnInt;
            }
            case SPEAKER: {
                db.beginTransaction();
                int returnInt = 0; //number of added rows
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(EventContract.SpeakerEntry.TABLE_NAME, null, value);
                        if (id != -1L) {
                            returnInt++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                return returnInt;
            }
            case SPEAKERS_IN_EVENTS: {
                db.beginTransaction();
                int returnInt = 0; //number of added rows
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(EventContract.SpeakersInEventsEntry.TABLE_NAME, null, value);
                        if (id != -1L) {
                            returnInt++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                return returnInt;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numberOfRowsDeleted = 0;
        switch (match) {
            case EVENT: {
                numberOfRowsDeleted = db.delete(EventContract.EventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case LOCATION: {
                numberOfRowsDeleted = db.delete(EventContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SPEAKER: {
                numberOfRowsDeleted = db.delete(EventContract.SpeakerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SPEAKERS_IN_EVENTS: {
                numberOfRowsDeleted = db.delete(EventContract.SpeakersInEventsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Null as selection deletes all rows
        if (selection == null || numberOfRowsDeleted != 0) {
            //notify that DB has changed.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        switch (match) {
            case EVENT: {
                rowsUpdated = db.update(EventContract.EventEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case LOCATION: {
                rowsUpdated = db.update(EventContract.LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case SPEAKER: {
                rowsUpdated = db.update(EventContract.SpeakerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case SPEAKERS_IN_EVENTS: {
                rowsUpdated = db.update(EventContract.SpeakersInEventsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
