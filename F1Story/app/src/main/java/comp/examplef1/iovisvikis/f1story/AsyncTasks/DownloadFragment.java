package comp.examplef1.iovisvikis.f1story.AsyncTasks;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import comp.examplef1.iovisvikis.f1story.Communication;
import comp.examplef1.iovisvikis.f1story.MyAdapters.SeasonEndDriver;

import comp.examplef1.iovisvikis.f1story.NewsService;
import comp.examplef1.iovisvikis.f1story.R;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by iovisvikis on 15/10/2016.
 */



public class DownloadFragment extends android.support.v4.app.Fragment{


    //reference to the service that downloads the news to the database
    private NewsService boundNewsService;
    private ServiceConnection serviceConnection;


    private boolean wasServiceStarted = false;

    private Intent serviceIntent;

    private Communication act;

    private String callerFragmentTag, callerSelectedName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = (Communication) getActivity();

        if(!wasServiceStarted & act.hasInternetConnection() & act.apiResponds()){

            serviceIntent = new Intent(getActivity().getApplicationContext(), NewsService.class);
            serviceIntent.putExtra("TOTAL_BRANCHES", getResources().getStringArray(R.array.news_sites).length);


            //bind the service to the activity
            serviceConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                    boundNewsService = ((NewsService.NewsServiceBinder) iBinder).getService();


                    //start activity if it was never triggered before (the boolean wasServiceStarted makes
                    // sure we won't restart service every time we get an orientation change)

                    if(act.hasInternetConnection() && !wasServiceStarted){
                        //Log.e("SERVICE_CONNECTED", "Connecting service");
                        getActivity().startService(serviceIntent);
                        wasServiceStarted = true;
                    }
                    else if(wasServiceStarted && boundNewsService != null)
                        boundNewsService.setServiceDone(true);

                }


                //only gets called if the service gets killed by the system or crashed
                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    //Log.e("SERVICE_DISCONNECTED", "Disconnecting service");
                    boundNewsService = null;
                }

            };

            getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        }

    }



    @Override
    public void onDetach() {
        super.onDetach();

        // activity about to be destroyed unbind the service
        if(serviceConnection!=null) {
            getActivity().unbindService(serviceConnection);
        }

        serviceConnection = null;
        //boundNewsService = null;
        act = null;

        serviceIntent = null;
    }



    public void setCallerFragmentTag(String fragmentTag){this.callerFragmentTag = fragmentTag;}

    public String getCallerFragmentTag(){return this.callerFragmentTag;}

    public void setCallerSelectedName(String selectedName){this.callerSelectedName = selectedName;}

    public String getCallerSelectedName(){return this.callerSelectedName;}

    public Communication getAct(){ return act;}




    public void startListAdapterTask(Object[] params){

        if(getAct().hasInternetConnection() && getAct().apiResponds()) {

            GetListAdapterTask adapterTask = new GetListAdapterTask();
            adapterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
    }


    /**
     * See whether we're in small screen mode or not.
     * @return _32 for 32x32 png in small screen or _48 for 48x48 png in large screen mode
     */
    public String getFlagFilename(String nationOrCountryName){

        return (act.isResultDrawer()) ? nationOrCountryName + "_32.png" : nationOrCountryName + "_48.png";

    }


    public int getUnknownFlagID(){

        //use
        return (getAct().isResultDrawer() &&  getResources().getDisplayMetrics().density <= 1.5) ? R.drawable.unknown_flag_32 : R.drawable.unknown_flag_48;
    }


    public String splitDown(String start){
        if(start.length() == 0)
            return "";
        else if(start.charAt(0) == ' ' || start.charAt(0) == '-')
            return "\n" + splitDown(start.substring(1));
        else
            return start.charAt(0) + splitDown(start.substring(1));
    }



    /**
     * Uploads the driver photo stored in assets/drivers folder under the filename driverId.png
     * @param container the ImageView that will contain the photo
     * @param driverId the driver's distinct id
     */
    public void uploadDriverPhoto(ImageView container, String driverId){

        try{
            container.setImageBitmap(BitmapFactory.decodeStream(getActivity().getAssets().open("drivers/" +
                    driverId + ".png")));
        }
        catch (FileNotFoundException fnf){
            container.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.unknownn_driver));
        }
        catch (IOException io){
            Log.e("MyCustmBasAdptr/DrvrPht", io.getMessage());
        }


    }


    /**
     * Uploads the driver photo stored in assets/constructors/ folder under the filename constructorId.gif
     * @param container the ImageView that will contain the photo
     * @param constructorId the driver's distinct id
     */
    public void uploadConstructorPhoto(ImageView container, String constructorId){

        try{
            container.setImageBitmap(BitmapFactory.decodeStream(getActivity().getAssets().open("constructors/" +
                                                                                                constructorId + ".gif")));
        }
        catch (FileNotFoundException fnf){
            container.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.unknown_const));
        }
        catch (IOException io){
            Log.e("MyCustmBsAdptr/ConstrPh", io.getMessage());
        }
    }


    /**
     * Uploads the nationality flag stored in assets/nationalities/ folder under the filename nationality.png
     * @param flagFrame the FrameLayout that will contain the flag png
     * @param nationality the distinct nationality string
     */
    public void uploadNationalityFlag(ImageView flagFrame, String nationality){

        try{

            flagFrame.setImageBitmap(BitmapFactory.decodeStream(getActivity().getAssets().open("nationalities/"
                                                                                    + getFlagFilename(nationality) )));
        }
        catch (FileNotFoundException fnf) { //if not found, replace with unknown flag
            flagFrame.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), getUnknownFlagID()));
        }
        catch (IOException io){
            Log.e("MyCustmBsAdptr/NatnFlg", io.getMessage());
        }

    }


    /**
     * Uploads the nationality flag stored in assets/nationalities/ folder under the filename nationality.png
     * @param flagFrame the FrameLayout that will contain the flag png
     * @param country the distinct nationality
     */
    public void uploadCountryFlag(ImageView flagFrame, String country){

        try{

            flagFrame.setImageBitmap(BitmapFactory.decodeStream(getActivity().getAssets().open("countries/"
                                                                                        + getFlagFilename(country) )));
        }
        catch (FileNotFoundException fnf){
            flagFrame.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), getUnknownFlagID()));
        }
        catch (IOException io){
            Log.e("MyCustmBsAdptr/NatnFlg", io.getMessage());
        }

    }



    public void uploadTheDrivers(String query, String message, String season, ViewGroup parent, LinearLayout addTo){

        //make sure you can't repeat the proccess adding more of the same
        if(addTo.getChildCount() == 0 ) {

            DriversTask driversTask = new DriversTask();

            driversTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[]{query, season, message, this, parent, addTo});
        }
        else
            addTo.removeAllViews();
    }



    public void addTheDriver(final SeasonEndDriver seasonEndDriver, ViewGroup parent, LinearLayout addTo){

        View driverAddition = getActivity().getLayoutInflater().inflate(R.layout.constructor_season_end_driver, parent, false);

        ImageView driverPhoto = driverAddition.findViewById(R.id.driverPhoto);

        uploadDriverPhoto(driverPhoto, seasonEndDriver.getId());

        //open driver information on wiki page
        driverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driverIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(seasonEndDriver.getUrl()));
                startActivity(driverIntent);
            }
        });


        AppCompatTextView driverNameTxt = driverAddition.findViewById(R.id.driverNameTxt);
        driverNameTxt.setText(splitDown(seasonEndDriver.getName()));


        ImageView driverFlag = driverAddition.findViewById(R.id.driverFlagFrame);
        uploadNationalityFlag(driverFlag, seasonEndDriver.getNationality());


        AppCompatTextView driverSeasonInfo = driverAddition.findViewById(R.id.driverSeasonInfoTxt);
        driverSeasonInfo.setText(getResources().getString(R.string.wins) + " " + seasonEndDriver.getWins() + "\n"
                + getResources().getString(R.string.points) + " " + seasonEndDriver.getPoints() + "\n"
                + getResources().getString(R.string.position) + " " + seasonEndDriver.getPosition());

        addTo.addView(driverAddition);
    }


    public boolean isServiceDone(){

        //if there is a service bound then ask it
        if(boundNewsService != null) {
            return boundNewsService.isServiceDone();
        }

        //if there is no bound service then an orientation change has occurred and the service should
        //already be executed and over
        return true;
    }


    public boolean getWasServiceStarted() {
        return wasServiceStarted;
    }

}//DownloadFragment




