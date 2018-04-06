package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;

import comp.examplef1.iovisvikis.f1story.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ioannisvisvikis on 8/8/17.
 */


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Bitmap siteLogo;

    private DownloadFragment host;

    private ArrayList<NewsObject> siteNews;

    
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { 
        
        View holderView = host.getActivity().getLayoutInflater().inflate(R.layout.news_article_row, parent, false);
    
        return new NewsViewHolder(holderView);
    
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {

        final NewsObject newsObject = siteNews.get(position);

        ImageView articlePicView = holder.getArticlePic();
        articlePicView.setImageBitmap(newsObject.getArticlePic());

        AppCompatTextView articleTitleView = holder.getArticleTitle();
        articleTitleView.setText(newsObject.getTitle() + "\n");

        ImageView siteLogoView = holder.getSmallLogoView();
        siteLogoView.setImageBitmap(siteLogo);

        articlePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String articleUrl = newsObject.getLink();
                Intent articleIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl));
                host.getActivity().startActivity(articleIntent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return siteNews.size();
    }
    


    public NewsAdapter(DownloadFragment host , final String assetsToLogoPath, final Cursor dataRows){

        this.host = host;

        this.siteNews = new ArrayList<>();

        String[] pathParts = assetsToLogoPath.split("\\."); //ex bitch.png--> ["bitch", "png"]
        String logoPath = pathParts[0] + "_small." + pathParts[1];  //so bitch.png --> bitch_small.png

        try {
            this.siteLogo = BitmapFactory.decodeStream(host.getActivity().getAssets().open(logoPath));
        }
        catch (IOException io){
            Log.e("NewsAdptr", io.getMessage());
        }

        //set the data
        if(dataRows.moveToFirst()){

            do{

                String title = dataRows.getString(0);
                String link = dataRows.getString(1);
                String pathToPic = dataRows.getString(2);

                NewsObject newsObject = new NewsObject(host.getActivity(), title, link, pathToPic, assetsToLogoPath);

                siteNews.add(newsObject);
            }
            while (dataRows.moveToNext());

        }

    }
    
    
    
    class NewsViewHolder extends RecyclerView.ViewHolder {
    
        private ImageView articlePic, smallLogoView;
        private AppCompatTextView articleTitle;


        public ImageView getArticlePic() {
            return articlePic;
        }

        public ImageView getSmallLogoView() {
            return smallLogoView;
        }

        public AppCompatTextView getArticleTitle() {
            return articleTitle;
        }

        public NewsViewHolder(View itemView) {
            super(itemView);
        
            this.articlePic = itemView.findViewById(R.id.articlePicView);
            this.articleTitle = itemView.findViewById(R.id.articleTitleView);
            this.smallLogoView = itemView.findViewById(R.id.small_logo_view);
            
        }
    
    }


}
