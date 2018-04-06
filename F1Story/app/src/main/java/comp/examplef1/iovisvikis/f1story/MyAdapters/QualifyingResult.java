package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iovisvikis on 15/5/2017.
 */

public class QualifyingResult{

    private String season;
    private String raceName;
    private String countryName;
    private String locality;
    private ArrayList<QualifyingRow> rows;


    public String getLocality() {
        return locality;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getSeason(){
        return season;
    }

    public String getRaceName(){
        return raceName;
    }

    public ArrayList<QualifyingRow> getRows() {
        return rows;
    }


    public QualifyingResult(JSONObject result){

        this.rows = new ArrayList<>();

        try{
            this.season = result.getString("season");
            this.raceName = result.getString("raceName");

            JSONObject circuit = result.getJSONObject("Circuit");
            JSONObject location = circuit.getJSONObject("Location");

            this.countryName = location.getString("country");
            this.locality = location.getString("locality");

            JSONArray qualRows = result.getJSONArray("QualifyingResults");

            for(int index=0; index < qualRows.length(); index++){
                JSONObject rowObject = qualRows.getJSONObject(index);

                QualifyingRow row = new QualifyingRow(rowObject);
                rows.add(row);
            }

        }
        catch (JSONException je){
            Log.e("QLFNGRESULT", je.getMessage());
        }
    }


}//QualifyingResult


