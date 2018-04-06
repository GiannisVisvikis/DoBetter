
package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iovisvikis on 25/2/2017.
 */

public class DriverChampion{


    private Driver driver;

    //each element in this ArrayList contains information about the season this driver won the championship,
    //  like season, rounds, points, wins and the constructor that this driver raced for
    private ArrayList<DriverChampionshipSeason> seasonsWon = new ArrayList<>();

    public Driver getDriver() {
        return driver;
    }

    public ArrayList<DriverChampionshipSeason> getSeasonsWon() {
        return seasonsWon;
    }



    public DriverChampion(JSONObject standingsListObject){

        try
        {
            JSONObject driverJSON = standingsListObject.getJSONArray("DriverStandings")
                                                        .getJSONObject(0)
                                                         .getJSONObject("Driver");

            this.driver = new Driver(driverJSON);

            DriverChampionshipSeason seasonWon = new DriverChampionshipSeason(standingsListObject);

            getSeasonsWon().add(seasonWon);

        }
        catch (JSONException e)
        {
            Log.e("DriverChampion", e.getMessage());
        }

    }



}//DriverChampion


