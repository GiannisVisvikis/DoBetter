package comp.examplef1.iovisvikis.f1story;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;

import android.view.MenuItem;


import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.AdjustTask;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;

import comp.examplef1.iovisvikis.f1story.MyDialogs.HelpDialog;
import comp.examplef1.iovisvikis.f1story.MyDialogs.StandingsDialog;

import java.util.HashMap;


/**
 * Created by iovisvikis on 3/11/2016.
 */


public class SuperFragment extends android.support.v4.app.Fragment{


    private String driverHelpText, constructorHelpText, circuitHelpText;

    //key defines what the fragment deals with (driver, constructor, circuit etc).
    private String key, selectedEraQuery = null;

    private SQLiteDatabase f1DataBase;

    private Communication act;

    private LinearLayout background;

    private DownloadFragment downloadFragment;

    private HashMap<AppCompatAutoCompleteTextView, String> tags = new HashMap<>();

    private AppCompatAutoCompleteTextView driversTxt, constructorsTxt, seasonsTxt, circuitsTxt;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //if the fragment is added after the activity is created then references will work fine here
        act = (Communication) getActivity();
        downloadFragment = act.getDownloadFragment();
        f1DataBase = act.getAppDatabase();

        driverHelpText = getResources().getString(R.string.driver_help);
        constructorHelpText = getResources().getString(R.string.constructor_help);
        circuitHelpText = getResources().getString(R.string.circuit_help);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //if the activity is also reconstructed (orientation change) onAttach assignments will not work as the activity might not have been created yet
        act = (Communication) getActivity();
        downloadFragment = act.getDownloadFragment();
        f1DataBase = act.getAppDatabase();

        if(savedInstanceState != null){
            //only needs to be retrieved during orientation changes when the activity also gets recreated
            this.selectedEraQuery = savedInstanceState.getString("SELECTED_ERA");

            //retrieve saved use input
            int index = 0;
            for(AppCompatAutoCompleteTextView view : getTags().keySet()){
                view.setText(savedInstanceState.getString(("USER_SELECTION" + index)));
            }

        }

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int index = 0;
        for(AppCompatAutoCompleteTextView view : getTags().keySet()){
            outState.putString("USER_SELECTION" + index, view.getText().toString());
        }

        if(this.selectedEraQuery != null)
            outState.putString("SELECTED_ERA", selectedEraQuery);
    }



    @Override
    public void onResume() {
        super.onResume();

        initializeTxtViews(); //will add the listeners and fill in the AutoComplete Text Views depending on the user selections so far

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       switch (item.getItemId()){
           case R.id.soundsMenu:
               return true;

           case R.id.notificationsMenu: //handled in main activity
               return true;

           case R.id.drivers_help:
               showHelp(getDriverHelpText());
               return true;

           case R.id.constructors_help:
               showHelp(getConstructorHelpText());
               return true;

           case R.id.circuits_help:
               showHelp(getCircuitHelpText());
               return true;

           case R.id.drivers_all:
               adjustEraChoice("all_drivers");
               return true;

           case R.id.drivers_50s:
               adjustEraChoice("1950s_drivers");
               return true;

           case R.id.drivers_60s:
               adjustEraChoice("1960s_drivers");
               return true;

           case R.id.drivers_70s:
               adjustEraChoice("1970s_drivers");
               return true;

           case R.id.drivers_80s:
               adjustEraChoice("1980s_drivers");
               return true;

           case R.id.drivers_90s:
               adjustEraChoice("1990s_drivers");
               return true;

           case R.id.drivers_00s:
               adjustEraChoice("2000s_drivers");
               return true;

           case R.id.drivers_10s:
               adjustEraChoice("2010s_drivers");
               return true;

           case R.id.constructors_all:
               adjustEraChoice("all_constructors");
               return true;

           case R.id.constructors_50s:
               adjustEraChoice("1950s_constructors");
               return true;

           case R.id.constructors_60s:
               adjustEraChoice("1960s_constructors");
               return true;

           case R.id.constructors_70s:
               adjustEraChoice("1970s_constructors");
               return true;

           case R.id.constructors_80s:
               adjustEraChoice("1980s_constructors");
               return true;

           case R.id.constructors_90s:
               adjustEraChoice("1990s_constructors");
               return true;

           case R.id.constructors_00s:
               adjustEraChoice("2000s_constructors");
               return true;

           case R.id.constructors_10s:
               adjustEraChoice("2010s_constructors");
               return true;

           case R.id.circuits_all:
               adjustEraChoice("all_circuits");
               return true;

           case R.id.circuits_50s:
               adjustEraChoice("1950s_circuits");
               return true;

           case R.id.circuits_60s:
               adjustEraChoice("1960s_circuits");
               return true;

           case R.id.circuits_70s:
               adjustEraChoice("1970s_circuits");
               return true;

           case R.id.circuits_80s:
               adjustEraChoice("1980s_circuits");
               return true;

           case R.id.circuits_90s:
               adjustEraChoice("1990s_circuits");
               return true;

           case R.id.circuits_00s:
               adjustEraChoice("2000s_circuits");
               return true;

           case R.id.circuits_10s:
               adjustEraChoice("2010s_circuits");
               return true;

           default:
               return true;
       }

    }



    @Override
    public void onDetach() {
        super.onDetach();

        act = null;
        downloadFragment = null;
        f1DataBase = null;
    }


    public String getCurrentEntry(){
        return getTags().keySet().iterator().next().getText().toString();
    }


    protected void initializeTextView(AppCompatAutoCompleteTextView view){
        String tag = getTags().get(view); //driver --> drivers
        Cursor cursor = f1DataBase.rawQuery("select " + tag + "_name from all_" + tag + 's', null);
        setInitialChoices(view, cursor);
    }



    protected  void initializeTxtViews(){

       for (AppCompatAutoCompleteTextView view : getTags().keySet())
           initializeTextView(view);
    }



    protected void setInitialChoices(final AppCompatAutoCompleteTextView autoComplete, Cursor cursor){

        if(getSelectedEraQuery() == null) { //this means that the user has not yet selected a specific era of drivers. constructors etc

            String[] names = new String[cursor.getCount()];

            if (cursor.moveToFirst()) {

                int index = 0;

                do {
                    names[index] = cursor.getString(0);
                    index++;

                } while (cursor.moveToNext());

                autoComplete.setAdapter(new ArrayAdapter<>(getActivity() , android.R.layout.simple_spinner_dropdown_item, names));

            }


            //set the listeners
            autoComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    autoComplete.showDropDown();
                }
            });

            autoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if(!autoComplete.isFocused())
                        autoComplete.dismissDropDown();
                    else
                        autoComplete.showDropDown();
                }
            });

            autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
            });

        }
        else{ //the user has selected a specific era, begin another async task to meet that need
            //here we are, read the above line
            setSelectedEra(getSelectedEraQuery());
        }

    }



    /**
     * @return the final request query to the api. Null if invalid choice was made by the user
     */
    public String formAdjustQuery(){

        String result = MainActivity.BASIC_URI;  //.../f1/

        if(seasonsTxt != null) { //the text view is present
            String seasonEntry = seasonsTxt.getText().toString();
            if (!seasonEntry.equalsIgnoreCase("")) {
                if (isValidEntry(seasonsTxt) != null)
                    result += seasonEntry + "/";
                else
                    return null;
            }
        }

        if(driversTxt != null) {
            String driverEntry = driversTxt.getText().toString();
            if (!driverEntry.equalsIgnoreCase("")) { //not empty
                if (isValidEntry(driversTxt) != null) //and valid
                    result += "drivers/" + getId(driversTxt) + "/";
                else //not empty and invalid
                    return null;
            }
        }

        if(constructorsTxt != null) {
            String constructorEntry = constructorsTxt.getText().toString();
            if (!constructorEntry.equalsIgnoreCase("")) {
                if (isValidEntry(constructorsTxt) != null)
                    result += "constructors/" + getId(constructorsTxt) + "/";
                else
                    return null;
            }
        }

        if(circuitsTxt != null) {
            String circuitEntry = circuitsTxt.getText().toString();
            if (!circuitEntry.equalsIgnoreCase("")) {
                if (isValidEntry(circuitsTxt) != null)
                    result += "circuits/" + getId(circuitsTxt) + "/";
                else
                    return null;
            }
        }

        return result;
    }



    protected String getId(AppCompatAutoCompleteTextView view){

        String name = view.getText().toString();

        String kind = getKey();

        String tag = kind + 's'; //driver --> drivers

        String id = DatabaseUtils.stringForQuery(f1DataBase, "select " + kind + "_id from all_" + tag + " where " + kind + "_name = ?", new String[]{name});

        return id;
    }


    /**
     * @param theView the auto complete text view to examine
     * @return either the url for the info or null if not a valid entry by the user
     */
    protected String isValidEntry(AutoCompleteTextView theView) {

        String entry, table, column, result = null;

        entry = theView.getText().toString();
        column = getTags().get(theView);   //driver
        table = getTags().get(theView) + 's';  //driver -->  drivers

        try {
            result = DatabaseUtils.stringForQuery(f1DataBase, "select url from all_" + table + " where " + column + "_name = ?", new String[]{entry});

        } catch (SQLiteDoneException e) { //no result from query
            Toast.makeText(getActivity(), entry + " " + getResources().getString(R.string.not_valid), Toast.LENGTH_SHORT).show();
            initializeTxtViews();
        }

        return result;
    }



    //must be called in childs onCreateView, so that references will have already been established
    protected void setTags(){
        getTags().clear();

        String tag = capitilizeFirstChar(getKey()); //driver --> Drivers

        if(driversTxt != null)
            tags.put(driversTxt, tag);

        if(constructorsTxt != null)
            tags.put(constructorsTxt, tag);

        if(seasonsTxt != null)
            tags.put(seasonsTxt, tag);

        if(circuitsTxt != null)
            tags.put(circuitsTxt, tag);

    }



    /**
     *
     * @param theView the View used by the user to make the choice
     * @return True if not empty and valid choice. False otherwise
     */
    protected  boolean checkEntry(AutoCompleteTextView theView){

        String userChoice = theView.getText().toString();

        if(!userChoice.equalsIgnoreCase("")){

            if(isValidEntry(theView) != null)
                return true;
            else
                return false;
        }
        else{
            Toast.makeText(getActivity(), getResources().getString(R.string.choose_a) + " " + getTags().get(theView), Toast.LENGTH_SHORT).show();
            return false;
        }
    }



    private void adjustEraChoice(String table_name){               //remember last selection
        this.selectedEraQuery = "select " + getKey() + "_name from '" + table_name + "';";
        setSelectedEra(getSelectedEraQuery());
    }



    public void setSelectedEra(String eraQuery){
        AdjustTask adjustTask = new AdjustTask();
        Object[] params = new Object[5];

        params[0] = getTags().keySet().iterator().next();//get the first and only element
        params[1] = getAct().getAppDatabase().rawQuery(eraQuery, null);
        params[2] = getDownloadFragment();
        params[3] = getKey();
        params[4] = false;

        adjustTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }



    public String getSelectedEraQuery() {
        return this.selectedEraQuery;
    }


    public void setDriversTxt(AppCompatAutoCompleteTextView driversTxt) {
        this.driversTxt = driversTxt;
    }


    private AppCompatAutoCompleteTextView getActiveAutoComplete(){

        if(driversTxt != null)
            return driversTxt;
        else if(constructorsTxt != null)
            return constructorsTxt;
        else if (circuitsTxt != null)
            return circuitsTxt;
        else
            return null;
    }


    public String getDriverHelpText() {
        return driverHelpText;
    }

    public String getConstructorHelpText() {
        return constructorHelpText;
    }

    public String getCircuitHelpText() {
        return circuitHelpText;
    }

    public AppCompatAutoCompleteTextView getDriversTxt() {
        return driversTxt;
    }


    public void setConstructorsTxt(AppCompatAutoCompleteTextView constructorsTxt) {
        this.constructorsTxt = constructorsTxt;
    }

    public AppCompatAutoCompleteTextView getConstructorsTxt() {
        return constructorsTxt;
    }


    public void setSeasonsTxt(AppCompatAutoCompleteTextView seasonsTxt) {
        this.seasonsTxt = seasonsTxt;
    }

    public AppCompatAutoCompleteTextView getSeasonsTxt() {
        return seasonsTxt;
    }


    public void setCircuitsTxt(AppCompatAutoCompleteTextView circuitsTxt) {
        this.circuitsTxt = circuitsTxt;
    }


    public AppCompatAutoCompleteTextView getCircuitsTxt() {
        return circuitsTxt;
    }


    public HashMap<AppCompatAutoCompleteTextView, String> getTags(){
        return tags;
    }





    public SQLiteDatabase getF1DataBase() {
        return f1DataBase;
    }

    public Communication getAct() {
        return act;
    }

    public void setBackground(LinearLayout background) {
        this.background = background;
    }

    public LinearLayout getBackground() {
        return background;
    }


    public void setKey(String key){this.key = key;}
    public  String getKey(){return key;}

    public DownloadFragment getDownloadFragment() {
        return downloadFragment;
    }



    /**
     * @param choiceName the driver/constructor etc chosen name by the user
     * @param choiceKind driver, constructor etc
     * @param purpose results, laps etc
     * @param allOption weather all is an option or not
     * @return a Bundle containing the dialog initialization information
     */
    protected Bundle setDialogArgs(String choiceName, String choiceKind, String purpose, Boolean allOption){

        Bundle bundle = new Bundle();

        bundle.putString("NAME", choiceName);
        bundle.putString("FRAGMENT_KIND", choiceKind);
        bundle.putString("PURPOSE", purpose);
        bundle.putBoolean("ALL_OPTIONS", allOption);

        return bundle;
    }




    protected void setDriversButton(AppCompatAutoCompleteTextView txtView){
        if(checkEntry(txtView)) {
            String name = txtView.getText().toString();
            Bundle dialogArgs = setDialogArgs(name, getKey(), "Drivers", true);

            //will be useful when downloading extra information, good to know which fragment called and what was the user's choice
            getAct().getDownloadFragment().setCallerFragmentTag(key);
            getAct().getDownloadFragment().setCallerSelectedName(name);

            getAct().launchSingleSelectionDialog(dialogArgs);
        }
    }




    protected void setConstructorsButton(AppCompatAutoCompleteTextView txtView){

        if(checkEntry(txtView)){

            String name = txtView.getText().toString();
            Bundle dialogArgs = setDialogArgs(name, getKey(), "Constructors", true);

            getAct().getDownloadFragment().setCallerFragmentTag(key);
            getAct().getDownloadFragment().setCallerSelectedName(name);

            getAct().launchSingleSelectionDialog(dialogArgs);

        }
    }



    protected void setCircuitsButton(AppCompatAutoCompleteTextView txtView){
        if (checkEntry(txtView)) {
            String name = txtView.getText().toString();

            Bundle dialogArgs = setDialogArgs(name, getKey(), "Circuits", true);

            getAct().getDownloadFragment().setCallerFragmentTag(key);
            getAct().getDownloadFragment().setCallerSelectedName(name);

            getAct().launchSingleSelectionDialog(dialogArgs);
        }
    }



    protected void setInfoButton(AppCompatAutoCompleteTextView txtView){
        if(checkEntry(txtView)){
            String id = getId(txtView);
            String url = DatabaseUtils.stringForQuery(getF1DataBase(), "select url from all_" + getKey() + "s where " + getKey() + "_id = ?", new String[]{id});
            Intent bioIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(bioIntent);
        }
    }



    protected void setResultsButton(AppCompatAutoCompleteTextView txtView)
    {
        if (checkEntry(txtView))
        {
            String name = txtView.getText().toString();
            Bundle args = setDialogArgs(name, getKey(), "Results", false);

            if(getKey().equalsIgnoreCase("circuit"))
                getAct().launchSingleSelectionDialog(args);
            else
                getAct().launchMultipleSelectionDialog(args);
        }
    }



    protected void setSeasonResultsButton(AppCompatAutoCompleteTextView txtView){

        String name = getActiveAutoComplete().getText().toString();

        if(name.equalsIgnoreCase("") || isValidEntry(getActiveAutoComplete()) != null){

            Bundle args = setDialogArgs(name, getKey(), "Standings", false);
            getAct().launchSingleSelectionDialog(args);

        }

    }



    protected void setChampsButton(AppCompatAutoCompleteTextView txtView){


        String name = txtView.getText().toString();

        if(!name.equalsIgnoreCase("")){//asking information about a specific drivers

            if (checkEntry(txtView)) {//not empty and a valid choice

                String id = getId(txtView);
                String query = MainActivity.BASIC_URI + getKey() + "s/" + id + "/" + getKey() + "Standings/1.json";

                String message = name + " " + getResources().getString(R.string.championships);

                Object[] params = {query, capitilizeFirstChar(getKey()) + "Standings", getDownloadFragment(),
                        message, new Bundle()};

                getDownloadFragment().startListAdapterTask(params);

            }
        }
        else{

            Bundle args = new Bundle();
            args.putString("NAME", name);
            args.putString("FRAGMENT_KIND", capitilizeFirstChar(getKey()));
            args.putString("PURPOSE", "Standings/1");
            args.putBoolean("ALL_OPTIONS", true);

            act.launchSingleSelectionDialog(args);
        }

    }



    protected void setPodiumsButton(AppCompatAutoCompleteTextView txtView){
        if(checkEntry(txtView)){
            String query = formAdjustQuery() + "results.json";
            Bundle args = new Bundle();
            args.putString("QUERY", query);
            StandingsDialog standingsDialog = new StandingsDialog();
            standingsDialog.setArguments(args);

            standingsDialog.show( getFragmentManager(), "STANDINGS_DIALOG");

            getFragmentManager().executePendingTransactions();
        }
    }


    private void showHelp(String helpTxt){

        Bundle args = new Bundle();
        args.putString("HELP_TXT", helpTxt);

        HelpDialog helpDialog = new HelpDialog();
        helpDialog.setArguments(args);

        helpDialog.show(getActivity().getSupportFragmentManager(), "HELP_DIALOG");

        getActivity().getSupportFragmentManager().executePendingTransactions();
    }


    public String getLastEntry(){
        return getTags().keySet().iterator().next().getText().toString();
    }

    public void restoreLastEntry(String lastEntry){
        getTags().keySet().iterator().next().setText(lastEntry);
    }


    private String capitilizeFirstChar(String start){
        return Character.toUpperCase(start.charAt(0)) + start.substring(1);
    }



}//SuperFragment



