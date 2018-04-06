package comp.examplef1.iovisvikis.f1story.MyDialogs;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import comp.examplef1.iovisvikis.f1story.R;

import java.io.IOException;

/**
 * Created by ioannisvisvikis on 8/8/17.
 */

public class UploadNewsDialog extends DialogFragment {


    private String assetsPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        this.assetsPath = args.getString("LOGO_ASSET_PATH");

        setCancelable(false);
        setRetainInstance(true);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.upload_news_dialog, container, false);

        ImageView logoView = root.findViewById(R.id.news_dialog_logo);

        try{
            logoView.setImageBitmap(BitmapFactory.decodeStream(getActivity().getAssets().open(assetsPath)));
        }
        catch (IOException ioe){
            Log.e("UploadNewsDialog", ioe.getMessage());
        }

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return root;
    }
    

    @Override
    public void onStart() {
        super.onStart();

        int width, height;

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            width = (int) (getResources().getDisplayMetrics().widthPixels * 0.70);
            height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);
        }
        else{
            width = (int) (getResources().getDisplayMetrics().widthPixels * 0.60);
            height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
        }

        getDialog().getWindow().setLayout(width, height);

    }



    //retain view across orientation changes
    @Override
    public void onDestroyView() {

        Dialog dialog = getDialog();

        if(dialog!= null && getRetainInstance()){
            dialog.setDismissMessage(null);
        }

        super.onDestroyView();
    }





}
