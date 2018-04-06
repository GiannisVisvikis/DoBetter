package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.content.Intent;
import android.database.sqlite.SQLiteDoneException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import android.support.v4.content.ContextCompat;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.AreResultsUploaded;
import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;

import comp.examplef1.iovisvikis.f1story.MainActivity;
import comp.examplef1.iovisvikis.f1story.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by iovisvikis on 14/4/2017.
 */


public class CalendarRaceAdapter extends RecyclerView.Adapter<CalendarRaceAdapter.CalendarViewHolder>{

    private ArrayList<CalendarRace> events;
    private DownloadFragment host;

    private DateTimeFormatter parser;
    private DateTime todayGMT;

    private ViewGroup parent;


    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.parent = parent;

        LayoutInflater inflater = host.getActivity().getLayoutInflater();
        
        View calendarHolderView = inflater.inflate(R.layout.race_calendar_row, parent, false);
        
        return new CalendarViewHolder(calendarHolderView);
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public void onBindViewHolder(final CalendarViewHolder holder, final int position) {

        final LayoutInflater inflater = host.getActivity().getLayoutInflater();
        final CalendarRace event = events.get(position);

        FrameLayout infoButton = holder.getInfoButton();
        final String uri = event.getCircuitUrl();
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                host.startActivity(infoIntent);
            }
        });

        final LinearLayout dateTimeLayout = holder.getDateTimeLayout();

        //String raceDateTimeTxt = race.getDate() + " " + race.getTime().substring(0, race.getTime().length() - 1);  //15:00:00Z --> 15:00:00

        //get the events session corresponding to this row



        try {

            final DateTime raceEvent = event.getRaceEvent();

            String circuitId = event.getCircuitId();

            //query to check if race results are available. Year is updated every time you change the txt file inside assets/events folder
            final String qualifyingQuery = MainActivity.BASIC_URI + raceEvent.getYear() + "/circuits/" + circuitId + "/qualifying.json";
            final String[] qualifyingParams = {qualifyingQuery};

            //query to check if events results are available
            final String resultsQuery = MainActivity.BASIC_URI + raceEvent.getYear() + "/circuits/" + circuitId + "/results.json";
            final String[] resultsParams = {resultsQuery};

            long millisToQualStart = event.getQualEvent().getMillis();
            long millisToQualEnd = event.getQualEvent().getMillis() + ( 75 * 60 * 1000); //1:15' past events started
            long nowMillis = todayGMT.getMillis();
            long millisToRaceStart = raceEvent.getMillis();
            long millisToRaceEnd = raceEvent.getMillis() + (150 * 60 * 1000); //2:30' past race started

            //convert race info to local time and date

            String localRaceDate = event.getLocalDate();
            String localRaceTime = event.getLocalTime();

            if(nowMillis < millisToQualStart){ //nothing started yet

                AppCompatTextView dateTxt = holder.getRaceDateTxt();
                dateTxt.setText(localRaceDate);

                AppCompatTextView timeTxt = holder.getRaceTimeTxt();

                timeTxt.setText(localRaceTime);

            }
            else{ // quals have started

                try {

                    // make a query to the ergast api and find out whether the results or the q3s are available. If so, set a click listener
                    //or add an under construction png and a toast message

                    FrameLayout qualifyingFrame = holder.getQualifyingOnPlace();

                    final FrameLayout qualifyingButton, resultsButton;

                    if(nowMillis > millisToQualStart && nowMillis < millisToQualEnd){ //events is on right now

                        Drawable inProgress = ContextCompat.getDrawable(host.getActivity(), R.drawable.qual_on);
                        qualifyingButton = setButton(inProgress);
                        qualifyingButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(host.getActivity(), host.getResources().getString(R.string.qualifying_in_progress), Toast.LENGTH_SHORT).show();
                            }
                        });
                        qualifyingFrame.addView(qualifyingButton);


                        AppCompatTextView dateTxt = holder.getRaceDateTxt();
                        dateTxt.setText(localRaceDate);

                        AppCompatTextView timeTxt = holder.getRaceTimeTxt();
                        timeTxt.setText(localRaceTime);

                    }
                    else if (nowMillis > millisToQualEnd && nowMillis < millisToRaceStart){ // events over but race not started yet search for events results
                        //check if events results are uploaded
                        boolean qualifyingResultsOn = new AreResultsUploaded().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, qualifyingParams).get();

                        if(qualifyingResultsOn){

                            Drawable qualsOn = ContextCompat.getDrawable(host.getActivity(), R.drawable.notify_qual);
                            qualifyingButton = setButton(qualsOn);
                            qualifyingButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view){
                                    Object[] params = {qualifyingQuery, "QualifyingResults", host, host.getResources().getString(R.string.getting_qualifying_results)};
                                    host.startListAdapterTask(params);
                                }
                            });

                        }
                        else {//not available yet, give an under construction png
                            Drawable qualUnderConstruction = ContextCompat.getDrawable(host.getActivity(), R.drawable.on_way);
                            qualifyingButton = setButton(qualUnderConstruction);
                            qualifyingButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(host.getActivity(), host.getResources().getString(R.string.qualifying_results_nont_ready_yet), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        qualifyingFrame.addView(qualifyingButton);

                        AppCompatTextView dateTxt = holder.getRaceDateTxt();
                        dateTxt.setText(localRaceDate);

                        AppCompatTextView timeTxt = holder.getRaceTimeTxt();
                        timeTxt.setText(localRaceTime);


                    }
                    else if (nowMillis > millisToRaceStart && nowMillis < millisToRaceEnd) { //race is on right now, events results should be on by now

                        dateTimeLayout.removeAllViews();

                        View qualifyings_results = inflater.inflate(R.layout.qualifyings_results, parent, false);

                        qualifyingButton = qualifyings_results.findViewById(R.id.qualifyingButton);
                        qualifyingButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Object[] params = {qualifyingQuery, "QualifyingResults", host, host.getResources().getString(R.string.getting_qualifying_results)};
                                host.startListAdapterTask(params);
                            }
                        });

                        Drawable resultsInProgress = ContextCompat.getDrawable(host.getActivity(), R.drawable.mic_race_on);
                        resultsButton = qualifyings_results.findViewById(R.id.resultsButton);
                        resultsButton.setBackground(resultsInProgress);
                        resultsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(host.getActivity(), host.getResources().getString(R.string.race_is_on_right_now), Toast.LENGTH_SHORT).show();
                            }
                        });

                        dateTimeLayout.addView(qualifyings_results);

                    }
                    else if (nowMillis > millisToRaceEnd && nowMillis < millisToRaceEnd + (24 * 60 * 60 * 1000)){ //difference is more than two hours but less than a day passed, check whether the results are on

                        dateTimeLayout.removeAllViews();

                        View qualifyings_results = inflater.inflate(R.layout.qualifyings_results, parent, false);

                        qualifyingButton = qualifyings_results.findViewById(R.id.qualifyingButton);
                        qualifyingButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Object[] params = {qualifyingQuery, "QualifyingResults", host, host.getResources().getString(R.string.getting_qualifying_results)};
                                host.startListAdapterTask(params);
                            }
                        });

                        boolean resultsUploaded = new AreResultsUploaded().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, resultsParams).get();

                        resultsButton = qualifyings_results.findViewById(R.id.resultsButton);

                        if(resultsUploaded){
                            resultsButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Object[] params = {resultsQuery, "Results", host, host.getResources().getString(R.string.getting_race_results)};
                                    host.startListAdapterTask(params);
                                }
                            });
                        }
                        else{
                            Drawable resultsUnderConstruction = ContextCompat.getDrawable(host.getActivity(), R.drawable.on_way);
                            resultsButton.setBackground(resultsUnderConstruction);
                            resultsButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(host.getActivity(), host.getResources().getString(R.string.race_results_under_construction), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        dateTimeLayout.addView(qualifyings_results);
                    }
                    else {//more than a day after the race, results should be uploaded by now
                        dateTimeLayout.removeAllViews();

                        View qualifyings_results = inflater.inflate(R.layout.qualifyings_results, parent, false);

                        qualifyingButton = qualifyings_results.findViewById(R.id.qualifyingButton);
                        qualifyingButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Object[] params = {qualifyingQuery, "QualifyingResults", host, host.getResources().getString(R.string.getting_qualifying_results)};
                                host.startListAdapterTask(params);
                            }
                        });

                        resultsButton =  qualifyings_results.findViewById(R.id.resultsButton);
                        resultsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Object[] params = {resultsQuery, "Results", host, host.getResources().getString(R.string.getting_race_results)};
                                host.startListAdapterTask(params);
                            }
                        });

                        dateTimeLayout.addView(qualifyings_results);
                    }

                }
                catch (InterruptedException ie){
                    Log.e("CLNDRADAPTER", ie.getMessage());
                }
                catch (ExecutionException ee){
                    Log.e("CLNDRADAPTER", ee.getMessage());
                }

            }


        }
        catch (SQLiteDoneException de){
            Log.e("CLNDRADPTR", " " + de);
        }


        ImageView flagFrame = holder.getCountryFlag();

        host.uploadCountryFlag(flagFrame, event.getCountryName());

        AppCompatTextView circuitTxt = holder.getCircuitNameTxt();
        circuitTxt.setText(event.getCircuitName());

        AppCompatTextView localityTxt = holder.getLocalityTxt();
        localityTxt.setText(event.getLocality());

    }


    @Override
    public int getItemCount(){
        return events.size();
    }



    private FrameLayout setButton(Drawable background){
        FrameLayout button = new FrameLayout(host.getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(params);
        button.setBackground(background);

        return button;
    }

    
    

    public CalendarRaceAdapter(DownloadFragment host, ArrayList<CalendarRace> events){

        //create a parser that parses GMT dates
        this.parser = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);
        this.todayGMT = new DateTime().toDateTime(DateTimeZone.UTC);

        //initialize class variables
        this.events = events;
        this.host = host;

    }



    class CalendarViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout getCalendarRowRoot() {
            return calendarRowRoot;
        }

        public LinearLayout getDateTimeLayout() {
            return dateTimeLayout;
        }

        public FrameLayout getQualifyingOnPlace() {
            return qualifyingOnPlace;
        }

        public FrameLayout getInfoButton() {
            return infoButton;
        }

        public AppCompatTextView getRaceDateTxt() {
            return raceDateTxt;
        }

        public AppCompatTextView getRaceTimeTxt() {
            return raceTimeTxt;
        }

        public AppCompatTextView getLocalityTxt() {
            return localityTxt;
        }

        public AppCompatTextView getCircuitNameTxt() {
            return circuitNameTxt;
        }

        public ImageView getCountryFlag() {
            return countryFlag;
        }

        private LinearLayout calendarRowRoot, dateTimeLayout;
        private FrameLayout qualifyingOnPlace, infoButton;
        private AppCompatTextView raceDateTxt, raceTimeTxt, localityTxt, circuitNameTxt;
        private ImageView countryFlag;

        public CalendarViewHolder(View itemView) {
            super(itemView);

            this.calendarRowRoot = itemView.findViewById(R.id.calendarRowRoot);
            this.dateTimeLayout = itemView.findViewById(R.id.dateTimeLayout);
            this.qualifyingOnPlace = itemView.findViewById(R.id.qualifyingOnPlace);
            this.infoButton = itemView.findViewById(R.id.infoButton);
            this.raceDateTxt = itemView.findViewById(R.id.race_dateTxt);
            this.raceTimeTxt = itemView.findViewById(R.id.race_timeTxt);
            this.countryFlag = itemView.findViewById(R.id.countryFlag);
            this.localityTxt = itemView.findViewById(R.id.localityTxt);
            this.circuitNameTxt = itemView.findViewById(R.id.circuitNameTxt);
        }
    }


}//CalendarRaceAdapter


