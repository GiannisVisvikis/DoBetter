package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 17/4/2017.
 */

public class Constructor{

    private String name;
    private String url;
    private String nationality;
    private String id;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getNationality() {
        return nationality;
    }


    public Constructor(JSONObject constructor){
        try{
            this.name = constructor.getString("name");
            this.url = constructor.getString("url");
            this.nationality = constructor.getString("nationality");
            this.id = constructor.getString("constructorId");
        }
        catch (JSONException e){
            Log.e("CONSTRUCTOR", e.getMessage());
        }
    }


}//Constructor


