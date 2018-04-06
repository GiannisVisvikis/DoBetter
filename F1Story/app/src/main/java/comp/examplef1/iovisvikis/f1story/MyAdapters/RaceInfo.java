package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 23/6/2017.
 */

public class RaceInfo{


    private String raceUrl, circuitName, locality, country, position, points, startingGrid, status, laps, time, fastest, speed;


    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRaceUrl() {
        return raceUrl;
    }

    public void setRaceUrl(String raceUrl) {
        this.raceUrl = raceUrl;
    }

    public String getCircuitName() {
        return circuitName;
    }

    public void setCircuitName(String circuitName) {
        this.circuitName = circuitName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getStartingGrid() {
        return startingGrid;
    }

    public void setStartingGrid(String startingGrid) {
        this.startingGrid = startingGrid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLaps() {
        return laps;
    }

    public void setLaps(String laps) {
        this.laps = laps;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFastest() {
        return fastest;
    }

    public void setFastest(String fastest) {
        this.fastest = fastest;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }




    public  RaceInfo(JSONObject jsonObject){

        try {
            setRaceUrl(jsonObject.getString("url"));

            JSONObject circuit = jsonObject.getJSONObject("Circuit");

            setCircuitName(circuit.getString("circuitName"));

            JSONObject location = circuit.getJSONObject("Location");

            setLocality(location.getString("locality"));
            setCountry(location.getString("country"));

            JSONArray resultsArray = jsonObject.getJSONArray("Results");

            JSONObject resultObj = (JSONObject) resultsArray.get(0);

            setPosition(resultObj.getString("position"));
            setPoints(resultObj.getString("points"));

            setStartingGrid(resultObj.getString("grid"));
            setStatus(resultObj.getString("status"));
            setLaps(resultObj.getString("laps"));


            if (getStatus().equalsIgnoreCase("finished")) {

                JSONObject fastestLap = resultObj.getJSONObject("FastestLap");
                JSONObject timeObject = resultObj.getJSONObject("Time");
                JSONObject fastestTimeObject = fastestLap.getJSONObject("Time");

                setTime(timeObject.getString("time"));
                setFastest(fastestTimeObject.getString("time"));

                JSONObject avgSpeed = fastestLap.getJSONObject("AverageSpeed");

                setSpeed(avgSpeed.getString("speed") + avgSpeed.getString("units"));

            }
            else{
                setFastest(" -- ");
                setSpeed(" -- ");
                setTime(" -- ");
            }


        }catch (JSONException exc){
            Log.e("RaceInfo", exc.getMessage());
        }

    }


}//RaceInfo
