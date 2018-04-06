package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.content.Intent;

import android.net.Uri;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;
import comp.examplef1.iovisvikis.f1story.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iovisvikis on 16/4/2017.
 */

public class UIResultAdapter extends RecyclerView.Adapter<UIResultAdapter.UIResultHolder> {

    private DownloadFragment host;
    
    private ArrayList<RaceObject> raceResults;

    private LayoutInflater inflater;

    private ViewGroup parent;




    @Override
    public UIResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        this.inflater = host.getActivity().getLayoutInflater();
        this.parent = parent;
        
        View holderView = inflater.inflate(R.layout.ui_results_header_row, parent, false);
        
        return new UIResultHolder(holderView);
    }
    
    
    
    @Override
    public void onBindViewHolder(UIResultHolder holder, int position) {

        RaceObject raceObject = raceResults.get(position);

        FrameLayout raceInfoButton = (FrameLayout) holder.getRaceInfoButton();
        final String raceUri = raceObject.getRaceUrl();
        raceInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent raceInfoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(raceUri));
                host.startActivity(raceInfoIntent);
            }
        });

        AppCompatTextView seasonTxt = holder.getSeasonTxt();
        String season = raceObject.getSeason();
        seasonTxt.setText(season);

        AppCompatTextView localityTxt = holder.getLocalityTxt();
        String locality = raceObject.getLocality();
        localityTxt.setText(locality);

        ImageView countryFlagFrame = holder.getCountryFlagFrame();
        host.uploadCountryFlag(countryFlagFrame, raceObject.getCountry());

        AppCompatTextView circuitNameTxt = holder.getCircuitNameTxt();
        String circuitName = raceObject.getCircuitName();
        circuitNameTxt.setText(circuitName);

        FrameLayout viewButton = holder.getViewButton();
        String lat = raceObject.getLang();
        String longt = raceObject.getLongt();

        //http://maps.google.com/maps?z=12&t=k&q=loc:latitude+longtitude
        //k for satellite, z for zoom
        final String viewUri = "http://maps.google.com/maps?z=12&t=k&q=loc:" + lat +"+" + longt;
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(viewUri));
                host.startActivity(viewIntent);
            }
        });


        LinearLayout driverEntries = holder.getDriversLayout();

        ArrayList<UIResult> driverStandings = raceObject.getResultRows();

        if(driverEntries.getChildCount() == 0) {

            for (UIResult standing : driverStandings) {//recycler view reuses the view, make sure it is empty or get duplicates

                View driverRoot = inflater.inflate(R.layout.ui_result_driver_row, parent, false);

                final Driver driver = standing.getDriver();
                final Constructor constructor = standing.getConstructor();

                ImageView driverPhoto = (ImageView) driverRoot.findViewById(R.id.driverPhoto);
                host.uploadDriverPhoto(driverPhoto, driver.getId());

                driverPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent driverIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(driver.getUrl()));
                        host.startActivity(driverIntent);
                    }
                });

                AppCompatTextView driverNameTxt = driverRoot.findViewById(R.id.driverNameTxt);
                String driverName = driver.getName();
                driverNameTxt.setText(driverName);

                ImageView driverFlagFrame = driverRoot.findViewById(R.id.driverFlagFrame);
                host.uploadNationalityFlag(driverFlagFrame, driver.getNationality());


                ImageView constructorPhoto = driverRoot.findViewById(R.id.constructorPhoto);
                host.uploadConstructorPhoto(constructorPhoto, constructor.getId());

                constructorPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent conIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constructor.getUrl()));
                        host.startActivity(conIntent);
                    }
                });

                ImageView constructorFlagFrame = driverRoot.findViewById(R.id.constructorFlagFrame);
                host.uploadNationalityFlag(constructorFlagFrame, constructor.getNationality());


                AppCompatTextView constructorNameTxt = driverRoot.findViewById(R.id.constructorNameTxt);
                String constructorName = constructor.getName();
                constructorNameTxt.setText(constructorName);


                //set up the race results information
                AppCompatTextView gridTxt = driverRoot.findViewById(R.id.gridTxt);
                String grid = standing.getGrid();
                gridTxt.setText(host.getResources().getString(R.string.grid) + "\n" + grid);

                AppCompatTextView positionTxt = driverRoot.findViewById(R.id.positionTxt);
                String pstn = standing.getPosition();
                positionTxt.setText(host.getResources().getString(R.string.position) + "\n" + pstn);

                AppCompatTextView timeTxt = driverRoot.findViewById(R.id.timeTxt);
                String time = standing.getTime();
                timeTxt.setText(host.getResources().getString(R.string.time) + "\n" + time);

                AppCompatTextView fastTxt = driverRoot.findViewById(R.id.fastestTxt);
                String fastest = standing.getFastest();
                fastTxt.setText(host.getResources().getString(R.string.fastest) + "\n" + fastest);

                AppCompatTextView pointsTxt = driverRoot.findViewById(R.id.pointsTxt);
                String points = standing.getPoints();
                pointsTxt.setText(host.getResources().getString(R.string.points) + "\n" + points);

                AppCompatTextView statusTxt = driverRoot.findViewById(R.id.statusTxt);
                String status = standing.getStatus();
                statusTxt.setText(host.getResources().getString(R.string.status) + "\n" + status);

                driverEntries.addView(driverRoot);
            }
        }
    }


    @Override
    public int getItemCount() {
        return raceResults.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public UIResultAdapter(DownloadFragment host, JSONArray resultsArray){

        this.host = host;
        this.raceResults = new ArrayList<>();

        try{
            for(int index=0; index<resultsArray.length(); index++){
                JSONObject resultObject = resultsArray.getJSONObject(index);

                RaceObject race = new RaceObject(resultObject);
                raceResults.add(race);
            }
            
        }
        catch (JSONException e){
            Log.e("UIRESULTADAPTER", e.getMessage());
        }
    }
    
    
    
    class UIResultHolder extends RecyclerView.ViewHolder{

        private FrameLayout raceInfoButton, viewButton;
        private AppCompatTextView seasonTxt, circuitNameTxt, localityTxt;
        private ImageView countryFlagFrame;
        private LinearLayout driversLayout;

        public FrameLayout getRaceInfoButton() {
            return raceInfoButton;
        }

        public FrameLayout getViewButton() {
            return viewButton;
        }

        public AppCompatTextView getSeasonTxt() {
            return seasonTxt;
        }

        public AppCompatTextView getCircuitNameTxt() {
            return circuitNameTxt;
        }

        public AppCompatTextView getLocalityTxt() {
            return localityTxt;
        }

        public ImageView getCountryFlagFrame() {
            return countryFlagFrame;
        }

        public LinearLayout getDriversLayout() {
            return driversLayout;
        }

        public UIResultHolder(View itemView) {
            super(itemView);

            this.raceInfoButton = itemView.findViewById(R.id.raceInfoButton);
            this.seasonTxt = itemView.findViewById(R.id.seasonTxt);
            this.circuitNameTxt = itemView.findViewById(R.id.circuitNameTxt);
            this.countryFlagFrame = itemView.findViewById(R.id.countryFlagFrame);
            this.localityTxt = itemView.findViewById(R.id.localityTxt);
            this.viewButton = itemView.findViewById(R.id.viewButton);
            this.driversLayout = itemView.findViewById(R.id.driversLayout);
        }


    }


}//UIResultAdapter


