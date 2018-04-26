package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import comp.examplef1.iovisvikis.f1story.APICommunicator;
import comp.examplef1.iovisvikis.f1story.Communication;
import comp.examplef1.iovisvikis.f1story.MainActivity;


/**
 * Checks for new entries in the api that must be pushed in the main app database (F1_STORY.db)
 * If any found, they will be placed inside the JSONArray accordingly. Null will be inserted if no (driver/constructor/circuit/seasons) update found
 *
 */
public class CheckUpdatesLoader extends AsyncTaskLoader<String[]>
{

    private String[] cached = null;

    private final String driversCheckAddress = MainActivity.BASIC_URI + "drivers.json?limit=10000&offset=0";
    private final String constructorsCheckAddress = MainActivity.BASIC_URI + "constructors.json?limit=10000&offset=0";
    private final String circuitsCheckAddress = MainActivity.BASIC_URI + "circuits.json?limit=10000&offset=0";
    private final String seasonsCheckAddress = MainActivity.BASIC_URI + "seasons.json?limit=10000&offset=0";


    private Communication act;


    public CheckUpdatesLoader(@NonNull Context context, MainActivity activity)
    {
        super(context);

        this.act = activity;
    }


    @Override
    protected void onStartLoading()
    {
        Log.e("CheckUpdatesLoader", "Checking updates Loader activated");
        if(cached != null)
        {
            Log.e("CheckUpdatesLoader", "Checking updates Loader returning cached results");
            deliverResult(cached);
        }
        else
        {
            Log.e("CheckUpdatesLoader", "Checking updates Loader loading");
            forceLoad();
        }
    }



    @Nullable
    @Override
    public String[] loadInBackground()
    {

        String[] result = new String[]{null, null, null, null};

        APICommunicator apiCom = new APICommunicator();

       if(act.apiResponds())
       {

           String driversResponse = apiCom.getTotalEntries(driversCheckAddress);
           long apiDrivers = -10;
           if(driversResponse != null)
               apiDrivers = Integer.parseInt(driversResponse);

           long apiConstructors = -10;
           String constructorsResponse = apiCom.getTotalEntries(constructorsCheckAddress);
           if(constructorsResponse != null)
               apiConstructors = Integer.parseInt(constructorsResponse);

           String circuitsResponse = apiCom.getTotalEntries(circuitsCheckAddress);
           long apiCircuits = -10;
           if(circuitsResponse != null)
               apiCircuits = Integer.parseInt(apiCom.getTotalEntries(circuitsCheckAddress));

           String seasonsResponse = apiCom.getTotalEntries(seasonsCheckAddress);
           long apiSeasons = -10;
           if(seasonsResponse != null)
               apiSeasons = Integer.parseInt(seasonsResponse);

           SQLiteDatabase f1Database = getContext().openOrCreateDatabase(MainActivity.DATABASE_NAME, Context.MODE_PRIVATE, null);

           long databaseDrivers = DatabaseUtils.longForQuery(f1Database, "select count(*) from all_drivers", null);
           long databaseConstructors = DatabaseUtils.longForQuery(f1Database, "select count(*) from all_constructors", null);
           long databaseCircuits = DatabaseUtils.longForQuery(f1Database, "select count(*) from all_circuits", null);
           long databaseSeasons = DatabaseUtils.longForQuery(f1Database, "select count(*) from all_seasons", null);

           if (apiDrivers > 0 && apiDrivers > databaseDrivers)
               result[0] = apiCom.getInfo(MainActivity.BASIC_URI + "drivers.json?limit=" + apiDrivers + "&" + "offset=0");

           if( apiConstructors > 0 && apiConstructors > databaseConstructors)
               result[1] = apiCom.getInfo(MainActivity.BASIC_URI + "constructors.json?limit=" + apiConstructors + "&" + "offset=0");

           if(apiCircuits > 0 && apiCircuits > databaseCircuits)
               result[2] = apiCom.getInfo(MainActivity.BASIC_URI + "circuits.json?limit=" + apiCircuits + "&" + "offset=0");

           if( apiSeasons > 0 && apiSeasons > databaseSeasons)
               result[3] = apiCom.getInfo(MainActivity.BASIC_URI + "seasons.json?limit=" + apiSeasons + "&" + "offset=0");

           Log.e("CheckUpdatesLoader", "Checking updates Loaded in background");
       }

        return result;
    }



    @Override
    public void deliverResult(@Nullable String[] data)
    {
        if(cached == null)
        {
            cached = data;
        }

        super.deliverResult(data);
    }



}
