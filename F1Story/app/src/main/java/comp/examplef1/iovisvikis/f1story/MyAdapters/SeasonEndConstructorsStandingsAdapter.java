package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.content.Intent;

import android.net.Uri;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;

import comp.examplef1.iovisvikis.f1story.MainActivity;
import comp.examplef1.iovisvikis.f1story.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by iovisvikis on 28/4/2017.
 */

public class SeasonEndConstructorsStandingsAdapter extends RecyclerView.Adapter<SeasonEndConstructorsStandingsAdapter.SeasonEndConstructorsHolder>{

    private ArrayList<SeasonEndAllConstructors> constructorsRows;
    private DownloadFragment host;
    private LayoutInflater inflater;
    private ViewGroup parent;



    @Override
    public SeasonEndConstructorsHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        this.inflater = (LayoutInflater) host.getActivity().getLayoutInflater();
        this.parent = parent;

        View holderView = inflater.inflate(R.layout.season_end_results, parent, false);

        return new SeasonEndConstructorsHolder(holderView);
    }

    @Override
    public void onBindViewHolder(SeasonEndConstructorsHolder holder, int position) {

        final SeasonEndAllConstructors constructorResult = constructorsRows.get(position);

        FrameLayout infoButton = holder.getInfoButton();
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constructorResult.getUrl()));
                host.startActivity(infoIntent);
            }
        });

        AppCompatTextView seasonTxt = holder.getSeasonTxt();
        seasonTxt.setText(host.getResources().getString(R.string.season) + "\n" + constructorResult.getSeason());

        AppCompatTextView roundsTxt = holder.getRoundsTxt();
        roundsTxt.setText(host.getResources().getString(R.string.rounds) + "\n" + constructorResult.getRounds());

        LinearLayout seasonRows = holder.getAddSeasonRows();

        if(seasonRows.getChildCount() == 0) {//recycler view reuses the view, make sure it is empty or get duplicates

            for (final SeasonEndConstructors row : constructorResult.getRows()) {

                final CardView child = (CardView) inflater.inflate(R.layout.season_end_constructor, parent, false);

                final Constructor constructor = row.getConstructor();

                AppCompatTextView positionPointsWinsTxt = child.findViewById(R.id.positionPointsWinsTxt);
                String pstn = row.getPosition();
                String points = row.getPoints();
                String wins = row.getWins();
                positionPointsWinsTxt.setText(host.getResources().getString(R.string.position) + " " + pstn + "\n" +
                        host.getResources().getString(R.string.points) + " " + points + "\n" +
                        host.getResources().getString(R.string.wins) + " " + wins);

                //set the constructor logo
                ImageView constructorLogo = child.findViewById(R.id.constructorPhoto);
                host.uploadConstructorPhoto(constructorLogo, constructor.getId());

                final String infoString = constructor.getUrl();
                constructorLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent constructorInfoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(infoString));
                        host.startActivity(constructorInfoIntent);
                    }
                });

                AppCompatTextView constructorNameTxt = child.findViewById(R.id.constructorNameTxt);
                String constructorName = constructor.getName();
                constructorNameTxt.setText(constructorName);

                ImageView constructorFlagFrame = child.findViewById(R.id.constructorFlagFrame);
                String constructorNationality = constructor.getNationality();
                host.uploadNationalityFlag(constructorFlagFrame, constructorNationality);


                //Set up the drivers on demand for performance reasons. Takes too long to upload all
                final FrameLayout getDriversFrame = child.findViewById(R.id.getDriversFrame);
                getDriversFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String message = row.getConstructor().getName() + " " + constructorResult.getSeason() + " " +
                                host.getResources().getString(R.string.drivers);

                        String query = MainActivity.BASIC_URI + constructorResult.getSeason() +
                                "/constructors/" + row.getConstructor().getId() + "/drivers.json";

                        LinearLayout driversContainer = child.findViewById(R.id.constructorDriversLayout);

                        //populate the drivers in the container
                        host.uploadTheDrivers(query, message, constructorResult.getSeason(), parent, driversContainer);

                    }
                });

                seasonRows.addView(child);
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
        return constructorsRows.size();
    }





    public SeasonEndConstructorsStandingsAdapter(DownloadFragment host, JSONArray results) {

        this.host = host;

        this.constructorsRows = new ArrayList<>();

        try {

            for(int index=0; index < results.length(); index++){

                JSONObject root = results.getJSONObject(index);

                SeasonEndAllConstructors row = new SeasonEndAllConstructors(root);
                constructorsRows.add(row);
            }

        }
        catch (JSONException e) {
            Log.e("SSNENDCNSTRCSADAPTER", e.getMessage());
        }

    }



    class SeasonEndConstructorsHolder extends RecyclerView.ViewHolder {

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

        public SeasonEndConstructorsHolder(View itemView) {
            super(itemView);


            this.infoButton = itemView.findViewById(R.id.infoButton);
            this.seasonTxt = itemView.findViewById(R.id.seasonTxt);
            this.roundsTxt = itemView.findViewById(R.id.roundsTxt);
            this.addSeasonRows = itemView.findViewById(R.id.add_season_rows);

        }



    }


}
