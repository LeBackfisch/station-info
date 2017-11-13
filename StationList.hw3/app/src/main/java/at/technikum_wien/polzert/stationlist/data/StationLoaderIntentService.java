package at.technikum_wien.polzert.stationlist.data;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Sebastian on 13.11.2017.
 */

public class StationLoaderIntentService extends IntentService {
    public StationLoaderIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
