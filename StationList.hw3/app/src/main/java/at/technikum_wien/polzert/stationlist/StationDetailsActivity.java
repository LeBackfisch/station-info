package at.technikum_wien.polzert.stationlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import at.technikum_wien.polzert.stationlist.data.Station;

public class StationDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details);

        if (savedInstanceState == null) {
            StationDetailsFragment fragment = new StationDetailsFragment();
            Bundle args = new Bundle();

            if (getIntent().getParcelableExtra(StationDetailsFragment.STATION_EXTRA) != null)
                args.putParcelable(StationDetailsFragment.STATION_EXTRA, getIntent().getParcelableExtra(StationDetailsFragment.STATION_EXTRA));

            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_display_container, fragment)
                .commit();
        }
    }
}
