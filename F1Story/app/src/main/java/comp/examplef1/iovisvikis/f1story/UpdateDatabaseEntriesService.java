package comp.examplef1.iovisvikis.f1story;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class UpdateDatabaseEntriesService extends Service
{



    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {


        Log.e("UPDATE_DATABASE", "Starting update database service");

        Thread updateDatabaseThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase f1Database = getApplicationContext().openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);

                //TODO Put all the updating shit in here

                Log.e("UPDATE_DATABASE", "Finishing update database service");
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


}
