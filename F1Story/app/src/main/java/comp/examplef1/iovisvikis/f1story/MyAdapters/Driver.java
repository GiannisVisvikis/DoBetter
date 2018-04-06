package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 17/4/2017.
 */

public class Driver{

    private String name, url, nationality, id;


    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getNationality() {
        return nationality;
    }

    public String getId() {return id;}


    public Driver(JSONObject driver){

        try{
            this.name = driver.getString("givenName") + "\n" + driver.getString("familyName");
            this.url = driver.getString("url");
            this.nationality = driver.getString("nationality");
            this.id = driver.getString("driverId");

        }
        catch (JSONException e){
            Log.e("DRIVER", e.getMessage());
        }

    }


}//Driver


