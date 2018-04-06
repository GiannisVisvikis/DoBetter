
package comp.examplef1.iovisvikis.f1story.MyAdapters;

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
 * Created by iovisvikis on 4/4/2017.
 */

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverViewHolder> {

    private ArrayList<Driver> drivers;
    private DownloadFragment host;
    private LayoutInflater inflater;
    private ViewGroup parent;




    public DriverAdapter(DownloadFragment host, JSONArray driversArray)
    {
        this.host = host;

        this.drivers = new ArrayList<>();

        try
        {
            for(int index=0; index<driversArray.length(); index++)
            {
                JSONObject driverJSON = driversArray.getJSONObject(index);
                Driver driver = new Driver(driverJSON);
                drivers.add(driver);
            }

        }
        catch (JSONException je)
        {
            Log.e("DrvrAdptr", je.getMessage());
        }

    }

    @Override
    public DriverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        inflater = host.getActivity().getLayoutInflater();
        this.parent = parent;

        View holderView = inflater.inflate(R.layout.driver_row, parent, false);

        return new DriverViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(DriverViewHolder holder, int position) {

        final Driver driver = drivers.get(position);

        ImageView driverPhoto = holder.getDriverPhoto();
        host.uploadDriverPhoto(driverPhoto, driver.getId());

        driverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = driver.getUrl();
                Intent wikIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                host.startActivity(wikIntent);
            }
        });


        AppCompatTextView driverTxt = holder.getDriverNameTxt();
        driverTxt.setText(host.splitDown(driver.getName()));

        ImageView driverFlag = holder.getDriverFlagFrame();
        host.uploadNationalityFlag(driverFlag, driver.getNationality());

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

                        String query = MainActivity.BASIC_URI + host.getCallerFragmentTag() + "s/" + choiceId + "/drivers/" +
                                driver.getId() + "/seasons.json";

                        UploadSeasonsTask seasonsTask = new UploadSeasonsTask();

                        Object[] seasonTaskParams = new Object[]{query, host.getResources().getString(R.string.getting_data),
                                host, seasonsContainer};

                        seasonsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, seasonTaskParams);

                    } catch (SQLiteException sqlite) {
                        Log.e("DrvrAdptr/UpldSsns", sqlite.getMessage());
                    }
                }
                else
                    seasonsContainer.showDropDown();

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
        return drivers.size();
    }



    class DriverViewHolder extends RecyclerView.ViewHolder {

        private ImageView driverPhoto, driverFlagFrame;
        private AppCompatTextView driverNameTxt;
        private RelativeLayout seasonsLayout;
        private AppCompatButton calendarLayout;
        private AppCompatAutoCompleteTextView seasonsContainer;

        public ImageView getDriverPhoto() {
            return driverPhoto;
        }

        public ImageView getDriverFlagFrame() {
            return driverFlagFrame;
        }

        public AppCompatTextView getDriverNameTxt() {
            return driverNameTxt;
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

        public DriverViewHolder(View itemView) {
            super(itemView);

            this.driverPhoto = itemView.findViewById(R.id.driverPhoto);
            this.driverNameTxt = itemView.findViewById(R.id.driverNameTxt);
            this.driverFlagFrame = itemView.findViewById(R.id.driverFlagFrame);
            this.seasonsLayout = itemView.findViewById(R.id.seasonsLayout);
            this.calendarLayout = itemView.findViewById(R.id.calendarLayout);
            this.seasonsContainer = itemView.findViewById(R.id.seasonsContainer);
            
        }

    }

}//DriverAdapter




