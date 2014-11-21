package nl.frankkie.convention;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by fbouwens on 21-11-14.
 */
public class EventAdapter extends CursorAdapter {

    public EventAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
