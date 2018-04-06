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
 * Created by iovisvikis on 27/4/2017.
 */

public class SeasonEndDriverStandingsAdapter extends RecyclerView.Adapter<SeasonEndDriverStandingsAdapter.SeasonsEndResultsHolder> {


    private ArrayList<SeasonEndAllDrivers> rows;
    
    private DownloadFragment host;
    private LayoutInflater inflater;
    private ViewGroup parent;
    
    

    public SeasonEndDriverStandingsAdapter(DownloadFragment host, JSONArray results){

        this.host = host;
        this.rows = new ArrayList<>();

        try {

            for(int index=0; index<results.length(); index++){
                JSONObject root = results.getJSONObject(index);
                SeasonEndAllDrivers row = new SeasonEndAllDrivers(root);

                rows.add(row);
            }

        }
        catch (JSONException e){
            Log.e("SSNENDDRVRSADAPTER", e.getMessage());
        }
    }

    @Override
    public SeasonsEndResultsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        inflater = host.getActivity().getLayoutInflater();
        this.parent = parent;
        
        View holderView = inflater.inflate(R.layout.season_end_results, parent, false);
        
        return new SeasonsEndResultsHolder(holderView);
    }


    @Override
    public void onBindViewHolder(SeasonsEndResultsHolder holder, int position) {

        final SeasonEndAllDrivers driverRow = rows.get(position);

        FrameLayout infoButton = holder.getInfoButton();
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(driverRow.getUrl()));
                host.startActivity(infoIntent);
            }
        });

        AppCompatTextView seasonTxt = holder.getSeasonTxt();
        seasonTxt.setText(host.getResources().getString(R.string.season) + "\n" + driverRow.getSeason());

        AppCompatTextView roundsTxt = holder.getRoundsTxt();
        roundsTxt.setText(host.getResources().getString(R.string.rounds) + "\n"  + driverRow.getRounds());

        LinearLayout driverRows = holder.getAddSeasonRows();

        if(driverRows.getChildCount() == 0) {//recycler view reuses the view, make sure it is empty or get duplicates

            for (SeasonEndDrivers row : driverRow.getRows()) {

                View child = inflater.inflate(R.layout.season_result_driver, parent, false);

                Driver driver = row.getDriver();

                AppCompatTextView positionTxt = child.findViewById(R.id.positionTxt);
                String pstn = row.getPosition();
                positionTxt.setText(host.getResources().getString(R.string.position) + " " + pstn);

                AppCompatTextView pointsTxt = child.findViewById(R.id.pointsTxt);
                String points = row.getPoints();
                pointsTxt.setText(host.getResources().getString(R.string.points) + " " + points);

                AppCompatTextView winsTxt = child.findViewById(R.id.winsTxt);
                String wins = row.getWins();
                winsTxt.setText(host.getResources().getString(R.string.wins) + " " + wins);


                ImageView driverPhoto = child.findViewById(R.id.driverPhoto);
                host.uploadDriverPhoto(driverPhoto, driver.getId());

                //Get the driver photo and attach the click listener
                final String driverInfoString = driver.getUrl();
                driverPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent driverinfoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(driverInfoString));
                        host.startActivity(driverinfoIntent);
                    }
                });


                AppCompatTextView driverNameTxt = child.findViewById(R.id.driverNameTxt);
                String driverName = driver.getName();
                driverNameTxt.setText(host.splitDown(driverName));

                ImageView driverFlagFrame = child.findViewById(R.id.driverFlagFrame);
                String driverNationality = driver.getNationality();
                host.uploadNationalityFlag(driverFlagFrame, driverNationality);


                //Same for constructor
                Constructor constructor = row.getConstructor();

                ImageView constructorPhoto = child.findViewById(R.id.constructorPhoto);
                host.uploadConstructorPhoto(constructorPhoto, constructor.getId());

                final String constructorUrl = constructor.getUrl();
                constructorPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent wikiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constructorUrl));
                        host.startActivity(wikiIntent);
                    }
                });

                AppCompatTextView constructorNameTxt = child.findViewById(R.id.constructorNameTxt);
                String constructorName = constructor.getName();
                constructorNameTxt.setText(host.splitDown(constructorName));

                ImageView constructorFlagFrame = child.findViewById(R.id.constructorFlagFrame);
                String constructorNationality = constructor.getNationality();
                host.uploadNationalityFlag(constructorFlagFrame, constructorNationality);

                driverRows.addView(child);
            }
        }
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public int getItemCount() {
        return rows.size();
    }

    
    
    

    class SeasonsEndResultsHolder extends RecyclerView.ViewHolder{

        private LinearLayout addSeasonRows;

        public LinearLayout getAddSeasonRows() {
            return addSeasonRows;
        }

        public FrameLayout getInfoButton() {
            return infoButton;
        }

        public AppCompatTextView getSeasonTxt() {
            return seasonTxt;
        }

        public AppCompatTextView getRoundsTxt() {
            return roundsTxt;
        }

        private FrameLayout infoButton;
        private AppCompatTextView seasonTxt, roundsTxt;
        
        public SeasonsEndResultsHolder(View itemView) {
            super(itemView);
            
            this.infoButton = itemView.findViewById(R.id.infoButton);
            this.seasonTxt = itemView.findViewById(R.id.seasonTxt);
            this.roundsTxt = itemView.findViewById(R.id.roundsTxt);
            this.addSeasonRows = itemView.findViewById(R.id.add_season_rows);
        }
    
    }
    

}//SeasonEndDriverStandingsAdapter


