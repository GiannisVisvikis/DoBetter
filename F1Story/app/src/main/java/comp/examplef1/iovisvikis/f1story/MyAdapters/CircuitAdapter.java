package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import android.os.AsyncTask;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.UploadSeasonsTask;
import comp.examplef1.iovisvikis.f1story.MainActivity;
import comp.examplef1.iovisvikis.f1story.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iovisvikis on 30/3/2017.
 */

public class CircuitAdapter extends RecyclerView.Adapter<CircuitAdapter.CircuitViewHolder> {

    
    private ArrayList<CircuitEntry> circuits;
    private DownloadFragment host;




    public CircuitAdapter(DownloadFragment host, JSONArray circuitsArray) {

        this.host = host;
        this.circuits = new ArrayList<>();

        for (int index=0; index < circuitsArray.length(); index++){

            try {
                JSONObject circuit = circuitsArray.getJSONObject(index);
                CircuitEntry entry = new CircuitEntry(circuit);
                circuits.add(entry);
            }
            catch (JSONException e){
                Log.e("CIRCUITADAPTER", e.getMessage());
            }
        }

    }

    @Override
    public CircuitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) host.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View holder = inflater.inflate(R.layout.circuit_row, parent, false);

        return new CircuitViewHolder(holder);
    }



    @Override
    public void onBindViewHolder(CircuitViewHolder holder, int position) {

        final CircuitEntry entry = circuits.get(position);

        FrameLayout infoButton = holder.getInfoButton();
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = entry.getUrl();
                Intent wikInfo = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                host.startActivity(wikInfo);
            }
        });


        AppCompatTextView localityTxt = holder.getLocalityTxt();
        localityTxt.setText(entry.getLocality());

        AppCompatTextView circuitNameTxt = holder.getCircuitNameTxt();
        circuitNameTxt.setText(entry.getName());

        //set the country flag
        ImageView countryFlagFrame = holder.getCountryFlag();
        host.uploadCountryFlag(countryFlagFrame, entry.getCountry());


        final AutoCompleteTextView seasonsContainer = holder.getSeasonsContainer();

        AppCompatButton calendarButton = holder.getCalendarLayout();
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(seasonsContainer.getAdapter() == null) {//results not loaded in yet
                    try {

                        String choiceId = DatabaseUtils.stringForQuery(host.getAct().getAppDatabase(),
                                "select " + host.getCallerFragmentTag() + "_id from all_" + host.getCallerFragmentTag() +
                                        "s where " + host.getCallerFragmentTag() + "_name = '" + host.getCallerSelectedName() + "';", null);

                        String query = MainActivity.BASIC_URI + host.getCallerFragmentTag() + "s/" + choiceId + "/circuits/" +
                                entry.getId() + "/seasons.json";

                        UploadSeasonsTask seasonsTask = new UploadSeasonsTask();

                        Object[] seasonTaskParams = new Object[]{query, host.getResources().getString(R.string.getting_data),
                                host, seasonsContainer};

                        seasonsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, seasonTaskParams);

                    } catch (SQLiteException sqlite) {
                        Log.e("CostrctrAdptr/UpldSsns", sqlite.getMessage());
                    }
                }
                else
                    seasonsContainer.showDropDown();

            }
        });


        //set up the view button
        FrameLayout viewButton = holder.getViewButton();
        viewButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String lat = entry.getLangt();
                String longt = entry.getLongt();

                //http://maps.google.com/maps?z=12&t=k&q=loc:latitude+longtitude
                //k for satellite, z for zoom
                String viewUri = "http://maps.google.com/maps?z=12&t=k&q=loc:" + lat +"+" + longt;
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(viewUri));
                host.startActivity(viewIntent);
            }
        });
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
        return circuits.size();
    }


    class CircuitViewHolder extends RecyclerView.ViewHolder {
        
        private FrameLayout infoButton, viewButton;    
        private AppCompatTextView circuitNameTxt, localityTxt;
        private ImageView countryFlag;
        private RelativeLayout seasonsLayout;
        private AppCompatButton calendarLayout;
        private AppCompatAutoCompleteTextView seasonsContainer;


        public FrameLayout getInfoButton() {
            return infoButton;
        }

        public FrameLayout getViewButton() {
            return viewButton;
        }

        public AppCompatTextView getCircuitNameTxt() {
            return circuitNameTxt;
        }

        public AppCompatTextView getLocalityTxt() {
            return localityTxt;
        }

        public ImageView getCountryFlag() {
            return countryFlag;
        }

        public RelativeLayout getSeasonsLayout() {
            return seasonsLayout;
        }

        public AppCompatButton getCalendarLayout() {
            return calendarLayout;
        }

        public AppCompatAutoCompleteTextView getSeasonsContainer() {
            return seasonsContainer;
        }

        public CircuitViewHolder(View itemView) {
            super(itemView);
        
            this.infoButton = itemView.findViewById(R.id.infoButton);
            this.circuitNameTxt = itemView.findViewById(R.id.circuitNameTxt);
            this.countryFlag = itemView.findViewById(R.id.countryFlag);
            this.localityTxt = itemView.findViewById(R.id.localityTxt);
            this.seasonsLayout = itemView.findViewById(R.id.seasonsLayout);
            this.calendarLayout = itemView.findViewById(R.id.calendarLayout);
            this.seasonsContainer = itemView.findViewById(R.id.seasonsContainer);
            this.viewButton = itemView.findViewById(R.id.viewButton);   
        }
    
    }


}//CircuitAdapter





