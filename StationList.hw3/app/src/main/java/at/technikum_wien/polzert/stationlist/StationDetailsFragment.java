package at.technikum_wien.polzert.stationlist;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import at.technikum_wien.polzert.stationlist.data.Station;

public class StationDetailsFragment extends LocationAwareFragment implements OnMapReadyCallback {
  public static final String STATION_EXTRA = "station";

  private TextView mDistanceTextView;
  private Station mStation;
  private Location mLocation;
  private MapView mapView = null;
  private GoogleMap map = null;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_station_details, container, false);
    Bundle args = getArguments();
    if (args != null) {
      mStation = args.getParcelable(STATION_EXTRA);
      mDistanceTextView = (TextView) rootView.findViewById(R.id.tv_station_details_distance);

      if (mStation != null && !mStation.equals("")) {
        ((TextView) rootView.findViewById(R.id.tv_station_details_name)).setText(mStation.getName());
        ((TextView) rootView.findViewById(R.id.tv_station_details_latitude)).setText(String.format("%1.3f", mStation.getLatitude()));
        ((TextView) rootView.findViewById(R.id.tv_station_details_longitude)).setText(String.format("%1.3f", mStation.getLongitude()));
        ((TextView) rootView.findViewById(R.id.tv_station_details_lines)).setText(TextUtils.join("\n", mStation.getLineList()));
        Utils.showDistance(mDistanceTextView, mStation, mLocation);
        ImageView stationIconImageView = (ImageView) rootView.findViewById(R.id.iv_station_details_icon);
        if (mStation.isSTrainStation())
          if (mStation.isSubwayStation())
            stationIconImageView.setImageResource(R.mipmap.ic_both);
          else
            stationIconImageView.setImageResource(R.mipmap.ic_strain);
        else if (mStation.isSubwayStation())
          stationIconImageView.setImageResource(R.mipmap.ic_subway);
        else
          stationIconImageView.setImageResource(R.mipmap.ic_unknown);
      }
      mapView = (MapView) rootView.findViewById(R.id.map);
      mapView.onCreate(savedInstanceState);
      mapView.getMapAsync(this);

    }
    return rootView;
  }

  @Override
  void onLocationResult(Location location) {
    mLocation = location;
    Utils.showDistance(mDistanceTextView, mStation, mLocation);
  }


  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;
    map.getUiSettings().setZoomControlsEnabled(false);
    LatLng stationMarker = new LatLng(mStation.getLatitude(), mStation.getLongitude());
    map.addMarker(new MarkerOptions().position(stationMarker).title(mStation.getName()));
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(stationMarker,10));
  }
}


