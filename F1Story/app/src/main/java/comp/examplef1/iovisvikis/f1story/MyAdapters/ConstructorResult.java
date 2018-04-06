package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iovisvikis on 10/4/2017.
 */

public class ConstructorResult{


    private String raceName;

    private Constructor constructor;

    private String season;
    private String raceUrl;

    public Constructor getConstructor(){return constructor;}

    public String getRaceUrl() {
        return raceUrl;
    }

    public String getRaceName() {
        return raceName;
    }

    public String getSeason() {
        return season;
    }


    private ArrayList<ConstructorResultDriver> drivers;


    public ArrayList<ConstructorResultDriver> getDrivers() {
        return drivers;
    }


    public ConstructorResult(JSONObject resultObject){

        try {
            this.drivers = new ArrayList<>();

            this.raceName = resultObject.getString("raceName");
            this.raceUrl = resultObject.getString("url");
            this.season = resultObject.getString("season");

            JSONArray results = resultObject.getJSONArray("Results");

            JSONObject constructorJSON = results.getJSONObject(0).getJSONObject("Constructor");

            this.constructor = new Constructor(constructorJSON);

            for(int index=0; index<results.length(); index++){
                JSONObject result = results.getJSONObject(index);
                ConstructorResultDriver driver = new ConstructorResultDriver(result);
                drivers.add(driver);
            }

        }catch (JSONException e){
            Log.d("CONSTRUCTORRESULT", e.getMessage());
        }
    }



}//ConstructorResult
