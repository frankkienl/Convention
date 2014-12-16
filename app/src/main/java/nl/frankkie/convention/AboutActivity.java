package nl.frankkie.convention;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class AboutActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initNavigationDrawer();

        initUI();
    }

    public void initUI(){
        Button btnViewMap = (Button) findViewById(R.id.about_view_maps);
        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                //i.setData(Uri.parse("https://www.google.nl/maps?t=m&z=15&cid=11779929733433826402"));
                //i.setData(Uri.parse("geo:52.3118607,4.6636143"));
                //i.setData(Uri.parse("geo:0,0?q=52.3118607,4.6636143(Venue)"));
                i.setData(Uri.parse("geo:0,0?q=IJweg%201094%202133%20MH%20Hoofddorp"));
                startActivity(i);
            }
        });
    }


    public void initNavigationDrawer(){
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
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Util.navigateFromNavDrawer(this, position);
    }
}
