package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import comp.examplef1.iovisvikis.f1story.APICommunicator;
import comp.examplef1.iovisvikis.f1story.MyAdapters.CalendarRaceAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.CircuitAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.ConstructorAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.ConstructorChampionsAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.ConstructorPodiumAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.ConstructorResultAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.CurrentGridAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.DriverAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.DriverChampionsAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.DriverPodiumAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.QualifyingResultsAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.SeasonEndConstructorsStandingsAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.SeasonEndDriverStandingsAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.SomeResultsAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.UIResultAdapter;
import comp.examplef1.iovisvikis.f1story.MyDialogs.DataDialog;
import comp.examplef1.iovisvikis.f1story.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 16/3/2017.
 */

public class GetListAdapterTask extends AsyncTask<Object, Void, RecyclerView.Adapter>{


    private String query, purpose, message;
    private DownloadFragment host;
    
    private DataDialog dialog;
    

    @Override
    protected RecyclerView.Adapter doInBackground(Object... objects){

        query = (String) objects[0];
        purpose = (String) objects[1];
        host = (DownloadFragment) objects[2];

        //disable orientation changes in small screen modes for result screen
        if(host.getAct().isResultDrawer()) {
           host.getAct().blockOrientationChanges();
        }

        message = (String) objects[3];

        publishProgress();

        APICommunicator api = new APICommunicator();
        //Log.e("GETLISTADQUERY", query);
        String[] results = api.getFinalRequestString(query);

        //Log.e("QUERY&KEY", query + " " + purpose);

        int totalResults = Integer.parseInt(results[0]);
        if(totalResults > 0){

            String finalQuery = results[1];

            String jsonInfo = api.getInfo(finalQuery);

            try
            {
                JSONObject root = new JSONObject(jsonInfo);

                JSONArray resultsArray = api.getData(root, purpose);

                if(purpose.equalsIgnoreCase("QualifyingResults")) //want events results
                    return new QualifyingResultsAdapter(host, resultsArray);
                else if(purpose.equalsIgnoreCase("Results"))
                {
                    try
                    { //see if extra args got passed in (meant for podiums if extra args are present)
                        Bundle args = (Bundle) objects[4];

                        if (query.contains("driv"))
                            return new DriverPodiumAdapter(host, resultsArray, args);
                        else
                            return new ConstructorPodiumAdapter(host, resultsArray, args);
                    } catch (IndexOutOfBoundsException e) { //no extra args got passed, intended to display driver champions, not podiums
                        if (query.contains("driv"))
                            return new SomeResultsAdapter(host, resultsArray);
                        else if (query.contains("const"))
                            return new ConstructorResultAdapter(host, resultsArray);
                        else
                            return new UIResultAdapter(host, resultsArray);
                    }
                }
                else if(purpose.equalsIgnoreCase("DriverStandings"))
                {
                    try { // more than one seasons, looking for all champions

                        Bundle args = (Bundle) objects[4];
                        return new DriverChampionsAdapter(host, resultsArray);
                    }
                    catch (IndexOutOfBoundsException indx){//single season standings results, looking for specific season final results
                        return new SeasonEndDriverStandingsAdapter(host, resultsArray);
                    }
                }
                else if(purpose.equalsIgnoreCase("ConstructorStandings")){
                    try{
                        //if present, meant for all champions
                        Bundle args = (Bundle) objects[4];
                        return new ConstructorChampionsAdapter(host, resultsArray);
                    }//if not, meant for single season constructor results
                    catch (IndexOutOfBoundsException indx){
                        return new SeasonEndConstructorsStandingsAdapter(host, resultsArray);
                    }
                }
                else if(purpose.equalsIgnoreCase("Circuits"))
                    return new CircuitAdapter(host, resultsArray);
                else if(purpose.equalsIgnoreCase("Constructors"))
                    try {
                        String currentGrid = (String) objects[4];
                        //return new CurrentGridAdapter(host, resultsArray);

                    } catch (IndexOutOfBoundsException ie) {
                        return new ConstructorAdapter(host, resultsArray);
                    }
                else if(purpose.equalsIgnoreCase("Drivers")) {
                    return new DriverAdapter(host, resultsArray);

                }/*
                else if(purpose.equalsIgnoreCase("Races"))
                    return new CalendarRaceAdapter(host, resultsArray);*/
            }
            catch(JSONException e){
                Log.d("ListAdapterTask", e.getMessage());
            }
        }

        return null;
    }



    @Override
    protected void onProgressUpdate(Void... values) {

        Bundle args = new Bundle();
        args.putString("MESSAGE", message);
        dialog = new DataDialog();
        dialog.setArguments(args);

        dialog.show(host.getFragmentManager(), "CHAMPS_DIALOG");
    }



    @Override
    protected void onPostExecute(RecyclerView.Adapter baseAdapter){

        if(dialog != null)
        {
            dialog.dismissAllowingStateLoss();
        }

        if(baseAdapter != null && baseAdapter.getItemCount() != 0) {

            host.getAct().setResultFragment(baseAdapter);
        }
        else {
            Toast.makeText(host.getActivity(), host.getActivity().getResources().getString(R.string.no_results_for_this_selection), Toast.LENGTH_SHORT).show();
            host.getAct().allowOrientationChanges();
        }

    }



}//GetListAdapterTask



