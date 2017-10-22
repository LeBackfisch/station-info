package at.technikum_wien.polzert.stationlist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public abstract class LocationAwareFragment extends Fragment {
  private static final String LOG_TAG = LocationAwareFragment.class.getCanonicalName();
  private FusedLocationProviderClient mFusedLocationClient;
  private LocationRequest mLocationRequest;
  private LocationCallback mLocationCallback;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

    mLocationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        if (locationResult != null && locationResult.getLocations().size() > 0)
          LocationAwareFragment.this.onLocationResult(locationResult.getLocations().get(locationResult.getLocations().size() - 1));
      }
    };

    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(10000);
    mLocationRequest.setFastestInterval(5000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (checkPermissions()) {
      startLocationUpdates();
    } else if (!checkPermissions()) {
      requestPermissions();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    stopLocationUpdates();
  }

  private void startLocationUpdates() {
    try {
      Log.d(LOG_TAG, "Starting location updates.");
      mFusedLocationClient.getLastLocation()
        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
          @Override
          public void onSuccess(Location location) {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
              onLocationResult(location);
              try
              {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
              }
              catch (SecurityException ex) {
                Log.e(LOG_TAG, "Could not start location updates.", ex);
              }
            }
          }
        })
      .addOnFailureListener(getActivity(), new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
          }
          catch (SecurityException ex) {
            Log.e(LOG_TAG, "Could not start location updates.", ex);
          }
        }
      });
    }
    catch (SecurityException ex) {
      Log.e(LOG_TAG, "Could not start location updates.", ex);
    }
  }

  private void stopLocationUpdates() {
    Log.d(LOG_TAG, "Stopping location updates.");
    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
  }

  private void requestPermissions() {
    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
  }

  private boolean checkPermissions() {
    boolean result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    Log.d(LOG_TAG, "Permission: " + result);
    return result;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == 0) {
      if (grantResults.length <= 0) {
        Log.d(LOG_TAG, "Permission request canceled");
      }
      else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        startLocationUpdates();
        Log.d(LOG_TAG, "Permission granted");
      }
      else {
        Log.d(LOG_TAG, "Permission denied");
      }
    }
  }

  abstract void onLocationResult(Location location);
}
