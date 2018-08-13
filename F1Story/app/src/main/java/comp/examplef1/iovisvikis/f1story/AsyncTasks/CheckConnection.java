package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.content.Context;
import android.net.ConnectivityManager;

import android.support.v4.content.AsyncTaskLoader;




/**
 * Created by iovisvikis on 21/2/2017.
 */

public class CheckConnection extends AsyncTaskLoader<Boolean> {


    public CheckConnection(Context context) {
        super(context);
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();
    }


    @Override
    public Boolean loadInBackground() {
        Context context = getContext();

        ConnectivityManager connectivityManager =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean aBoolean = (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting());

        return aBoolean;
    }


}


