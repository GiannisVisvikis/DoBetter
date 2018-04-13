package comp.examplef1.iovisvikis.f1story;

import android.app.Service;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;

import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import comp.examplef1.iovisvikis.f1story.MyAdapters.CircuitEntry;
import comp.examplef1.iovisvikis.f1story.MyAdapters.Constructor;

public class UpdateDatabaseEntriesService extends Service
{


    public static final String DRIVERS_UPDATE_TAG = "Drivers";
    public static final String CONSTRUCTORS_UPDATE_TAG = "Constructors";
    public static final String CIRCUITS_UPDATE_TAG = "Circuits";
    public static final String SEASONS_UPDATE_TAG = "Seasons";

    private final APICommunicator apiCom = new APICommunicator();
    private SQLiteDatabase f1Database;

    @Override
    public void onCreate()
    {
        super.onCreate();

        f1Database = getApplicationContext().openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        /*
            The service is initiated by an async task loader that returns a String[] containing the info

            if intent.getStringExtra(tag) != null, then a string is there that contains info for a JSONObject.
            Start updating the database
         */

        final String driversTag = intent.getStringExtra(DRIVERS_UPDATE_TAG);
        final String constructorsTag = intent.getStringExtra(CONSTRUCTORS_UPDATE_TAG);
        final String circuitsTag = intent.getStringExtra(CIRCUITS_UPDATE_TAG);
        final String seasonsTag = intent.getStringExtra(SEASONS_UPDATE_TAG);


        Thread updateDatabaseThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase f1Database = getApplicationContext().openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);

                if(driversTag != null)
                {
                    ArrayList<JSONObject> newDrivers = getNewEntries(driversTag, DRIVERS_UPDATE_TAG);

                    StringBuilder insertQueryBuilder = new StringBuilder("INSERT INTO ALL_" + DRIVERS_UPDATE_TAG + " VALUES " );

                    for(JSONObject jsonObject : newDrivers)
                    {
                        ServiceDriver newDriver = new ServiceDriver(jsonObject);

                        insertQueryBuilder.append("('" + newDriver.getId() + "', '" + newDriver.getName() + " " + newDriver.getSurname() + "', '" + newDriver.getUrl() + "'),");

                        //Log.e("Adding Driver", newDriver.getName() + " " + newDriver.getSurname());
                    }

                    String insertQuery = insertQueryBuilder.substring(0, insertQueryBuilder.length() - 1);

                    f1Database.execSQL(insertQuery);
                }

                if(constructorsTag != null)
                {
                    ArrayList<JSONObject> newConstructors = getNewEntries(constructorsTag, CONSTRUCTORS_UPDATE_TAG);

                    StringBuilder insertConsQueryBuilder = new StringBuilder("INSERT INTO ALL_" + CONSTRUCTORS_UPDATE_TAG + " VALUES " );

                    for(JSONObject jsonObject : newConstructors)
                    {
                        Constructor newConstructor = new Constructor(jsonObject);

                        insertConsQueryBuilder.append("('" + newConstructor.getId() + "', '" + newConstructor.getName() + "', '" + newConstructor.getUrl() + "'),");

                        //Log.e("Adding Constructor", newConstructor.getName());
                    }

                    String insertQuery = insertConsQueryBuilder.substring(0, insertConsQueryBuilder.length() - 1);

                    f1Database.execSQL(insertQuery);
                }

                if(circuitsTag != null)
                {
                    ArrayList<JSONObject> newCircuits = getNewEntries(circuitsTag, CIRCUITS_UPDATE_TAG);

                    StringBuilder insertCircsQueryBuilder = new StringBuilder("INSERT INTO ALL_" + CIRCUITS_UPDATE_TAG + " VALUES " );

                    for(JSONObject jsonObject : newCircuits)
                    {
                        CircuitEntry newCircuit = new CircuitEntry(jsonObject);

                        insertCircsQueryBuilder.append("('" + newCircuit.getId() + "', '" + newCircuit.getName() + "', '" + newCircuit.getUrl() + "'),");

                        //Log.e("Adding Circuit", newCircuit.getName());
                    }

                    String insertQuery = insertCircsQueryBuilder.substring(0, insertCircsQueryBuilder.length() - 1);

                    f1Database.execSQL(insertQuery);
                }


                if(seasonsTag != null)
                {
                    try
                    {
                        int seasonCount = (int) DatabaseUtils.longForQuery(f1Database, "select count(*) from all_seasons", null);

                        //start index count = 1
                        JSONArray seasonsArray = apiCom.getData(new JSONObject(seasonsTag), SEASONS_UPDATE_TAG);

                        StringBuilder insertSeasonsQueryBuilder = new StringBuilder("INSERT INTO ALL_SEASONS VALUES ");

                        for(int index = seasonCount; index<seasonsArray.length(); index++)
                        {
                            JSONObject newSeasonEntryObject = seasonsArray.getJSONObject(index);
                            String newSeason = newSeasonEntryObject.getString("season");
                            String url = newSeasonEntryObject.getString("url");

                            insertSeasonsQueryBuilder.append("('" + newSeason + "', '" + url + "'),");

                            Log.e("Adding season", newSeason);
                        }

                        String insertSeasonQuery = insertSeasonsQueryBuilder.substring(0, insertSeasonsQueryBuilder.length() - 1);

                        f1Database.execSQL(insertSeasonQuery);
                    }
                    catch (JSONException je)
                    {
                        Log.e("UpdsEntrSrvcSeasons", je.getMessage());
                    }
                    catch (SQLiteDoneException sq)
                    {
                        Log.e("UpdsEntrSrvcSeasons", sq.getMessage());
                    }
                }

                stopSelf();
            }
        });

        updateDatabaseThread.start();

        return START_REDELIVER_INTENT;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }



    private ArrayList<JSONObject> getNewEntries(String rootJSONString, String tag)
    {
        ArrayList<JSONObject> newEntries = new ArrayList<>();

        try
        {
            JSONArray allData = apiCom.getData(new JSONObject(rootJSONString), tag);

            for(int index=0; index<allData.length(); index++)
            {
                JSONObject newEntryObject = null;
                String id = null;

                try
                {
                    newEntryObject = allData.getJSONObject(index);

                    switch (tag)
                    {
                        case DRIVERS_UPDATE_TAG:
                            ServiceDriver newDriver = new ServiceDriver(newEntryObject);
                            id = newDriver.getId();
                            break;

                        case CONSTRUCTORS_UPDATE_TAG:
                            Constructor newConstructor = new Constructor(newEntryObject);
                            id = newConstructor.getId();
                            break;

                        case CIRCUITS_UPDATE_TAG:
                            CircuitEntry newCircuit = new CircuitEntry(newEntryObject);
                            id = newCircuit.getId();
                            break;

                    }

                    DatabaseUtils.stringForQuery(f1Database, "select " + tag.substring(0, tag.length() - 1) + "_NAME from all_" + tag +
                            " where " + tag.substring(0, tag.length() - 1) + "_ID = '" + id + "'", null);

                }
                catch (SQLiteDoneException sql)
                {
                    //Log.e("NewEntrSrvcSQLiteDone", sql.getMessage());
                    Log.e("NewEntrSrvcSQLiteDone", "Found new entry for " + tag + " : " + id);
                    newEntries.add(newEntryObject);
                }
            }

        }
        catch (JSONException je)
        {
            Log.e("UpdDtbEntrSrvcGtNwEntrs", je.getMessage());
        }

        return newEntries;
    }




    private class ServiceDriver
    {
        private String name, surname, url, nationality, id;


        public String getName() {
            return name;
        }

        public String getSurname() { return this.surname; }

        public String getUrl() {
            return url;
        }

        public String getNationality() {
            return nationality;
        }

        public String getId() {return id;}


        public ServiceDriver(JSONObject driver){

            try{
                this.name = driver.getString("givenName");
                this.surname = driver.getString("familyName");
                this.url = driver.getString("url");
                this.nationality = driver.getString("nationality");
                this.id = driver.getString("driverId");

            }
            catch (JSONException e){
                Log.e("DRIVER", e.getMessage());
            }

        }

    }


}
