package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 15/5/2017.
 */

public class QualifyingRow{


    private String q1,q2,q3, position;
    private Driver driver;
    private Constructor constructor;



    public String getQ1() {
        return q1;
    }

    public String getQ2() {
        return q2;
    }

    public String getQ3() {
        return q3;
    }

    public String getPosition() {
        return position;
    }

    public Driver getDriver() {
        return driver;
    }

    public Constructor getConstructor() {
        return constructor;
    }



    public QualifyingRow(JSONObject qualifyingRow){

        try{
            this.position = qualifyingRow.getString("position");
            this.q1 = qualifyingRow.getString("Q1");

            if(qualifyingRow.has("Q2"))
                this.q2 = qualifyingRow.getString("Q2");
            else
                this.q2 = "--------";

            if(qualifyingRow.has("Q3"))
                this.q3 = qualifyingRow.getString("Q3");
            else
                this.q3 = "--------";

            this.driver = new Driver(qualifyingRow.getJSONObject("Driver"));
            this.constructor = new Constructor(qualifyingRow.getJSONObject("Constructor"));

        }
        catch (JSONException je){
            Log.e("QLFNGROW", je.getMessage());
        }

    }



}//QualifyingRow

