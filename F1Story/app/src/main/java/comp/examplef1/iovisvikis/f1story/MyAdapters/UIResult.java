package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 17/4/2017.
 */

public class UIResult{

    private String grid, position, points, fastest,  time, status;

    private Driver driver;
    private Constructor constructor;


    public String getGrid() {
        return grid;
    }

    public String getPosition() {
        return position;
    }

    public String getPoints() {
        return points;
    }


    public String getFastest() {
        return fastest;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }



    public Driver getDriver() {
        return driver;
    }

    public Constructor getConstructor() {
        return constructor;
    }



    public UIResult(JSONObject result){
        try{
            this.grid = result.getString("grid");
            this.position = result.getString("position");
            this.points = result.getString("points");
            this.status = result.getString("status");

            JSONObject driver = result.getJSONObject("Driver");
            this.driver = new Driver(driver);

            JSONObject constructor = result.getJSONObject("Constructor");
            this.constructor = new Constructor(constructor);

            JSONObject time = result.getJSONObject("Time");
            this.time = time.getString("time");

            JSONObject fastestLap = result.getJSONObject("FastestLap");
            JSONObject lapTime = fastestLap.getJSONObject("Time");
            this.fastest = lapTime.getString("time");

        }
        catch (JSONException e){
            Log.e("UIResult", e.getMessage());
        }
    }

}//UIResult



