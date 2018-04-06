package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 10/4/2017.
 */

public class ConstructorResultDriver{


    private String position;
    private String points;
    private String laps;
    private  String grid;

    private Driver driver;

    public Driver getDriver(){return driver;}

    public String getPosition() {
        return position;
    }

    public String getPoints() {
        return points;
    }

    public String getLaps() {
        return laps;
    }

    public String getGrid() {
        return grid;
    }



    public ConstructorResultDriver(JSONObject resultObject){

        try{
            this.grid = resultObject.getString("grid");
            this.laps = resultObject.getString("laps");
            this.position = resultObject.getString("position");
            this.points = resultObject.getString("points");

            JSONObject driverObject = resultObject.getJSONObject("Driver");

            this.driver = new Driver(driverObject);

        }catch (JSONException e){
            Log.e("CONSTDRIVADAPT", e.getMessage());
        }

    }


}//ConstructorDriver
