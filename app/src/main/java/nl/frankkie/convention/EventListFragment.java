package nl.frankkie.convention;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.support.v4.app.*;
import android.support.v4.content.*;
import nl.frankkie.convention.data.*;
import android.net.*;

/**
 * A list fragment representing a list of Events. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link EventDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class EventListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    EventAdapter mEventAdapter;
	int EVENT_LOADER=1;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

  public static final int COL_ID = 0;
  public static final int COL_TITLE = 1;
  public static final int COL_TIME = 2;
  public static final int COL_IMAGE = 3;
	
	public static final String[] EVENTCOLUMNS=new String[]{
	  EventContract.EventEntry.TABLE_NAME + EventContract.EventEntry._ID,
	  EventContract.EventEntry.COLUMN_NAME_TITLE,
	  EventContract.EventEntry.COLUMN_NAME_START_TIME,
	  EventContract.EventEntry.COLUMN_NAME_IMAGE
	};
	
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	    Uri uri = Uri.parse(EventContract.PATH_EVENT);
		String sortOrder=EventContract.EventEntry.COLUMN_NAME_START_TIME+" ASC";
	  	CursorLoader cl = new CursorLoader(getActivity(),uri,EVENTCOLUMNS,null,null,sortOrder);
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mEventAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
mEventAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventListFragment() {
    }

    GridView mGridView;
    int mPosition;

    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mEventAdapter = new EventAdapter(getActivity(), null, 0);

        mGridView = (GridView) inflater.inflate(R.layout.fragment_event_gridlist, container, false);
        mGridView.setAdapter(mEventAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mEventAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    mCallbacks.onItemSelected("" + cursor.getInt(COL_ID));
                }
                mPosition = position;
            }
        });
		
        return mGridView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	  super.onActivityCreated(savedInstanceState);
	  getLoaderManager().initLoader(EVENT_LOADER,null,this);
	}

	@Override
	public void onResume() {
	  super.onResume();
	  getLoaderManager().restartLoader(EVENT_LOADER,null,this);
	}
	

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        mGridView.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            mGridView.setItemChecked(mActivatedPosition, false);
        } else {
            mGridView.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
