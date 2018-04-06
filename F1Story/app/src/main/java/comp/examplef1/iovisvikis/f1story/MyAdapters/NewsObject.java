package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import comp.examplef1.iovisvikis.f1story.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ioannisvisvikis on 8/7/17.
 */

public class NewsObject {


    private String title, link;

    private Bitmap articlePic;


    public String getTitle() {
        return title;
    }


    public String getLink() {
        return link;
    }


    public Bitmap getArticlePic() {
        return articlePic;
    }


    public NewsObject(Context context, String title, String link, String pathToPic, String pathToLogo){

        this.title = title;
        this.link = link;

        try {
            this.articlePic = BitmapFactory.decodeStream(new FileInputStream(new File(pathToPic)));
        }
        catch (FileNotFoundException fnf){
            try {
                this.articlePic = BitmapFactory.decodeStream(context.getAssets().open(pathToLogo));
            }
            catch (IOException io){
                this.articlePic = BitmapFactory.decodeResource(context.getResources(), R.drawable.unknown_const);
            }
        }
    }



}
