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
 * Created by iovisvikis on 22/4/2017.
 */

public class CircuitFragment extends SuperFragment{


    private AppCompatImageButton driversButton, constructorsButton, infoButton, resultButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setKey(getResources().getString(R.string.circuit_tag));
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.circuit_fragment, container, false);

        setCircuitsTxt((AppCompatAutoCompleteTextView) root.findViewById(R.id.circuit_circuitSelection));

        setTags();

        driversButton = root.findViewById(R.id.circuit_drivers);

        constructorsButton = root.findViewById(R.id.circuit_constructors);

        infoButton = root.findViewById(R.id.circuit_info);

        resultButton = root.findViewById(R.id.circuit_results);

        return root;
    }



    @Override
    public void onResume() {
        super.onResume();

        driversButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDriversButton(getCircuitsTxt());
            }
        });

        constructorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setConstructorsButton(getCircuitsTxt());
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoButton(getCircuitsTxt());
            }
        });

        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResultsButton(getCircuitsTxt());
            }
        });

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.circ_frag_menu, menu);

    }



}//CircuitFragment




