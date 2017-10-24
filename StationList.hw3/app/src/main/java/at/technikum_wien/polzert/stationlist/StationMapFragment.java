package at.technikum_wien.polzert.stationlist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import at.technikum_wien.polzert.stationlist.data.Station;
import at.technikum_wien.polzert.stationlist.data.StationListEvent;
import at.technikum_wien.polzert.stationlist.data.StationParser;


public class StationMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    MapView mapView;
    View mView;
    private static final int STATION_LOADER = 22;
    private Toast mToast;
    private List<Station> stations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_station_map, container, false);
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @Subscribe
    public void onStationEvent(StationListEvent event) {
        setMarkers(event.stationList);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) mView.findViewById(R.id.full_map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(false);

    }

    public void setMarkers(List<Station> data){
       int i = 0;
        LatLng stationMarker = null;
        while(i < data.size()){
            Station station = data.get(i);
            stationMarker = new LatLng(station.getLatitude(), station.getLongitude());
            map.addMarker(new MarkerOptions().position(stationMarker).title(station.getName()));
            i++;
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(stationMarker,10));
    }

}