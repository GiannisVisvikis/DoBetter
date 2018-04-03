package comp.examplef1.iovisvikis.f1story;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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


public class MainActivity extends AppCompatActivity implements Communication
{

    private final String RESULT_FRAGMENT_TAG = "RESULT_FRAGMENT_TAG";
    private final String DOWNLOAD_FRAGMENT_TAG = "DOWNLOAD_FRAGMENT_TAG";
    private final String SOUND_FRAGMENT_TAG = "SOUND_FRAGMENT_TAG";

    private ResultFragment resultFragment;

    private View root;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private FrameLayout resultFragmentDrawer;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);


        FragmentManager fragmentManager = getSupportFragmentManager();

        //check for prior state
        if(savedInstanceState == null)
        {
            resultFragment = new ResultFragment();
            add the rest of the fragments. Add the no internet and not responding activities

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.result_fragment_place, resultFragment, RESULT_FRAGMENT_TAG);

            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
        }
        else
        {
            resultFragment = (ResultFragment) fragmentManager.findFragmentByTag(RESULT_FRAGMENT_TAG);
        }


        Log.e("RESULT", "Result fragment is added : " + (resultFragment.isAdded()));

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

            case R.id.action_settings:
                return true;

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

}
