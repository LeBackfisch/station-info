package at.technikum_wien.polzert.stationlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import at.technikum_wien.polzert.stationlist.data.Station;
import at.technikum_wien.polzert.stationlist.data.StationParser;

public class StationListFragment extends LocationAwareFragment  implements StationAdapter.StationItemClickedListener,
       // LoaderManager.LoaderCallbacks<List<Station>>,
        SharedPreferences.OnSharedPreferenceChangeListener {
  private static final String LOG_TAG = MainActivity.class.getCanonicalName();
 // private static final int STATION_LOADER = 22;

  private RecyclerView mStationsRecyclerView;
  private StationAdapter mAdapter;
  private Toast mToast;
  private List<Station> stations = new ArrayList<>();

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    stations = ((Main2Activity)getActivity()).getStationList();
    View rootView = inflater.inflate(R.layout.fragment_station_list, container, false);
    mStationsRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_stations);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    mStationsRecyclerView.setLayoutManager(layoutManager);
    mAdapter = new StationAdapter(this);
    mStationsRecyclerView.setAdapter(mAdapter);

    List<Station> stations =((Main2Activity)getActivity()).getStationList();
    mAdapter.switchStations(stations);
  //  getActivity().getSupportLoaderManager().restartLoader(STATION_LOADER, null, this);
    return rootView;
  }

  @Override
  void onLocationResult(Location location) {
    Log.d(LOG_TAG, "New location");
    mAdapter.setLocation(location);
  }

  @Override
  public void onStationItemClicked(Station station) {
    Intent intent = new Intent(getActivity(), StationDetailsActivity.class);
    intent.putExtra(StationDetailsFragment.STATION_EXTRA, station);
    startActivity(intent);
  }

/**  @Override
  public Loader<List<Station>> onCreateLoader(int id, Bundle args) {
    return new AsyncTaskLoader<List<Station>>(getActivity()) {
      @Override
      protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
      }

      @Override
      public List<Station> loadInBackground() {
        try {
          URL stationURL = new URL(getString(R.string.datasource_url));
          Log.d(LOG_TAG, "Starting station list download from " + stationURL.toString() + " ...");
          HttpURLConnection connection = (HttpURLConnection) stationURL.openConnection();
          connection.setConnectTimeout(5000);
          if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.d(LOG_TAG, "Unexpected response code: " + connection.getResponseCode() + ", while trying to load station list.");
            return null;
          }
          Log.d(LOG_TAG, "Starting station list parser ...");
          List<Station> stationList = StationParser.parseJson(getContext(), connection.getInputStream());
          Log.d(LOG_TAG, "Station list parsing finished.");
          return stationList;
        }
        catch(IOException ex) {
          Log.d(LOG_TAG, "Exception while loading the station list.", ex);
          return null;
        }
      }
    };
  }

  @Override
  public void onLoadFinished(Loader<List<Station>> loader, List<Station> data) {
    Log.d(LOG_TAG, "Station list loading finished.");
    if (data == null) {
      if (mToast != null)
        mToast.cancel();
      mToast = Toast.makeText(getActivity(), R.string.station_load_error, Toast.LENGTH_LONG);
      mToast.show();
      Log.e(LOG_TAG, "Could not load station data.");
    }
    else
      mAdapter.switchStations(data);
  }

  @Override
  public void onLoaderReset(Loader<List<Station>> loader) {
  }
 **/

 public void switchStations(List<Station> data){
  mAdapter.switchStations(data);
}

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.main, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == R.id.action_reload) {
     // getActivity().getSupportLoaderManager().restartLoader(STATION_LOADER, null, this);
      return true;
    }
    if (itemId == R.id.action_settings) {
      Intent intent = new Intent(getActivity(), SettingsActivity.class);
      startActivity(intent);
      return true;
    }
    if(itemId == R.id.action_map) {
      Intent intent = new Intent(getActivity(), MapsActivity.class);
      startActivity(intent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(getString(R.string.pref_strain_visible_key)) || key.equals(getString(R.string.pref_subway_visible_key))) {
    //  getActivity().getSupportLoaderManager().restartLoader(STATION_LOADER, null, this);
    }
  }
}
