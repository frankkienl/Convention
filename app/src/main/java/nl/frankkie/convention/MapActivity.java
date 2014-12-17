package nl.frankkie.convention;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class MapActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initNavigationDrawer();

        initUI();
    }

    @Override
    public void onBackPressed() {
        if (wv != null) {
            if (wv.canGoBack()) {
                wv.goBack();
                return;
            }
        }
        super.onBackPressed();
    }

    public void initUI() {
        wv = (WebView) findViewById(R.id.map_webview);
        //http://stackoverflow.com/questions/3808532/how-to-set-the-initial-zoom-width-for-a-webview
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.loadUrl("file:///android_asset/map.html");
        wv.getSettings().setBuiltInZoomControls(true);
    }


    public void initNavigationDrawer() {
        //call from onCreate
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * Callback from Hamburger-menu
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Util.navigateFromNavDrawer(this, position);
    }
}
