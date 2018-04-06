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

/**
 * Created by iovisvikis on 15/5/2017.
 */

public class QualifyingResultsAdapter extends RecyclerView.Adapter<QualifyingResultsAdapter.QualifyingRecyclerViewHolder> {

    private ArrayList<QualifyingResult> results;

    private DownloadFragment host;
    private LayoutInflater inflater;
    private ViewGroup parent;


    @Override
    public QualifyingRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        inflater = host.getActivity().getLayoutInflater();
        this.parent = parent;

        View holderView = inflater.inflate(R.layout.qualifying_header, parent, false);
        return new QualifyingRecyclerViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(QualifyingRecyclerViewHolder holder, int position) {

        QualifyingResult result = results.get(position);

        AppCompatTextView seasonTxt = holder.getSeasonTxt();
        seasonTxt.setText(result.getSeason());

        ImageView countryFlagFrame = holder.getCountryFlagFrame();
        host.uploadCountryFlag(countryFlagFrame, result.getCountryName());


        AppCompatTextView raceNameTxt = holder.getRaceNameTxt();
        raceNameTxt.setText(result.getRaceName());

        AppCompatTextView localityTxt = holder.getLocalityTxt();
        localityTxt.setText(result.getLocality());

        LinearLayout qualifyingRows = holder.getQualifyingRowsLayout();

        if(qualifyingRows.getChildCount() == 0) {//recycler view reuses the view, make sure it is empty or get duplicates

            for (QualifyingRow qualifyingRow : result.getRows()) {

                View rowView = inflater.inflate(R.layout.qualifying_day, parent, false);

                AppCompatTextView gridTxt = rowView.findViewById(R.id.gridTxt);
                gridTxt.setText(host.getResources().getString(R.string.grid) + "\n" + qualifyingRow.getPosition());

                AppCompatTextView qsTxt = rowView.findViewById(R.id.qsTxt);
                qsTxt.setText("Q1: " + qualifyingRow.getQ1() + "\tQ2: " + qualifyingRow.getQ2() + "\tQ3: " + qualifyingRow.getQ3());

                final Driver driver = qualifyingRow.getDriver();

                //Get the driver photo container
                ImageView driverPhoto = rowView.findViewById(R.id.driverPhoto);
                //fill in the image
                host.uploadDriverPhoto(driverPhoto, driver.getId());

                //attach a listener that goes to the wiki page with information about the driver
                driverPhoto.setOnClickListener(new View.OnClickListener() {
                    final String url = driver.getUrl();

                    @Override
                    public void onClick(View view) {
                        Intent infoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        host.startActivity(infoIntent);
                    }
                });

                ImageView driverFlagFrame = rowView.findViewById(R.id.driverFlagFrame);
                host.uploadNationalityFlag(driverFlagFrame, driver.getNationality());

                AppCompatTextView driverNameTxt = rowView.findViewById(R.id.driverNameTxt);
                driverNameTxt.setText(driver.getName());


                //SET THE CONSSTRUCTOR INFORMATION
                final Constructor constructor = qualifyingRow.getConstructor();

                ImageView constructorPhoto = rowView.findViewById(R.id.constructorPhoto);
                host.uploadConstructorPhoto(constructorPhoto, constructor.getId());

                constructorPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = constructor.getUrl();
                        Intent wikIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        host.startActivity(wikIntent);
                    }
                });

                ImageView constructorFlagFrame = rowView.findViewById(R.id.constructorFlagFrame);
                host.uploadNationalityFlag(constructorFlagFrame, constructor.getNationality());

                AppCompatTextView constructorNameTxt = rowView.findViewById(R.id.constructorNameTxt);
                constructorNameTxt.setText(constructor.getName());

                qualifyingRows.addView(rowView);
            }
        }
    }


    @Override
    public int getItemCount() {
        return results.size();
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }




    public QualifyingResultsAdapter(DownloadFragment host, JSONArray resultsArray){

        this.host = host;

        this.results = new ArrayList<>();

        try{

            for(int index=0; index < resultsArray.length(); index++){
                JSONObject indexResult = resultsArray.getJSONObject(index);

                QualifyingResult result = new QualifyingResult(indexResult);
                results.add(result);
            }

        }
        catch (JSONException je){
            Log.e("QLFNGRSLTSADPTR", je.getMessage());
        }
    }





    class QualifyingRecyclerViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView seasonTxt, raceNameTxt, localityTxt;

        public AppCompatTextView getSeasonTxt() {
            return seasonTxt;
        }

        public AppCompatTextView getRaceNameTxt() {
            return raceNameTxt;
        }

        public AppCompatTextView getLocalityTxt() {
            return localityTxt;
        }

        public ImageView getCountryFlagFrame() {
            return countryFlagFrame;
        }

        public LinearLayout getQualifyingRowsLayout() {
            return qualifyingRowsLayout;
        }

        private ImageView countryFlagFrame;
        private LinearLayout qualifyingRowsLayout;

        public QualifyingRecyclerViewHolder(View itemView) {
            super(itemView);

            this.seasonTxt = itemView.findViewById(R.id.seasonTxt);
            this.raceNameTxt = itemView.findViewById(R.id.raceNameTxt);
            this.countryFlagFrame = itemView.findViewById(R.id.countryFlagFrame);
            this.localityTxt = itemView.findViewById(R.id.localityTxt);
            this.qualifyingRowsLayout = itemView.findViewById(R.id.qualifyingRowsLayout);

        }


    }


}//QualifyingResultsAdapter


