package nl.frankkie.convention;

import android.app.usage.UsageEvents;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import nl.frankkie.convention.data.EventContract;
import nl.frankkie.convention.util.GcmUtil;
import nl.frankkie.convention.util.Util;

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
        Button scanButton = (Button) v.findViewById(R.id.qr_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQR();
            }
        });
        return v;
    }

    public void scanQR(){
        IntentIntegrator ii = new IntentIntegrator(getActivity());
        ii.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (intentResult != null){
            //Check if correct QR code and stuff
            //Doing this in AsyncTask, becauase database lookup can take time, same goes for hashing.
            CheckQRCodeTask task = new CheckQRCodeTask(getActivity(), intentResult.getContents());
            task.execute();
        } else {
            Toast.makeText(getActivity(),"No QR code scanned", Toast.LENGTH_SHORT).show();
        }
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

    public class CheckQRCodeTask extends AsyncTask<Void,Void,Void>{
        Context context;
        String qrdata;
        public CheckQRCodeTask(Context context, String qrdata){
            this.context = context;
            this.qrdata = qrdata;
        }
        @Override
        protected Void doInBackground(Void... params) {
            //Create Hash
            String regId = GcmUtil.gcmGetRegId(context);
            String hash = Util.sha1Hash(qrdata + "_" + regId);
            //Check database
            Cursor cursor = context.getContentResolver().query(
                    EventContract.QrEntry.buildQrByHashUri(hash),
                    QRLIST_COLUMNS, //projection (which columns)
                    null, //selection - intentionally null
                    null, //selectionArgs - intentionally null
                    null //sortorder does not matter with 1 result
            );
            int numRows = cursor.getCount();
            if (numRows == 0){
                //Not on the list
                Toast.makeText(context,"QR Code not on the list!", Toast.LENGTH_LONG).show();
                return null;
            }
            //if not found before, add a row.
            cursor.moveToFirst();
            String timeFound = cursor.getString(COL_FOUND_TIME);
            if (timeFound == null || "".equals(timeFound)){
                //not found before. Add row
                ContentValues cv = new ContentValues();
                cv.put(EventContract.QrFoundEntry.COLUMN_NAME_QR_ID, cursor.getInt(COL_ID));
                cv.put(EventContract.QrFoundEntry.COLUMN_NAME_TIME, System.currentTimeMillis());
                context.getContentResolver().insert(EventContract.QrFoundEntry.CONTENT_URI, cv);
                Toast.makeText(context, "Good work! You have found QR Code:\n" + cursor.getString(COL_NAME), Toast.LENGTH_LONG).show();
                //Sync to cloud!!!!

            } else {
                Toast.makeText(context, "You have found this QR Code before. Go look for another one!", Toast.LENGTH_LONG).show();
            }
            cursor.close();
            return null;
        }
    }
}
