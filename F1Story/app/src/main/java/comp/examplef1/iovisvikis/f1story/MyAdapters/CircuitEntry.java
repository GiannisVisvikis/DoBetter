package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 30/3/2017.
 */

public class CircuitEntry{


    private String url, name, langt, longt, country, locality, id;

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getLangt() {
        return langt;
    }

    public String getLongt() {
        return longt;
    }

    public String getCountry() {
        return country;
    }

    public String getLocality() {
        return locality;
    }

    public String getId(){return id;}


    public CircuitEntry(JSONObject circuitObject){
        try {
            this.url = circuitObject.getString("url");
            this.name = circuitObject.getString("circuitName");
            this.id = circuitObject.getString("circuitId");

            JSONObject location = circuitObject.getJSONObject("Location");

            this.langt = location.getString("lat");
            this.longt = location.getString("long");
            this.locality = location.getString("locality");
            this.country = location.getString("country");

        }
        catch (JSONException e){
            Log.e("CIRCUITENTRY", e.getMessage());
        }

    }



}//CircuitEntry
