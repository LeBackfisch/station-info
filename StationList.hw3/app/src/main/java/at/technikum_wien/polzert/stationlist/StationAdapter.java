package at.technikum_wien.polzert.stationlist;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import at.technikum_wien.polzert.stationlist.data.Station;

class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {
    private List<Station> mStations;
    private StationItemClickedListener mItemClickedListener;
    private Location mLocation;
    private AsyncTask mReorderTask;

    class StationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mStationNameTextView;
        private ImageView mStationIconImageView;
        private TextView mStationDistanceTextView;

        StationViewHolder(View itemView) {
            super(itemView);
            mStationNameTextView = (TextView) itemView.findViewById(R.id.tv_station_name);
            mStationIconImageView = (ImageView) itemView.findViewById(R.id.iv_station_icon);
            mStationDistanceTextView = (TextView) itemView.findViewById(R.id.tv_station_distance);
            itemView.setOnClickListener(this);
        }

        void bind(Station station) {
            mStationNameTextView.setText(station.getName());
            Utils.showDistance(mStationDistanceTextView, station, mLocation);
            if (station.isSTrainStation())
                if (station.isSubwayStation())
                    mStationIconImageView.setImageResource(R.mipmap.ic_both);
                else
                    mStationIconImageView.setImageResource(R.mipmap.ic_strain);
            else if (station.isSubwayStation())
                mStationIconImageView.setImageResource(R.mipmap.ic_subway);
            else
                mStationIconImageView.setImageResource(R.mipmap.ic_unknown);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            if (index < mStations.size())
                mItemClickedListener.onStationItemClicked(mStations.get(getAdapterPosition()));
        }
    }

    interface StationItemClickedListener {
        void onStationItemClicked(Station station);
    }

    public StationAdapter(List<Station> mStations, StationItemClickedListener itemClickedListener) {
        this.mStations = mStations;
        this.mItemClickedListener = itemClickedListener;
    }

    public StationAdapter(StationItemClickedListener itemClickedListener) {
        this.mStations = new LinkedList<>();
        this.mItemClickedListener = itemClickedListener;
    }

    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.station_item, parent, false);
        return new StationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StationViewHolder holder, int position) {
        if (position < mStations.size())
            holder.bind(mStations.get(position));
    }

    @Override
    public int getItemCount() {
        return mStations.size();
    }

    public void switchStations(List<Station> stations) {
        reorderStations(stations);
    }

    private void reorderStations(final List<Station> stations) {
        if (mReorderTask != null)
            mReorderTask.cancel(true);
        mReorderTask = new AsyncTask<Void, Void, List<Station>>() {
            private List<Station> mNewList;
            private Location mNewLocation;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mNewList = stations;
                mNewLocation = mLocation;
            }

            @Override
            protected List<Station> doInBackground(Void... params) {
                if (mNewLocation != null)
                    Collections.sort(mNewList, new StationDistanceComparator());
                return mNewList;
            }

            @Override
            protected void onPostExecute(List<Station> stations) {
                super.onPostExecute(stations);
                mStations = mNewList;
                notifyDataSetChanged();
            }

            class StationDistanceComparator implements Comparator<Station> {
                @Override
                public int compare(Station a, Station b) {
                    return Float.valueOf(Utils.calcDistance(a, mNewLocation)).compareTo(Utils.calcDistance(b, mNewLocation));
                }
            }

        }.execute();
    }

    public void setLocation(Location mLocation) {
        this.mLocation = mLocation;
        reorderStations(mStations);
    }
}
