package nl.frankkie.convention;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import nl.frankkie.convention.data.EventContract;
import nl.frankkie.convention.data.EventDbHelper;

/**
 * Created by fbouwens on 19-11-14.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = "ConventionTest";

    public void testDeleteDb() throws Throwable {
        //TODO: make a test here
        //https://github.com/udacity/Sunshine/blob/6.10-update-map-intent/app/src/androidTest/java/com/example/android/sunshine/app/test/TestProvider.java
        mContext.deleteDatabase(EventDbHelper.DATABASE_NAME);
    }

    public void testInsertReadProvider() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        EventDbHelper dbHelper = new EventDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestDb.getEventContentValues();

        long eventRowId;
        eventRowId = db.insert(EventContract.EventEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(eventRowId != -1);
        Log.d(LOG_TAG, "New row id: " + eventRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                EventContract.EventEntry.CONTENT_URI,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // columns to filter by row groups
                null // sort order
        );

        TestDb.validateCursor(cursor, testValues);

        dbHelper.close();
    }

    public void testGetType(){
        //Test to see if it (UriMatcher in ContentProvider) returns the correct types for the Uri's
        // content://nl.frankkie.convention/event/
        String type = mContext.getContentResolver().getType(EventContract.EventEntry.CONTENT_URI);
        // vnd.android.cursor.dir/nl.frankkie.convention/event
        assertEquals(EventContract.EventEntry.CONTENT_TYPE,type); //list

        // content://nl.frankkie.convention/event/1
        type = mContext.getContentResolver().getType(EventContract.EventEntry.buildEventUri(1));
        // vnd.android.cursor.item/nl.frankkie.convention/event
        assertEquals(EventContract.EventEntry.CONTENT_ITEM_TYPE, type); //item

        // content://nl.frankkie.convention/location/
        type = mContext.getContentResolver().getType(EventContract.LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/nl.frankkie.convention/location
        assertEquals(EventContract.LocationEntry.CONTENT_TYPE, type);

        // content://nl.frankkie.convention/location/1
        type = mContext.getContentResolver().getType(EventContract.LocationEntry.buildLocationUri(1));
        // vnd.android.cursor.item/nl.frankkie.convention/location
        assertEquals(EventContract.LocationEntry.CONTENT_ITEM_TYPE, type);

        // content://nl.frankkie.convention/speaker/
        type = mContext.getContentResolver().getType(EventContract.SpeakerEntry.CONTENT_URI);
        // vnd.android.cursor.dir/nl.frankkie.convention/speaker
        assertEquals(EventContract.SpeakerEntry.CONTENT_TYPE, type);

        // content://nl.frankkie.convention/speaker/1
        type = mContext.getContentResolver().getType(EventContract.SpeakerEntry.buildSpeakerUri(1));
        // vnd.android.cursor.item/nl.frankkie.convention/speaker
        assertEquals(EventContract.SpeakerEntry.CONTENT_ITEM_TYPE, type);
    }
}
