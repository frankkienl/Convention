package nl.frankkie.convention;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.frankkie.convention.data.EventContract;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 */
public class EventDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int EVENT_DETAIL_LOADER = 0;
    public static final int EVENT_SPEAKERS_LOADER = 1;
    public static final String[] EVENT_COLUMNS = {
            EventContract.EventEntry.TABLE_NAME + "." + EventContract.EventEntry._ID,
            EventContract.EventEntry.COLUMN_NAME_TITLE,
            EventContract.EventEntry.TABLE_NAME + "." + EventContract.EventEntry.COLUMN_NAME_DESCRIPTION,
            EventContract.EventEntry.COLUMN_NAME_KEYWORDS,
            EventContract.EventEntry.COLUMN_NAME_START_TIME,
            EventContract.EventEntry.COLUMN_NAME_END_TIME,
            EventContract.EventEntry.COLUMN_NAME_COLOR,
            EventContract.EventEntry.COLUMN_NAME_IMAGE,
            EventContract.LocationEntry.COLUMN_NAME_NAME,
            EventContract.LocationEntry.TABLE_NAME + "." + EventContract.LocationEntry.COLUMN_NAME_DESCRIPTION
    };
    public static final String[] SPEAKERS_COLUMNS = {
            EventContract.SpeakerEntry.TABLE_NAME + "." + EventContract.SpeakerEntry._ID,
            EventContract.SpeakerEntry.TABLE_NAME + "." + EventContract.SpeakerEntry.COLUMN_NAME_NAME,
            EventContract.SpeakerEntry.TABLE_NAME + "." + EventContract.SpeakerEntry.COLUMN_NAME_DESCRIPTION,
            EventContract.SpeakerEntry.TABLE_NAME + "." + EventContract.SpeakerEntry.COLUMN_NAME_COLOR,
            EventContract.SpeakerEntry.TABLE_NAME + "." + EventContract.SpeakerEntry.COLUMN_NAME_IMAGE
    };

    String mId;
    //Views
    TextView mTitle;
    TextView mDescription;
    TextView mKeywords;
    TextView mStartTime;
    TextView mEndTime;
    TextView mLocation;
    TextView mLocationDescription;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i == EVENT_DETAIL_LOADER) {
            Uri uri = EventContract.EventEntry.buildEventUri(Long.parseLong(mId));
            //Is sortOrder needed? We'll get only 1 row.
            String sortOrder = EventContract.EventEntry.COLUMN_NAME_START_TIME + " ASC";
            CursorLoader cl = new CursorLoader(getActivity(), uri, EVENT_COLUMNS, null, null, sortOrder);
            return cl;
        } else if (i == EVENT_SPEAKERS_LOADER) {
            Uri uri = EventContract.SpeakersInEventsEntry.buildSpeakersInEventUri(Long.parseLong(mId));
            String sortOrder = "";
            CursorLoader cl = new CursorLoader(getActivity(), uri, SPEAKERS_COLUMNS, null, null, sortOrder);
            return cl;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        //set View-content
        if (cursorLoader.getId() == EVENT_DETAIL_LOADER) {
            if (data != null && data.moveToFirst()) {
                int eventId = data.getInt(0);
                mTitle.setText(data.getString(1));
                mDescription.setText(data.getString(2));
                mKeywords.setText(data.getString(3));
                mStartTime.setText(Util.getDataTimeString(data.getLong(4)));
                mEndTime.setText(Util.getDataTimeString(data.getLong(5)));
                String color = data.getString(6);
                String image = data.getString(7);
                mLocation.setText(data.getString(8));
                mLocationDescription.setText(data.getString(9));
            }
        } else if (cursorLoader.getId() == EVENT_SPEAKERS_LOADER) {
            //List of speakers of this Event.
            if (data != null && data.moveToFirst()) {
                //TODO: put this into some ListView Adapter to show a list of speakers.
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //do nothing
    }

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            //Set the id, that will be used in onCreateLoader (CursorLoader)
            mId = getArguments().getString(ARG_ITEM_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);

        //Should I do this in a ViewHolder? Nah, its not a ListView item.
        mTitle = (TextView) rootView.findViewById(R.id.event_detail_title);
        mDescription = (TextView) rootView.findViewById(R.id.event_detail_description);
        mKeywords = (TextView) rootView.findViewById(R.id.event_detail_keywords);
        mStartTime = (TextView) rootView.findViewById(R.id.event_detail_starttime);
        mEndTime = (TextView) rootView.findViewById(R.id.event_detail_endtime);
        mLocation = (TextView) rootView.findViewById(R.id.event_detail_location);
        mLocationDescription = (TextView) rootView.findViewById(R.id.event_detail_location_description);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Loaders depend on Activity not on Fragment!
        getLoaderManager().initLoader(EVENT_DETAIL_LOADER, null, this);
    }
}
