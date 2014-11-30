package nl.frankkie.convention;

import android.app.usage.UsageEvents;
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


//import nl.frankkie.convention.dummy.DummyContent;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 */
public class EventDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int EVENT_DETAIL_LOADER = 0;
    public static final String[] EVENT_COLUMNS = {
            EventContract.EventEntry._ID,
            EventContract.EventEntry.COLUMN_NAME_TITLE,
            EventContract.EventEntry.COLUMN_NAME_DESCRIPTION,
            EventContract.EventEntry.COLUMN_NAME_KEYWORDS,
            EventContract.EventEntry.COLUMN_NAME_START_TIME,
            EventContract.EventEntry.COLUMN_NAME_END_TIME,
            EventContract.EventEntry.COLUMN_NAME_LOCATION_ID,
            EventContract.EventEntry.COLUMN_NAME_COLOR,
            EventContract.EventEntry.COLUMN_NAME_IMAGE
    };

    String mId;
    //Views
    TextView mTitle;
    TextView mDescription;
    TextView mKeywords;
    TextView mStartTime;
    TextView mEndTime;
    TextView mLocation;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = EventContract.EventEntry.buildEventUri(Long.parseLong(mId));
        String sortOrder = EventContract.EventEntry.COLUMN_NAME_START_TIME + " ASC";
        CursorLoader cl = new CursorLoader(getActivity(), uri, EVENT_COLUMNS, null, null, sortOrder);
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        //set View-content
        if (data != null && data.moveToFirst()) {
            mTitle.setText(data.getString(data.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_TITLE)));
            mDescription.setText(data.getString(data.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_DESCRIPTION)));
            mKeywords.setText(data.getString(data.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_KEYWORDS)));
            mStartTime.setText(EventContract.getDataTimeString(data.getLong(data.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_START_TIME))));
            mEndTime.setText(EventContract.getDataTimeString(data.getLong(data.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_END_TIME))));
            mLocation.setText(data.getString(data.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_LOCATION_ID)));
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
     * The dummy content this fragment is presenting.
     */
    //private DummyContent.DummyItem mItem;
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
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            //TODO: USE CONTENT-PROVIDER !
            mId = getArguments().getString(ARG_ITEM_ID);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);

        // Show the dummy content as text in a TextView.
        //if (mItem != null) {
        //    ((TextView) rootView.findViewById(R.id.event_detail)).setText(mItem.content);
        //}

        //Should I do this in a ViewHolder? Nah, its not a ListView item.
        mTitle = (TextView) rootView.findViewById(R.id.event_detail_title);
        mDescription = (TextView) rootView.findViewById(R.id.event_detail_description);
        mKeywords = (TextView) rootView.findViewById(R.id.event_detail_keywords);
        mStartTime = (TextView) rootView.findViewById(R.id.event_detail_starttime);
        mEndTime = (TextView) rootView.findViewById(R.id.event_detail_endtime);
        mLocation = (TextView) rootView.findViewById(R.id.event_detail_location);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Loaders depend on Activity not on Fragment!
        getLoaderManager().initLoader(EVENT_DETAIL_LOADER,null,this);
    }
}
