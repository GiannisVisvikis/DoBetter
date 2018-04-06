package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 27/4/2017.
 */

public class SeasonEndDrivers{


    private String position, points, wins;
    private Driver driver;
    private Constructor constructor;

    public String getPosition() {
        return position;
    }

    public String getPoints() {
        return points;
    }

    public String getWins() {
        return wins;
    }

    public Driver getDriver() {
        return driver;
    }

    public Constructor getConstructor() {
        return constructor;
    }




    public SeasonEndDrivers(JSONObject resultRow){

        try{

            this.position = resultRow.getString("position");
            this.points = resultRow.getString("points");
            this.wins = resultRow.getString("wins");

            this.driver = new Driver(resultRow.getJSONObject("Driver"));

            JSONArray constructorsArray = resultRow.getJSONArray("Constructors");
            JSONObject constructor = constructorsArray.getJSONObject(0);
            this.constructor = new Constructor(constructor);

        }
        catch (JSONException e){
            Log.e("SEASONENDRIVERS", e.getMessage());
        }
    }


}//SeasonEndDrivers


