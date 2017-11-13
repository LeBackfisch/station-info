package at.technikum_wien.polzert.stationlist.data;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import at.technikum_wien.polzert.stationlist.MainActivity;
import at.technikum_wien.polzert.stationlist.R;

/**
 * Created by Sebastian on 13.11.2017.
 */

public class UpdateJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        emptyDB();
        fillDB();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    public void emptyDB(){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    getContentResolver().delete(StationsContract.StationsTable.CONTENT_URI, null, null);
                }
                catch (Exception e){

                }
                return null;
            }
        }.execute();
    }


    public void fillDB(){
        new AsyncTask<Void, Void, Void>() {
            List<Station> stationList = new LinkedList<Station>();
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL stationURL = new URL(getString(R.string.datasource_url));
                    HttpURLConnection connection = (HttpURLConnection) stationURL.openConnection();
                    connection.setConnectTimeout(5000);
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                    stationList = StationParser.parseJson(getApplicationContext(), connection.getInputStream());
                }
                catch(IOException ex) {
                    return null;
                }

                for (int i = 0; i < stationList.size(); i++){
                    ContentValues contentValues = new ContentValues();
                    Station station = stationList.get(i);
                    contentValues.put(StationsContract.StationsTable.CONLUMN_NAME, station.getName());
                    String positon = station.getLatitude()+", "+station.getLongitude();
                    contentValues.put(StationsContract.StationsTable.COLUMN_POSITION, positon);
                    String linesstring = "";
                    String[] lines = station.getLines().toArray(new String[station.getLines().size()]);

                    for (int j = 0; j < lines.length; j++){
                        linesstring+=lines[j]+",";
                    }
                    linesstring = linesstring.substring(0, linesstring.length() - 2);
                    contentValues.put(StationsContract.StationsTable.COLUMN_LINES, linesstring);
                    Uri uri = getContentResolver().insert(StationsContract.StationsTable.CONTENT_URI, contentValues);
                }
                return null;
            }
        }.execute();
    }
}
