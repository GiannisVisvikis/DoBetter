package comp.examplef1.iovisvikis.f1story.MyDialogs;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import comp.examplef1.iovisvikis.f1story.R;

/**
 * Created by iovisvikis on 30/3/2017.
 */

public class DataDialog extends android.support.v4.app.DialogFragment{

    String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        message = getArguments().getString("MESSAGE");

        setRetainInstance(true);
        setCancelable(false);

    }


    @Override
    public void onStart() {
        super.onStart();

        int width, height;

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            height = (int) (getResources().getDisplayMetrics().heightPixels * 0.60);
        }
        else{
            width = (int) (getResources().getDisplayMetrics().widthPixels * 0.60);
            height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
        }

        getDialog().getWindow().setLayout(width, height);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View root = getActivity().getLayoutInflater().inflate(R.layout.fetching_data, null);
        AppCompatTextView messageTxt = root.findViewById(R.id.messageTxtView);
        messageTxt.setText(message);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return root;

    }


    @Override
    public void onDestroyView() {

        Dialog dialog = getDialog();

        if(dialog!= null && getRetainInstance()){
            dialog.setDismissMessage(null);
        }

        super.onDestroyView();
    }



}//DataDialog




