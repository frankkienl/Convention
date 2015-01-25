package nl.frankkie.convention;

import android.app.usage.UsageEvents;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import nl.frankkie.convention.data.EventContract;

/**
 * Created by fbouwens on 23-01-15.
 */
public class QrListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
/*
    Rubber Ducky needed.
    How much is the user allowed to know (in advance) ?
    Like:
    - How much QR codes are there in total (to be found)
    - Names of non-found QR codes
    - Descriptions of non-found QR codes
    - Images of non-found QR codes

    So, like, When the game starts (nothing found yet)
    Does the player see:
    - an empty list ; User does not know how many, names or descriptions ; List will fill, show only found.
    - a list of names ; User does know how many and the name, not the descriptions
     */

    //The booleans are not used yet.
    public static boolean showNumber = true; //User can know how many there are in total
    public static boolean showNames = true; //Show list of names; showNumber has more priority
    public static boolean showImage = false; //Show image when not found yet.

    int QRLIST_LOADER = 0;
    private QrListAdapter mListAdapter;
    private ListView mListView;

    public static final int COL_ID = 0;
    public static final int COL_HASH = 1;
    public static final int COL_NAME = 2;
    public static final int COL_DESCRIPTION = 3;
    public static final int COL_IMAGE = 4;
    public static final int COL_FOUND_TIME = 5;

    public static final String[] QRLIST_COLUMNS = {
            EventContract.QrEntry.TABLE_NAME + "." + EventContract.QrEntry._ID,
            EventContract.QrEntry.COLUMN_NAME_HASH,
            EventContract.QrEntry.COLUMN_NAME_NAME,
            EventContract.QrEntry.COLUMN_NAME_DESCRIPTION,
            EventContract.QrEntry.COLUMN_NAME_IMAGE,
            EventContract.QrFoundEntry.TABLE_NAME + "." + EventContract.QrFoundEntry.COLUMN_NAME_TIME
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse("content://nl.frankkie.convention/qr/"); //list of all QR codes to be found
        CursorLoader cl = new CursorLoader(getActivity(), uri, QRLIST_COLUMNS, null, null, null);
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.swapCursor(null);
    }

    //Mandatory empty constructor
    public QrListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //mListAdapter = new QrListAdapter(getActivity(), null, 0); //Cursor comes later
        View v = inflater.inflate(R.layout.fragment_qr_list, container, false);
        //mListView = (ListView) v.findViewById(android.R.id.list);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListAdapter = new QrListAdapter(getActivity(), null, 0);
        mListView = getListView();
        mListView.setAdapter(mListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(QRLIST_LOADER, null, this);
    }
}
