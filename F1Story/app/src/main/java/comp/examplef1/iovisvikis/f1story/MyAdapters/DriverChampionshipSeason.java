package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 28/6/2017.
 */

public class DriverChampionshipSeason
{

    private String season, wins, rounds, points;

    private Constructor constructor;



    public Constructor getConstructor() {
        return constructor;
    }

    public String getSeason() {
        return season;
    }

    public String getWins() {
        return wins;
    }

    public String getRounds() {
        return rounds;
    }

    public String getPoints() {
        return points;
    }



    public DriverChampionshipSeason(JSONObject listObject)
    {
        try
        {
            this.season = listObject.getString("season");
            this.rounds = listObject.getString("round");

            JSONObject standingsObject = listObject.getJSONArray("DriverStandings").getJSONObject(0);

            this.wins = standingsObject.getString("wins");
            this.points = standingsObject.getString("points");

            JSONArray constructorsArray = standingsObject.getJSONArray("Constructors");

            //get the last one
            JSONObject constructorJSON = constructorsArray.getJSONObject(constructorsArray.length() - 1);

            this.constructor = new Constructor(constructorJSON);
        }
        catch (JSONException je)
        {
            Log.e("DrvrChmpnshpSsn", je.getMessage());
        }



    }


}
