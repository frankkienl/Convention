package nl.frankkie.convention;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fbouwens on 21-11-14.
 */
public class EventAdapter extends CursorAdapter {

    public static final String DATE_FORMAT = "E, HH:mm"; //example: Sunday, 16:30
    SimpleDateFormat displayDataFormat = new SimpleDateFormat(DATE_FORMAT);

    public EventAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //I use only 1 view type, unlike Sunshine.
        View view = LayoutInflater.from(context).inflate(R.layout.gridview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        //Set the viewholder as tag, this is for performance gain, when this view is re-used.
        //As you don't have to call findViewById again.
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.titleView.setText(cursor.getString(EventListFragment.COL_TITLE));
        long time = cursor.getLong(EventListFragment.COL_TIME);
        viewHolder.timeView.setText(displayDataFormat.format(new Date(time)));
        //TODO: dynamic image
        viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
    }

    public static class ViewHolder {
        //Using ViewHolder, like in Sunshine.
        //See: https://github.com/udacity/Sunshine/blob/6.10-update-map-intent/app/src/main/java/com/example/android/sunshine/app/ForecastAdapter.java
        public final ImageView imageView;
        public final TextView titleView;
        public final TextView timeView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.gridview_item_backgroudimage);
            titleView = (TextView) view.findViewById(R.id.gridview_item_eventname);
            timeView = (TextView) view.findViewById(R.id.gridview_item_eventtime);
        }
    }
}
