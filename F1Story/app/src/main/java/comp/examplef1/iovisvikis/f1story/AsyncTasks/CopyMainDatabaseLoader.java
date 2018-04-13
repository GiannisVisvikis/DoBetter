package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import comp.examplef1.iovisvikis.f1story.MainActivity;


public class CopyMainDatabaseLoader extends AsyncTaskLoader<SQLiteDatabase>
{

    private SQLiteDatabase result;

    private Context context;
    private String databaseFilePath;


    public CopyMainDatabaseLoader(@NonNull Context context, String databaseFilePath)
    {
        super(context);

        this.context = context;
        this.databaseFilePath = databaseFilePath;
    }


    @Override
    protected void onStartLoading()
    {

        if(result != null)
        {
            deliverResult(result);
        }
        else
        {
            forceLoad();
        }

        super.onStartLoading();
    }


    @Nullable
    @Override
    public SQLiteDatabase loadInBackground()
    {
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try
        {
            input = new BufferedInputStream(context.getAssets().open("databases/" + MainActivity.DATABASE_NAME));

            new File(databaseFilePath).getParentFile().mkdirs();

            output = new BufferedOutputStream(new FileOutputStream(databaseFilePath));

            int b;

            while ( (b = input.read()) != -1 )
            {
                output.write(b);
            }

        }
        catch (FileNotFoundException fnf)
        {
            Log.e("CpMnDtbsLdrFNF", fnf.getMessage());
        }
        catch (IOException io1)
        {
            Log.e("CpMnDtbsLdrIOE", io1.getMessage());
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
                Log.e("CpMnDtbsLdr", io2.getMessage());
            }
        }
        return context.openOrCreateDatabase(MainActivity.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }



    @Override
    public void deliverResult(@Nullable SQLiteDatabase data)
    {
        if(result == null)
        {
            result = data;
        }

        super.deliverResult(data);
    }


}
