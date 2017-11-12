package at.technikum_wien.polzert.stationlist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TabHost;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import at.technikum_wien.polzert.stationlist.data.SectionsPageAdapter;
import at.technikum_wien.polzert.stationlist.data.Station;
import at.technikum_wien.polzert.stationlist.data.StationListEvent;
import at.technikum_wien.polzert.stationlist.data.StationLoader;
import at.technikum_wien.polzert.stationlist.data.StationParser;
import at.technikum_wien.polzert.stationlist.data.StationsContract;

public class MainActivity extends AppCompatActivity implements
       // LoaderManager.LoaderCallbacks<List<Station>>
    LoaderManager.LoaderCallbacks<Cursor>
{

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private static final int STATION_LOADER = 22;
    private List<Station> stationList = new ArrayList<>();
    private Toast mToast;
    private FragmentManager fm = getSupportFragmentManager();
    private StationListFragment stationListFragment;
    private StationMapFragment stationMapFragment;
    private Cursor mCursor;

    public List<Station> getStationList(){
        getSupportLoaderManager().restartLoader(StationLoader.LOADER_ID, null, this);
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

        startLoader(false);
    }

    private void setutpViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(stationListFragment, "StationList");
        adapter.addFragment(stationMapFragment, "StationMap");
        viewPager.setAdapter(adapter);
    }

    private void startLoader(boolean reload) {
        LoaderManager loaderManager = getSupportLoaderManager();
        if (reload)
            loaderManager.restartLoader(StationLoader.LOADER_ID, null, this);
        else
            loaderManager.initLoader(StationLoader.LOADER_ID, null, this);
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
            // getSupportLoaderManager().restartLoader(StationLoader.LOADER_ID, null, this);
            fillDB();
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case StationLoader.LOADER_ID:
                return new StationLoader(this);
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Station> stations = getDataFromCursor(data);
        EventBus.getDefault().post(new StationListEvent(stations));
        swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swapCursor(null);
    }

    public void swapCursor(Cursor cursor){
        if (mCursor != null)
            mCursor.close();
        mCursor = cursor;
    }

    public List<Station> getDataFromCursor(Cursor cursor){
        List<Station> stations = new LinkedList<>();
        if(cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndex("station_name"));
            String position = cursor.getString(cursor.getColumnIndex("position"));
            String lines = cursor.getString(cursor.getColumnIndex("lines"));
            Station station = parseToStation(name, position, lines);
            stations.add(station);
        }
        return  stations;
    }

    public Station parseToStation(String name, String position, String lines){
        Station station = new Station();
        HashSet<String> linesset = new HashSet<>();
        station.setName(name);
        String posParts[] = position.split(",");
        station.setLatitude(Double.parseDouble(posParts[0]));
        station.setLongitude(Double.parseDouble(posParts[1]));
        String linesParts[] = lines.split(",");
        for (int i = 0; i < linesParts.length; i++){
            linesset.add(linesParts[i]);
        }
        station.setLines(linesset);

        return station;
    }

    public void fillDB(){
        new AsyncTask<Void, Void, Void>() {
            List<Station> stationList = new LinkedList<Station>();
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL stationURL = new URL(getString(R.string.datasource_url));
                    HttpURLConnection connection = (HttpURLConnection) stationURL.openConnection();
                    connection.setConnectTimeout(5000);
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                     stationList = StationParser.parseJson(MainActivity.this, connection.getInputStream());
                }
                catch(IOException ex) {
                    return null;
                }

                for (int i = 0; i < stationList.size(); i++){
                    ContentValues contentValues = new ContentValues();
                    Station station = stationList.get(i);
                    contentValues.put(StationsContract.StationsTable.CONLUMN_NAME, station.getName());
                    String positon = station.getLatitude()+", "+station.getLongitude();
                    contentValues.put(StationsContract.StationsTable.COLUMN_POSITION, positon);
                    String linesstring = "";
                    String[] lines = station.getLines().toArray(new String[station.getLines().size()]);

                    for (int j = 0; j < lines.length; j++){
                        linesstring+=lines[j]+", ";
                    }
                    linesstring = linesstring.substring(0, linesstring.length() - 2);
                    contentValues.put(StationsContract.StationsTable.COLUMN_LINES, linesstring);
                    Uri uri = getContentResolver().insert(StationsContract.StationsTable.CONTENT_URI, contentValues);
                }
                return null;
            }
        }.execute();
    }
  /*  @Override
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
        */

}
