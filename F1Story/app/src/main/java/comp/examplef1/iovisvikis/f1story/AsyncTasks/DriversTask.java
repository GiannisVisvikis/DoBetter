package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import comp.examplef1.iovisvikis.f1story.APICommunicator;
import comp.examplef1.iovisvikis.f1story.MainActivity;
import comp.examplef1.iovisvikis.f1story.MyAdapters.Driver;
import comp.examplef1.iovisvikis.f1story.MyAdapters.SeasonEndDriver;
import comp.examplef1.iovisvikis.f1story.MyDialogs.DataDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ioannisvisvikis on 7/25/17.
 */

public class DriversTask extends AsyncTask<Object, Object, Void> {

    @Override
    protected Void doInBackground(Object... strings) {

        APICommunicator api = new APICommunicator();
        String query = (String) strings[0];
        String season = (String) strings[1];
        String message = (String) strings[2];
        DownloadFragment host = (DownloadFragment) strings[3];
        ViewGroup parent = (ViewGroup) strings[4];
        LinearLayout addTo = (LinearLayout) strings[5];

        Bundle args = new Bundle();
        args.putString("MESSAGE", message);

        DataDialog dataDialog = new DataDialog();
        dataDialog.setArguments(args);
        dataDialog.show(host.getActivity().getSupportFragmentManager(), "DATA_DIALOG");


        String finalQuery = api.getFinalRequestString(query)[1];

        String rootInfo = api.getInfo(finalQuery);

        try{

            JSONObject root = new JSONObject(rootInfo);

            JSONArray drivers = api.getData(root, "Drivers");

            for(int  index = 0; index < drivers.length(); index++){

                Driver driver = new Driver(drivers.getJSONObject(index));

                String newQuery = MainActivity.BASIC_URI + season + "/drivers/" + driver.getId() +
                        "/driverStandings.json";

                DataDialog closeDialog = null;

                if(index == drivers.length() - 1)
                    closeDialog = dataDialog;

                CreateSeasonEndDriver seasonEndDriverTask = new CreateSeasonEndDriver();
                Object[] params = {newQuery, api, parent, addTo, host, closeDialog};

                //tas.execute must be called in the UI thread
                publishProgress(new Object[]{seasonEndDriverTask, params});
            }

            return null;
        }
        catch (JSONException je){
            Log.e("SsnEnsCnstrs/DrvrsTsk", je.getMessage());
        }

        return null;
    }


    @Override
    protected void onProgressUpdate(Object... objects) {
        super.onProgressUpdate(objects);

        CreateSeasonEndDriver task = (CreateSeasonEndDriver) objects[0];
        Object[] params = (Object[]) objects[1];

        task.execute(params);
    }

}


