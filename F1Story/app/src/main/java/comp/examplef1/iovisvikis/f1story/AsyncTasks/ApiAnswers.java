package comp.examplef1.iovisvikis.f1story.AsyncTasks;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;


/**
 * Created by iovisvikis on 28/3/2017.
 */

public class ApiAnswers extends AsyncTaskLoader<Boolean> {


    public ApiAnswers(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();
    }


    @Nullable
    @Override
    public Boolean loadInBackground() {

        boolean result = false;
        HttpURLConnection con = null;

        try{
            URL url = new URL("https://ergast.com/api/f1/drivers");

            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(6000);
            con.setReadTimeout(6000);

            con.connect();

            result = con.getResponseCode() == HttpURLConnection.HTTP_OK;

        }
        catch (SocketTimeoutException stoe){
            System.out.println(stoe.getMessage());
        }
        catch (MalformedURLException murl){
            System.out.println(murl.getMessage());
        }
        catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }
        finally
        {
            if(con != null)
                con.disconnect();
        }

        return result;
    }


}//ApiAnswers


