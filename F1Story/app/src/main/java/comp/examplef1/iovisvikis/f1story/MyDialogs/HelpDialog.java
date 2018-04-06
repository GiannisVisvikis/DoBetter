package comp.examplef1.iovisvikis.f1story.MyDialogs;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import comp.examplef1.iovisvikis.f1story.R;

/**
 * Created by iovisvikis on 10/5/2017.
 */

public class HelpDialog extends DialogFragment{

    private Dialog dialog;
    private String text;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        text = args.getString("HELP_TXT");

        setRetainInstance(true);
        setCancelable(false);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.help_dialog_layout, container, false);

        AppCompatTextView helpTxt = (AppCompatTextView) root.findViewById(R.id.helpTxtView);
        helpTxt.setText(text);

        FrameLayout okButton = (FrameLayout) root.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpDialog.this.dismiss();
            }
        });

        int width, height;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            width = (int) ( (getResources().getDisplayMetrics().widthPixels) * 0.90);
            height = (int) ( (getResources().getDisplayMetrics().heightPixels) * 0.5);
        }
        else{
            width = (int) ((getResources().getDisplayMetrics().widthPixels) * 0.50);
            height = getResources().getDisplayMetrics().heightPixels;
        }


        dialog = getDialog();

        dialog.getWindow().setLayout(width, height);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return root;
    }



    @Override
    public void onDestroyView() {

        dialog = getDialog();

        // handles https://code.google.com/p/android/issues/detail?id=17423
        if(getRetainInstance() && dialog != null){
            dialog.setDismissMessage(null);
        }

        super.onDestroyView();
    }


}//HelpDialog
