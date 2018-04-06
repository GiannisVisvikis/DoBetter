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



/**
 * Created by iovisvikis on 6/4/2017.
 */

public class ConstructorsFragment extends SuperFragment{


    private AppCompatImageButton drivers, circuits, champions, info, podiums, results, seasonResults;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setKey(getResources().getString(R.string.constructor_tag));
    }


    //references needed onActivityCreated
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.constructor_fragment, container, false);

        setConstructorsTxt((AppCompatAutoCompleteTextView) root.findViewById(R.id.constructor_constructorSelection));

        setTags();
        //initializeTxtViews();

        drivers = root.findViewById(R.id.constructor_drivers);

        circuits = root.findViewById(R.id.constructor_circuits);

        champions = root.findViewById(R.id.constructor_champions);

        info = root.findViewById(R.id.constructor_info);

        podiums = root.findViewById(R.id.constructor_podiums);

        results = root.findViewById(R.id.constructor_results);

        seasonResults = root.findViewById(R.id.constructor_season_results);

        return root;
    }


    //making sure that initializeTxtViews() is called AFTER THE ACTIVITY IS CREATED and the fragment got references to its fields already, after the activity
    //is created. The listeners need a reference to the activity's fields.
    @Override
    public void onResume() {
        super.onResume();

        drivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDriversButton(getConstructorsTxt());
            }
        });

        circuits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCircuitsButton(getConstructorsTxt());
            }
        });

        champions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChampsButton(getConstructorsTxt());
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoButton(getConstructorsTxt());
            }
        });

        podiums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPodiumsButton(getConstructorsTxt());
            }
        });

        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResultsButton(getConstructorsTxt());
            }
        });

        seasonResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSeasonResultsButton(getConstructorsTxt());
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.const_frag_menu, menu);

    }


}//ConstructorsFragment





