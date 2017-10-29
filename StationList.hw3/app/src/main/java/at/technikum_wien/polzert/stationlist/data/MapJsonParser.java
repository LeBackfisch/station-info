package at.technikum_wien.polzert.stationlist.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Sebastian on 29.10.2017.
 */

public class MapJsonParser {
    public MapJsonParser(){
    }

    public static Map<String, Object> toMap(InputStream is) throws JSONException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String inputStr;
        while ((inputStr = reader.readLine()) != null)
            builder.append(inputStr);
        return toMap(new JSONObject(builder.toString()));
    }

    private static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> ret = new HashMap<>();
        Iterator<String> keys = object.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            ret.put(key, getValue(object.get(key)));
        }
        return ret;
    }

    private static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> ret = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            ret.add(getValue(array.get(i)));
        }
        return ret;
    }

    private static Object getValue(Object value) throws JSONException {
        if(value instanceof JSONArray) {
            value = toList((JSONArray) value);
        }
        else if(value instanceof JSONObject) {
            value = toMap((JSONObject) value);
        }
        return value;
    }
}
