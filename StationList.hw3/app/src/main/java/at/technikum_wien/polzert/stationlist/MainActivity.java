package at.technikum_wien.polzert.stationlist;

import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TabHost;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import at.technikum_wien.polzert.stationlist.data.SectionsPageAdapter;
import at.technikum_wien.polzert.stationlist.data.Station;
import at.technikum_wien.polzert.stationlist.data.StationListEvent;
import at.technikum_wien.polzert.stationlist.data.StationParser;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Station>> {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private static final int STATION_LOADER = 22;
    private List<Station> stationList = new ArrayList<>();
    private Toast mToast;
    private FragmentManager fm = getSupportFragmentManager();
    private StationListFragment stationListFragment;
    private StationMapFragment stationMapFragment;

    public List<Station> getStationList(){
        getSupportLoaderManager().restartLoader(STATION_LOADER, null, this);
        return stationList;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        stationListFragment = new StationListFragment();
        stationMapFragment = new StationMapFragment();
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setutpViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setutpViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(stationListFragment, "StationList");
        adapter.addFragment(stationMapFragment, "StationMap");
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_reload) {
             getSupportLoaderManager().restartLoader(STATION_LOADER, null, this);
            return true;
        }
        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(itemId == R.id.action_location){
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();

            Location loc = stationMapFragment.GetLocation();
            if(loc != null)
            {
                stationMapFragment.ZoomMarker(loc);
            }
        }
        if(itemId == R.id.action_remove_route){

             if(stationMapFragment.GetLocation() != null);
             {
                stationMapFragment.RemovePolyline();
             }
        }
        if(itemId == R.id.action_location_update){
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            TabLayout.Tab tab = tabLayout.getTabAt(1);


            Location loc = stationMapFragment.GetLocation();
            if(loc != null)
            {
                if(stationMapFragment.checkifLocationChanged()){
                    loc = stationMapFragment.GetLocation();
                    tab.select();
                    stationMapFragment.ZoomMarker(loc);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Station>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Station>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public List<Station> loadInBackground() {
                try {
                    URL stationURL = new URL(getString(R.string.datasource_url));
                    HttpURLConnection connection = (HttpURLConnection) stationURL.openConnection();
                    connection.setConnectTimeout(5000);
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                    List<Station> stationList = StationParser.parseJson(getContext(), connection.getInputStream());
                    return stationList;
                }
                catch(IOException ex) {
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Station>> loader, List<Station> data) {
        if (data == null) {
            if (mToast != null)
                mToast.cancel();
            mToast = Toast.makeText(this, R.string.station_load_error, Toast.LENGTH_LONG);
            mToast.show();
        }
        else
            stationList = data;
        EventBus.getDefault().post(new StationListEvent(data));
    }

    @Override
    public void onLoaderReset(Loader<List<Station>> loader) {
    }


}
