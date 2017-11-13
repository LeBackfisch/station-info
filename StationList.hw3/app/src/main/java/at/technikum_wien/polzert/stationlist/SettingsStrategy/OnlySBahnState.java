package at.technikum_wien.polzert.stationlist.SettingsStrategy;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import at.technikum_wien.polzert.stationlist.data.Station;

/**
 * Created by Sebastian on 13.11.2017.
 */

public class OnlySBahnState implements State {
    private static final String TAG = "OnlySBahn";
    @Override
    public List<Station> getStations(List<Station> stations) {
       List<Station> stations1 = (ArrayList)((ArrayList) stations).clone();
        List<Station> stationList = new ArrayList<>();
        for (int i=0; i < stations1.size(); i++){

            if(stations1.get(i).isSTrainStation()){
                stationList.add(stations1.get(i));
            }
        }
        Log.d(TAG,""+stationList.size());
        return stationList;
    }
}
