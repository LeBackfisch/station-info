package at.technikum_wien.polzert.stationlist;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import at.technikum_wien.polzert.stationlist.data.MapJsonParser;
import at.technikum_wien.polzert.stationlist.data.Station;
import at.technikum_wien.polzert.stationlist.data.StationListEvent;
import at.technikum_wien.polzert.stationlist.data.StationParser;


public class StationMapFragment extends LocationAwareFragment implements OnMapReadyCallback {

    private static final String LOG_TAG = StationMapFragment.class.getCanonicalName();
    private GoogleMap map;
    MapView mapView;
    View mView;
    Location mLocation = null;
    private static final int STATION_LOADER = 22;
    private Toast mToast;
    private List<Station> stations;
    private Marker MyPositionMarker;
    private Polyline polyline;

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

    public Location GetLocation(){
        return mLocation;
    }

    public void ZoomMarker(Location location){

        LatLng mymarkerposition = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mymarkerposition, 15));
    }

    @Override
    void onLocationResult(Location location) {
        mLocation = location;
        LatLng myPositionMarker = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(myPositionMarker).title("My Position"));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.clear();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onStationEvent(StationListEvent event) {
        if(map != null){
            map.clear();
        }
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
        if(mLocation == null){
            mLocation = new Location("");
            mLocation.setLatitude(48.2025);
            mLocation.setLongitude(16.379111);
        }
        map.getUiSettings().setZoomControlsEnabled(true);

    }

    public void setMarkers(List<Station> data){
       int i = 0;

        LatLng stationMarker = null;

        while(i < data.size()){
            Station station = data.get(i);
            stationMarker = new LatLng(station.getLatitude(), station.getLongitude());
           if(station.isSTrainStation() && station.isSubwayStation()){
               BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_both);
               map.addMarker(new MarkerOptions().position(stationMarker).title(station.getName()).icon(icon));
            }
            else if(station.isSTrainStation() && !station.isSubwayStation()){
               BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_strain);
               map.addMarker(new MarkerOptions().position(stationMarker).title(station.getName()).icon(icon));
           }
           else if (!station.isSTrainStation() && station.isSubwayStation()){
               BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_subway);
               map.addMarker(new MarkerOptions().position(stationMarker).title(station.getName()).icon(icon));
           }
           map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
               @Override
               public boolean onMarkerClick(Marker marker) {
                   if(mLocation != null){
                       if(polyline != null){
                           polyline.remove();
                       }

                       String url = "https://maps.googleapis.com/maps/api/directions/json" +
                               "?origin=" + mLocation.getLatitude() + "," + mLocation.getLongitude() +
                               "&destination=" + marker.getPosition().latitude + "," +  marker.getPosition().longitude +
                               "&sensor=false&units=metric&mode=walking" +
                               "&key=" + getString(R.string.google_maps_key);
                       new RouteTask().execute(url);
                   }
                   return true;
               }
           });



            i++;
        }
        if(mLocation != null){
            LatLng myPositionMarker = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            MyPositionMarker = map.addMarker(new MarkerOptions().position(myPositionMarker).title("My Position"));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(stationMarker,10));
    }
    public void RemovePolyline(){
        if(polyline != null){
            polyline.remove();
        }
    }

    public boolean checkifLocationChanged(){
        Location newLocation = GetLocationOnce();
        if(newLocation != null){
            if(newLocation != mLocation){
                mLocation = newLocation;
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }


    private class RouteTask extends AsyncTask<String, Void, List<LatLng>>{

        @Override
        protected void onPostExecute(List<LatLng> points) {
            super.onPostExecute(points);
            PolylineOptions routeOptions = new PolylineOptions()
                    .addAll(points)
                    .width(5)
                    .color(Color.BLUE);
            polyline = map.addPolyline(routeOptions);
        }

        @Override
        protected List<LatLng> doInBackground(String... urlString) {
            HttpsURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                Map<String, Object> response = MapJsonParser.toMap(is);

                List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
                List<Map<String, Object>> legs = (List<Map<String, Object>>) routes.get(0).get("legs");
                List<Map<String, Object>> steps = (List<Map<String, Object>>) legs.get(0).get("steps");

                List<LatLng> points = new ArrayList<>();

                Map<String, Double> coordinate = (Map<String, Double>) steps.get(0).get("start_location");
                points.add(new LatLng(coordinate.get("lat"), coordinate.get("lng")));
                for (int i = 0; i < steps.size(); i++) {
                    coordinate = (Map<String, Double>) steps.get(i).get("end_location");
                    points.add(new LatLng(coordinate.get("lat"), coordinate.get("lng")));
                }

                return points;
            }
            catch (MalformedURLException ex) {
                Log.e(LOG_TAG, "Malformed URL", ex);
            }
            catch (IOException ex) {
                Log.e(LOG_TAG, "I/O error", ex);
            }
            catch (JSONException ex) {
                Log.e(LOG_TAG, "Json Exception", ex);
            }
            finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }
    }
}