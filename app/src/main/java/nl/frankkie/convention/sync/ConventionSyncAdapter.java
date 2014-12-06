package nl.frankkie.convention.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Created by FrankkieNL on 6-12-2014.
 */
public class ConventionSyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver mContentResolver;
    public ConventionSyncAdapter(Context c, boolean autoInit){
        super(c,autoInit);
        mContentResolver = c.getContentResolver();
    }

    public ConventionSyncAdapter(Context c, boolean autoInit, boolean allowParallel){
        super(c,autoInit,allowParallel);
        mContentResolver = c.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //TODO: sync data from server to database

    }
}
