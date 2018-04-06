package comp.examplef1.iovisvikis.f1story;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import comp.examplef1.iovisvikis.f1story.R;


/**
 * Created by iovisvikis on 28/1/2017.
 */

public class DriversFragment extends SuperFragment{


    private AppCompatImageButton constructors, circuits, champs, info, podiums, raceResults, seasonResults;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setKey(getResources().getString(R.string.driver_tag));
    }



    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.driver_fragment, container, false);

        //set up the drivers selection before setting the tags
        setDriversTxt((AppCompatAutoCompleteTextView) root.findViewById(R.id.driver_driverSelection));

        setTags();

        constructors = root.findViewById(R.id.driver_constructors);

        circuits = root.findViewById(R.id.driver_circuits);

        champs = root.findViewById(R.id.driver_champions);

        info = root.findViewById(R.id.driver_info);

        podiums = root.findViewById(R.id.driver_podiums);

        raceResults = root.findViewById(R.id.driver_results);

        seasonResults = root.findViewById(R.id.driver_season_results);

        return root;
    }



    //making sure that initializeTxtViews() is called AFTER THE ACTIVITY IS CREATED and the fragment got references to its fields
    @Override
    public void onResume() {
        super.onResume();

        constructors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setConstructorsButton(getDriversTxt());
            }
        });

        circuits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCircuitsButton(getDriversTxt());
            }
        });

        champs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChampsButton(getDriversTxt());
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoButton(getDriversTxt());
            }
        });

        podiums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                setPodiumsButton(getDriversTxt());
            }
        });

        raceResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResultsButton(getDriversTxt());
            }
        });

        seasonResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSeasonResultsButton(getDriversTxt());
            }
        });

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.drv_frag_menu, menu);

    }




}//DriversFragment



