package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import comp.examplef1.iovisvikis.f1story.Communication;
import comp.examplef1.iovisvikis.f1story.MyAdapters.NewsAdapter;
import comp.examplef1.iovisvikis.f1story.MyDialogs.UploadNewsDialog;

/**
 * Created by ioannis on 09/12/2017.
 */

/**
 * This class presents the user a dialog until the news service terminates if for some reason is not yet done it's work.
 */

public class ShowNewsDialogTask extends AsyncTask<Object, DownloadFragment, Object[]> {

    private UploadNewsDialog newsDialog;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        newsDialog = new UploadNewsDialog();

    }

    @Override
    protected Object[] doInBackground(Object... strings) {

        String assetPath = (String) strings[0];
        DownloadFragment host = (DownloadFragment) strings[1];

        Cursor data = (Cursor) strings[2];
        SQLiteDatabase sitesDatabase = (SQLiteDatabase) strings[3];

        Bundle args = new Bundle();
        args.putString("LOGO_ASSET_PATH", assetPath);
        newsDialog.setArguments(args);

        publishProgress(host);

        while (!host.isServiceDone()) {

            //wait for it to finish
        }

        return new Object[]{host,assetPath, data, sitesDatabase};
    }


    @Override
    protected void onProgressUpdate(DownloadFragment... values) {
        super.onProgressUpdate(values);

        DownloadFragment host = values[0];

        newsDialog.show(host.getActivity().getSupportFragmentManager(), "NEWS_DIALOG");

    }


    @Override
    protected void onPostExecute(Object[] results) {
        super.onPostExecute(results);

        DownloadFragment host = (DownloadFragment) results[0];
        String pathToAssets = (String) results[1];
        Cursor data = (Cursor) results[2];
        SQLiteDatabase sitesDatabase = (SQLiteDatabase) results[3];

        //service is done by now. Fet the results and pass them inside the result fragment
        NewsAdapter newsAdapter = new NewsAdapter(host, pathToAssets, data);
        ((Communication) host.getActivity()).setResultFragment(newsAdapter);

        sitesDatabase.close();

        newsDialog.dismiss();
        newsDialog = null;

    }

}