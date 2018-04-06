package comp.examplef1.iovisvikis.f1story.MyDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.AdjustTask;

import comp.examplef1.iovisvikis.f1story.Communication;
import comp.examplef1.iovisvikis.f1story.R;

/**
 * Created by iovisvikis on 9/4/2017.
 */

public class StandingsDialog extends android.support.v4.app.DialogFragment{


    private String query;
    private Communication act;
    private boolean firstSelected, secondSelected, thirdSelected = false; //keep track of what gets selected


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        act = (Communication) getActivity();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Bundle args = getArguments();
        query = args.getString("QUERY"); //eg http://ergast/com/api/f1/driver/driverId/results.json

    }


    @Override
    public void onStart() {
        super.onStart();

        //Set up the dialog dimensions
        int width, height;

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
            height = (int) (getResources().getDisplayMetrics().heightPixels * 0.60);
        }
        else{
            width = (int) (getResources().getDisplayMetrics().widthPixels * 0.60);
            height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
        }

        getDialog().getWindow().setLayout(width, height);

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.podiums_dialog_layout, container, false);

        final AppCompatCheckBox checkFirst = root.findViewById(R.id.checkFirst);
        final AppCompatCheckBox checkSecond = root.findViewById(R.id.checkSecond);
        final AppCompatCheckBox checkThird = root.findViewById(R.id.checkThird);


        AppCompatButton previousButton = root.findViewById(R.id.previousButton);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StandingsDialog.this.dismiss();
            }
        });


        final AppCompatSpinner seasonSpinner = root.findViewById(R.id.standingsSpinner);


        String adjustQuery = query.split("results")[0] + "seasons.json";
        AdjustTask task = new AdjustTask();
        Object[] params = {seasonSpinner, adjustQuery, act.getDownloadFragment(), "Seasons", false};
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);


        AppCompatButton nextButton = root.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                if(seasonSpinner != null && seasonSpinner.getSelectedItem()!= null){
                    if(checkFirst.isChecked())
                        firstSelected = true;
                    if(checkSecond.isChecked())
                        secondSelected = true;
                    if(checkThird.isChecked())
                        thirdSelected = true;

                    if(firstSelected || secondSelected || thirdSelected){
                        Bundle args = new Bundle();
                        args.putBoolean("FIRST_SELECTED", firstSelected);
                        args.putBoolean("SECOND_SELECTED", secondSelected);
                        args.putBoolean("THIRD_SELECTED", thirdSelected);

                        StandingsDialog.this.dismiss();

                        //start the task that downloads the data and sets the adapter

                        String season = seasonSpinner.getSelectedItem().toString();
                        String[] splitQuery = query.split("/f1");

                        String finalQuery = splitQuery[0] + "/f1/" + season + splitQuery[1];

                        //Log.e("PODIUMS_QUERY", finalQuery + "");

                        Object[] params = {finalQuery, "Results", act.getDownloadFragment(), getActivity().getResources().getString(R.string.getting_podiums), args};
                        act.getDownloadFragment().startListAdapterTask(params);
                    }
                    else{
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.podium_place), Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.wait_for_season), Toast.LENGTH_SHORT).show();
                }


            }
        });


        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return root;
    }


    @Override
    public void onDestroyView() {

        //don't destroy view on orientation changes
        Dialog dialog = getDialog();

        if(dialog!= null && getRetainInstance()){
            dialog.setDismissMessage(null);
        }

        super.onDestroyView();
    }


    @Override
    public void onDetach() {
        super.onDetach();

        act = null;
    }

}//StandingsDialog




