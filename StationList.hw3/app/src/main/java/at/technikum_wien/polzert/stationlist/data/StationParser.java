package at.technikum_wien.polzert.stationlist.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;
import android.util.JsonReader;

import at.technikum_wien.polzert.stationlist.R;

public abstract class StationParser {
    public static List<Station> parseJson(Context context, InputStream is) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        List<Station> stations = new LinkedList<>();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("type")) {
                    reader.skipValue();
                }
                else if (name.equals("totalFeatures")) {
                    reader.skipValue();
                }
                else if (name.equals("features")) {
                    stations = readFeaturesArray(context, reader);
                }
                else if (name.equals("crs")) {
                    readCrs(reader);
                }
            }
            reader.endObject();
        } finally {
            reader.close();
        }
        Collections.sort(stations, new Comparator<Station>() {
            @Override
            public int compare(Station a, Station b)
            {
                return  a.getName().compareTo(b.getName());
            }
        });
        return stations;
    }

    private static List<Station> readFeaturesArray(Context context, JsonReader reader) throws IOException {
        HashMap<String, Station> stations = new HashMap<>();
        boolean strain = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_strain_visible_key),
                                                                                          context.getResources().getBoolean(R.bool.pref_strain_visible_default));
        boolean subway = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_subway_visible_key),
                                                                                           context.getResources().getBoolean(R.bool.pref_subway_visible_default));

        reader.beginArray();
        while(reader.hasNext()) {
            final Station station = readStation(reader);

            if (stations.containsKey(station.getName())) {
                Station oldStation = stations.get(station.getName());
                for (String line : station.getLines())
                    oldStation.getLines().add(line);
            }
            else
                stations.put(station.getName(), station);
        }
        reader.endArray();
        LinkedList<Station> ret = new LinkedList<>();
        for (Station s : stations.values()) {
            if ((strain && s.isSTrainStation()) || (subway && s.isSubwayStation()))
                ret.add(s);
        }
        return ret;
    }

    private static Station readStation(JsonReader reader) throws IOException {
        double lat = 0, lon = 0;
        String stationName = "";
        String stationKey = "";
        HashSet<String> lines = new HashSet<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("type")) {
                reader.skipValue();
            }
            else if (name.equals("id")) {
                stationKey = reader.nextString();
            }
            else if (name.equals("geometry")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name2 = reader.nextName();
                    if (name2.equals("type")) {
                        reader.skipValue();
                    }
                    else if (name2.equals("coordinates")) {
                        reader.beginArray();
                        lon = reader.nextDouble();
                        lat = reader.nextDouble();
                        reader.endArray();
                    }
                }
                reader.endObject();
            }
            else if (name.equals("geometry_name")) {
                reader.skipValue();
            }
            else if (name.equals("properties")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name2 = reader.nextName();
                    if (name2.equals("OBJECTID")) {
                        reader.skipValue();
                    }
                    else if (name2.equals("HTXT")) {
                        stationName = reader.nextString();
                    }
                    else if (name2.equals("HTXTK")) {
                        reader.skipValue();
                    }
                    else if (name2.equals("HLINIEN")) {
                        String lineString = reader.nextString();
                        StringTokenizer tok = new StringTokenizer(lineString, ", ");
                        while (tok.hasMoreTokens()) {
                            lines.add(tok.nextToken());
                        }
                    }
                    else if (name2.equals("DIVA_ID")) {
                        reader.skipValue();
                    }
                    else if (name2.equals("SE_ANNO_CAD_DATA")) {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            }
        }
        reader.endObject();
        return new Station(stationName, lat, lon, lines);
    }

    private static void readCrs(JsonReader reader) throws IOException{
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("type")) {
                reader.skipValue();
            } else if (name.equals("properties")) {
                readCrsProperties(reader);
            }
        }
        reader.endObject();
    }

    private static void readCrsProperties(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                reader.skipValue();
            }
        }
        reader.endObject();
    }
}
