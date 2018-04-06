package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iovisvikis on 28/4/2017.
 */

public class SeasonEndAllDrivers {


    private String season, rounds, url;
    private ArrayList<SeasonEndDrivers> rows;


    public String getSeason() {
        return season;
    }

    public String getRounds() {
        return rounds;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<SeasonEndDrivers> getRows() {
        return rows;
    }


    public SeasonEndAllDrivers(JSONObject root){

        this.rows = new ArrayList<>();

        try {
            this.season = root.getString("season");
            this.rounds = root.getString("round");
            this.url = "https://en.wikipedia.org/wiki/" + season + "_Formula_One_season";

            JSONArray standings = root.getJSONArray("DriverStandings");

            for(int index=0; index<standings.length(); index++){

                JSONObject seasonEntry = standings.getJSONObject(index);
                SeasonEndDrivers row = new SeasonEndDrivers(seasonEntry);

                rows.add(row);
            }
        }
        catch (JSONException e){
            Log.e("SSNENDDRVRSADAPTER", e.getMessage());
        }

    }




}//SeasonEndAllDrivers


