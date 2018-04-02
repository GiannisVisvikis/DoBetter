package comp.examplef1.iovisvikis.f1story;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements Communication
{



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

        Toast.makeText(getApplicationContext(), "There is a result drawer : " + isResultDrawer() , Toast.LENGTH_SHORT).show();

        //TODO check for prior state



        resultFragmentDrawer = findViewById(R.id.result_fragment_place);


        //TODO get the fragments in place




        //get reference to the toolbar
        Toolbar toolbar = findViewById(R.id.the_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);


        //get reference to the navigation view and handle click events
        mNavigationView = findViewById(R.id.nav_view);
        //if not called all navigation drawer icons will appear gray (that's a no no)
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



        //get reference to the drawer layout and set listener
        mDrawerLayout = findViewById(R.id.drawer_layout);

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


        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

    }



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

    @Override
    public boolean isResultDrawer()
    {
        return root.getTag() != null;
    }

}
