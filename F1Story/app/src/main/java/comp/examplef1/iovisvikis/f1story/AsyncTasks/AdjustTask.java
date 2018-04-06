package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import comp.examplef1.iovisvikis.f1story.APICommunicator;
import comp.examplef1.iovisvikis.f1story.MainActivity;
import comp.examplef1.iovisvikis.f1story.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


/**
 * Created by iovisvikis on 3/1/2017.
 *
 *
 * React to selections made by the user on UI elements  by adjusting the other possible selections
 * so he can only make a valid request to the ergast API with results to show
 *
 */


// NO NEED TO CHECK FOR CONNECTION -- already checked before execution


public class AdjustTask extends AsyncTask<Object, Void, Object[]> {


    @Override
    protected Object[] doInBackground(Object... objects) {

        ArrayAdapter<String> arrayAdapter = null;

        Object theView = objects[0];

        //the second argument passed is either a string for a call to the api, or a cursor from the database
        String apiQuery = null;
        Cursor cursor = null;

        APICommunicator api = new APICommunicator();
        try {
            apiQuery = (String) objects[1]; //it is a string to make a call to the Api
        }
        catch (ClassCastException e){ //is not a string to make a call to the API. It is a cursor from the database;
            cursor = (Cursor) objects[1];
        }

        DownloadFragment theFragment = (DownloadFragment) objects[2];
        String key = (String) objects[3];
        Boolean allOptions = (Boolean) objects[4];


        MainActivity hostingActivity = (MainActivity) theFragment.getActivity();

        String[] entries = null;

        if((apiQuery != null) && hostingActivity.hasInternetConnection() && hostingActivity.apiResponds()) {


            String finalApiQuery = api.getFinalRequestString(apiQuery)[1];
            String jsonInfo = api.getInfo(finalApiQuery);

            try {
                JSONObject root = new JSONObject(jsonInfo);

                JSONArray results = api.getData(root, key);

                if(allOptions){

                    entries = new String[results.length() + 1];
                    entries[0] = hostingActivity.getDownloadFragment().getResources().getString(R.string.all);

                    for (int index = 0; index < results.length(); index++) {

                        JSONObject resultObject = results.getJSONObject(index);

                        String entryRow;

                        if (key.equalsIgnoreCase("Drivers"))
                            entryRow = resultObject.getString("givenName") + " " + resultObject.getString("familyName");
                        else if (key.equalsIgnoreCase("Constructors"))
                            entryRow = resultObject.getString("name");
                        else if (key.equalsIgnoreCase("Circuits"))
                            entryRow = resultObject.getString("circuitName");
                        else if (key.equalsIgnoreCase("seasons"))
                            entryRow = resultObject.getString("season");
                        else //looking for races
                            entryRow = resultObject.getString("raceName");

                        entries[index +1] = entryRow;
                    }
                }
                else{ //not supposed to have an ALL option

                    entries = new String[results.length()];

                    for (int index = 0; index < results.length(); index++) {

                        JSONObject resultObject = results.getJSONObject(index);

                        String entryRow;

                        if (key.equalsIgnoreCase("Drivers"))
                            entryRow = resultObject.getString("givenName") + " " + resultObject.getString("familyName");
                        else if (key.equalsIgnoreCase("Constructors"))
                            entryRow = resultObject.getString("name");
                        else if (key.equalsIgnoreCase("Circuits"))
                            entryRow = resultObject.getString("circuitName");
                        else if (key.equalsIgnoreCase("seasons"))
                            entryRow = resultObject.getString("season");
                        else //looking for races
                            entryRow = resultObject.getString("raceName");

                        entries[index] = entryRow;
                    }
                }

            } catch (JSONException je) {
                Log.d("AdjustTask", je.getMessage());
            }
        }
        else if(cursor != null){

            //if the AdjustTask is called through a selection dialog, then cursor.moveToFirst() method is already called. If not, there
            //is a problem. So, perform a check and see if the cursor is already on a result row
            if(cursor.getPosition() == -1){
                cursor.moveToFirst();
            }

            //cursor might have been moved forward (if constructors information is asked). So entries length may vary
            //ranging from cursor.length (if not moved to any position) to cursor.length - position.
            int cursorPosition = cursor.getPosition();

            if(allOptions){
                entries = new String[cursor.getCount() - cursorPosition + 1];
                entries[0] = hostingActivity.getResources().getString(R.string.all);

                //no need to move to first. Already done this before calling on the task.
                // If constructors called, it is already set to 1958
                int index = 1;

                do{
                    entries[index] = cursor.getString(0);
                    index++;
                }
                while (cursor.moveToNext());

            }
            else {
                entries = new String[cursor.getCount() - cursorPosition];

                int index = 0;

                do {
                    entries[index] = cursor.getString(0);
                    index++;
                }
                while (cursor.moveToNext());
            }
        }

        arrayAdapter = new ArrayAdapter<>(hostingActivity, android.R.layout.simple_spinner_dropdown_item, entries);

        return new Object[]{theView, hostingActivity, arrayAdapter};
    }



    @Override
    protected void onPostExecute(Object... objects) {

        Object theView = objects[0];

        MainActivity hostingActivity = (MainActivity) objects[1];

        ArrayAdapter<String> stringArrayAdapter = (ArrayAdapter<String>) objects[2];

        if(stringArrayAdapter != null){
            if(theView.getClass() == AppCompatAutoCompleteTextView.class) {
                ((AppCompatAutoCompleteTextView) theView).setAdapter(stringArrayAdapter);
                ((AppCompatAutoCompleteTextView) theView).setSelection(0);
            }
            else if(theView.getClass() == AppCompatSpinner.class) {
                ((AppCompatSpinner) theView).setAdapter(stringArrayAdapter);
                ((AppCompatSpinner) theView).setSelection(0);
            }
            else
                Toast.makeText(hostingActivity, "Wrong view passed in Adjust Task", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(hostingActivity, "Check Internet Connection", Toast.LENGTH_SHORT).show();


    }


}//AdjustTask



