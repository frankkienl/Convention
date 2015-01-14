package nl.frankkie.convention;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by FrankkieNL on 14-1-2015.
 */
public class EasterEggActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        setContentView(wv);
        wv.loadUrl("http://frankkie.nl/pony/livewallpaper/main.html");
        Toast.makeText(this,"You found the Easter Egg!",Toast.LENGTH_LONG).show();
    }
}
