package comp.examplef1.iovisvikis.f1story;

import android.app.Service;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;


import android.support.annotation.Nullable;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by ioannisvisvikis on 9/27/17.
 */



public class NewsService extends Service{

    private APICommunicator apiCommunicator;

    private int totalBranches;

    private boolean isServiceDone;
    private NewsServiceBinder mBinder;

    //if a key is here, the branch thread is started. If a sub threadId is in the corresponding ArrayList, it is finished
    private HashMap<String, ArrayList<Long>> sitesThreads;

    //branch threads DONE list. Will be used to monitor whether a branch thread is finished or not. If finished, then it is in here
    private ArrayList<Long> finishedBranchThreads;


    @Override
    public void onCreate() {
        super.onCreate();

        this.apiCommunicator = APICommunicator.getInstance();
        this.isServiceDone = false;
        this.finishedBranchThreads = new ArrayList<>();
        this.sitesThreads = new HashMap<>();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new NewsServiceBinder();
        return mBinder;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!isServiceDone){

            Log.e("NEWS_SERVICE", "STARTED");

            //just in case shit's not empty
            finishedBranchThreads.clear();

            totalBranches = intent.getIntExtra("TOTAL_BRANCHES", 0);

            //create a background service thread
            Thread serviceThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    //Log.e("NEWS_SERVICE_STARTED", "Service is started");

                    //Create branch threads for each RSS site to be searched. Each branch thread creates it's own sub threads
                    //that download the article images (into directories per site), links and titles and store that information
                    // into a database. Links and titles are held as text, images are held as file paths to the directory that
                    // contains the appropriate image for the article. The service thread then waits for all branch threads to
                    // terminate


                    final SQLiteDatabase newsDatabase = openOrCreateDatabase(MainActivity.NEWS_TABLES_DATABASE, MODE_PRIVATE, null);

                    Thread motorSportBranch = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            searchMotorsportComPage(newsDatabase);
                        }
                    });

                    motorSportBranch.start();



                    Thread autoSportBranch = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            searchAutosportComPage(newsDatabase);
                        }
                    });

                    autoSportBranch.start();



                    Thread espnBranch = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            searchEspnComPage(newsDatabase);
                        }
                    });

                    espnBranch.start();

                    //Make service thread wait for all branch threads to terminate
                    while (finishedBranchThreads.size() < totalBranches){

                        //wait for branch threads to finish
                    }

/*

                //Test and make sure not more files are created each time the service is run
                File fileStorage = getFilesDir();

                Log.e("File directories ", fileStorage.length() + "");

                for(File dirs : fileStorage.listFiles()){

                    Log.e(dirs.getName(), "hosts " + dirs.listFiles().length + " number of pics");

                }

*/

                    newsDatabase.close();

                    //Log.e("FINISHING_NEWS_SERVICE", "Finishing service");

                    //stop the service thread
                    isServiceDone = true;

                    Log.e("NEWS_SERVICE", "ALL_DONE");

                    stopSelf();

                }

            });

            serviceThread.start();

        }

        return START_NOT_STICKY; //if stopped by the OS, continue from where left off
    }



    public void setServiceDone(boolean serviceDone) {
        isServiceDone = serviceDone;
    }


    public boolean isServiceDone(){
        return this.isServiceDone;
    }





    public class NewsServiceBinder extends Binder{

        public NewsService getService(){
            return NewsService.this;
        }

    }



    private void searchEspnComPage(final SQLiteDatabase newsDatabase){

        //will be used as a mean for the subThreads to notify this method's master thread they're all done
        final Object lock = new Object();

        final String rssInfo = apiCommunicator.getInfo("http://www.espn.com/espn/rss/rpm/news");

        newsDatabase.execSQL("create table if not exists espn_table (id integer primary key, title text not null, link text not null, path_to_pic text not null);");
        newsDatabase.execSQL("delete from espn_table;");
        newsDatabase.execSQL("vacuum;");

        //get the Directory where the pic files will be stored in
        final File picsDirectory = new File(getFilesDir().getAbsolutePath() + "/espn");

        if(!picsDirectory.exists())
            picsDirectory.mkdir();

        //add to map, monitor all inner threads
        addSiteThreads("ESPN");

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            Document document = builder.parse(new InputSource(new StringReader(rssInfo)));
            document.getDocumentElement().normalize();

            Node image = document.getElementsByTagName("image").item(0);
            Node url = image.getChildNodes().item(0);

            String imageUrl = url.getTextContent();

            //save the article pic in a file
            File picFile = null;

            try{

                URL downloadUrl = new URL(imageUrl);
                HttpURLConnection picCon = (HttpURLConnection) downloadUrl.openConnection();

                picFile = new File(picsDirectory.getAbsolutePath() + "/EspnPic");

                BufferedInputStream is = new BufferedInputStream(picCon.getInputStream());
                BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(picFile));

                int toWrite;

                while( (toWrite = is.read()) != -1 ){

                    os.write(toWrite);
                }

                is.close();
                os.close();
                picCon.disconnect();

            }
            catch (MalformedURLException murl){
                Log.e("MlfrmdURL_Espn", murl.getMessage());
            }catch (IOException e) {
                Log.e("IOExcptn_Espn", e.getMessage());
            }


            //get the article and link information from the RSS XML
            NodeList items = document.getElementsByTagName("item");

            final String picFileUrl = picFile.getAbsolutePath();

            //count hom many rss objects have to do with formula1. Unfortunately it's a mess mixed all motor sports together
            int counter = 0;
            ArrayList<Node> f1Items = new ArrayList<>();

            for(int counterIndex=0; counterIndex < items.getLength(); counterIndex++) {

                Node itemNode = items.item(counterIndex);

                Node counterLinkNode = ((Element) itemNode).getElementsByTagName("link").item(0);
                String counterLink = counterLinkNode.getTextContent();

                if(counterLink.substring(20, 22).equalsIgnoreCase("f1")) {

                    f1Items.add(itemNode);
                    counter++;
                }

            }

            final  int totalThreads = counter;

            for(int index=0; index < counter; index++){

                final int id = index + 1;

                Node itemNode = f1Items.get(index);

                Node linkNode = ((Element) itemNode).getElementsByTagName("link").item(0);
                final String link =linkNode.getTextContent();

                Node titleNode = ((Element) itemNode).getElementsByTagName("title").item(0);
                String title = titleNode.getTextContent();

                final String newTitle = title.replaceAll("'", "''");

                Thread subWorkingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //store data to database
                        String subThreadQuery = "insert into espn_table values (" + id + ", '" + newTitle + "', '" +
                                link + "', '" + picFileUrl + "');";

                        //Log.e("ESPN_QUERY", subThreadQuery);

                        newsDatabase.execSQL(subThreadQuery);

                        addFinishedSubThread("ESPN", Thread.currentThread().getId());

                        if (getFinishedThreadsNo("ESPN") == totalThreads) { //last sub thread finishing satisfies this

                            synchronized (lock) { //notify branch thread to resume operation

                                lock.notify();
                            }
                        }
                    }
                });

                subWorkingThread.start();

            }

            //all sub threads fired up ... wait for them to finish
            synchronized (lock) {
                try {
                    lock.wait();

                }
                catch (InterruptedException ie){
                    Log.e("Espn_Thread", ie.getMessage());
                }
            }

            //all subthreads finished. Add motorsport branch thread to DONE list
            addBranchToFinished(Thread.currentThread().getId());

            //Log.e("ESPN_BRANCH", "Espn thread done");

        }
        catch (ParserConfigurationException pce){
            System.out.println("PCE thrown " + pce.getMessage());
        }
        catch (IOException io){
            System.out.println("IO thrown " + io.getMessage());
        }
        catch (SAXException sax){
            System.out.println("SAX thrown " + sax.getMessage());
        }

    }





    private void searchAutosportComPage(final SQLiteDatabase newsDatabase){

        //will be used as a mean for the subThreads to notify this method's master thread they're all done
        final Object lock = new Object();

        final String rssInfo = apiCommunicator.getInfo("https://www.autosport.com/rss/feed/f1");

        newsDatabase.execSQL("create table if not exists autosport_table (id integer primary key, title text not null, link text not null, path_to_pic text not null);");
        newsDatabase.execSQL("delete from autosport_table;");
        newsDatabase.execSQL("vacuum;");

        //get the Directory where the pic files will be stored in
        final File picsDirectory = new File(getFilesDir().getAbsolutePath() + "/autosport");

        if(!picsDirectory.exists())
            picsDirectory.mkdir();

        //add to map, monitor all inner threads
        addSiteThreads("AUTOSPORT");

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            Document document = builder.parse(new InputSource(new StringReader(rssInfo)));
            document.getDocumentElement().normalize();

            NodeList picList = document.getElementsByTagName("image");

            Element imageElement = (Element) picList.item(0);
            Element urlElement = (Element) imageElement.getElementsByTagName("url").item(0);
            String imageUrl = urlElement.getTextContent();

            //save the article pic in a file
            File picFile = null;

            try{

                URL downloadUrl = new URL(imageUrl);
                HttpURLConnection picCon = (HttpURLConnection) downloadUrl.openConnection();

                picFile = new File(picsDirectory.getAbsolutePath() + "/AutosportPic");

                BufferedInputStream is = new BufferedInputStream(picCon.getInputStream());
                BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(picFile));

                int toWrite;

                while( (toWrite = is.read()) != -1 ){

                    os.write(toWrite);
                }

                is.close();
                os.close();
                picCon.disconnect();

            }
            catch (MalformedURLException murl){
                Log.e("MlfrmdURL_Autosport", murl.getMessage());
            } catch (IOException e) {
                Log.e("IOExcptn_Autosport", e.getMessage());
            }

            final String picFileUrl = picFile.getAbsolutePath();


            //get the link and title information from the RSS XML
            NodeList items = document.getElementsByTagName("item");

            final  int totalThreads = items.getLength();

            for(int index=0; index < items.getLength(); index++){

                final int id = index + 1;

                Element itemElement = (Element) items.item(index);

                Element titleElement =  (Element) itemElement.getElementsByTagName("title").item(0);
                Element linkElement =  (Element) itemElement.getElementsByTagName("link").item(0);

                String title = titleElement.getTextContent();

                final String newTitle = title.replaceAll("'", "''");
                final String linkUrl  = linkElement.getTextContent();

                Thread subWorkingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //store data to database
                        String subThreadQuery = "insert into autosport_table values (" + id + ", '" + newTitle + "', '" +
                                linkUrl + "', '" + picFileUrl + "');";

                        //Log.e("AUTOSPORT_QUERY", subThreadQuery);

                        newsDatabase.execSQL(subThreadQuery);

                        addFinishedSubThread("AUTOSPORT", Thread.currentThread().getId());

                        if(getFinishedThreadsNo("AUTOSPORT") == totalThreads){ //last thread finishing satisfies this

                            synchronized (lock){ //notify branch thread to resume operation

                                lock.notify();
                            }
                        }
                    }
                });

                subWorkingThread.start();

            }

            //all sub threads fired up ... wait for them to finish
            synchronized (lock) {
                try {
                    lock.wait();

                }
                catch (InterruptedException ie){
                    Log.e("AutoSport_Thread", ie.getMessage());
                }
            }

            //all subthreads finished. Add motorsport branch thread to DONE list
            addBranchToFinished(Thread.currentThread().getId());

            //Log.e("AUTOSPORT_BRANCH", "AutoSport thread done");


        }
        catch (ParserConfigurationException pce){
            System.out.println("PCE thrown " + pce.getMessage());
        }
        catch (IOException io){
            System.out.println("IO thrown " + io.getMessage());
        }
        catch (SAXException sax){
            System.out.println("SAX thrown " + sax.getMessage());
        }

    }





    private void searchMotorsportComPage(final SQLiteDatabase newsDatabase){

        //will be used as a mean for the subThreads to notify this method's master thread they're all done
        final Object lock = new Object();

        final String rssInfo = apiCommunicator.getInfo(getApplicationContext().getResources().getString(R.string.motorsport_rss_address));

        newsDatabase.execSQL("create table if not exists motorsport_table (id integer primary key, title text not null, link text not null, path_to_pic text not null);");
        newsDatabase.execSQL("delete from motorsport_table;");
        newsDatabase.execSQL("vacuum;");

        //get the Directory where the pic files will be stored in
        final File picsDirectory = new File(getFilesDir().getAbsolutePath() + "/motorsport");

        if(!picsDirectory.exists())
            picsDirectory.mkdir();

        //add to map, monitor all inner threads
        addSiteThreads("MOTORSPORT");

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            Document document = builder.parse(new InputSource(new StringReader(rssInfo)));
            document.getDocumentElement().normalize();

            NodeList itemNodes = document.getElementsByTagName("item");

            final int totalThreads = itemNodes.getLength();

            for (int itemIndex=0; itemIndex < itemNodes.getLength(); itemIndex++){

                final int id = itemIndex + 1;

                Node itemNode = itemNodes.item(itemIndex);
                org.w3c.dom.Element item = (org.w3c.dom.Element) itemNode;

                String title = item.getElementsByTagName("title").item(0).getTextContent();

                final String newTitle = title.replaceAll("'", "''");
                final String linkUrl = item.getElementsByTagName("link").item(0).getTextContent();
                Element enclosure = (Element) item.getElementsByTagName("enclosure").item(0);
                final String picUrl = enclosure.getAttribute("url");

                Thread subWorkingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        File picFile = null;
                        //save the article pic in a file
                        try{

                            URL downloadUrl = new URL(picUrl);
                            HttpURLConnection picCon = (HttpURLConnection) downloadUrl.openConnection();

                            picFile = new File(picsDirectory.getAbsolutePath() + "/" + id);

                            BufferedInputStream is = new BufferedInputStream(picCon.getInputStream());
                            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(picFile));

                            int toWrite;

                            while( (toWrite = is.read()) != -1 ){

                                os.write(toWrite);
                            }

                            is.close();
                            os.close();
                            picCon.disconnect();

                        }
                        catch (MalformedURLException murl){
                            Log.e("MlfrmdURL_Motorsport", murl.getMessage());
                        } catch (IOException e) {
                            Log.e("IOExcptn_Motorsport", e.getMessage());
                        }


                        String picUrl = getFilesDir().getAbsolutePath() + "/motorsport/nowhere_to_be_found";

                        if(picFile != null)
                            picUrl = picFile.getAbsolutePath();

                        //if the file name is not valid, it will be caught on the news adapter and a question mark will take
                        //the image place

                        //store data to database
                        String subThreadQuery = "insert into motorsport_table values (" + id + ", '" + newTitle + "', '" +
                                linkUrl + "', '" + picUrl + "');";

                        //Log.e("MOTORSPORT_QUERY", subThreadQuery);

                        newsDatabase.execSQL(subThreadQuery);


                        addFinishedSubThread("MOTORSPORT", Thread.currentThread().getId());

                        if(getFinishedThreadsNo("MOTORSPORT") == totalThreads){ //last thread finishing satisfies this

                            synchronized (lock){ //notify branch thread to resume operation

                                    lock.notify();
                            }
                        }
                    }
                });

                subWorkingThread.start();

            }

            //all sub threads fired up ... wait for them to finish
            synchronized (lock) {
                try {
                    lock.wait();

                }
                catch (InterruptedException ie){
                    Log.e("Motrsport_Thread", ie.getMessage());
                }
            }

            //all subthreads finished. Add motorsport branch thread to DONE list
            addBranchToFinished(Thread.currentThread().getId());

            //Log.e("MOTORSPORT_BRANCH", "MotorSport thread done");


        }
        catch (ParserConfigurationException pce){
            System.out.println("PCE thrown " + pce.getMessage());
        }
        catch (IOException io){
            System.out.println("IO thrown " + io.getMessage());
        }
        catch (SAXException sax){
            System.out.println("SAX thrown " + sax.getMessage());
        }

    }






    private synchronized void addSiteThreads(String siteLabel){

        sitesThreads.put(siteLabel, new ArrayList<Long>());

    }

    private  synchronized  void addFinishedSubThread(String key, long threadId){

        sitesThreads.get(key).add(threadId);

    }


    /**
     * A thread gets inside the ArrayList that corresponds to the key, only if it is finished working.
     * @param key the site name the subthreads are working on
     * @return the number of threads finished working on it
     */
    private synchronized int getFinishedThreadsNo(String key){
        return sitesThreads.get(key).size();
    }


    private synchronized void addBranchToFinished(Long finishedBranchThreadId){
        finishedBranchThreads.add(finishedBranchThreadId);
    }


    private synchronized boolean areBranchesDone(){
        return (finishedBranchThreads.size() == totalBranches);
    }


}
