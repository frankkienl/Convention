package nl.frankkie.convention.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nl.frankkie.convention.data.EventContract.*;

/**
 * Created by fbouwens on 19-11-14.
 */
public class EventDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "events.db";

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    public void createTables(SQLiteDatabase db) {
        String sqlEvent = "CREATE TABLE " + EventEntry.TABLE_NAME + " ( " +
                EventEntry._ID + " INTEGER PRIMARY KEY, " +
                EventEntry.COLUMN_NAME_TITLE + " TEXT, " +
                EventEntry.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                EventEntry.COLUMN_NAME_KEYWORDS + " TEXT, " +
                EventEntry.COLUMN_NAME_IMAGE + " TEXT, " +
                EventEntry.COLUMN_NAME_COLOR + " TEXT, " +
                EventEntry.COLUMN_NAME_START_TIME + " TEXT, " +
                EventEntry.COLUMN_NAME_END_TIME + " TEXT, " +
                EventEntry.COLUMN_NAME_LOCATION_ID + " TEXT, " +
                EventEntry.COLUMN_NAME_SORT_ORDER + " INTEGER )";
        db.execSQL(sqlEvent);

        String sqlSpeaker = "CREATE TABLE " + SpeakerEntry.TABLE_NAME + " ( " +
                SpeakerEntry._ID + " INTEGER PRIMARY KEY, " +
                SpeakerEntry.COLUMN_NAME_NAME + " TEXT, " +
                SpeakerEntry.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                SpeakerEntry.COLUMN_NAME_IMAGE + " TEXT, " +
                SpeakerEntry.COLUMN_NAME_COLOR + " TEXT )";
        db.execSQL(sqlSpeaker);

        String sqlLocation = "CREATE TABLE " + LocationEntry.TABLE_NAME + " ( " +
                LocationEntry._ID + " INTEGER PRIMARY KEY, " +
                LocationEntry.COLUMN_NAME_NAME + " TEXT, " +
                LocationEntry.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                LocationEntry.COLUMN_NAME_MAP_LOCATION + " TEXT, " +
                LocationEntry.COLUMN_NAME_FLOOR + " INTEGER )";
        db.execSQL(sqlLocation);

        String sqlSpeakersInEvents = "CREATE TABLE " + SpeakersInEventsEntry.TABLE_NAME + " ( " +
                SpeakersInEventsEntry._ID + " INTEGER PRIMARY KEY, " +
                SpeakersInEventsEntry.COLUMN_NAME_EVENT_ID + " INTEGER, " +
                SpeakersInEventsEntry.COLUMN_NAME_SPEAKER_ID + " INTEGER )";
        db.execSQL(sqlSpeakersInEvents);

        String sqlFavorites = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoritesEntry.COLUMN_NAME_TYPE + " TEXT, " +
                FavoritesEntry.COLUMN_NAME_ITEM_ID + " INTEGER, " +
                "UNIQUE(" + FavoritesEntry.COLUMN_NAME_TYPE + "," + //Made type & id unique to prevent duplicates
                FavoritesEntry.COLUMN_NAME_ITEM_ID + ") ON CONFLICT REPLACE" + " )";
        db.execSQL(sqlFavorites);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //to upgrade, just delete and re-create.
        String sql = "DROP TABLE IF EXISTS ";
        db.execSQL(sql + EventEntry.TABLE_NAME);
        db.execSQL(sql + LocationEntry.TABLE_NAME);
        db.execSQL(sql + SpeakerEntry.TABLE_NAME);
        db.execSQL(sql + SpeakersInEventsEntry.TABLE_NAME);
        //For future updates, don't delete favorites, but upgrade.
        db.execSQL(sql + FavoritesEntry.TABLE_NAME);
        //
        createTables(db);
    }

}
