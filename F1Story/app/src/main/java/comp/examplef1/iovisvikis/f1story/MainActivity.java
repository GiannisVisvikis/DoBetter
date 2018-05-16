package comp.examplef1.iovisvikis.f1story;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.ApiAnswers;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.CalendarAsyncTaskLoader;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.CheckConnection;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.CheckUpdatesLoader;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.CopyMainDatabaseLoader;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.CurrentGridTaskLoader;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;
import comp.examplef1.iovisvikis.f1story.MyAdapters.CalendarRace;
import comp.examplef1.iovisvikis.f1story.MyAdapters.CalendarRaceAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.CurrentGridAdapter;
import comp.examplef1.iovisvikis.f1story.MyAdapters.CurrentGridRows;
import comp.examplef1.iovisvikis.f1story.MyAdapters.NewsSitesAdapter;
import comp.examplef1.iovisvikis.f1story.MyDialogs.MultipleSelectionDialog;
import comp.examplef1.iovisvikis.f1story.MyDialogs.NotificationsDialog;
import comp.examplef1.iovisvikis.f1story.MyDialogs.SingleSelectionDialog;
import comp.examplef1.iovisvikis.f1story.quiz.QuizActivity;


public class MainActivity extends AppCompatActivity implements Communication, ProviderInstaller.ProviderInstallListener
{

    public static final String BASIC_URI = "https://ergast.com/api/f1/";

    private final String RESULT_FRAGMENT_TAG = "RESULT_FRAGMENT_TAG";
    private final String DOWNLOAD_FRAGMENT_TAG = "DOWNLOAD_FRAGMENT_TAG";
    private final String SOUND_FRAGMENT_TAG = "SOUND_FRAGMENT_TAG";

    private final String SOUND_PREFERENCE_KEY = "SOUND_PREFERENCE";
    public final String NOTIFICATIONS_PREFERENCE_KEY = "NOTIFICATIONS_PREFERENCE";
    private  final String DATABASE_SERVICE_DONE = "DATA_SEVICE_DONE";
    public static final String SHARED_PREFERENCES_TAG = "com_example_visvikis_f1storypreferences";

    private final String PATCH_APPLIED_TAG = "PATCH_APPLIED";
    private final String SEARCHED_FOR_UPDATES_TAG = "SEARCHED_FOR_UPDATES";
    private final String PLAY_STARTUP_SOUND = "PLAY_STARTUP";
    private final String WAS_DRAWER_OPEN = "WAS DRAWER OPEN";

    public static final String NEWS_TABLES_DATABASE = "NEWS_SITES.db";
    public static final String DATABASE_NAME = "F1_STORY.db";

    //Code and boolean for the patch if needed
    private static final int ERROR_DIALOG_REQUEST_CODE = 0;
    private boolean patchApplied = false;
    private boolean mRetryProviderInstall;

    //Codes for the async task loaders to use
    private final int COPY_MAIN_DATABASE_CODE = 1;
    private final int CALENDAR_LOADER_CODE = 2;
    private final int CURRENT_GRID_LOADER_CODE = 3;
    private final int CHECK_UPDATES_LOADER_CODE = 4;

    private ResultFragment resultFragment;
    private SoundFragment soundFragment;
    private DownloadFragment downloadFragment;

    private Menu activityMenu;

    private boolean soundsOn, notificationsOn;
    private boolean searchedForUpdates = false;
    private boolean databaseServiceDone = false;
    private boolean playStartupSound = true;
    private boolean wasDrawerOpen = true;


    private View root;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private LinearLayout resultFragmentDrawer;

    private SQLiteDatabase f1Database;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) //need this now (before checking other saved variables)
        {
            patchApplied = savedInstanceState.getBoolean(PATCH_APPLIED_TAG);
        }

        if(!hasInternetConnection())
        {
            Intent noConnectionIntent = new Intent(this, NoConnectionActivity.class);
            startActivity(noConnectionIntent);
            this.finish();
        }

        if(!apiResponds())
        {
            Intent noResponseIntent = new Intent(this, NoResponseActivity.class);
            startActivity(noResponseIntent);
            this.finish();
        }

        root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        this.soundsOn = getFromPreferences(SOUND_PREFERENCE_KEY, true);
        this.notificationsOn = getFromPreferences(NOTIFICATIONS_PREFERENCE_KEY, false);


        //get the fragments in place
        FragmentManager fragmentManager = getSupportFragmentManager();

        //check for prior state
        if(savedInstanceState == null)
        {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) //previous versions will add this after the patch because it initiates a service that needs it
            {
                downloadFragment = new DownloadFragment();
                fragmentTransaction.add(downloadFragment, DOWNLOAD_FRAGMENT_TAG);
            }

            soundFragment = new SoundFragment();
            resultFragment = new ResultFragment();
            //add the rest of the fragments. Add the no internet and not responding activities


            fragmentTransaction.add(R.id.result_fragment_place, resultFragment, RESULT_FRAGMENT_TAG);

            fragmentTransaction.add(soundFragment, SOUND_FRAGMENT_TAG);

            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
        }
        else
        {
            patchApplied = savedInstanceState.getBoolean(PATCH_APPLIED_TAG); //already done that, but hey!

            downloadFragment = (DownloadFragment) fragmentManager.findFragmentByTag(DOWNLOAD_FRAGMENT_TAG);
            soundFragment = (SoundFragment) fragmentManager.findFragmentByTag(SOUND_FRAGMENT_TAG);
            resultFragment = (ResultFragment) fragmentManager.findFragmentByTag(RESULT_FRAGMENT_TAG);

            databaseServiceDone = savedInstanceState.getBoolean(DATABASE_SERVICE_DONE);
            searchedForUpdates = savedInstanceState.getBoolean(SEARCHED_FOR_UPDATES_TAG);
            playStartupSound = savedInstanceState.getBoolean(PLAY_STARTUP_SOUND);
            wasDrawerOpen = savedInstanceState.getBoolean(WAS_DRAWER_OPEN);
        }


        //check if the database is copied or not and return it. If pre lollipop, then do it after the security patch is installed
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP || patchApplied)
            setTheAppDatabase();


        resultFragmentDrawer = findViewById(R.id.result_drawer);

        //get reference to the toolbar
        Toolbar toolbar = findViewById(R.id.the_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);


        //get reference to the navigation view and handle click events
        mNavigationView = findViewById(R.id.nav_view);
        setTheNavigationView();

        //get reference to the drawer layout and set listener
        mDrawerLayout = findViewById(R.id.drawer_layout);
        setTheDrawer(toolbar);


        if(wasDrawerOpen)
            mDrawerLayout.openDrawer(mNavigationView);

        //TODO uncomment the MobileAds line here and add the app and the banner-interstitial ids. Check the app version code and you're good to go
        //MobileAds.initialize(this,getResources().getString(R.string.addMob_app_id));
        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);


    }//onCreate



    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState)
    {
        super.onPostCreate(savedInstanceState, persistentState);

        mDrawerToggle.syncState();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(SEARCHED_FOR_UPDATES_TAG, searchedForUpdates);
        outState.putBoolean(PLAY_STARTUP_SOUND, playStartupSound);
        outState.putBoolean(DATABASE_SERVICE_DONE, databaseServiceDone);
        outState.putBoolean(PATCH_APPLIED_TAG, patchApplied);

        wasDrawerOpen = mDrawerLayout.isDrawerOpen(mNavigationView);
        outState.putBoolean(WAS_DRAWER_OPEN, wasDrawerOpen);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        activityMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_options_menu, menu);

        //restore past selections recovered from shared preferences in onCreate method to menu icons
        MenuItem soundItem = activityMenu.findItem(R.id.soundsMenu);
        MenuItem notificationsItem = activityMenu.findItem(R.id.notificationsMenu);

        if(soundsOn)
            soundItem.setIcon(R.mipmap.ic_volume_up_black_24dp);
        else
            soundItem.setIcon(R.mipmap.ic_volume_off_black_24dp);

        if(notificationsOn)
            notificationsItem.setIcon(R.mipmap.ic_notifications_black_24dp);
        else
            notificationsItem.setIcon(R.mipmap.ic_notifications_off_black_24dp);

        onPrepareOptionsMenu(activityMenu);

        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){

            case R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.soundsOn:
                setSoundsOn(true);
                return true;

            case R.id.soundsOff:
                getSoundFragment().stopSound();
                setSoundsOn(false);
                return  true;

            case R.id.notificationsOn:
                //create the notifications table if it not yet exists
                try{
                    String notifTableCreate = "create table if not exists notifications_table (notif_id integer not null, event text not null, time_to_event text not null);";
                    f1Database.execSQL(notifTableCreate);
                }
                catch (SQLiteException sql){
                    Log.e("NotifDialog/212", sql.getLocalizedMessage());
                }

                NotificationsDialog notifydialog = new NotificationsDialog();
                notifydialog.show(getSupportFragmentManager(), "NOTIFICATION_DIALOG");
                return true;

            case R.id.notificationsOff:
                cancelAllNotifications();
                return true;


            case R.id.blown_exhausts:
                getSoundFragment().playSound("sounds/blown_exhausts.mp3");
                return true;

            case R.id.downshifting:
                getSoundFragment().playSound("sounds/downshifting.mp3");
                return true;

            case R.id.multiple_pass:
                getSoundFragment().playSound("sounds/multiple_pass.mp3");
                return true;

            case R.id.v8_sound:
                getSoundFragment().playSound("sounds/bmw_v8.mp3");
                return true;

            case R.id.v12_sound:
                getSoundFragment().playSound("sounds/ferrari_v12.mp3");
                return true;

            case R.id.matraFord1969:
                getSoundFragment().playSound("sounds/ford_matra_1969.mp3");
                return true;

            default:
                return false;
        }

    }


    @Override
    protected void onStart()
    {
        super.onStart();

        if(isSoundsOn() && playStartupSound)
        {
            getSoundFragment().playSound("sounds/app_start.mp3");
            setPlayStartupSound(false);
        }
    }


    @Override
    public void onBackPressed()
    {
        if(isResultDrawer() && mDrawerLayout.isDrawerOpen(resultFragmentDrawer))
            mDrawerLayout.closeDrawer(resultFragmentDrawer);
        else if(!mDrawerLayout.isDrawerOpen(mNavigationView))
            mDrawerLayout.openDrawer(mNavigationView);
        else
        {
            if(isSoundsOn()){

                getSoundFragment().playSound("sounds/app_closed.mp3");

                while (getSoundFragment().isExitSoundPlaying()){
                    //wait for the exit sound to stop before exiting the app
                }
            }

            super.onBackPressed();
        }
    }




    private void setTheNavigationView()
    {
        //if not called all navigation drawer icons will appear gray (because fuck Google guidelines, if you wanted to be told what to use by an iPhone)
        mNavigationView.setItemIconTintList(null);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                item.setChecked(true);

                switch (item.getItemId())
                {
                    case R.id.navigation_quiz:
                        Intent quizIntent = new Intent(getApplicationContext(), QuizActivity.class);
                        startActivity(quizIntent);
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.navigation_drivers:
                        DriversFragment driversFrag = new DriversFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_place, driversFrag, "DRIVERS_FRAGMENT").commit();
                        getSupportFragmentManager().executePendingTransactions();
                        getSoundFragment().playRandomSound();
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.navigation_constructors:
                        ConstructorsFragment constructorsFrag = new ConstructorsFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_place, constructorsFrag, "CONSTRUCTORS_FRAGMENT").commit();
                        getSupportFragmentManager().executePendingTransactions();
                        getSoundFragment().playRandomSound();
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.navigation_circuits:
                        CircuitFragment circuitFrag = new CircuitFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_place, circuitFrag, "CIRCUIT_FRAGMENT").commit();
                        getSupportFragmentManager().executePendingTransactions();
                        getSoundFragment().playRandomSound();
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.navigation_race_results:
                        Bundle args = new Bundle();
                        args.putString("NAME", "");
                        args.putString("FRAGMENT_KIND", "ui");
                        args.putString("PURPOSE", "Results");
                        args.putBoolean("ALL_OPTIONS", false);
                        launchMultipleSelectionDialog(args);
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.navigation_calendar:
                        getSupportLoaderManager().restartLoader(CALENDAR_LOADER_CODE, null, new LoaderManager.LoaderCallbacks<ArrayList<CalendarRace>>()
                        {
                            @NonNull
                            @Override
                            public Loader<ArrayList<CalendarRace>> onCreateLoader(int id, @Nullable Bundle args)
                            {
                                return new CalendarAsyncTaskLoader(getApplicationContext());
                            }

                            @Override
                            public void onLoadFinished(@NonNull Loader<ArrayList<CalendarRace>> loader, ArrayList<CalendarRace> data)
                            {
                                if (data.size() != 0)
                                {
                                    CalendarRaceAdapter adapter = new CalendarRaceAdapter(getDownloadFragment(), data);
                                    setResultFragment(adapter);
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "Could not get Calendar", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onLoaderReset(@NonNull Loader<ArrayList<CalendarRace>> loader)
                            {

                            }
                        });
                        mDrawerLayout.closeDrawer(mNavigationView);
                        return true;

                    case R.id.navigation_season_grid:
                        getSupportLoaderManager().restartLoader(CURRENT_GRID_LOADER_CODE, null, new LoaderManager.LoaderCallbacks<ArrayList<CurrentGridRows>>()
                        {
                            @NonNull
                            @Override
                            public Loader<ArrayList<CurrentGridRows>> onCreateLoader(int id, @Nullable Bundle args)
                            {
                                return new CurrentGridTaskLoader(getApplicationContext());
                            }

                            @Override
                            public void onLoadFinished(@NonNull Loader<ArrayList<CurrentGridRows>> loader, ArrayList<CurrentGridRows> data)
                            {
                                if(data.size() > 0){

                                    DownloadFragment host = getDownloadFragment();
                                    CurrentGridAdapter gridAdapter = new CurrentGridAdapter(host, data);
                                    setResultFragment(gridAdapter);
                                }
                            }

                            @Override
                            public void onLoaderReset(@NonNull Loader<ArrayList<CurrentGridRows>> loader)
                            {

                            }
                        });

                        mDrawerLayout.closeDrawer(mNavigationView);
                        return true;

                    case R.id.navigation_latest_news:
                        NewsSitesAdapter newsSitesAdapter = new NewsSitesAdapter(getDownloadFragment());
                        setResultFragment(newsSitesAdapter);
                        mDrawerLayout.closeDrawer(mNavigationView);
                        return true;

                    default:
                        return false;
                }

            }
        });

    }



    private void setTheDrawer(Toolbar toolbar)
    {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_closed)
        {
            public void onDrawerClosed(View view)
            {
                //Toast.makeText(getApplicationContext(), "Drawer is closed", Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                mDrawerLayout.closeDrawer(resultFragmentDrawer);

                //Toast.makeText(getApplicationContext(), "Drawer is open", Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            }
        };
    }




    @Override
    public boolean isResultDrawer()
    {
        return root.getTag() != null;
    }


    @Override
    public boolean hasInternetConnection()
    {
        CheckConnection checkConnectionTask = new CheckConnection();
        Activity[] checkParams = new Activity[]{this};
        boolean result = false;

        try
        {
           Object[] got = checkConnectionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, checkParams).get();
           result = (boolean) got[1];
        }
        catch (InterruptedException ie)
        {
            Log.e("hasInternetConn", ie.getMessage());
        }
        catch (ExecutionException ee)
        {
            Log.e("hasInternetConn", ee.getMessage());
        }

        //APPLY SECURITY PATCH FOR PRE LOLLIPOP DEVICES
        if(result && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && !patchApplied){
            Log.e("PATCH", "Applying");
            ProviderInstaller.installIfNeededAsync(this, this);
        }

        return result;

    }


    @Override
    public boolean apiResponds()
    {
        boolean result = false;

        try
        {
            ApiAnswers apiAnswers = new ApiAnswers();
            apiAnswers.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            result = apiAnswers.get();
        }
        catch (InterruptedException ie)
        {
            Log.e("hasInternetConn", ie.getMessage());
        }
        catch (ExecutionException ee)
        {
            Log.e("hasInternetConn", ee.getMessage());
        }

        return result;
    }



    @Override
    public DownloadFragment getDownloadFragment()
    {
        return downloadFragment;
    }


    @Override
    public SQLiteDatabase getAppDatabase()
    {
        return f1Database;
    }


    @Override
    public void setResultFragment(RecyclerView.Adapter adapterToSet)
    {
        mDrawerLayout.closeDrawer(mNavigationView);
        resultFragment.setTheAdapter(adapterToSet);

        if(isSoundsOn())
        {
            getSoundFragment().playSound("sounds/results_in.mp3");
        }

        //let the drawer with the results come out if present in the hierarchy
        if(isResultDrawer())
        {
            mDrawerLayout.openDrawer(resultFragmentDrawer);
        }

    }


    /**
     * I have to create another pending intent with the same request code and cancel it in order to cancel an existing alarm
     */
    @Override
    public void cancelAllNotifications(){

        try {

            String query = "select notif_id, event, time_to_event from notifications_table;";

            Cursor notificationsIds = f1Database.rawQuery(query, null);

            if(notificationsIds.moveToFirst()){

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                do {

                    //create a new alarm with the same exact pending intent and cancel it
                    int requestCode = notificationsIds.getInt(0);

                    Intent notiFyIntent = new Intent(MainActivity.this, NotificationBroadcast.class);

                    PendingIntent mAlarmPendingIntent = PendingIntent.getBroadcast(this, requestCode, notiFyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager.cancel(mAlarmPendingIntent);

                    //clear the notifications_table in the database
                    try{

                        f1Database.execSQL("delete from notifications_table where notif_id = " + requestCode + ";");

                    }catch (SQLiteException sql){
                        Log.e("MainAct/clearNotifs", sql.getMessage());
                    }

                }
                while (notificationsIds.moveToNext());

                //update the preferences file
                setNotificationsOn(false);
            }

        }
        catch (SQLiteException sqlite){ //Notifications off selected before any notifications were set, no table yet exists
            Log.e("CANCEL_NOTIFS", sqlite.getMessage());
        }

    }


    public SoundFragment getSoundFragment()
    {
        return soundFragment;
    }


    public void setSoundsOn(boolean on){
        this.soundsOn = on;

        writeToPreferences(SOUND_PREFERENCE_KEY, on);

        if(on)
            activityMenu.findItem(R.id.soundsMenu).setIcon(R.mipmap.ic_volume_up_black_24dp);
        else
            activityMenu.findItem(R.id.soundsMenu).setIcon(R.mipmap.ic_volume_off_black_24dp);

        onPrepareOptionsMenu(activityMenu); //refresh the menu, icons have changed
    }



    @Override
    public  void setNotificationsOn(boolean on)
    {
        this.notificationsOn = on;

        writeToPreferences(NOTIFICATIONS_PREFERENCE_KEY, on);

        if(on)
            activityMenu.findItem(R.id.notificationsMenu).setIcon(R.mipmap.ic_notifications_black_24dp);
        else
            activityMenu.findItem(R.id.notificationsMenu).setIcon(R.mipmap.ic_notifications_off_black_24dp);

        onPrepareOptionsMenu(activityMenu); //refresh the menu, icons have changed
    }




    @Override
    public boolean isSoundsOn() {
        return this.soundsOn;
    }



    @Override
    public void onDialogPositiveClick(String userInput, String key){

        String finalQuery = BASIC_URI + userInput;

        Object[] params;

        if(key.contains("/1")) {
            String newKey = deSlash(key);
            params = new Object[]{finalQuery, newKey, getDownloadFragment(), getResources().getString(R.string.getting_data), new Bundle()};
        }
        else
            params = new Object[]{finalQuery, key, getDownloadFragment(), getResources().getString(R.string.getting_data)};

        downloadFragment.startListAdapterTask(params);

    }


    /**
     * If input contains any slashes, get rid of it along with what follows it
     * @param input
     * @return the key without the slash if any contained ( example drivers/1 --> drivers )
     */
    private String deSlash(String input){

        if(input.length() == 0 || input.charAt(0) == '/')
            return "";
        else
            return input.charAt(0) + deSlash(input.substring(1));

    }


    @Override
    public void launchSingleSelectionDialog(Bundle args){

        SingleSelectionDialog dialog = (SingleSelectionDialog) getSupportFragmentManager().findFragmentByTag("SINGLE_DIALOG");

        if (dialog == null)
            dialog = new SingleSelectionDialog();

        dialog.setArguments(args);

        dialog.show(getSupportFragmentManager(), "SINGLE_DIALOG");

    }



    @Override
    public void launchMultipleSelectionDialog(Bundle args){

        MultipleSelectionDialog dialog = (MultipleSelectionDialog) getSupportFragmentManager().findFragmentByTag("MULTIPLE_DIALOG");

        if(dialog == null)
            dialog = new MultipleSelectionDialog();

        dialog.setArguments(args);

        dialog.show(getSupportFragmentManager(), "MULTIPLE_DIALOG");

        getSupportFragmentManager().executePendingTransactions();
    }


    @Override
    public boolean getFromPreferences(String key, boolean defaultValue){
        SharedPreferences preferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);

        return preferences.getBoolean(key, defaultValue);
    }

    @Override
    public void writeToPreferences(String key, boolean value){

        SharedPreferences preferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(key, value);
        editor.apply();

    }



    @Override
    public void blockOrientationChanges(){

        //find out what the orientation is and keep it until releasing it again
        int config = getResources().getConfiguration().orientation;

        //set this as permanent until informed otherwise

        if(config == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }



    @Override
    public void allowOrientationChanges(){
        //enable orientation changes
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }




    /**
     * Checks whether the application database is copied from the assets folder or not. Either initiates a loader that copies it or finds it and
     * assigns it to activity's reference
     *
     */
    private void setTheAppDatabase()
    {
        final String databaseFilePath = "/data/data/" + getPackageName() + "/databases/" + DATABASE_NAME;

        final File databaseFile = new File(databaseFilePath);

        if(!databaseFile.exists())
        {
            getSupportLoaderManager().initLoader(COPY_MAIN_DATABASE_CODE, null, new LoaderManager.LoaderCallbacks<SQLiteDatabase>()
            {
                @NonNull
                @Override
                public Loader<SQLiteDatabase> onCreateLoader(int id, @Nullable Bundle args)
                {
                    return new CopyMainDatabaseLoader(MainActivity.this, databaseFilePath);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<SQLiteDatabase> loader, SQLiteDatabase data)
                {
                    f1Database = data;

                    if(!searchedForUpdates)
                    {
                        checkForUpdates();
                    }

                }

                @Override
                public void onLoaderReset(@NonNull Loader<SQLiteDatabase> loader)
                {

                }
            });
        }
        else
        {
            Log.e("DATABASE_SET", "Database already copied");

            f1Database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

            if (!searchedForUpdates)
            {
                checkForUpdates();

            }

        }

    }



    public void checkForUpdates()
    {

        searchedForUpdates = true;

        getSupportLoaderManager().initLoader(CHECK_UPDATES_LOADER_CODE, null, new LoaderManager.LoaderCallbacks<String[]>()
        {
            @NonNull
            @Override
            public Loader<String[]> onCreateLoader(int id, @Nullable Bundle args)
            {
                return new CheckUpdatesLoader(getApplicationContext(), MainActivity.this);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<String[]> loader, String[] data)
            {
                if(data[0] != null || data[1] != null || data[2] != null || data[3] != null) //needs an update
                {
                    Intent updateDatabaseServiceIntent = new Intent(getApplicationContext(), UpdateDatabaseEntriesService.class);

                    if(data[0] != null)
                    {
                        updateDatabaseServiceIntent.putExtra(UpdateDatabaseEntriesService.DRIVERS_UPDATE_TAG, data[0]);
                    }

                    if(data[1] != null)
                    {
                        updateDatabaseServiceIntent.putExtra(UpdateDatabaseEntriesService.CONSTRUCTORS_UPDATE_TAG, data[1]);
                    }

                    if(data[2] != null)
                    {
                        updateDatabaseServiceIntent.putExtra(UpdateDatabaseEntriesService.CIRCUITS_UPDATE_TAG, data[2]);
                    }

                    if(data[3] != null)
                    {
                        updateDatabaseServiceIntent.putExtra(UpdateDatabaseEntriesService.SEASONS_UPDATE_TAG, data[3]);
                    }

                    if(!databaseServiceDone)
                    {
                        startService(updateDatabaseServiceIntent);
                        databaseServiceDone = true;
                    }

                }

            }

            @Override
            public void onLoaderReset(@NonNull Loader<String[]> loader)
            {

            }
        });
    }



    @Override
    public void setPlayStartupSound(boolean onOrOff)
    {
        this.playStartupSound = onOrOff;
    }



    //PATCH ASYNC CallBacks
    @Override
    public void onProviderInstalled()
    {
        Log.e("PATCH", "SUCCESS");
        patchApplied = true;
        setTheAppDatabase();


        //If you reached this, you 're on a pre lollipop device. Download fragment that initiates
        // news service upon creation has not been added yet. Add it now
        downloadFragment = new DownloadFragment();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(downloadFragment, DOWNLOAD_FRAGMENT_TAG).commit();
        fm.executePendingTransactions();

    }



    @Override
    public void onProviderInstallFailed(int i, Intent intent)
    {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        if (availability.isUserResolvableError(i)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            availability.showErrorDialogFragment(
                    this, i,
                    ERROR_DIALOG_REQUEST_CODE,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // The user chose not to take the recovery action
                            onProviderInstallerNotAvailable();
                        }
                    });
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            // Adding a fragment via GoogleApiAvailability.showErrorDialogFragment
            // before the instance state is restored throws an error. So instead,
            // set a flag here, which will cause the fragment to delay until
            // onPostResume.
            mRetryProviderInstall = true;
        }
    }


    /**
     * On resume, check to see if we flagged that we need to reinstall the
     * provider.
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mRetryProviderInstall) {
            // We can now safely retry installation.
            ProviderInstaller.installIfNeededAsync(this, this);
        }
        mRetryProviderInstall = false;
    }


    private void onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.

        Log.e("PATCH", "FAILED MISERABLY");

    }


}
