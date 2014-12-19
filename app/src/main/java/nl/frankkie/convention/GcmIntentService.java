package nl.frankkie.convention;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by FrankkieNL on 19-12-2014.
 */
public class GcmIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GcmIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
