package at.technikum_wien.polzert.stationlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import at.technikum_wien.polzert.stationlist.data.Station;
import at.technikum_wien.polzert.stationlist.data.StationListEvent;

public class StationListFragment extends LocationAwareFragment  implements StationAdapter.StationItemClickedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
  private static final String LOG_TAG = MainActivity.class.getCanonicalName();

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

  @Subscribe
  public void onStationEvent(StationListEvent event) {
   switchStations(event.stationList);
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    stations = ((MainActivity)getActivity()).getStationList();
    View rootView = inflater.inflate(R.layout.fragment_station_list, container, false);
    mStationsRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_stations);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    mStationsRecyclerView.setLayoutManager(layoutManager);
    mAdapter = new StationAdapter(this);
    mStationsRecyclerView.setAdapter(mAdapter);

    List<Station> stations =((MainActivity)getActivity()).getStationList();
    mAdapter.switchStations(stations);
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

 public void switchStations(List<Station> data){
  mAdapter.switchStations(data);
}


  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(getString(R.string.pref_strain_visible_key)) || key.equals(getString(R.string.pref_subway_visible_key))) {
    }
  }
}
