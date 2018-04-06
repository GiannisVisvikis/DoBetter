package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import comp.examplef1.iovisvikis.f1story.APICommunicator;
import comp.examplef1.iovisvikis.f1story.MyAdapters.SeasonEndDriver;
import comp.examplef1.iovisvikis.f1story.MyDialogs.DataDialog;

/**
 * Created by giannis on 2/12/2017.
 */
public class CreateSeasonEndDriver extends AsyncTask<Object, Object, DataDialog>{



    @Override
    protected DataDialog doInBackground(Object... objects) {

        String query = (String) objects[0];
        APICommunicator api = (APICommunicator) objects[1];
        ViewGroup parent = (ViewGroup) objects[2];
        LinearLayout addTo = (LinearLayout) objects[3];
        DownloadFragment host = (DownloadFragment) objects[4];
        DataDialog closeDialog = (DataDialog) objects[5];

        try{

            String jsonInfo = api.getInfo(query);

            JSONObject root = new JSONObject(jsonInfo);

            JSONArray lists = api.getData(root, "DriverStandings");

            //results are empty for some insignifficant drivers, skip them
            if(lists != null){

                JSONObject listsFirst = lists.getJSONObject(0);

                JSONArray driverStandings = listsFirst.getJSONArray("DriverStandings");

                JSONObject standingsFirst = driverStandings.getJSONObject(0);

                JSONObject driver = standingsFirst.getJSONObject("Driver");

                SeasonEndDriver seasonEndDriver = new SeasonEndDriver(driver, standingsFirst);

                //pass to the main thread to do the update
                publishProgress( new Object[]{seasonEndDriver, parent, addTo, host});
            }

        }
        catch (JSONException je){
            Log.e("SsnEnsCnstrs/CrtSsDrvr", je.getMessage());
        }

        return closeDialog;

    }


    @Override
    protected void onProgressUpdate(Object... objects) {
        super.onProgressUpdate(objects);

        SeasonEndDriver driver = (SeasonEndDriver) objects[0];
        ViewGroup parent = (ViewGroup) objects[1];
        LinearLayout addTo = (LinearLayout) objects[2];

        ( (DownloadFragment) objects[3] ).addTheDriver(driver, parent, addTo);

    }


    @Override
    protected void onPostExecute(DataDialog dataDialog) {
        super.onPostExecute(dataDialog);

        //only the last of these tasks returns not null dataDialog

        if (dataDialog != null)
            dataDialog.dismiss();

    }


}