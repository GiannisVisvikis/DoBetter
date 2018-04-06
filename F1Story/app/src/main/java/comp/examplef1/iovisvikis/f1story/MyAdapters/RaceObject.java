package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iovisvikis on 17/4/2017.
 */

public class RaceObject{


    private String season, raceUrl, circuitName, country, locality, lang, longt;

    private ArrayList<UIResult> resultRows;


    public String getSeason() {
        return season;
    }

    public String getRaceUrl() {
        return raceUrl;
    }

    public String getCircuitName() {
        return circuitName;
    }

    public String getCountry() {
        return country;
    }

    public String getLocality() {
        return locality;
    }

    public String getLang() {
        return lang;
    }

    public String getLongt() {
        return longt;
    }

    public ArrayList<UIResult> getResultRows() {
        return resultRows;
    }


    public RaceObject(JSONObject race){

        this.resultRows = new ArrayList<>();

        try{
            this.season = race.getString("season");
            this.raceUrl = race.getString("url");

            JSONObject circuit = race.getJSONObject("Circuit");
            this.circuitName = circuit.getString("circuitName");

            JSONObject location = circuit.getJSONObject("Location");

            this.locality = location.getString("locality");
            this.lang = location.getString("lat");
            this.longt = location.getString("long");
            this.country = location.getString("country");

            JSONArray results = race.getJSONArray("Results");

            for(int index=0; index<results.length(); index++){
                JSONObject result = results.getJSONObject(index);
                UIResult uiResult = new UIResult(result);
                resultRows.add(uiResult);
            }
        }
        catch (JSONException e){
            Log.e("RACEOBJECT", e.getMessage());
        }
    }

}//RaceObject



