package comp.examplef1.iovisvikis.f1story;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements Communication
{

    private final String RESULT_FRAGMENT_TAG = "RESULT_FRAGMENT_TAG";
    private final String DOWNLOAD_FRAGMENT_TAG = "DOWNLOAD_FRAGMENT_TAG";
    private final String SOUND_FRAGMENT_TAG = "SOUND_FRAGMENT_TAG";
    private final String DATABASE_NAME = "F1_STORY.db";

    private ResultFragment resultFragment;

    private View root;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private FrameLayout resultFragmentDrawer;

    private SQLiteDatabase f1Database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        //check if the database is copied or not and return it
        f1Database = getTheAppDatabase();

        //get the fragments in place
        FragmentManager fragmentManager = getSupportFragmentManager();

        //check for prior state
        if(savedInstanceState == null)
        {
            resultFragment = new ResultFragment();
            //add the rest of the fragments. Add the no internet and not responding activities

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.result_fragment_place, resultFragment, RESULT_FRAGMENT_TAG);

            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
        }
        else
        {
            resultFragment = (ResultFragment) fragmentManager.findFragmentByTag(RESULT_FRAGMENT_TAG);
        }


        resultFragmentDrawer = findViewById(R.id.result_fragment_place);

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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_options_menu, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            //TODO handle rest of the clicks here (navigation and fragments options)

        }


        return super.onOptionsItemSelected(item);
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
            //TODO make exit sound and exit the app
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

                Toast.makeText(getApplicationContext(), item.getTitle() + " got clicked", Toast.LENGTH_SHORT).show();

                mDrawerLayout.closeDrawers();

                return true;
            }
        });

    }



    private void setTheDrawer(Toolbar toolbar)
    {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_closed)
        {
            public void onDrawerClosed(View view)
            {
                Toast.makeText(getApplicationContext(), "Drawer is closed", Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {

                mDrawerLayout.closeDrawer(resultFragmentDrawer);

                Toast.makeText(getApplicationContext(), "Drawer is open", Toast.LENGTH_SHORT).show();
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
    public void setResultFragment(RecyclerView.Adapter adapterToSet)
    {
        mDrawerLayout.closeDrawer(mNavigationView);
        resultFragment.setTheAdapter(adapterToSet);

        //let the drawer with the results coome out if present in the hierarchy
        if(isResultDrawer())
        {
            mDrawerLayout.openDrawer(resultFragmentDrawer);
        }

    }


    /**
     * Checks whether the application database is copied from the assets folder or not. Either copies it or finds it and returns it
     * @return the app database
     */
    private SQLiteDatabase getTheAppDatabase()
    {
        String databaseFilePath = "/data/data/" + getPackageName() + "/databases/" + DATABASE_NAME;

        File databaseFile = new File(databaseFilePath);

        if(!databaseFile.exists())
        {
            Log.e("MainAct/CheckDtbs", "Copying database from assets");

            BufferedInputStream input = null;
            BufferedOutputStream output = null;

            try
            {
                input = new BufferedInputStream(getAssets().open("databases/" + DATABASE_NAME));
                output = new BufferedOutputStream(new FileOutputStream(databaseFile));

                int b;

                while ( (b = input.read()) != -1 )
                {
                    output.write(b);
                }

            }
            catch (FileNotFoundException fnf)
            {
                Log.e("MainAct/CheckDtbs", fnf.getMessage());
            }
            catch (IOException io1)
            {
                Log.e("MainAct/CheckDtbs", io1.getMessage());
            }
            finally
            {
                try
                {
                    input.close();
                    output.close();
                }
                catch (IOException io2)
                {
                    Log.e("MainAct/CheckDtbs", io2.getMessage());
                }
            }

        }
        else
        {
            Log.e("MainAct/CheckDtbs", "Database already copied from assets");
        }

        SQLiteDatabase result = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        long tables = DatabaseUtils.longForQuery(result,"SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence'", null);

        Log.e("NUM_OF_TABLES :" , tables + "");

        return result;
    }



}
