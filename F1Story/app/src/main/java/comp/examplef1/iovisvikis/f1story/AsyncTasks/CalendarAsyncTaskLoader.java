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

import comp.examplef1.iovisvikis.f1story.MyAdapters.CalendarRace;

public class CalendarAsyncTaskLoader extends AsyncTaskLoader<ArrayList<CalendarRace>>
{

    private Context context;
    private ArrayList<CalendarRace> cached = null;

    public CalendarAsyncTaskLoader(@NonNull Context context)
    {
        super(context);

        this.context = context;
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
    public ArrayList<CalendarRace> loadInBackground()
    {
        ArrayList<CalendarRace> result = new ArrayList<>();

        try{

            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("events/qualifyings_races_GMTs.txt")));

            String line;

            while ( (line = br.readLine()) != null ){

                String[] calendaRaceArgs = line.split(",");
                CalendarRace calendarRace = new CalendarRace(calendaRaceArgs);
                result.add(calendarRace);
            }

            br.close();
        }
        catch (IOException io){
            Log.e("GetTheCalEvents", io.getMessage());
        }

        return result;

    }


    @Override
    public void deliverResult(@Nullable ArrayList<CalendarRace> data)
    {
        if(cached == null)
        {
            cached = data;
        }

        super.deliverResult(data);
    }

}
