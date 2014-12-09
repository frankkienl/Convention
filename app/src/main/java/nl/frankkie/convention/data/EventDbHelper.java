package nl.frankkie.convention.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import nl.frankkie.convention.data.EventContract.*;

/**
 * Created by fbouwens on 19-11-14.
 */
public class EventDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "events.db";

    public EventDbHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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

        String sqlSpeakersInEvents = "CREATE TABLE " + SpeakersInEventsEntry.TABLE_NAME + " ( "+
                SpeakersInEventsEntry._ID + " INTEGER PRIMARY KEY, " +
                SpeakersInEventsEntry.COLUMN_NAME_EVENT_ID + " INTEGER, " +
                SpeakersInEventsEntry.COLUMN_NAME_SPEAKER_ID + " INTEGER )";
        db.execSQL(sqlSpeakersInEvents);

        //TODO remove dummydata
        insertDummyData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //to upgrade, just delete and re-create.
        String sql = "DROP TABLE IF EXISTS ";
        db.execSQL(sql + EventEntry.TABLE_NAME);
        db.execSQL(sql + LocationEntry.TABLE_NAME);
        db.execSQL(sql + SpeakerEntry.TABLE_NAME);
        db.execSQL(sql + SpeakersInEventsEntry.TABLE_NAME);
    }

    public void insertDummyData(SQLiteDatabase db){
        //Event
        ContentValues valuesOpening = new ContentValues();
        valuesOpening.put(EventEntry._ID, 1);
        valuesOpening.put(EventContract.EventEntry.COLUMN_NAME_TITLE, "Opening");
        valuesOpening.put(EventContract.EventEntry.COLUMN_NAME_DESCRIPTION, "The big opening");
        valuesOpening.put(EventContract.EventEntry.COLUMN_NAME_KEYWORDS, "opening, mandatory");
        valuesOpening.put(EventContract.EventEntry.COLUMN_NAME_IMAGE, "");
        valuesOpening.put(EventContract.EventEntry.COLUMN_NAME_COLOR, "#00FF00");
        valuesOpening.put(EventContract.EventEntry.COLUMN_NAME_START_TIME, 1424509200); //Sat, 9:00
        valuesOpening.put(EventContract.EventEntry.COLUMN_NAME_END_TIME, 1424512800); //Sat, 10:00
        valuesOpening.put(EventContract.EventEntry.COLUMN_NAME_LOCATION_ID, 1);
        valuesOpening.put(EventContract.EventEntry.COLUMN_NAME_SORT_ORDER, 0);
        db.insert(EventEntry.TABLE_NAME,null,valuesOpening);
        ContentValues VAPanel = new ContentValues();
        valuesOpening.put(EventEntry._ID, 2);
        VAPanel.put(EventContract.EventEntry.COLUMN_NAME_TITLE, "VA Panel");
        VAPanel.put(EventContract.EventEntry.COLUMN_NAME_DESCRIPTION, "Voice Actors panel");
        VAPanel.put(EventContract.EventEntry.COLUMN_NAME_KEYWORDS, "VA, panel");
        VAPanel.put(EventContract.EventEntry.COLUMN_NAME_IMAGE, "");
        VAPanel.put(EventContract.EventEntry.COLUMN_NAME_COLOR, "#0000FF");
        VAPanel.put(EventContract.EventEntry.COLUMN_NAME_START_TIME, 1424512800); //Sat, 10:00
        VAPanel.put(EventContract.EventEntry.COLUMN_NAME_END_TIME, 1424516400); //Sat, 11:00
        VAPanel.put(EventContract.EventEntry.COLUMN_NAME_LOCATION_ID, 1);
        VAPanel.put(EventContract.EventEntry.COLUMN_NAME_SORT_ORDER, 1);
        db.insert(EventEntry.TABLE_NAME,null,VAPanel);
        ContentValues karaoke = new ContentValues();
        valuesOpening.put(EventEntry._ID, 3);
        karaoke.put(EventContract.EventEntry.COLUMN_NAME_TITLE, "Karaoke");
        karaoke.put(EventContract.EventEntry.COLUMN_NAME_DESCRIPTION, "Playing Karaoke");
        karaoke.put(EventContract.EventEntry.COLUMN_NAME_KEYWORDS, "game, karaoke");
        karaoke.put(EventContract.EventEntry.COLUMN_NAME_IMAGE, "");
        karaoke.put(EventContract.EventEntry.COLUMN_NAME_COLOR, "#00FFFF");
        karaoke.put(EventContract.EventEntry.COLUMN_NAME_START_TIME, 1424510100); //Sat, 9:15
        karaoke.put(EventContract.EventEntry.COLUMN_NAME_END_TIME, 1424538000); //Sat, 17:00
        karaoke.put(EventContract.EventEntry.COLUMN_NAME_LOCATION_ID, 2);
        karaoke.put(EventContract.EventEntry.COLUMN_NAME_SORT_ORDER, 1);
        db.insert(EventEntry.TABLE_NAME,null,karaoke);
    }


}
