package nl.frankkie.convention;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MapActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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
        setContentView(R.layout.activity_map);

        initNavigationDrawer();

        initUI();
    }

    public void initUI() {
        //TODO
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback from Hamburger-menu
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0: {
                //TODO: my schedule activity
                break;
            }
            case 1: {
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.setClass(this, EventListActivity.class);
                startActivity(i);
                break;
            }
            case 2: {
                //do nothing, you are already here
                break;
            }
            case 3: {
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.setClass(this, AboutActivity.class);
                startActivity(i);
                break;
            }
        }
    }
}
