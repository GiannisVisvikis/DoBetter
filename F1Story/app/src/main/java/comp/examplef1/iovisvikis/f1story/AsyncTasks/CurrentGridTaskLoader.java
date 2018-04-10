package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import comp.examplef1.iovisvikis.f1story.MyAdapters.CurrentGridRows;

public class CurrentGridTaskLoader extends AsyncTaskLoader<ArrayList<CurrentGridRows>>
{

    private ArrayList<CurrentGridRows> cached = null;

    public CurrentGridTaskLoader(@NonNull Context context)
    {
        super(context);
    }


    @Override
    protected void onStartLoading()
    {
        if(cached != null)
        {
            deliverResult(cached);
        }
        else
        {
            forceLoad();
        }
    }


    @Nullable
    @Override
    public ArrayList<CurrentGridRows> loadInBackground()
    {
        ArrayList<CurrentGridRows> theRows = new ArrayList<>();

        BufferedReader br = null;

        try
        {
            br = new BufferedReader(new InputStreamReader(getContext().getAssets().open("grid/the_grid.txt")));

            String line;

            while ( (line = br.readLine()) != null ){

                String[] gridRowInfo = line.split(",");
                CurrentGridRows gridRow = new CurrentGridRows(gridRowInfo);
                theRows.add(gridRow);
            }

        }
        catch (IOException ioe){
            Log.e("NavScrollViewFrag", ioe.getMessage());
        }
        finally
        {
            try
            {
                br.close();
            }
            catch (IOException io)
            {
                Log.e("CurrGridTskLoader", io.getMessage());
            }
        }

        return theRows;
    }


    @Override
    public void deliverResult(@Nullable ArrayList<CurrentGridRows> data)
    {
        if(cached == null)
        {
            cached = data;
        }

        super.deliverResult(data);
    }

}
