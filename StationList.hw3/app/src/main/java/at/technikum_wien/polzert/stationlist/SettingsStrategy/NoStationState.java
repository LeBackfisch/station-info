package at.technikum_wien.polzert.stationlist.SettingsStrategy;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import at.technikum_wien.polzert.stationlist.data.Station;

/**
 * Created by Sebastian on 13.11.2017.
 */

public class NoStationState implements State {
    List<Station> stationList = new ArrayList<>();
    private static final String TAG = "NoStation";
    @Override
    public List<Station> getStations(List<Station> stations) {
        List<Station> stations1 = (ArrayList)((ArrayList) stations).clone();
        Log.d(TAG,""+stationList.size());
        return stationList;
    }
}
