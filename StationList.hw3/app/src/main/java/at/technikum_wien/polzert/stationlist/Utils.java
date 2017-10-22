package at.technikum_wien.polzert.stationlist;

import android.location.Location;
import android.widget.TextView;

import java.util.Locale;

import at.technikum_wien.polzert.stationlist.data.Station;

class Utils {
  static void showDistance(TextView textView, Station station, Location location) {
    if (textView == null)
      return;
    if (location == null || station == null) {
      textView.setText("---");
      return;
    }
    float distance = calcDistance(station, location);
    if (distance < 1000)
      textView.setText(String.format(Locale.getDefault(), "%1.3fm", distance));
    else
      textView.setText(String.format(Locale.getDefault(), "%1.3fkm", distance / 1000.0));
  }

  static float calcDistance(Station a, Location location) {
    float f[] = new float[1];
    Location.distanceBetween(a.getLatitude(), a.getLongitude(), location.getLatitude(), location.getLongitude(), f);
    return f[0];
  }
}
