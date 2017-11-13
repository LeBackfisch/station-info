package at.technikum_wien.polzert.stationlist.SettingsStrategy;

import java.util.List;

import at.technikum_wien.polzert.stationlist.data.Station;

/**
 * Created by Sebastian on 13.11.2017.
 */

public interface State {
    List<Station> getStations(List<Station> stations);
}
