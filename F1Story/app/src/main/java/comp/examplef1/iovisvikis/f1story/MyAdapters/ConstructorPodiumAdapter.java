package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.os.Bundle;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by iovisvikis on 11/4/2017.
 */

public class ConstructorPodiumAdapter extends ConstructorResultAdapter {


    private ArrayList<ConstructorResult> podiumResults;
    private HashMap<ConstructorResult, ArrayList<ConstructorResultDriver>> podiumDrivers;



    @Override
    public void onBindViewHolder(ConstructorResultViewHolder holder, int position) {

        ConstructorResult cpResult = podiumResults.get(position);
        ArrayList<ConstructorResultDriver> drivers = podiumDrivers.get(cpResult);

        handleConstructorResultHolderView(holder, cpResult, drivers);
    }


    @Override
    public int getItemCount() {
        return podiumResults.size();
    }


    public ConstructorPodiumAdapter(DownloadFragment host, JSONArray resultsArray, Bundle args) {
        super(host, resultsArray);

        this.podiumResults = new ArrayList<>();
        this.podiumDrivers = new HashMap<>();

        boolean firstSelected = args.getBoolean("FIRST_SELECTED");
        boolean secondSelected = args.getBoolean("SECOND_SELECTED");
        boolean thirdSelected = args.getBoolean("THIRD_SELECTED");

        for(ConstructorResult result : getAllResults()){

            for(ConstructorResultDriver driver : result.getDrivers()){

                String position = driver.getPosition();

                if((position.equalsIgnoreCase("1") && firstSelected) ||
                        (position.equalsIgnoreCase("2") && secondSelected) ||
                            (position.equalsIgnoreCase("3") && thirdSelected))
                {

                    if(this.podiumDrivers.get(result) == null)
                    {
                        this.podiumDrivers.put(result, new ArrayList<ConstructorResultDriver>());
                        this.podiumResults.add(result);
                    }

                    this.podiumDrivers.get(result).add(driver);

                }

            }

        }

    }



}//ConstructorPodiumAdapter
