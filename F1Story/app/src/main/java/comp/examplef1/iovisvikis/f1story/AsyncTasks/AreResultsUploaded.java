package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.os.AsyncTask;

import comp.examplef1.iovisvikis.f1story.APICommunicator;

/**
 * Created by iovisvikis on 13/5/2017.
 */

public class AreResultsUploaded extends AsyncTask<String, Void, Boolean> {



    @Override
    protected Boolean doInBackground(String... strings) {

        String query = strings[0];

        APICommunicator api = new APICommunicator();

        String[] finalInfo = api.getFinalRequestString(query);

        if(Integer.parseInt(finalInfo[0]) > 0) { //there are results for this query
            return true;
        }

        return false;
    }


}//AreResultsUploaded
