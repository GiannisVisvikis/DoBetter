package comp.examplef1.iovisvikis.f1story.MyDialogs;

import android.app.Dialog;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;

import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.AdjustTask;
import comp.examplef1.iovisvikis.f1story.Communication;
import comp.examplef1.iovisvikis.f1story.MainActivity;
import comp.examplef1.iovisvikis.f1story.R;


/**
 * Created by iovisvikis on 6/2/2017.
 */

public class SelectionDialog extends android.support.v4.app.DialogFragment{

    private Bundle args;

    private AppCompatSpinner seasonSpinner, roundSpinner;
    private AppCompatImageButton xMark, checkMark;

    private SpinnerAdapter seasonAdapter;

    private int seasonIndex;

    private Communication act;
    private Boolean allOptions;
    private String choiceId, choiceKind, choiceName;

    private String purpose; //why is this dialog called? Drivers? Circuits? Results?


    private String answer; //the final query formed after this dialog ended


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        act = (Communication) getActivity();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);

    }



    //get the screen dimensions and adjust the dialog accordingly
    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog =  getDialog();

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        int dialogWidth, dialogHeight;

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            dialogWidth = 9 * screenWidth/10;
            dialogHeight = screenHeight/2;
        }
        else {
            dialogWidth = 3 * screenWidth /5;
            dialogHeight = 4* screenHeight/5;
        }

        dialog.getWindow().setLayout(dialogWidth, dialogHeight);

        act.blockOrientationChanges();
    }



    @Override
    public void onDetach(){
        super.onDetach();

        this.act = null;
        
        if(seasonSpinner != null){
            setSeasonAdapter(seasonSpinner.getAdapter());
            setSeasonIndex(seasonSpinner.getSelectedItemPosition());
        }

    }


    //retain dialog through orientation changes
    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }


    protected void setSpinners(AppCompatSpinner newSeasonSpinner, AppCompatSpinner newRoundSpinner){

        this.seasonSpinner = newSeasonSpinner;

        if(newRoundSpinner != null)
            this.roundSpinner = newRoundSpinner;

        setContents(seasonSpinner);

    }



    /**
     * MUST BE CALLED AFTER the round spinner is initialized if present
     *
     * @param theSpinner the season selection spinner
     */
    private void setContents(final AppCompatSpinner theSpinner){

        args = getArguments(); //initial selections are stored here

        choiceName = args.getString("NAME");
        choiceKind = args.getString("FRAGMENT_KIND");  //driver, constructor etc

        purpose = args.getString("PURPOSE"); //example results, circuits, lapTimes
        allOptions = args.getBoolean("ALL_OPTIONS");

        if(act.hasInternetConnection()){

            //start a dialogue for season picking
            if(getSeasonAdapter() == null) {
                Object query;
                if (!choiceName.equalsIgnoreCase("")) { //name selected --> id needed
                    choiceId = DatabaseUtils.stringForQuery(getAct().getAppDatabase(), "Select " + choiceKind + "_id from all_" + choiceKind + "s where " + choiceKind + "_name = ?", new String[]{choiceName});
                    query = MainActivity.BASIC_URI + choiceKind + "s/" + choiceId + "/seasons.json";
                }
                else{
                    query = getAct().getAppDatabase().rawQuery("select season_name from all_seasons", null);

                    //move to first result
                    ((Cursor) query).moveToFirst();

                    //constructors championship began in 1958. Move from 1950 to 1958 if constructors fragment called the
                    //dialog
                    if (choiceKind.equalsIgnoreCase("constructor"))
                        ((Cursor) query).move(8);

                }

                AdjustTask task = new AdjustTask();
                Object[] params = {theSpinner, query, getAct().getDownloadFragment(), "Seasons", allOptions};
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
            }
            else{
                seasonSpinner.setAdapter(getSeasonAdapter());
            }


            //set a listener to season picking to adjust round choices if any present
            if(roundSpinner != null){ //either no adapter yet, or i want rounds to adjust to a change made in seasons
                theSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        //adjust the races/rounds to season selection if round selection is present in the UI
                        String seasonChoice;

                        if(!seasonSpinner.getSelectedItem().toString().equalsIgnoreCase(act.getDownloadFragment().getResources().getString(R.string.all)))
                            seasonChoice = seasonSpinner.getSelectedItem().toString();
                        else seasonChoice = "";

                        String selectionQuery = MainActivity.BASIC_URI + seasonChoice + "/races.json";

                        AdjustTask roundTask = new AdjustTask();

                        Object[] roundParams = {roundSpinner, selectionQuery, getAct().getDownloadFragment(), "Races", false};  //false for not showing all available races. Can lead to out of memory exception

                        roundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, roundParams);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }
        }
    }



    protected void setXMark(AppCompatImageButton newExMark){
        this.xMark = newExMark;

        xMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.allowOrientationChanges();
                SelectionDialog.this.dismiss();
            }
        });

    }


    protected void setCheckMark(AppCompatImageButton newCheckMark){

        this.checkMark = newCheckMark;

        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //activate the click only of the adapters are loaded on the spinners
                if((seasonSpinner != null && seasonSpinner.getSelectedItem() != null)){

                    setAnswer();
                    String answer = getAnswer();

                    if (purpose.contains("Standings"))
                        getAct().onDialogPositiveClick(answer, choiceKind + purpose);
                    else
                        getAct().onDialogPositiveClick(answer, purpose);

                    act.allowOrientationChanges();

                    SelectionDialog.this.dismiss();

                }
                else {
                    Toast.makeText(getContext(), getResources().getString(R.string.wait_for_season), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    protected void setAnswer(){



        String newAnswer;

        if(!seasonSpinner.getSelectedItem().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.all))) //call for a specific season
            newAnswer = seasonSpinner.getSelectedItem().toString() + "/";
        else
            newAnswer = "";

        String roundInput;

        //check if spinner is instantiated and loaded a position
        if(roundSpinner != null && roundSpinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION){

            roundInput = Integer.toString(roundSpinner.getSelectedItemPosition() + 1) + "/";
            newAnswer += roundInput;
        }

        if(!choiceName.equalsIgnoreCase("")) {
            newAnswer += choiceKind + "s/" + choiceId + "/";
            //example newAnswer = 2012/5/drivers/ralph_schumacher/circuits.json
        }

        if (purpose.contains("Standings")){
            newAnswer += choiceKind + purpose + ".json";
            //example ergast.com/api/f1/2012/4/driverStandings.json
        }
        else{
            newAnswer += purpose + ".json";
            //example ergast.com/api/f1/2012/4/results.json
        }

        this.answer = newAnswer;
    }


    protected String getAnswer(){
        return answer;
    }


    public Communication getAct(){return act;}


    public void setSeasonSpinner(AppCompatSpinner newSeasonSpinner){
        this.seasonSpinner = newSeasonSpinner;
    }


    public void setRoundSpinner(AppCompatSpinner newRoundSpinner){
        this.roundSpinner = newRoundSpinner;
    }


    public AppCompatSpinner getSeasonSpinner(){return seasonSpinner;}

    public AppCompatSpinner getRoundSpinner(){return roundSpinner;}

    private void setSeasonAdapter(SpinnerAdapter newSeasonAdapter){this.seasonAdapter = newSeasonAdapter;}
    private SpinnerAdapter getSeasonAdapter(){return this.seasonAdapter;}

    public void setSeasonIndex(int seasonIndex) {
        this.seasonIndex = seasonIndex;
    }



}//SelectionDialog



