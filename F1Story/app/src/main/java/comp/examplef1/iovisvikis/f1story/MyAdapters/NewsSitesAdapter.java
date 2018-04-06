package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.ShowNewsDialogTask;
import comp.examplef1.iovisvikis.f1story.Communication;
import comp.examplef1.iovisvikis.f1story.MainActivity;

import comp.examplef1.iovisvikis.f1story.R;

import java.io.IOException;

/**
 * Created by ioannisvisvikis on 8/8/17.
 */

public class NewsSitesAdapter extends RecyclerView.Adapter<NewsSitesAdapter.NewSiteHolder> {

    private DownloadFragment host;

    //this is an ArrayList of paths to news sites logos
    private String[] newsSiteLogoPaths;


    @Override
    public NewSiteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = host.getActivity().getLayoutInflater().inflate(R.layout.news_site_row, parent, false);
        return new NewSiteHolder(holderView);
    }

    @Override
    public void onBindViewHolder(NewSiteHolder holder, final int position) {


        ImageView logoLayout = holder.getNewSiteLogoFrame();

        try {
            logoLayout.setImageBitmap(BitmapFactory.decodeStream(host.getActivity().getAssets().open(newsSiteLogoPaths[position])));

        } catch (IOException io) {
            Log.e("NewsAdptr/getView", io.getMessage());
        }


        logoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase sitesDatabase = host.getActivity().openOrCreateDatabase(MainActivity.NEWS_TABLES_DATABASE, Context.MODE_PRIVATE, null);


                String pathToAssets = newsSiteLogoPaths[position];

                Cursor data;

                switch (pathToAssets) {

                    case "logos/motorsport.png":
                        data = sitesDatabase.rawQuery("select title, link, path_to_pic from motorsport_table;", null);
                        break;

                    case "logos/espn.png":
                        data = sitesDatabase.rawQuery("select title, link, path_to_pic from espn_table;", null);
                        break;

                    case "logos/autosport.png":
                        data = sitesDatabase.rawQuery("select title, link, path_to_pic from autosport_table;", null);
                        break;

                    default:
                        data = null;
                }

                //find out whether the service ever started
                boolean isServiceDone = host.isServiceDone();
                boolean wasServiceStarted = host.getWasServiceStarted();

                //is service still running, wait for the results and set the news adapter inside a task that waits
                //for the service to be completed

                if ( !(wasServiceStarted && isServiceDone) ){

                    //Trigger the AsyncTask that displays the dialog
                    ShowNewsDialogTask newsDialogTask = new ShowNewsDialogTask();
                    Object[] dialogTaskParams = new Object[]{pathToAssets, host, data, sitesDatabase};
                    newsDialogTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dialogTaskParams);

                }
                else{

                    //service is done by now. Fet the results and pass them inside the result fragment
                    NewsAdapter newsAdapter = new NewsAdapter(host, pathToAssets, data);
                    ((Communication) host.getActivity()).setResultFragment(newsAdapter);

                    sitesDatabase.close();
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return newsSiteLogoPaths.length;
    }


    public NewsSitesAdapter(DownloadFragment host) {

        this.host = host;

        //If you want to add a news site, go to arrays.xml and add another item. Then go to NewsService
        // and add a method that reads the RSS and stores the results into a database and start the method in a branch thread
        this.newsSiteLogoPaths = host.getActivity().getResources().getStringArray(R.array.news_sites);

    }



    class NewSiteHolder extends RecyclerView.ViewHolder {

        private ImageView newSiteLogoFrame;

        public ImageView getNewSiteLogoFrame() {
            return newSiteLogoFrame;
        }

        public NewSiteHolder(View itemView) {
            super(itemView);

            this.newSiteLogoFrame = itemView.findViewById(R.id.news_site_logo_frame);

        }

    }


}
