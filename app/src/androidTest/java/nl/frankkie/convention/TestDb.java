package nl.frankkie.convention;

import android.app.usage.UsageEvents;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import nl.frankkie.convention.data.EventContract;
import nl.frankkie.convention.data.EventDbHelper;

/**
 * Created by fbouwens on 19-11-14.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = "ConventionTest";

    public void testCreateDb() throws Throwable {
        Log.d(LOG_TAG, "Running testCreateDb");
        //Delete database, to start with clean environment.
        mContext.deleteDatabase(EventDbHelper.DATABASE_NAME);
        //Open database, this should also create databases
        SQLiteDatabase db = new EventDbHelper(mContext).getWritableDatabase();
        assertTrue(db.isOpen());
        //Don't forget to close
        db.close();
    }

    public void testInsertReadDb() {
        SQLiteDatabase db = new EventDbHelper(mContext).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventContract.EventEntry.COLUMN_NAME_TITLE, "Opening");
        values.put(EventContract.EventEntry.COLUMN_NAME_DESCRIPTION, "The big opening");
        values.put(EventContract.EventEntry.COLUMN_NAME_KEYWORDS, "opening, mandatory");
        values.put(EventContract.EventEntry.COLUMN_NAME_IMAGE, "");
        values.put(EventContract.EventEntry.COLUMN_NAME_COLOR, "#00FF00");
        values.put(EventContract.EventEntry.COLUMN_NAME_START_TIME, 1424509200);
        values.put(EventContract.EventEntry.COLUMN_NAME_END_TIME, 1424512800);
        values.put(EventContract.EventEntry.COLUMN_NAME_LOCATION_ID, 0);
        values.put(EventContract.EventEntry.COLUMN_NAME_SORT_ORDER, 0);

        long rowId = db.insert(EventContract.EventEntry.TABLE_NAME,null,values);
        assertTrue(rowId != -1);
        Log.d(LOG_TAG, "rowId = " + rowId);

    }
}
