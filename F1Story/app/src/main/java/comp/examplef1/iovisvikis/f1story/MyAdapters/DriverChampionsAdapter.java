package comp.examplef1.iovisvikis.f1story.MyAdapters;


import android.content.Intent;

import android.net.Uri;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;
import comp.examplef1.iovisvikis.f1story.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by iovisvikis on 27/3/2017.
 */

public class DriverChampionsAdapter extends RecyclerView.Adapter<DriverChampionsAdapter.DriverChampionsHolder> {

    
    
    private ArrayList<String> idsList;  //ids will be kept here for keeping chronological order
    private HashMap<String, DriverChampion> idsTochampions; //ids to DriverChampion objects
    
    private DownloadFragment host;
    private LayoutInflater inflater;
    private ViewGroup parent;
    




    public DriverChampionsAdapter(DownloadFragment host, JSONArray standingsList) {
        
        this.host = host;
        
        this.idsList = new ArrayList<>();
        this.idsTochampions = new HashMap<>();

        try
        {
            for(int index=0; index<standingsList.length(); index++)
            {
                JSONObject standingsListObject = standingsList.getJSONObject(index);

                String driverId = standingsListObject.getJSONArray("DriverStandings")
                                                        .getJSONObject(0)
                                                        .getJSONObject("Driver")
                                                        .getString("driverId");

                if(idsTochampions.get(driverId) == null) //new champion entry
                {
                    DriverChampion newChampion = new DriverChampion(standingsListObject);
                    idsList.add(driverId);
                    idsTochampions.put(driverId, newChampion);
                }
                else //already have created this champ, fetch it's info and add another championship
                {
                    DriverChampion oldChamp = idsTochampions.get(driverId);
                    DriverChampionshipSeason newSeason = new DriverChampionshipSeason(standingsListObject);
                    oldChamp.getSeasonsWon().add(newSeason);
                }
            }
        }
        catch (JSONException je)
        {
            Log.e("DrvrChmpnsAdptr", je.getMessage());
        }

    }

    @Override
    public DriverChampionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        inflater = host.getActivity().getLayoutInflater();
        this.parent = parent;

        View holderView = inflater.inflate(R.layout.driver_champion_row, parent, false);
        return  new DriverChampionsHolder(holderView);
    }


    @Override
    public void onBindViewHolder(DriverChampionsHolder holder, int position) {

        String driverId = idsList.get(position);

        DriverChampion champ = idsTochampions.get(driverId);

        final Driver driverInfo = champ.getDriver();

        ImageView driverPhoto = holder.getDriverPhoto();
        host.uploadDriverPhoto(driverPhoto, driverId);

        driverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = driverInfo.getUrl();
                Intent wikIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                host.startActivity(wikIntent);
            }
        });

        AppCompatTextView driverNameTxt = holder.getDriverNameTxt();
        driverNameTxt.setText(host.splitDown(driverInfo.getName()));

        ImageView driverFlagView = holder.getDriverFlagFrame();
        host.uploadNationalityFlag(driverFlagView, driverInfo.getNationality());

        AppCompatTextView championshipsTxt = holder.getChampionshipsTxt();
        championshipsTxt.setText(host.getResources().getString(R.string.championships) + "\n" + champ.getSeasonsWon().size());

        LinearLayout child = holder.getConstructorsLayout();

        if(child.getChildCount() == 0) {//recycler view reuses the view, make sure it is empty or get duplicates

            for (DriverChampionshipSeason seasonInfo : champ.getSeasonsWon()) {

                View season = inflater.inflate(R.layout.constructor_addition, parent, false);

                AppCompatTextView seasonsAndRounds = season.findViewById(R.id.seasonRoundsTxt);
                seasonsAndRounds.setText(host.getResources().getString(R.string.season) + " " + seasonInfo.getSeason() + "\n" +
                        host.getResources().getString(R.string.rounds) + " " + seasonInfo.getRounds() + "\n" +
                        host.getResources().getString(R.string.wins) + " " + seasonInfo.getWins() + "\n" +
                        host.getResources().getString(R.string.points) + " " + seasonInfo.getPoints());

                final Constructor constructor = seasonInfo.getConstructor();

                ImageView constructorImage = season.findViewById(R.id.constructorPhoto);
                host.uploadConstructorPhoto(constructorImage, constructor.getId());

                constructorImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = constructor.getUrl();
                        Intent wikIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        host.startActivity(wikIntent);
                    }
                });


                AppCompatTextView constructorTxt = season.findViewById(R.id.constructorTxt);
                constructorTxt.setText(host.splitDown(constructor.getName()));

                ImageView constructorFlagFrame = season.findViewById(R.id.constructorFlagFrame);
                host.uploadNationalityFlag(constructorFlagFrame, constructor.getNationality());

                child.addView(season);
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
        return idsList.size();
    }




    class DriverChampionsHolder extends RecyclerView.ViewHolder {


        private ImageView driverPhoto, driverFlagFrame;
        private AppCompatTextView driverNameTxt, championshipsTxt;
        private LinearLayout constructorsLayout;

        public ImageView getDriverPhoto() {
            return driverPhoto;
        }

        public ImageView getDriverFlagFrame() {
            return driverFlagFrame;
        }

        public AppCompatTextView getDriverNameTxt() {
            return driverNameTxt;
        }

        public AppCompatTextView getChampionshipsTxt() {
            return championshipsTxt;
        }

        public LinearLayout getConstructorsLayout() {
            return constructorsLayout;
        }

        public DriverChampionsHolder(View itemView) {
            super(itemView);

            this.driverPhoto = itemView.findViewById(R.id.driverPhoto);
            this.driverNameTxt = itemView.findViewById(R.id.driverNameTxt);
            this.driverFlagFrame = itemView.findViewById(R.id.driverFlagFrame);
            this.championshipsTxt = itemView.findViewById(R.id.championshipsTxt);
            this.constructorsLayout = itemView.findViewById(R.id.constructorsLayout);

        }


    }


}//DriverChampionsAdapter



