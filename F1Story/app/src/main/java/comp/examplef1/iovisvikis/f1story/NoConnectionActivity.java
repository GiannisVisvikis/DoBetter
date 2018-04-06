package comp.examplef1.iovisvikis.f1story;



import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import comp.examplef1.iovisvikis.f1story.R;

import java.io.IOException;

/**
 * Created by iovisvikis on 19/10/2016.
 */

public class NoConnectionActivity extends AppCompatActivity {

    private MediaPlayer mp;

    private boolean playEngine = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
            playEngine = savedInstanceState.getBoolean("PLAY_MUSIC");


        setContentView(R.layout.no_internet_activity);

        if (playEngine) {

            setPlayer();

            mp.start();

            playEngine = false;
        }

        AppCompatButton button = (AppCompatButton) findViewById(R.id.checkAgainButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInternetConnection()){

                    Intent goBack = new Intent(NoConnectionActivity.this, MainActivity.class);
                    startActivity(goBack);
                    finish();
                }
                else {

                    setPlayer();

                    mp.start();

                    playEngine = false;

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection_active), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void setPlayer() {

        try{
            AssetFileDescriptor afd = getAssets().openFd("sounds/engine_fail.mp3");

            mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mp.start();
        }
        catch (IOException io){
            Toast.makeText(this, "Sound not found", Toast.LENGTH_SHORT).show();
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
                mp = null;
            }
        });

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("PLAY_MUSIC", playEngine);
    }

    private boolean isInternetConnection(){
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = conManager.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnected())
            return true;

        return false;
    }


}//NoConnectionActivity



