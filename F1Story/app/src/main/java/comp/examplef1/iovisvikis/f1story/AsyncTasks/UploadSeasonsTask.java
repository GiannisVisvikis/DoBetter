package comp.examplef1.iovisvikis.f1story.AsyncTasks;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import comp.examplef1.iovisvikis.f1story.APICommunicator;
import comp.examplef1.iovisvikis.f1story.MyDialogs.DataDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ioannisvisvikis on 7/31/17.
 */

public class UploadSeasonsTask extends AsyncTask<Object, String, Object[]>{

    private DownloadFragment host;

    private DataDialog dataDialog;


    @Override
    protected Object[] doInBackground(Object... objects) {

        String query = (String) objects[0];
        String dialogMessage = (String) objects[1];
        host = (DownloadFragment) objects[2];

        AutoCompleteTextView container = (AutoCompleteTextView) objects[3];

        publishProgress(dialogMessage);

        APICommunicator api = new APICommunicator();

        try{

            String finalQuery = api.getFinalRequestString(query)[1];
            String jsonRootString = api.getInfo(finalQuery);

            JSONObject root = new JSONObject(jsonRootString);

            JSONArray seasonsArray = api.getData(root, "Seasons");

            String[] allSeasons = new String[seasonsArray.length()];

            for(int index = 0; index < seasonsArray.length(); index++){

                JSONObject seasonObject = seasonsArray.getJSONObject(index);

                String season = seasonObject.getString("season");

                allSeasons[index] = season;
            }


            ArrayAdapter result =  new ArrayAdapter<String>(host.getActivity(), android.R.layout.simple_spinner_dropdown_item, allSeasons);

            return new Object[]{container, result};

        }
        catch (JSONException je){
            Log.e("UpldSsnsTsk", je.getMessage());
        }

        return null;
    }


    @Override
    protected void onProgressUpdate(String... values) {
        dataDialog = new DataDialog();
        Bundle args = new Bundle();
        args.putString("MESSAGE", values[0]);
        dataDialog.setArguments(args);
        dataDialog.show(host.getFragmentManager(), "DATA_DIALOG");
    }


    @Override
    protected void onPostExecute(Object... objects) {

        AutoCompleteTextView container = (AutoCompleteTextView) objects[0];
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) objects[1];

        if(adapter != null){
            container.setAdapter(adapter);
            container.showDropDown();
        }

        dataDialog.dismiss();

        dataDialog = null;
        host = null;
    }



}


