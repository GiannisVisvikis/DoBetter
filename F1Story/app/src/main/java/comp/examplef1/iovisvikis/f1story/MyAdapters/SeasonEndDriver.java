package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 26/6/2017.
 */

public class SeasonEndDriver extends Driver {

    private String points, position, wins;

    public String getPoints() {
        return points;
    }

    public String getPosition() {
        return position;
    }

    public String getWins() {
        return wins;
    }



    public SeasonEndDriver(JSONObject driver, JSONObject driverInfo){

        super(driver);

        try {
            this.points = driverInfo.getString("points");
            this.wins = driverInfo.getString("wins");
            this.position = driverInfo.getString("position");
        }
        catch (JSONException je){
            Log.e("SsnEndDrvr", je.getMessage());
        }


    }

}
