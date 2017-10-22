package at.technikum_wien.polzert.stationlist.data;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;

public class Station implements Parcelable {
    private String name;
    private double latitude;
    private double longitude;
    private HashSet<String> lines;

    public Station() {
    }

    public Station(String name, double latitude, double longitude, HashSet<String> lines) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public HashSet<String> getLines() {
        return lines;
    }

    public void setLines(HashSet<String> lines) {
        this.lines = lines;
    }

    public List<String> getLineList() {
        List<String> ret = new LinkedList<String>(lines);
        Collections.sort(ret, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });
        return ret;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeDouble(getLatitude());
        dest.writeDouble(getLongitude());
        dest.writeList(new LinkedList<String>(getLines()));
    }

    public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>() {
      public Station createFromParcel(Parcel in) {
        return new Station(in);
      }
      public Station[] newArray(int size) {
          return new Station[size];
        }
    };

    private Station(Parcel in) {
        setName(in.readString());
        setLatitude(in.readDouble());
        setLongitude(in.readDouble());
        List<String> lineList = new LinkedList<>();
        in.readList(lineList, getClass().getClassLoader());
        setLines(new HashSet<>(lineList));
    }

    public boolean isSTrainStation() {
        for (String line : getLines())
            if (line.startsWith("S"))
                return true;
        return false;
    }

    public boolean isSubwayStation() {
        for (String line : getLines())
            if (line.startsWith("U"))
                return true;
        return false;
    }
}
