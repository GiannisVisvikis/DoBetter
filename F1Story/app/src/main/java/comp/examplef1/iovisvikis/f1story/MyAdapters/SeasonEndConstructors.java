package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 28/4/2017.
 */

public class SeasonEndConstructors {

    private String position, points, wins, season;
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

    public Constructor getConstructor() {
        return constructor;
    }



    public SeasonEndConstructors(JSONObject resultRow, String season){

        try{

            this.position = resultRow.getString("position");
            this.points = resultRow.getString("points");
            this.wins = resultRow.getString("wins");
            this.season = season;

            this.constructor = new Constructor(resultRow.getJSONObject("Constructor"));

        }
        catch (JSONException e){
            Log.e("SsnEnsCnstrctrs", e.getMessage());
        }

    }





}//SeasonEndConstructors
