package nl.frankkie.convention;

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
        assertEquals(false,db.isOpen());
        //Don't forget to close
        db.close();
    }
}
