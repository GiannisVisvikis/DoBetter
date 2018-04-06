
package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iovisvikis on 14/3/2017.
 */

public class SomeResult{

    private Driver driver;

    private String season;

    private Constructor constructor;

    private ArrayList<RaceInfo> racesInfo;


    public String getSeason() {
        return season;
    }

    private void setSeason(String season) {
        this.season = season;
    }

    public Driver getDriver() {
        return driver;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public ArrayList<RaceInfo> getRacesInfo() {
        return racesInfo;
    }




    public SomeResult(JSONArray allResultsArray){

        racesInfo = new ArrayList<>();

        try {

            JSONObject basic = allResultsArray.getJSONObject(0);

            setSeason(basic.getString("season"));

            JSONObject resultsRoot = basic.getJSONArray("Results").getJSONObject(0);

            JSONObject driverObj = resultsRoot.getJSONObject("Driver");

            this.driver = new Driver(driverObj);

            JSONObject constructorObj = resultsRoot.getJSONObject("Constructor");

            this.constructor = new Constructor(constructorObj);


            for(int index=0; index < allResultsArray.length(); index++){

                JSONObject resultObject = allResultsArray.getJSONObject(index);

                RaceInfo infoObj = new RaceInfo(resultObject);

                racesInfo.add(infoObj);
            }

        }
        catch (JSONException e){
            Log.d("RaceResult", e.getMessage());
        }
    }



}//SomeResult

