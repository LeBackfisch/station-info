package at.technikum_wien.polzert.stationlist.data;

import java.util.List;

/**
 * Created by Sebastian on 24.10.2017.
 */
public class StationListEvent {

    public final List<Station> stationList;

    public StationListEvent(List<Station> stations) {
        stationList = stations;
    }
}