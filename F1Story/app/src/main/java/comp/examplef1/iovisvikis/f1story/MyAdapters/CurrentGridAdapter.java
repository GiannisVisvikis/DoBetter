package comp.examplef1.iovisvikis.f1story.MyAdapters;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import comp.examplef1.iovisvikis.f1story.APICommunicator;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;
import comp.examplef1.iovisvikis.f1story.MainActivity;
import comp.examplef1.iovisvikis.f1story.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by iovisvikis on 17/5/2017.
 */

public class CurrentGridAdapter extends RecyclerView.Adapter<CurrentGridAdapter.GridHolder>{


    private final ArrayList<CurrentGridRows> rows;
    private DownloadFragment host;


    public CurrentGridAdapter(DownloadFragment host, ArrayList<CurrentGridRows> rows){

        this.host = host;
        this.rows = rows;
    }


   /*     //OLD CONSTRUCTOR
      public CurrentGridAdapter(DownloadFragment host, JSONArray resultsArray){

        this.rows = new ArrayList<>();
        this.host = host;

        try {

            for (int index = 0; index < resultsArray.length(); index++) {
                JSONObject result  = resultsArray.getJSONObject(index);

                Constructor constructor = new Constructor(result);
                String constructorId = constructor.getId();

                //ten constructors = 10 calls to the api better than twenty drivers = 20 calls performance-wise
                String query = MainActivity.BASIC_URI + "current/constructors/" + constructorId + "/drivers.json";

                DownloadConstructor constructorTask = new DownloadConstructor();
                ArrayList<Driver> drivers = constructorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                                                                            new String[]{query}).get();


                for(Driver driver : drivers) {

                    CurrentGridRows gridRow = new CurrentGridRows(driver, constructor);
                    rows.add(gridRow);
                }

            }

        }
        catch (JSONException e){
            Log.e("CrrntGrdAdptr", e.getMessage());
        }
        catch (InterruptedException inter)
        {
            Log.e("CrrntGrdAdptr", inter.getMessage());
        }
        catch (ExecutionException ee)
        {
            Log.e("CrrntGrdAdptr", ee.getMessage());
        }
    }

*/

    @Override
    public GridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GridHolder(host.getActivity().getLayoutInflater().inflate(R.layout.current_grid, parent, false));
    }



    @Override
    public void onBindViewHolder(GridHolder holder, int position) {


        final CurrentGridRows gridRow = rows.get(position);


        ImageView driverPhoto = holder.getDriverPhoto();
        host.uploadDriverPhoto(driverPhoto, gridRow.getDriverId());

        driverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url =gridRow.getDriverURL();

                if(!url.equalsIgnoreCase(""))
                {
                    Intent wikIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    host.startActivity(wikIntent);
                }
            }
        });


        AppCompatTextView driverNameTxt = holder.getDriverNameTxt();
        String driverName = gridRow.getDriverName();
        driverNameTxt.setText(host.splitDown(driverName));


        ImageView driverFlag = holder.getDriverFlagFrame();
        host.uploadNationalityFlag(driverFlag, gridRow.getDriverNationality());


        ImageView constructorPhoto = holder.getConstructorPhoto();
        host.uploadConstructorPhoto(constructorPhoto, gridRow.getConstructorId());

        constructorPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = gridRow.getConstructorURL();

                if(!url.equalsIgnoreCase(""))
                {
                    Intent wikIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    host.startActivity(wikIntent);
                }
            }
        });


        AppCompatTextView constructorNameTxt = holder.getConstructorNameTxt();
        String constructorName = gridRow.getConstructorName();
        constructorNameTxt.setText(host.splitDown(constructorName));


        ImageView constructorFlag = holder.getConstructorFlagFrame();
        host.uploadNationalityFlag(constructorFlag, gridRow.getConstructorNationality());

    }

    @Override
    public int getItemCount() {
        return rows.size();
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }





    private class DownloadConstructor extends AsyncTask<String, Void, ArrayList<Driver>>{

        APICommunicator api = APICommunicator.getInstance();

        ArrayList<Driver> result;

        @Override
        protected void onPreExecute() {

            result = new ArrayList<>();

        }



        @Override
        protected ArrayList<Driver> doInBackground(String... strings) {

            String query = strings[0];

            String allInfo = api.getFinalRequestString(query)[1];

            String jsonInfo = api.getInfo(allInfo);

            try
            {
                JSONObject root = new JSONObject(jsonInfo);

                JSONArray drivers = api.getData(root, "Drivers");

               for(int index = 0; index<drivers.length(); index++)
               {
                   result.add(new Driver(drivers.getJSONObject(index)));
               }

            }
            catch (JSONException je)
            {
                Log.e("CrrntGrdAdptr/CnstcrTsk", je.getMessage());
            }

            return result;
        }


    }



    class GridHolder extends RecyclerView.ViewHolder {

        private ImageView driverPhoto, constructorPhoto, driverFlagFrame, constructorFlagFrame;
        private AppCompatTextView driverNameTxt, constructorNameTxt;

        public ImageView getDriverPhoto() {
            return driverPhoto;
        }

        public ImageView getConstructorPhoto() {
            return constructorPhoto;
        }

        public ImageView getDriverFlagFrame() {
            return driverFlagFrame;
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

        public GridHolder(View itemView) {
            super(itemView);

            this.driverPhoto = itemView.findViewById(R.id.driverPhoto);
            this.driverNameTxt = itemView.findViewById(R.id.driverNameTxt);
            this.driverFlagFrame = itemView.findViewById(R.id.driverFlagFrame);
            this.constructorPhoto = itemView.findViewById(R.id.constructorPhoto);
            this.constructorNameTxt = itemView.findViewById(R.id.constructorNameTxt);
            this.constructorFlagFrame = itemView.findViewById(R.id.constructorFlagFrame);

        }

    }


}//CurrentGridAdapter


