package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.content.Intent;
import android.net.Uri;

import android.support.v7.widget.AppCompatTextView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;
import comp.examplef1.iovisvikis.f1story.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iovisvikis on 14/3/2017.
 */


public class SomeResultsAdapter extends RecyclerView.Adapter<SomeResultsAdapter.SomeResultsViewHolder> {


    private ArrayList<SomeResult> resultArray;

    private DownloadFragment host;
    private LayoutInflater inflater;
    private ViewGroup parent;


    protected  ArrayList<SomeResult> getResultArray(){
        return resultArray;
    }


    @Override
    public SomeResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.inflater = host.getActivity().getLayoutInflater();
        this.parent = parent;
        View holderView = inflater.inflate(R.layout.some_results_header, parent, false);
        return new SomeResultsViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(SomeResultsViewHolder holder, int position){

        setTheHolder(holder, position, resultArray.get(0).getRacesInfo());

    }


    @Override
    public int getItemCount(){
        return resultArray.size();
    }



    protected void setTheHolder(SomeResultsViewHolder holder, int position, List<RaceInfo> results){

        SomeResult someResult = resultArray.get(position);

        //set the driver information in the header
        final Driver driver = someResult.getDriver();

        ImageView driverPhotoFrame = holder.getDriverImage();
        host.uploadDriverPhoto(driverPhotoFrame, driver.getId());

        driverPhotoFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = driver.getUrl();
                Intent wikiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                host.startActivity(wikiIntent);
            }
        });

        ImageView driverFlagFrame = holder.getDriverFlagFrame();
        host.uploadNationalityFlag(driverFlagFrame, driver.getNationality());

        AppCompatTextView driverNameTxt = holder.getDriverNameTxt();
        driverNameTxt.setText(host.splitDown(driver.getName()));


        //set the constructor information in the header
        final Constructor constructor = someResult.getConstructor();
        ImageView constructorPhotoFrame = holder.getConstructorImage();
        host.uploadConstructorPhoto(constructorPhotoFrame, constructor.getId());

        constructorPhotoFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = constructor.getUrl();
                Intent wikiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                host.startActivity(wikiIntent);
            }
        });

        ImageView constructorFlagFrame = holder.getConstructorFlagFrame();
        host.uploadNationalityFlag(constructorFlagFrame, constructor.getNationality());

        AppCompatTextView constNameTxt = holder.getConstructorNameTxt();
        constNameTxt.setText(host.splitDown(constructor.getName()));

        AppCompatTextView seasonTxt = holder.getSeasonTxt();
        seasonTxt.setText( host.getResources().getString(R.string.season) + " : " + someResult.getSeason());


        //fetch the some_results_rows_container and add the information
        LinearLayout resultRowsFrame = holder.getSomeResultsRowsContainer();

        if(resultRowsFrame.getChildCount() == 0) { //recycler view reuses the view, make sure it is empty or get duplicates

            for (final RaceInfo raceInfo : results) {

                View rowRoot = inflater.inflate(R.layout.some_results_row, parent, false);

                FrameLayout info = rowRoot.findViewById(R.id.infoButton);
                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = raceInfo.getRaceUrl();
                        Intent wikiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        host.startActivity(wikiIntent);
                    }
                });


                AppCompatTextView raceNameTxt = rowRoot.findViewById(R.id.raceNameTxt);
                raceNameTxt.setText(raceInfo.getCircuitName());

                ImageView countryFrame = rowRoot.findViewById(R.id.raceCountryFlag);
                host.uploadCountryFlag(countryFrame, raceInfo.getCountry());

                AppCompatTextView localityTxt = rowRoot.findViewById(R.id.localityTxt);
                localityTxt.setText(raceInfo.getLocality());

                AppCompatTextView gridTxt = rowRoot.findViewById(R.id.gridTxt);
                gridTxt.setText(host.getResources().getString(R.string.grid) + "\n" + raceInfo.getStartingGrid());

                AppCompatTextView pointsTxt = rowRoot.findViewById(R.id.pointsTxt);
                pointsTxt.setText(host.getResources().getString(R.string.points) + "\n" + raceInfo.getPoints());

                AppCompatTextView positionTxt = rowRoot.findViewById(R.id.positionTxt);
                positionTxt.setText(host.getResources().getString(R.string.position) + "\n" + raceInfo.getPosition());

                AppCompatTextView statusTxt = rowRoot.findViewById(R.id.statusTxt);
                statusTxt.setText(host.getResources().getString(R.string.status) + "\n" + raceInfo.getStatus());

                AppCompatTextView lapTxt = rowRoot.findViewById(R.id.lapsTxt);
                lapTxt.setText(host.getResources().getString(R.string.laps) + "\n" + raceInfo.getLaps());

                AppCompatTextView fastTxt = rowRoot.findViewById(R.id.fastestTxt);
                fastTxt.setText(host.getResources().getString(R.string.fastest) + "\n" + raceInfo.getFastest());

                AppCompatTextView speedTxt = rowRoot.findViewById(R.id.speedTxt);
                speedTxt.setText(host.getResources().getString(R.string.speed) + "\n" + raceInfo.getSpeed());

                AppCompatTextView timeTxt = rowRoot.findViewById(R.id.timeTxt);
                timeTxt.setText(host.getResources().getString(R.string.time) + "\n" + raceInfo.getTime());

                resultRowsFrame.addView(rowRoot);
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



    /**
     *
     * @param frag The hosting getDownloadFragment()
     * @param newResultsArray is what api.getData method returns
     */
    public SomeResultsAdapter(DownloadFragment frag, JSONArray newResultsArray){

        this.host = frag;
        this.resultArray = new ArrayList<>();

        SomeResult some = new SomeResult(newResultsArray);

        resultArray.add(some);

    }




    class SomeResultsViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout someResultsHeader, someResultsRowsContainer;
        private ImageView driverImage, driverFlagFrame, constructorImage, constructorFlagFrame;
        private AppCompatTextView driverNameTxt, constructorNameTxt, seasonTxt;


        public LinearLayout getSomeResultsHeader() {
            return someResultsHeader;
        }

        public LinearLayout getSomeResultsRowsContainer() {
            return someResultsRowsContainer;
        }

        public ImageView getDriverImage() {
            return driverImage;
        }

        public ImageView getDriverFlagFrame() {
            return driverFlagFrame;
        }

        public ImageView getConstructorImage() {
            return constructorImage;
        }

        public ImageView getConstructorFlagFrame() {
            return constructorFlagFrame;
        }

        public AppCompatTextView getDriverNameTxt() {
            return driverNameTxt;
        }

        public AppCompatTextView getConstructorNameTxt() {
            return constructorNameTxt;
        }

        public AppCompatTextView getSeasonTxt() {
            return seasonTxt;
        }

        public SomeResultsViewHolder(View itemView) {
            super(itemView);

            this.someResultsHeader = itemView.findViewById(R.id.some_results_header);
            this.driverImage = itemView.findViewById(R.id.driverImage);
            this.driverNameTxt = itemView.findViewById(R.id.driverNameTxt);
            this.driverFlagFrame = itemView.findViewById(R.id.driverFlagFrame);
            this.constructorImage = itemView.findViewById(R.id.constructorImage);
            this.constructorNameTxt = itemView.findViewById(R.id.constructorNameTxt);
            this.constructorFlagFrame = itemView.findViewById(R.id.constructorFlagFrame);
            this.seasonTxt = itemView.findViewById(R.id.seasonTxt);
            this.someResultsRowsContainer = itemView.findViewById(R.id.some_resuls_row_container);

        }



    }



}//ResultsAdapter



