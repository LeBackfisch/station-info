package at.technikum_wien.polzert.stationlist.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sebastian on 12.11.2017.
 */

public class StationsContract {
    public static final String CONTENT_AUTHORITY = "at.technikum_wien.polzert.stationlist.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String PATH_STATIONS = "stations";

    public static final class StationsTable implements BaseColumns {
        public static final String TABLE_NAME = "stations";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATIONS).build();

        public static final String CONLUMN_NAME = "station_name";
        public static final String COLUMN_POSITION = "position";
        public static final String COLUMN_LINES = "lines";

        public static final Uri getTaskWithUri (long id){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }
}
