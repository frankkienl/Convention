package nl.frankkie.convention;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

/**
 * Created by fbouwens on 23-01-15.
 */
public class QrListAdapter extends CursorAdapter {

    public QrListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.qr_listview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.mName.setText(cursor.getString(QrListFragment.COL_NAME));
        viewHolder.mDescription.setText(cursor.getString(QrListFragment.COL_DESCRIPTION));
        String imageStr = cursor.getString(QrListFragment.COL_IMAGE);
        int foundTime = cursor.getInt(QrListFragment.COL_FOUND_TIME);
        if (foundTime > 0) { //is found? non-zero is yes.
            //When found, show image.
            Ion.with(context)
                    .load(imageStr)
                    .withBitmap()
                    .error(R.drawable.ic_launcher2)
                    .placeholder(R.drawable.ic_launcher2)
                    .intoImageView(viewHolder.mImage);
            //description
            viewHolder.mDescription.setText(cursor.getString(QrListFragment.COL_DESCRIPTION));
            viewHolder.mName.setText(cursor.getString(QrListFragment.COL_NAME));
        } else {
            //Not found, check if still allowed to show image
            if (QrListFragment.showImage) {
                Ion.with(context)
                        .load(imageStr)
                        .withBitmap()
                        .error(R.drawable.ic_launcher2_gray)
                        .placeholder(R.drawable.ic_launcher2_gray)
                        .intoImageView(viewHolder.mImage);
            } else {
                //Not found, not allowed to show image
                viewHolder.mImage.setImageResource(R.drawable.ic_launcher2_gray);
            }
        }
    }

    public class ViewHolder {
        public final ImageView mImage;
        public final TextView mName;
        public final TextView mDescription;

        public ViewHolder(View view) {
            this.mImage = (ImageView) view.findViewById(R.id.qrlistitem_image);
            this.mName = (TextView) view.findViewById(R.id.qrlistitem_name);
            this.mDescription = (TextView) view.findViewById(R.id.qrlistitem_description);
        }
    }
}
