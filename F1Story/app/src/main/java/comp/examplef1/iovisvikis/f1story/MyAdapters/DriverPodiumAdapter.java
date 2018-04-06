package comp.examplef1.iovisvikis.f1story.MyAdapters;

/**
 * Created by iovisvikis on 10/4/2017.
 */

import android.os.Bundle;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by iovisvikis on 9/4/2017.
 */


public class DriverPodiumAdapter extends SomeResultsAdapter{


    private ArrayList<RaceInfo> firstPlaces, secondPlaces, thirdPlaces;

    private ArrayList<RaceInfo> finalPlaces = new ArrayList<>();



    @Override
    public void onBindViewHolder(SomeResultsViewHolder holder, int position) {
        setTheHolder(holder, position, finalPlaces);
    }

    /*@Override
    public int getItemCount() {
        return finalPlaces.size();
    }
*/

    public DriverPodiumAdapter(DownloadFragment host, JSONArray resultsArray, Bundle args){

        super(host, resultsArray);

        boolean firstSelected = args.getBoolean("FIRST_SELECTED");
        boolean secondSelected = args.getBoolean("SECOND_SELECTED");
        boolean thirdSelected = args.getBoolean("THIRD_SELECTED");

        if(firstSelected)
            firstPlaces = new ArrayList<>();

        if(secondSelected)
            secondPlaces = new ArrayList<>();

        if(thirdSelected)
            thirdPlaces = new ArrayList<>();


        for(int index=0; index<getResultArray().get(0).getRacesInfo().size(); index++){
                                    //resultArray only contains one element

            RaceInfo race = getResultArray().get(0).getRacesInfo().get(index);
            String position = race.getPosition();

            if(position.equalsIgnoreCase("1") && firstSelected)
                firstPlaces.add(race);
            else if(position.equalsIgnoreCase("2") && secondSelected)
                secondPlaces.add(race);
            else if(position.equalsIgnoreCase("3") && thirdSelected)
                thirdPlaces.add(race);
        }

        if(firstSelected){
            finalPlaces.addAll(firstPlaces);
        }

        if(secondSelected){
            finalPlaces.addAll(secondPlaces);
        }

        if(thirdSelected){
            finalPlaces.addAll(thirdPlaces);
        }

    }


}//DriverPodiumAdapter




