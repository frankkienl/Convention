package nl.frankkie.convention.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fbouwens on 19-11-14.
 */
public class EventContract {

    //Used for ContentProvider
    public static final String CONTENT_AUTHORITY = "nl.frankkie.convention";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_EVENT = "event";
    public static final String PATH_SPEAKER = "speaker";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_SPEAKERS_IN_EVENTS = "speakers_in_events";

    public static abstract class EventEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        //
        public static final String TABLE_NAME = "event";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_KEYWORDS = "keywords";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_END_TIME = "end_time";
        public static final String COLUMN_NAME_LOCATION_ID = "location_id";
        public static final String COLUMN_NAME_SORT_ORDER = "sort_order";

        public static Uri buildEventUri(long id) {
            //This method is used in Sunshine
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

    public static abstract class SpeakerEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SPEAKER).build();
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_SPEAKER;
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_SPEAKER;
        public static final String TABLE_NAME = "speaker";
        //
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_COLOR = "color";

        public static Uri buildSpeakerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static abstract class LocationEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String TABLE_NAME = "location";
        //
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_MAP_LOCATION = "map_location";
        public static final String COLUMN_NAME_FLOOR = "floor"; //where 0 is ground-level.

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


    public static abstract class SpeakersInEventsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SPEAKERS_IN_EVENTS).build();
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_SPEAKERS_IN_EVENTS;
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_SPEAKERS_IN_EVENTS;
        public static final String TABLE_NAME = "speakers_in_events";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
        public static final String COLUMN_NAME_SPEAKER_ID = "speaker_id";

        public static Uri buildSpeakersInEventsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSpeakersInEventUri(long eventId) {
            return ContentUris.withAppendedId(CONTENT_URI.buildUpon().appendPath("event").build(), eventId);
        }
    }

}
