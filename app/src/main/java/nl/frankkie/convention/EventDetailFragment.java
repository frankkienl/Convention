package nl.frankkie.convention;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            EventContract.LocationEntry.TABLE_NAME + "." + EventContract.LocationEntry._ID,
            EventContract.LocationEntry.COLUMN_NAME_NAME,
            EventContract.LocationEntry.TABLE_NAME + "." + EventContract.LocationEntry.COLUMN_NAME_DESCRIPTION,
            EventContract.FavoritesEntry.TABLE_NAME + "." + EventContract.FavoritesEntry._ID //If filled, its starred.
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
    TextView mSpeakersHeader;
    LinearLayout mSpeakersContainer;
    CheckBox mStar;

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
                int locationId = data.getInt(8);
                mLocation.setText(data.getString(9));
                mLocationDescription.setText(data.getString(10));
                //Star
                mStar.setOnCheckedChangeListener(null); //remove before changing, add again later.
                if (!data.isNull(11)) { //null when not starred.
                    mStar.setChecked(true);
                    //We don't actually care what the ID is,
                    //We only care if its present or not.
                } else {
                    mStar.setChecked(false);
                }
                mStar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        persistFavorite(isChecked);
                    }
                });
            }
        } else if (cursorLoader.getId() == EVENT_SPEAKERS_LOADER) {
            //List of speakers of this Event.
            if (data == null || data.getCount() < 1) { //.getCount gives number of rows
                //There are no rows, return.
                return;
            }
            //There are speakers, make Speaker header visible
            mSpeakersHeader.setVisibility(View.VISIBLE);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            mSpeakersContainer.removeAllViews(); //clear content
            while (data.moveToNext()) {
                ViewGroup speakerItem = (ViewGroup) inflater.inflate(R.layout.event_detail_speaker_item, mSpeakersContainer, false);
                TextView sName = (TextView) speakerItem.findViewById(R.id.event_detail_speaker_item_name);
                TextView sDescription = (TextView) speakerItem.findViewById(R.id.event_detail_speaker_item_description);
                ImageView sImage = (ImageView) speakerItem.findViewById(R.id.event_detail_speaker_item_image);
                sName.setText(data.getString(1));
                sDescription.setText(data.getString(2));
                String imageUrl = data.getString(4);
                mSpeakersContainer.addView(speakerItem);
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
        mSpeakersHeader = (TextView) rootView.findViewById(R.id.event_detail_label_speakers);
        mSpeakersHeader.setVisibility(View.GONE); //Make visible (again) if there are Speakers for this event.
        mSpeakersContainer = (LinearLayout) rootView.findViewById(R.id.event_detail_speakers_container);
        mStar = (CheckBox) rootView.findViewById(R.id.event_detail_star);

        //
        showStarTip();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Loaders depend on Activity not on Fragment!
        getLoaderManager().initLoader(EVENT_DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(EVENT_SPEAKERS_LOADER, null, this);
    }

    public void showStarTip() {
        //Show Star-tip
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean shownStarTip = prefs.getBoolean("prefs_shown_star_tip", false);
        if (!shownStarTip) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.star_tip_title);
            builder.setMessage(R.string.star_tip_message);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Set to shown=true, when user presses the OK-button.
                    prefs.edit().putBoolean("prefs_shown_star_tip", true).apply();
                    //Using apply (instead of commit), because we don't want to stall the UI-thread.
                    //apply will make the change in memory, and then save it to persistent story
                    //on a background thread.
                }
            });
            builder.create().show();
        }
    }

    public void persistFavorite(boolean checked) {
        if (checked) {
            //If checked, add a row to DB
            ContentValues cv = new ContentValues();
            //cv.put(EventContract.FavoritesEntry._ID,null);
            //giving null or no ID at all will generate an ID.
            //We do not need to know what the ID of the row will be.
            cv.put(EventContract.FavoritesEntry.COLUMN_NAME_TYPE, EventContract.FavoritesEntry.TYPE_EVENT);
            cv.put(EventContract.FavoritesEntry.COLUMN_NAME_ITEM_ID, mId); //Id of this Event
            getActivity().getContentResolver().insert(EventContract.FavoritesEntry.CONTENT_URI, cv);
        } else {
            //If not checked, remove row from DB
            //We use EventID in where-clause, hence, we don't need to know the RowID of the Favorite.
            getActivity().getContentResolver().delete(EventContract.FavoritesEntry.CONTENT_URI,
                    EventContract.FavoritesEntry.COLUMN_NAME_TYPE + " = '" + EventContract.FavoritesEntry.TYPE_EVENT +
                            "' AND " + EventContract.FavoritesEntry.COLUMN_NAME_ITEM_ID + " = ?", new String[]{mId});
        }
    }
}
