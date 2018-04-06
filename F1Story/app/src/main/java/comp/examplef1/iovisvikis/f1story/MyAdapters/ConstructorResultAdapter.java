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
 * Created by iovisvikis on 10/4/2017.
 */


public class ConstructorResultAdapter extends RecyclerView.Adapter<ConstructorResultAdapter.ConstructorResultViewHolder>{


    private ArrayList<ConstructorResult> allResults;
    private DownloadFragment host;
    private LayoutInflater inflater;
    private ViewGroup parent;


    public ArrayList<ConstructorResult> getAllResults() {
        return allResults;
    }




    @Override
    public ConstructorResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.inflater = host.getActivity().getLayoutInflater();
        this.parent = parent;
        
        return new ConstructorResultViewHolder(inflater.inflate(R.layout.constructor_result_row, parent, false));
    }

    @Override
    public void onBindViewHolder(ConstructorResultViewHolder holder, int position) {

        ConstructorResult conResult = allResults.get(position);
        ArrayList<ConstructorResultDriver> drivers = conResult.getDrivers();
        
        handleConstructorResultHolderView(holder, conResult, drivers);
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
        return allResults.size();
    }



    public void handleConstructorResultHolderView(ConstructorResultViewHolder holder, final ConstructorResult conResult, ArrayList<ConstructorResultDriver> drivers) {
        
        final Constructor constructor = conResult.getConstructor();

        ImageView constructorPhoto = holder.getConstructorPhoto();
        host.uploadConstructorPhoto(constructorPhoto, constructor.getId());

        constructorPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = constructor.getUrl();
                Intent wikiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                host.startActivity(wikiIntent);
            }
        });


        AppCompatTextView constructorTxt = holder.getConstructorNameTxt();
        String constructorName = host.splitDown(constructor.getName());
        constructorTxt.setText(constructorName);


        ImageView constructorFlagFrame = holder.getConstructorFlagFrame();
        host.uploadNationalityFlag(constructorFlagFrame, constructor.getNationality());


        AppCompatTextView seasonTxt = holder.getSeasonTxt();
        String season = conResult.getSeason();
        seasonTxt.setText(season);

        AppCompatTextView raceNameTxt = holder.getRaceNameTxt();
        String raceName = conResult.getRaceName();
        raceNameTxt.setText(raceName);


        FrameLayout infoFrame = holder.getRaceInfoButton();
        infoFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = conResult.getRaceUrl();
                Intent raceIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                host.startActivity(raceIntent);
            }
        });


        LinearLayout driversLayout = holder.getDriverAttachment();

        if(driversLayout.getChildCount() == 0) {

            for (int index = 0; index < drivers.size(); index++) {

                ConstructorResultDriver constructorResultDriver = drivers.get(index);

                View driverRow = inflater.inflate(R.layout.constructor_result_driver, parent, false);

                final Driver driver = constructorResultDriver.getDriver();

                ImageView driverPhoto = driverRow.findViewById(R.id.driverPhoto);
                host.uploadDriverPhoto(driverPhoto, driver.getId());

                driverPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String driverUrl = driver.getUrl();
                        Intent driverIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(driverUrl));
                        host.startActivity(driverIntent);
                    }
                });

                AppCompatTextView driverNameTxt = driverRow.findViewById(R.id.driverNameTxt);
                driverNameTxt.setText(host.splitDown(driver.getName()));


                ImageView driverFlagFrame = driverRow.findViewById(R.id.driverFlagFrame);
                host.uploadNationalityFlag(driverFlagFrame, driver.getNationality());


                AppCompatTextView pointsTxt = driverRow.findViewById(R.id.pointsTxt);
                String points = constructorResultDriver.getPoints();
                pointsTxt.setText(host.getResources().getString(R.string.points) + " " + points);

                AppCompatTextView lapsTxt = driverRow.findViewById(R.id.lapsTxt);
                String laps = constructorResultDriver.getLaps();
                lapsTxt.setText(host.getResources().getString(R.string.laps) + " " + laps);

                AppCompatTextView gridTxt = driverRow.findViewById(R.id.gridTxt);
                String grid = constructorResultDriver.getGrid();
                gridTxt.setText(host.getResources().getString(R.string.grid) + " " + grid);

                AppCompatTextView positionTxt = driverRow.findViewById(R.id.positionTxt);
                String position = constructorResultDriver.getPosition();
                positionTxt.setText(host.getResources().getString(R.string.position) + " " + position);

                driversLayout.addView(driverRow);
            }
        }
    }

    
    

    public ConstructorResultAdapter(DownloadFragment host, JSONArray resultsArray){

        this.host = host;
        this.allResults = new ArrayList<>();

        for(int index=0; index < resultsArray.length(); index++){

            try {
                JSONObject result = resultsArray.getJSONObject(index);

                ConstructorResult constructorResult = new ConstructorResult(result);
                allResults.add(constructorResult);
            }
            catch (JSONException e){
                Log.e("CONSTRESADAPT", e.getMessage());
            }

        }

    }
    


    class ConstructorResultViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView constructorPhoto, constructorFlagFrame;
        private AppCompatTextView constructorNameTxt, seasonTxt, raceNameTxt;
        private  FrameLayout raceInfoButton;
        private LinearLayout driverAttachment;

        public ImageView getConstructorPhoto() {
            return constructorPhoto;
        }

        public ImageView getConstructorFlagFrame() {
            return constructorFlagFrame;
        }

        public AppCompatTextView getConstructorNameTxt() {
            return constructorNameTxt;
        }

        public AppCompatTextView getSeasonTxt() {
            return seasonTxt;
        }

        public AppCompatTextView getRaceNameTxt() {
            return raceNameTxt;
        }

        public FrameLayout getRaceInfoButton() {
            return raceInfoButton;
        }

        public LinearLayout getDriverAttachment() {
            return driverAttachment;
        }

        public ConstructorResultViewHolder(View itemView) {
            super(itemView);
        
            this.constructorPhoto = itemView.findViewById(R.id.constructorPhoto);
            this.constructorFlagFrame = itemView.findViewById(R.id.constructorFlagFrame);
            this.constructorNameTxt = itemView.findViewById(R.id.constructorNameTxt);
            this.seasonTxt = itemView.findViewById(R.id.seasonTxt);
            this.raceNameTxt = itemView.findViewById(R.id.raceNameTxt);
            this.raceInfoButton = itemView.findViewById(R.id.raceInfoButton);
            this.driverAttachment = itemView.findViewById(R.id.driverAttachment);
        }
    
    }


}//ConstructorResultadapter



