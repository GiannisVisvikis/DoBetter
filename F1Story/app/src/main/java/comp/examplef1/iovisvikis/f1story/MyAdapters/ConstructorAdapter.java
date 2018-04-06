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
 * Created by iovisvikis on 2/4/2017.
 */

public class ConstructorAdapter extends RecyclerView.Adapter<ConstructorAdapter.ConstructorViewHolder>{

    private ArrayList<Constructor> constructors;
    private DownloadFragment host;



    public ConstructorAdapter(DownloadFragment host, JSONArray resultsArray){

        this.constructors = new ArrayList<>();
        this.host = host;

        try
        {
            for(int index=0; index < resultsArray.length(); index++)
            {
                JSONObject constructorJSON = resultsArray.getJSONObject(index);

                Constructor constructor = new Constructor(constructorJSON);

                constructors.add(constructor);
            }
        }
        catch (JSONException je)
        {
            Log.e("CnstrtctrAdptrCnstrPht", je.getMessage());
        }

    }

    @Override
    public ConstructorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConstructorViewHolder(host.getActivity().getLayoutInflater().inflate(R.layout.constructor_row, parent, false));
    }


    @Override
    public void onBindViewHolder(ConstructorViewHolder holder, int position) {

        final Constructor constructor = constructors.get(position);

        ImageView constructorPhoto = holder.getConstructorPhoto();
        host.uploadConstructorPhoto(constructorPhoto, constructor.getId());

        constructorPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent conIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constructor.getUrl()));
                host.startActivity(conIntent);
            }
        });


        AppCompatTextView conNameTxt = holder.getConstructorTxt();
        conNameTxt.setText(host.splitDown(constructor.getName()));

        ImageView conFlag = holder.getConstructorFlagFrame();
        host.uploadNationalityFlag(conFlag, constructor.getNationality());

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

                        String query = MainActivity.BASIC_URI + host.getCallerFragmentTag() + "s/" + choiceId + "/constructors/" +
                                constructor.getId() + "/seasons.json";

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
        return constructors.size();
    }


    class ConstructorViewHolder extends RecyclerView.ViewHolder {

        private ImageView constructorPhoto, constructorFlagFrame;
        private AppCompatTextView constructorTxt;
        private RelativeLayout seasonsLayout;
        private AppCompatButton calendarLayout;
        private AppCompatAutoCompleteTextView seasonsContainer;

        public ImageView getConstructorPhoto() {
            return constructorPhoto;
        }

        public ImageView getConstructorFlagFrame() {
            return constructorFlagFrame;
        }

        public AppCompatTextView getConstructorTxt() {
            return constructorTxt;
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

        public ConstructorViewHolder(View itemView) {
            super(itemView);

            this.constructorPhoto = itemView.findViewById(R.id.constructorPhoto);
            this.constructorTxt = itemView.findViewById(R.id.constructorTxt);
            this.constructorFlagFrame = itemView.findViewById(R.id.constructorFlagFrame);
            this.seasonsLayout = itemView.findViewById(R.id.seasonsLayout); 
            this.calendarLayout = itemView.findViewById(R.id.calendarLayout);
            this.seasonsContainer = itemView.findViewById(R.id.seasonsContainer);
        }


    }


}//ConstructorAdapter




