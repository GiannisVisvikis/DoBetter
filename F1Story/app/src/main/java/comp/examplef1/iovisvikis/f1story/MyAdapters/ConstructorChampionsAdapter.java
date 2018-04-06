
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
import comp.examplef1.iovisvikis.f1story.MainActivity;
import comp.examplef1.iovisvikis.f1story.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ConstructorChampionsAdapter extends RecyclerView.Adapter<ConstructorChampionsAdapter.ConstructorChampionsHolder> {

    private DownloadFragment host;
    private ViewGroup parent;
    private LayoutInflater inflater;

    private ArrayList<ConstructorChampion> champs; //this determines the size
    private ArrayList<String> championTeamsIDs;
    private HashMap<String, ConstructorChampion> teamIDsToChampions;



    @Override
    public ConstructorChampionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.inflater = host.getActivity().getLayoutInflater();
        this.parent = parent;

        View holderView = inflater.inflate(R.layout.constructor_champion_row, parent, false);

        return new ConstructorChampionsHolder(holderView);
    }


    @Override
    public void onBindViewHolder(ConstructorChampionsHolder holder, int position){

        ConstructorChampion champ = champs.get(position);

        final Constructor constructor = champ.getChampionshipYears().get(0).getRows().get(0).getConstructor();

        ImageView constructorPhoto = holder.getConstructorPhoto();
        host.uploadConstructorPhoto(constructorPhoto, constructor.getId());

        constructorPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wikIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constructor.getUrl()));
                host.startActivity(wikIntent);
            }
        });

        AppCompatTextView constructorNameTxt = holder.getConstructorNameTxt();
        constructorNameTxt.setText(host.splitDown(constructor.getName()));

        ImageView constructorFlag = holder.getConstructorFlagFrame();
        host.uploadNationalityFlag(constructorFlag, constructor.getNationality());

        AppCompatTextView championshipsTxt = holder.getChampionshipsTxt();
        championshipsTxt.setText(host.getResources().getString(R.string.championships) +
                "\n" + champ.getChampionshipYears().size());

        LinearLayout championshipsLayout = holder.getChampionshipsLayout();
        //the recycler view does not call onCreateViewHolder every time, so this view will get reused again and again every
        //time you scroll down the list. If the linear layout does not get cleared each time this method gets called again
        //then there will be duplicates. Therefore, either clear possible entries before executing the code again, or do not
        //execute the code if non empty

        if(championshipsLayout.getChildCount() == 0) {//recycler view reuses the view, make sure it is empty or get duplicates

            for (final SeasonEndAllConstructors constructorYear : champ.getChampionshipYears()) {

                final LinearLayout seasonAddition = (LinearLayout) inflater.inflate(R.layout.season_addition, parent, false);

                FrameLayout info = seasonAddition.findViewById(R.id.infoButton);
                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent seasonIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constructorYear.getUrl()));
                        host.startActivity(seasonIntent);
                    }
                });

                AppCompatTextView seasonTxt = seasonAddition.findViewById(R.id.seasonTxt);
                seasonTxt.setText(host.getResources().getString(R.string.season) + "\n" + constructorYear.getSeason());

                AppCompatTextView roundsTxt = seasonAddition.findViewById(R.id.roundsTxt);
                roundsTxt.setText(host.getResources().getString(R.string.rounds) + "\n" + constructorYear.getRounds());

                final SeasonEndConstructors endConstructor = constructorYear.getRows().get(0); //only one contained

                AppCompatTextView winsTxt = seasonAddition.findViewById(R.id.winsTxt);
                winsTxt.setText(host.getResources().getString(R.string.wins) + "\n" + endConstructor.getWins());

                AppCompatTextView pointsTxt = seasonAddition.findViewById(R.id.pointsTxt);
                pointsTxt.setText(host.getResources().getString(R.string.points) + "\n" + endConstructor.getPoints());

                //setting the button that will upload the constructor's season drivers
                final FrameLayout helmetFrame = seasonAddition.findViewById(R.id.stewartHelmet);
                final  LinearLayout driversContainer = seasonAddition.findViewById(R.id.driversContainer);

                helmetFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String season = constructorYear.getSeason();

                        String query = MainActivity.BASIC_URI + season +
                                "/constructors/" + constructor.getId() + "/drivers.json";

                        String message = constructor.getName() + " " + season + " " +
                                host.getResources().getString(R.string.drivers);

                        //populate the drivers container
                        host.uploadTheDrivers(query, message, season, parent, driversContainer);
                    }
                });

                championshipsLayout.addView(seasonAddition);
            }
        }
    }



    @Override
    public int getItemCount() {
        return champs.size();
    }



    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }





    public ConstructorChampionsAdapter(DownloadFragment host, JSONArray resultsArray)
    {
        this.host = host;

        this.champs = new ArrayList<>();
        this.championTeamsIDs = new ArrayList<>();
        this.teamIDsToChampions = new HashMap<>();

        setHasStableIds(true);

        try
        {
            for(int index = 0; index < resultsArray.length(); index++)
            {
                JSONObject seasonResult = resultsArray.getJSONObject(index);

                JSONObject constructorSeasonResult = seasonResult.getJSONArray("ConstructorStandings").getJSONObject(0);

                String constructorId = constructorSeasonResult.getJSONObject("Constructor").getString("constructorId");

                if( teamIDsToChampions.get(constructorId) == null ) //new entry
                {
                    ConstructorChampion champ = new ConstructorChampion(seasonResult);
                    teamIDsToChampions.put(constructorId, champ);
                    championTeamsIDs.add(constructorId);
                    champs.add(champ);
                }
                else
                {
                    ConstructorChampion champ = teamIDsToChampions.get(constructorId);
                    SeasonEndAllConstructors seasonChamp = new SeasonEndAllConstructors(seasonResult);
                    champ.getChampionshipYears().add(seasonChamp);
                }


            }

        }
        catch (JSONException je)
        {
            Log.e("CnstrctrChmpnsAdptr", je.getMessage());
        }

    }



    class ConstructorChampionsHolder extends RecyclerView.ViewHolder {

        private ImageView constructorPhoto, constructorFlagFrame;
        private AppCompatTextView constructorNameTxt, championshipsTxt;
        private LinearLayout championshipsLayout;

        public ImageView getConstructorPhoto() {
            return constructorPhoto;
        }

        public ImageView getConstructorFlagFrame() {
            return constructorFlagFrame;
        }

        public AppCompatTextView getConstructorNameTxt() {
            return constructorNameTxt;
        }

        public AppCompatTextView getChampionshipsTxt() {
            return championshipsTxt;
        }

        public LinearLayout getChampionshipsLayout() {
            return championshipsLayout;
        }

        public ConstructorChampionsHolder(View itemView) {
            super(itemView);

            this.constructorPhoto = itemView.findViewById(R.id.constructorPhoto);
            this.constructorNameTxt = itemView.findViewById(R.id.constructorNameTxt);
            this.constructorFlagFrame = itemView.findViewById(R.id.constructorFlagFrame);
            this.championshipsTxt = itemView.findViewById(R.id.championshipsTxt);
            this.championshipsLayout = itemView.findViewById(R.id.championshipsLayout);

        }



    }


}