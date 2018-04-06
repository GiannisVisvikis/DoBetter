package comp.examplef1.iovisvikis.f1story;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Random;


/**
 * Created by iovisvikis on 24/10/2016.
 */


public class SoundFragment extends android.support.v4.app.Fragment{

    private Communication act;
    private MediaPlayer mp;

    private String[] shortSoundsPaths = new String[]{"sounds/blown_exhausts_short.mp3", "sounds/gears_down.mp3", "sounds/gears_up.mp3"};

    private boolean isReleased;

    private Random random = new Random();

    private String soundfilePath;

    public MediaPlayer getPlayer(){
        return this.mp;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        act = (Communication) getActivity();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }


    @Override
    public void onDetach() {
        super.onDetach();

        act = null;
    }


    public void playSound(String filePath){

        if(act.isSoundsOn()) {

            stopSound();

            try {
                //shit still does not work
                AssetFileDescriptor afd = getActivity().getAssets().openFd(filePath);
                //Log.e("AFD_IS_NULL", (afd == null) + "");
                FileDescriptor fd = afd.getFileDescriptor();

                this.soundfilePath = filePath;

                mp = new MediaPlayer();
                mp.setDataSource(fd, afd.getStartOffset(), afd.getLength());
                mp.prepare();
            }
            catch (IOException io){
                Toast.makeText(getActivity(), "Sound not found", Toast.LENGTH_SHORT).show();
            }

            isReleased = false;
            //set a listener to free resource on completion
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                    isReleased = true;
                    mp = null;
                }
            });

            mp.start();
        }

    }


    public void playRandomSound(){

        int randomIndex = random.nextInt(shortSoundsPaths.length);
        playSound(shortSoundsPaths[randomIndex]);
    }

    
    public void stopSound(){
        //if not released then it is still playing (gets released inside the completion listener)
        if(mp != null && !isReleased)
            mp.stop();
            mp = null;
    }


    public boolean isExitSoundPlaying(){
        //only want to wait when exiting the app
        return soundfilePath.equalsIgnoreCase("sounds/app_closed.mp3") && mp.isPlaying();
    }



}//SoundFragment



