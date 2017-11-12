package at.technikum_wien.polzert.stationlist.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;

/**
 * Created by Sebastian on 12.11.2017.
 */

public class StationLoader extends CursorLoader {
    public static final int LOADER_ID = 42;

    private static final String SORT_ORDER =
            StationsContract.StationsTable.CONLUMN_NAME + " DESC";

    public StationLoader(Context context) {
        super(context, StationsContract.StationsTable.CONTENT_URI,
                null, null, null, SORT_ORDER);
    }
}
