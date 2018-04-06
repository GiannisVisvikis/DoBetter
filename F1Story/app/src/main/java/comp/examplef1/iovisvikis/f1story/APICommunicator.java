package comp.examplef1.iovisvikis.f1story;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by iovisvikis on 15/10/2016.
 */

public class APICommunicator
{



    public String getInfo(String uri){

        StringBuilder sb = new StringBuilder();
        BufferedReader br;

        try{
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;

            while( (line=br.readLine()) != null ){
                sb.append(line);
            }

            br.close();
            con.disconnect();

        }catch (MalformedURLException uex){
            Log.e("APIgetInfo", uex.getMessage());
        }catch (IOException ioe) {
            Log.e("APIgetInfo", ioe.getMessage());
        }
        return sb.toString();
    }



    /**
     * Searches deep inside the root JSONObject and retrieves the desired information depending on the key
     * @param root the JSONObject to search into
     * @param key the label of information to look for
     * @return A JSONArray containing all the JSONObjects that contain information needed
     */
    public JSONArray getData(JSONObject root, final String key){
        return getData(root, null, key, 0);
    }
    private JSONArray getData(JSONObject root, JSONArray rootParent, final String key, int index){

        if(index < root.length()){

            try{

                JSONArray names = root.names();
                String name = names.getString(index);
                Object child = root.get(name);

                if(name.equalsIgnoreCase(key)){
                    if(child.getClass() == JSONArray.class) {
                        if (rootParent == null)
                            return (JSONArray) child;
                        else
                            return rootParent;
                    }
                    else if(child.getClass() == JSONObject.class)
                        return rootParent;
                    else
                        return getData(root, rootParent, key, ++index);
                }
                else{
                    JSONArray deepSearch = null;
                    if(child.getClass() == JSONObject.class)
                        deepSearch = getData((JSONObject) child, rootParent, key, 0); //search for it in here
                    else if(child.getClass() == JSONArray.class)
                        deepSearch = getData((JSONArray) child, key, 0); //search in here

                    if(deepSearch != null) //found it somewhere in before
                        return deepSearch;
                    else
                        return getData(root, rootParent, key, ++index); //not found it so far. Search next index
                }

            }catch(JSONException e){
                Log.d("ApiComGetData", e.getMessage());
            }
        }

        return null;  //didn't find what i was looking for anywhere in this JSONObject
    }



    private JSONArray getData(JSONArray array, final String key, int index){

        if(index < array.length()){

            try {
                Object child = array.get(index);

                JSONArray depthSearch;

                if (child.getClass() == JSONObject.class) {
                    depthSearch = getData((JSONObject) child, array, key, 0);
                } else {
                    depthSearch = getData((JSONArray) child, key, 0);
                }

                if (depthSearch != null) {
                    return depthSearch;
                } else {
                    return getData(array, key, ++index);
                }
            }
            catch (JSONException e){
                Log.d("ApiComGetData", e.getMessage());
            }
        }

        return null; //nowhere to be found in this array
    }



    private String getTotalEntries(String jsonUri){

        String info = getInfo(jsonUri);

        String total = null;

        try{
            JSONObject root = new JSONObject(info);
            JSONObject mrData = root.getJSONObject("MRData");
            total = mrData.getString("total");
        }
        catch (JSONException je){
            Log.e("APIComm", je.getMessage());
        }

        return total;
    }


    public String[] getFinalRequestString(String uri){

        String totalEntries = getTotalEntries(uri);

        String finalRequest =  uri + "?limit="+totalEntries+"&offset=0";

        return new String[]{totalEntries, finalRequest};
    }






}//APICommunicator
